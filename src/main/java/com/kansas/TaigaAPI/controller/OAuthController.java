package com.kansas.TaigaAPI.controller;

import com.kansas.TaigaAPI.model.AuthRequest;
import com.kansas.TaigaAPI.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class OAuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/auth")
    public String auth(@RequestBody AuthRequest authRequest) {
        String token = authenticationService.authenticate(authRequest.getUsername(), authRequest.getPassword());
        return token;
    }
}
