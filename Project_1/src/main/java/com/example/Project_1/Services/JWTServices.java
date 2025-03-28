package com.example.Project_1.Services;

import com.example.Project_1.Model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public String generateToken(String username , Role role){
        Map<String ,Object> obj = new HashMap<>();
        obj.put("role",role.name());

        return Jwts.builder()
                .claims(obj)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .signWith(getKey())
                .compact();
    }

    public String First(String token){
        return Second(token, Claims::getSubject);
    }

    public List<String> extraRole(String token) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("JWT Claims: " + claims);

            Object roleObject = claims.get("role");

            if (roleObject == null) {
                throw new IllegalArgumentException("Role not found in JWT token");
            }

            if (roleObject instanceof String string) {
                return Collections.singletonList(string);
            } else if (roleObject instanceof List<?> roleList) {
                return roleList.stream()
                        .filter(obj -> obj instanceof String)
                        .map(obj -> (String) obj)
                        .collect(Collectors.toList());
            }else if(roleObject instanceof Map<?,?>rolMap){
                Object authority= rolMap.get("authority");
                if (authority instanceof String){
                    return Collections.singletonList((String) authority);
                }
            }
            throw new IllegalArgumentException("Unexpected role format in JWT token");

        } catch (Exception e) {
            System.err.println("Error extracting role: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    private <T> T Second(String token, Function<Claims,T> function){
        final Claims claims = Three(token);
        return function.apply(claims);
    }

    private Claims Three (String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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
