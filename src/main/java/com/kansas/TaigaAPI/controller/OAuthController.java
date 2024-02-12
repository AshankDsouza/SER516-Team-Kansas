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
    public void auth(@RequestBody AuthRequest authRequest) {
        authenticationService.authenticate(authRequest.getUsername(), authRequest.getPassword());
    }
}
