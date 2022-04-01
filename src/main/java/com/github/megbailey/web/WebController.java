package com.github.megbailey.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/oauth-callback")
    public String OAuthCallback() {
        return "OAuth2 complete. You can close this window";
    }

}
