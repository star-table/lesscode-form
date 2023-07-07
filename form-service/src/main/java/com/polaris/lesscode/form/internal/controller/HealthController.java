package com.polaris.lesscode.form.internal.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner/api/v1/health")
public class HealthController {

    @GetMapping
    public String health(){
        return "ok";
    }
}
