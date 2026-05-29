package com.hzx.springaiquickstart.controller;

import com.hzx.springaiquickstart.Service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/rag")
public class RagController {

    @Autowired
    private RagService ragService;

    @RequestMapping("/ask")
    public Map<String,String> ask(@RequestParam(value = "question") String question){

        String resp = ragService.answer(question);

        return Map.of("question:" ,question,"resp:",resp);
    }

}
