package com.example.identityService.service;

import com.example.identityService.DTO.request.LoginRequestDTO;
import com.example.identityService.DTO.request.RegisterRequestDTO;
import com.example.identityService.DTO.response.LoginResponseDTO;
import com.example.identityService.entity.Account;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.repository.IAccountRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    IAccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;

    public LoginResponseDTO login(LoginRequestDTO request){
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new AppExceptions(ErrorCode.NOTFOUND_EMAIL));

        boolean success = passwordEncoder.matches(request.getPassword(), account.getPassword());
        if(!success) throw new AppExceptions(ErrorCode.INVALID_EMAIL_PASSWORD);

        String accessToken = tokenService.generateToken(account);
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .email(account.getEmail())
                .fullname(account.getFullname())
                .build();
    }

    public boolean register(RegisterRequestDTO request){
        accountRepository
                .findByEmail(request.getEmail())
                .ifPresent(_ -> {
                    throw new AppExceptions(ErrorCode.USER_EXISTED);
                });

        accountRepository.save(Account.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .fullname(request.getFullname())
                        .address(request.getAddress())
                        .gender(request.getGender())
                .build());
        return true;
    }
}
