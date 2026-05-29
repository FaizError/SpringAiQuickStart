package com.hzx.springaiquickstart.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@RestController
@RequestMapping("/ai")
public class ChatController {

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

    @RequestMapping("/chat")
    public String chat(@RequestParam(value = "message") String message){

        // 与模型直接对话 调用deepSeekChatModel.call()方法
        return deepSeekChatModel.call(message);
    }

    @RequestMapping("/chatStream")
    public Flux<ChatResponse> chatStream(@RequestParam(value = "message") String message){

        // 与模型对话 流式返回内容
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> stream = deepSeekChatModel.stream(prompt);

        System.out.println("返回内容:" + stream);

        return stream;
    }

    @RequestMapping("/chatStream2")
    public Flux<String> chatStream2(@RequestParam(value = "message") String message, HttpServletResponse response){

        response.setCharacterEncoding("UTF-8");

        // 与模型对话 流式返回内容
        Prompt prompt = new Prompt(new UserMessage(message));
        Flux<ChatResponse> stream = deepSeekChatModel.stream(prompt);

        Flux<String> resp = stream.map(new Function<ChatResponse, String>() {

            @Override
            public String apply(ChatResponse chatResponse) {
                return chatResponse.getResult().getOutput().getText();
            }
        });

        Flux<String> map = stream.map(chatResponse -> {
            return chatResponse.getResult().getOutput().getText();
        });

        System.out.println("返回内容:" + resp);

        return resp;
    }

    @RequestMapping("/runTimeOptions")
    public Flux<String> runTimeOptions(@RequestParam(value = "message") String message, @RequestParam(value = "temp" , required = false) Double temp, HttpServletResponse response){

        response.setCharacterEncoding("UTF-8");

        System.out.println("收到消息: " + message + ", temp: " + temp);

        Prompt prompt = null;

        if(temp != null){
            DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions.builder().temperature(temp).build();
            prompt = new Prompt(message,deepSeekChatOptions);
        }else {
            prompt = new Prompt(message);
            System.out.println("使用默认的 配置...");
        }

        Flux<ChatResponse> stream = deepSeekChatModel.stream(prompt);

        Flux<String> resp = stream.map(chatResponse -> {
            return chatResponse.getResult().getOutput().getText();
        });

        System.out.println("返回内容:" + resp);

        return resp;
    }

}
