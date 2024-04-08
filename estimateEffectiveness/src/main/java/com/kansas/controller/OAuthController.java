package com.kansas.controller;

import com.kansas.model.AuthRequest;
import com.kansas.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;


@RestController
@RequestMapping("/api")
public class OAuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/auth")
    public ResponseEntity<HashMap> auth(@RequestBody AuthRequest authRequest) {
        HashMap hashMap = new HashMap<>();
        try{
            String token = authenticationService.authenticate(authRequest.getUsername(), authRequest.getPassword());
            hashMap.put("token",token);
            return new ResponseEntity<>(hashMap, HttpStatus.OK);
        }catch (Exception e){
            hashMap.put("message","Access denied");
            return new ResponseEntity<>(hashMap, HttpStatus.FORBIDDEN);
        }

    }
}
