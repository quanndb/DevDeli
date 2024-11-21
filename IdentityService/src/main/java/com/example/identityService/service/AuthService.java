package com.example.identityService.service;

import com.example.identityService.DTO.request.ChangePasswordRequestDTO;
import com.example.identityService.DTO.request.LoginRequestDTO;
import com.example.identityService.DTO.request.RegisterRequestDTO;
import com.example.identityService.DTO.request.UpdateProfileRequestDTO;
import com.example.identityService.DTO.response.CloudResponseDTO;
import com.example.identityService.DTO.response.LoginResponseDTO;
import com.example.identityService.DTO.response.UserResponseDTO;
import com.example.identityService.entity.Account;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.mapper.AccountMapper;
import com.example.identityService.mapper.CloudImageMapper;
import com.example.identityService.repository.IAccountRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    IAccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;

    CloudinaryService cloudinaryService;
    CloudImageMapper cloudImageMapper;

    RedisTemplate<String, String> redisTemplate;

    AccountMapper accountMapper;

    public LoginResponseDTO login(LoginRequestDTO request){
        Account account = getAccountByEmail(request.getEmail());

        boolean success = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if(!success) throw new AppExceptions(ErrorCode.INVALID_EMAIL_PASSWORD);

        String accessToken = tokenService.generateToken(account);
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .email(account.getEmail())
                .fullname(account.getFullname())
                .cloudImageUrl(account.getCloudImageUrl())
                .build();
    }

    public boolean register(RegisterRequestDTO request){
        accountRepository
                .findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new AppExceptions(ErrorCode.USER_EXISTED);
                });
        Account newAccount = accountMapper.toAccount(request);
        newAccount.setPassword(passwordEncoder.encode(request.getPassword()));

        accountRepository.save(newAccount);
        return true;
    }

    public Boolean logout(String token) {
        verifyToken(token);

        Jwt decodedToken = tokenService.getTokenDecoded(token);
        try{
            redisTemplate.opsForValue()
                    .set("token_id:"+decodedToken.getId(),
                            Objects.requireNonNull(decodedToken.getExpiresAt()).toString(),
                            Duration.ofMinutes(30));
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

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

    public void verifyToken(String token){
        boolean isValid = tokenService.verifyToken(token);
        if(!isValid) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
    }

    public Account getAccountByEmail(String email){
        return accountRepository.findByEmail(email)
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));
    }

    public Account getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(email.equals("anonymous")) throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
        return getAccountByEmail(email);
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

    public boolean changePassword(@Valid ChangePasswordRequestDTO request) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        if(currentPassword.equals(newPassword)) throw new AppExceptions(ErrorCode.PASSWORD_MUST_DIFFERENCE);

        Account foundUser = getCurrentUser();
        boolean isCorrectPassword = passwordEncoder.matches(request.getCurrentPassword(), foundUser.getPassword());
        if(!isCorrectPassword) throw new AppExceptions(ErrorCode.WRONG_PASSWORD);

        foundUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(foundUser);
        return true;
    }
}
