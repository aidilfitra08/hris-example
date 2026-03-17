package com.hrisexample.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public Object root(
            @RequestHeader(value = "Accept", defaultValue = "") String accept) {

        if (accept.contains("text/html")) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("timestamp", timestamp);
            return mav;
        }

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("status", "UP");
        json.put("message", "Welcome to HRIS API");
        json.put("timestamp", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return ResponseEntity.ok()
                .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
                .body(json);
    }
}
