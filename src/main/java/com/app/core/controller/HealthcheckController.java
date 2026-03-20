package com.app.core.controller;

import com.app.core.annotation.PublicMethod;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@PublicMethod
@RestController
@RequestMapping("")
public class HealthcheckController {

    @GetMapping("/healthz")
    public ResponseEntity<Object> healthz() {
        return ResponseEntity.ok().build();
    }
}
