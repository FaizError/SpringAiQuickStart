package com.hzx.springaiquickstart.controller;

import com.hzx.springaiquickstart.Service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/embedding")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    @RequestMapping("/findSimText")
    public Map findSimText(@RequestParam(value = "message") String message){

        return Map.of(message,embeddingService.findSimText(message));
    }
}
