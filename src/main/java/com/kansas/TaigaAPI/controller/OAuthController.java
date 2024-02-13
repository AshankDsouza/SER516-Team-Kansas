package com.kansas.TaigaAPI.controller;

import com.kansas.TaigaAPI.model.AuthRequest;
import com.kansas.TaigaAPI.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/api")
public class OAuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/auth")
    public HashMap auth(@RequestBody AuthRequest authRequest) {
        String token = authenticationService.authenticate(authRequest.getUsername(), authRequest.getPassword());
        HashMap authToken=new HashMap<>();
        authToken.put("token",token);
        return authToken;
    }
}
