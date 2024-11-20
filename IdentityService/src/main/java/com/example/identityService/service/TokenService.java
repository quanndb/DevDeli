package com.example.identityService.service;

import com.example.identityService.entity.Account;
import com.example.identityService.entity.Role;
import com.example.identityService.exception.AppExceptions;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.repository.IRoleRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TokenService {

    @NonFinal
    @Value("${app.secret}")
    String SECRET_KEY;

    IRoleRepository roleRepository;

    public String generateToken(Account account) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
            Role role = roleRepository.findById(account.getRoleId())
                    .orElseThrow(()->new AppExceptions(ErrorCode.ROLE_NOTFOUND));
            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(account.getEmail())
                    .issuer("lowland")
                    .issueTime(new Date(System.currentTimeMillis()))
                    .expirationTime(new Date(System.currentTimeMillis() + 60*60*1000))
                    .claim("scope", role.getName())
                    .build();

            Payload payload = new Payload(jwtClaimsSet.toJSONObject());

            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        try{
            if (!(verified && expiryTime.after(new Date()))) {
                throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
            }
            return true;
        }
        catch (AppExceptions exceptions){
            return false;
        }
    }
}
