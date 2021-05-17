package com.custom.config.auth;

import com.custom.util.Property;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtToken {
    static String key = Property.getJwtKey();

    static public Claims getUserInfo(String token){
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    static public String returnJwt(Map map){
        Map<String,Object> claims = new HashMap<>();
        claims.put("data",map);

        byte[] decodedSecret = Base64.getDecoder().decode(key);
        SecretKey decodedKey = Keys.hmacShaKeyFor(decodedSecret);

        Date expiration = new Date(System.currentTimeMillis()+30*24*60*60*1000L);

        String jwt= Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .setSubject("").signWith(decodedKey).compact();
        return jwt;
    }
}
