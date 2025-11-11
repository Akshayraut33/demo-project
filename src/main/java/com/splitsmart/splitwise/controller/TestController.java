package com.splitsmart.splitwise.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TestController {

    @GetMapping("/")
    public ResponseEntity<String> testApi(){
        return ResponseEntity.status(200).body("working");
    }

    @GetMapping("data")
    public ResponseEntity<String> getData(){
        return ResponseEntity.status(200).body("The data is here");
    }
    
    
}
