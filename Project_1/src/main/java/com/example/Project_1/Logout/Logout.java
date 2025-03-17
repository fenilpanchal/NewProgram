package com.example.Project_1.Logout;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class Logout {

    private final Set<String> logoutToken = new HashSet<>();

    public void SetLogoutToken(String token){
         logoutToken.add(token);
    }

    public boolean isLogoutToken(String token){
        return logoutToken.contains(token);
    }
 }
