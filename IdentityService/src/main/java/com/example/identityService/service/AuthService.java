package com.example.identityService.service;

import com.example.identityService.DTO.request.*;
import com.example.identityService.DTO.response.CloudResponseDTO;
import com.example.identityService.DTO.response.LoginResponseDTO;
import com.example.identityService.DTO.response.UserResponseDTO;
import com.example.identityService.Util.TimeConverter;
import com.example.identityService.entity.Account;
import com.example.identityService.entity.EnumRole;
import com.example.identityService.entity.Logs;
import com.example.identityService.entity.Token;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.mapper.AccountMapper;
import com.example.identityService.mapper.CloudImageMapper;
import com.example.identityService.repository.IAccountRepository;
import com.example.identityService.repository.ILoggerRepository;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    @NonFinal
    @Value(value = "${app.baseUrl}")
    String APP_BASEURL;

    @NonFinal
    @Value(value = "${security.authentication.max-login-attempt}")
    Integer MAX_LOGIN_ATTEMPT;
    @NonFinal
    @Value(value = "${security.authentication.login-delay-fail}")
    String LOGIN_DELAY_FAIL;
    @NonFinal
    @Value(value = "${security.authentication.max-forgot-password-attempt}")
    Integer MAX_FORGOT_PASSWORD_ATTEMPT;
    @NonFinal
    @Value(value = "${security.authentication.delay-forgot-password}")
    String DELAY_FORGOT_PASSWORD;

    @NonFinal
    @Value(value = "${security.authentication.jwt.access-token-life-time}")
    String ACCESS_TOKEN_LIFE_TIME;
    @NonFinal
    @Value(value = "${security.authentication.jwt.refresh-token-life-time}")
    String REFRESH_TOKEN_LIFE_TIME;
    @NonFinal
    @Value(value = "${security.authentication.jwt.email-token-life-time}")
    String EMAIL_TOKEN_LIFE_TIME;

    IAccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;

    CloudinaryService cloudinaryService;
    CloudImageMapper cloudImageMapper;
    EmailService emailService;
    ILoggerRepository loggerRepository;

    RedisTemplate<String, String> redisTemplate;

    AccountMapper accountMapper;

    // -----------------------------Login logout start-------------------------------
    public LoginResponseDTO login(LoginRequestDTO request, String ip){
        Account account = getAccountByEmail(request.getEmail());
        if(!account.isVerified()) throw new AppExceptions(ErrorCode.NOT_VERIFY_ACCOUNT);
        boolean success = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if(!success){
            String key = String.join("","login-attempt:", account.getEmail());
            String attemptTimeString = redisTemplate.opsForValue()
                    .get(key);

            if(attemptTimeString != null && Integer.parseInt(attemptTimeString) == MAX_LOGIN_ATTEMPT)
                throw new AppExceptions(ErrorCode.TOO_MUCH_LOGIN_FAIL);
            int value = attemptTimeString != null ? Integer.parseInt(attemptTimeString) + 1 : 1;
            redisTemplate.opsForValue().set(key, Integer.toString(value),
                    Duration.ofMillis(TimeConverter.convertToMilliseconds(LOGIN_DELAY_FAIL)));
            throw new AppExceptions(ErrorCode.INVALID_EMAIL_PASSWORD);
        }

        return loginProcess(account, ip);
    }

    public LoginResponseDTO loginProcess(Account account, String ip){
        boolean isNewIp = !loggerRepository.existsByEmailAndIp(account.getEmail(),ip);
        if(isNewIp){
            sendConfirmValidIp(account.getEmail(), ip);
            throw new AppExceptions(ErrorCode.UNKNOWN_IP_REQUESTED);
        }

        String accessToken = tokenService.accessTokenFactory(account);
        String refreshToken = tokenService.generateRefreshToken(account.getEmail(), ip);
        loggerRepository.save(Logs.builder()
                .dateTime(LocalDateTime.now())
                .actionName("LOGIN")
                .email(account.getEmail())
                .ip(ip)
                .build());
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(account.getEmail())
                .fullname(account.getFullname())
                .cloudImageUrl(account.getCloudImageUrl())
                .build();
    }

    public Boolean logout(String accessToken, String refreshToken) {
        boolean isDisabledAccessToken = tokenService.deActiveToken(new Token(accessToken,
                TimeConverter.convertToMilliseconds(ACCESS_TOKEN_LIFE_TIME)));
        boolean isDisabledRefreshToken = tokenService.deActiveToken(new Token(refreshToken,
                TimeConverter.convertToMilliseconds(REFRESH_TOKEN_LIFE_TIME)));
        return  isDisabledAccessToken && isDisabledRefreshToken;
    }

    public void sendConfirmValidIp(String email, String ip){
        String verifyToken = tokenService.generateTempEmailToken(email,ip);
        String verifyUrl = String.join("",APP_BASEURL,"auth/verification?token=",verifyToken);
        emailService
                .sendEmail(new EmailRequestDTO("Confirm that is you",
                        String.join(" ","Please click here to confirm your IP", verifyUrl)
                        ,List.of(email)));
    }
    // -----------------------------Login logout end-------------------------------

    // -----------------------------Registration flow start-------------------------------
    public boolean register(RegisterRequestDTO request, String ip){
        accountRepository
                .findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new AppExceptions(ErrorCode.USER_EXISTED);
                });
        Account newAccount = accountMapper.toAccount(request);
        newAccount.setRoleId(EnumRole.USER.getId());
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));

        accountRepository.save(newAccount);
        loggerRepository.save(Logs.builder()
                        .dateTime(LocalDateTime.now())
                        .actionName("REGISTRATION")
                        .email(newAccount.getEmail())
                        .ip(ip)
                .build());

        sendVerifyEmail(newAccount.getEmail(), ip);
        return true;
    }

    public void sendVerifyEmail(String email, String ip){
        String verifyToken = tokenService.generateTempEmailToken(email, ip);
        String verifyUrl = String.join("",APP_BASEURL,"auth/verification?token=",verifyToken);
        emailService
                .sendEmail(new EmailRequestDTO("Confirm your registration",
                                String.join(" ","Please click here to confirm your registration", verifyUrl)
                                ,List.of(email)));
    }

    public Object verifyEmailAndIP(String token, String ip){
        Claims claims = tokenService.extractClaims(token);
        String email = claims.getSubject();
        String ipFromToken = claims.get("IP").toString();
        if(!tokenService.verifyToken(token) || !ip.equals(ipFromToken))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        boolean foundLog =loggerRepository
                .existsByEmailAndIp(email, ipFromToken);

        Account account = getAccountByEmail(email);
        if(foundLog){
            account.setVerified(true);
            accountRepository.save(account);
            return true;
        }

        loggerRepository.save(Logs.builder()
                .dateTime(LocalDateTime.now())
                .actionName("CONFIRM_IP")
                .email(email)
                .ip(ip)
                .build());

        return loginProcess(account, ip);
    }
    // -----------------------------Registration flow end-------------------------------

    // -----------------------------User information start-------------------------------
    // profile
    public UserResponseDTO getProfile() {
        Account foundUser = getCurrentUser();
        return UserResponseDTO.builder()
                .email(foundUser.getEmail())
                .address(foundUser.getAddress())
                .fullname(foundUser.getFullname())
                .gender(foundUser.getGender())
                .cloudImageUrl(foundUser.getCloudImageUrl())
                .build();
    }

    public boolean updateProfile(UpdateProfileRequestDTO request, MultipartFile image) throws IOException {
        Account foundUser = getCurrentUser();
        if(request != null){
            accountMapper.updateAccount(foundUser, request);
        }
        if(request != null && request.getCloudImageUrl() == null && image != null) {
            String oldCloudId = foundUser.getCloudImageId();
            if(oldCloudId != null){
                cloudinaryService.delete(oldCloudId);
            }
            Map<?,?> cloudResponse = cloudinaryService.upload(image);
            CloudResponseDTO cloudResponseDTO = cloudImageMapper.toCloudResponse(cloudResponse);
            foundUser.setCloudImageId(cloudResponseDTO.getPublicId());
            foundUser.setCloudImageUrl(cloudResponseDTO.getUrl());
        }
        accountRepository.save(foundUser);
        return true;
    }

    // password
    public boolean changePassword(ChangePasswordRequestDTO request, String ip) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        if(currentPassword.equals(newPassword)) throw new AppExceptions(ErrorCode.PASSWORD_MUST_DIFFERENCE);

        Account foundUser = getCurrentUser();
        boolean isCorrectPassword = passwordEncoder.matches(request.getCurrentPassword(), foundUser.getPassword());
        if(!isCorrectPassword) throw new AppExceptions(ErrorCode.WRONG_PASSWORD);

        foundUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(foundUser);

        loggerRepository.save(Logs.builder()
                .dateTime(LocalDateTime.now())
                .actionName("CHANGE_PASSWORD")
                .email(foundUser.getEmail())
                .ip(ip)
                .build());
        return true;
    }

    public boolean forgotPassword(String email, String ip) {
        getAccountByEmail(email);
        sendForgotPasswordEmail(email, ip);
        return true;
    }

    public boolean resetPassword(String token, String newPassword, String ip) {
        String email = tokenService.getTokenDecoded(token).getSubject();

        String key = String.join("","forgot-password-attempt:", email);
        String attempValueString = redisTemplate.opsForValue().get(key);

        String activeToken = attempValueString != null ? attempValueString.split("@")[1] : null;
        if(!tokenService.verifyToken(token) || email == null || !Objects.equals(activeToken, token))
            throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        Account foundAccount = getAccountByEmail(email);
        foundAccount.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(foundAccount);

        loggerRepository.save(Logs.builder()
                .dateTime(LocalDateTime.now())
                .actionName("RESET_PASSWORD")
                .email(foundAccount.getEmail())
                .ip(ip)
                .build());

        tokenService.deActiveToken(new Token(token, TimeConverter.convertToMilliseconds(EMAIL_TOKEN_LIFE_TIME)));

        sendResetPasswordSuccess(email);
        return true;
    }

    public void sendForgotPasswordEmail(String email, String ip){
        String forgotPasswordToken = tokenService.generateTempEmailToken(email, ip);

        String key = String.join("","forgot-password-attempt:", email);
        String attempValueString = redisTemplate.opsForValue().get(key);

        Integer attemptTime = attempValueString != null ? Integer.parseInt(attempValueString.split("@")[0])+1 : 1;

        if(attemptTime.equals(MAX_FORGOT_PASSWORD_ATTEMPT+1))
            throw new AppExceptions(ErrorCode.TOO_MUCH_FORGOT_PASSWORD_ATTEMPT);

        String value = String.join("@", attemptTime.toString(), forgotPasswordToken);

        redisTemplate.opsForValue().set(key, value,
                Duration.ofMillis(TimeConverter.convertToMilliseconds(DELAY_FORGOT_PASSWORD)));

        String verifyUrl = String.join("",APP_BASEURL,"auth/resetPassword?token=",forgotPasswordToken);
        emailService
                .sendEmail(new EmailRequestDTO("Confirm your reset password process",
                        String.join(" ","Please click here to reset password", verifyUrl)
                        ,List.of(email)));
    }

    public void sendResetPasswordSuccess(String email){
        emailService
                .sendEmail(new EmailRequestDTO("Change password successfully",
                        String.join(" ","Your password has been set at", LocalDateTime.now().toString())
                        ,List.of(email)));
    }
    // -----------------------------User information end-------------------------------

    // -----------------------------Utilities start-------------------------------
    public Account getAccountByEmail(String email){
        return accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
    }

    public Account getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email.equals("anonymous")) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        return getAccountByEmail(email);
    }

    public String getNewAccessToken(String refreshToken){
        String email = tokenService.getTokenDecoded(refreshToken).getSubject();
        if(!tokenService.verifyToken(refreshToken) || email == null) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);

        Account foundAccount = getAccountByEmail(email);
        return tokenService.accessTokenFactory(foundAccount);
    }
    // -----------------------------Utilities end-------------------------------
}
