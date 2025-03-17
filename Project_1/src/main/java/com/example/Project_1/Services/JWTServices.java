package com.example.Project_1.Services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServices {

    public String secretKey= "";

    private SecretKey getKey (){
        byte [] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    public JWTServices(){
        try{
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        }catch (NoSuchAlgorithmException no){
            throw new RuntimeException();
        }
    }

    public String generateToken( String username){

        Map<String ,Object> obj = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(obj)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String First(String token){
        return Second(token, Claims::getSubject);
    }

    private <T> T Second(String token, Function<Claims,T> function){
        final Claims claims = Third(token);
        return function.apply(claims);
    }

    private Claims Third(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String username = First(token);
        return (username.equals(userDetails.getUsername()) && !isFour(token));
    }

    private boolean isFour(String token){
        return Five(token).before(new Date());
    }

    private Date Five(String token){
        return Second(token,Claims::getExpiration);
    }
    
}
