package com.taste.zip.tastezip.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity login() {
        return new ResponseEntity();
    }

    @PostMapping("/registration")
    public ResponseEntity registration() {
        return new ResponseEntity();
    }
}
