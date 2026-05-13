package com.project.appointmentmanager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {


    private String secretKey = "yourSecretKeyHereMakeItLongEnough123456";
    private long expiration = 86400000;


    //jwt take secretKey in Key object not just nay string
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    //takes email and role from use during login and generate the token (called on login)
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)              // who this token belongs to
                .claim("role", role)        //store role inside token
                .setIssuedAt(new Date())        //created right now
                .setExpiration(new Date(System.currentTimeMillis() + expiration))   // expires in 24 hours
                .signWith(getSigningKey())              // sign it with secret key
                .compact();                             // build the final token string
    }


//    extractEmail() and extractRole() — called on EVERY REQUEST   get from payload
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token); //this line throws if anything is wrong like tampered or expired
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //internal helper - do the token verification
//    his is the actual verification step. parseClaimsJws() checks the signature and expiry in one shot.
//    If valid, it returns the payload so you can read email and role from it.
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();   //return payload aka claims
    }
}