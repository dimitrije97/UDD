package com.example.boilerplate.service.impl;

import com.example.boilerplate.model.UserModel;
import com.example.boilerplate.service.ITokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService implements ITokenService {

    @Value("${security.secret}")
    private String secret;

    @Value("${security.expiration}")
    private String expiration;

    @Override
    public String generateAccessToken(UserModel user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getEmail());
        return Jwts.builder().setClaims(claims)
            .setExpiration(new Date(System.currentTimeMillis() + Integer.parseInt(expiration) * 1000))
            .signWith(SignatureAlgorithm.HS512, secret).compact();
    }
}
