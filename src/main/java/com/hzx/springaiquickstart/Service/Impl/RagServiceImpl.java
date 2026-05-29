package com.hzx.springaiquickstart.Service.Impl;

import com.hzx.springaiquickstart.Service.RagService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class RagServiceImpl implements RagService {

    // 存储本地文档切分内容
    private List<String> docs = new ArrayList<>();

    // 存储本地文档对应的向量
    private List<float[]> vectors = new ArrayList<>();

    // 嵌入模型
    private EmbeddingModel embeddingModel;

    // 聊天客户端
    private ChatClient chatClient;

    // 加载文档
    // 切分文档向量化

    public RagServiceImpl(ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel,ChatClient.Builder chatBuilder) throws IOException {

        this.embeddingModel = zhiPuAiEmbeddingModel;
        this.chatClient = chatBuilder.build();

        // 1.加载本地文档
        ClassPathResource classPathResource = new ClassPathResource("古代诗歌常用意象分类.txt");
        String content = new String(classPathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // 2.分割文档内容 通过embedding生成向量
        // a.分割文档
        String[] split = content.split("----");
        for (String part: split){
            System.out.println("part >>>>> " + part);

            // 存储切分的内容
            docs.add(part);

            // b.将片断向量化
            vectors.add(embeddingModel.embed(part));
        }

    }


    /**
     * 对用户输入的问题进行回答
     * @param question
     * @return
     */
    @Override
    public String answer(String question) {

        // 1.对用户提问内容向量化
        float[] embed = embeddingModel.embed(question);

        // 2.将向量化内容与知识库中各个向量进行相似度比对, 获取最相似的top2
        // 定义 top2相似度 的值和下标
        double maxOne = -1;
        int indexOne = -1;
        double maxTwo = -1;
        int indexTwo = -1;
        for (int i = 0; i < vectors.size(); i++){

            // -1~1
            double sim = cosineSimilarity(embed, vectors.get(i));
            if(sim > maxOne){
                // 赋值top2相似度 的值和下标
                maxTwo = maxOne;
                maxOne = sim;
                indexTwo = indexOne;
                indexOne = i;
            }else if(sim > maxTwo){
                maxTwo = sim;
                indexTwo = i;
            }

        }

        // 3.将获取到的top2 文档内容作为提示词交给 chat大模型
        // 获取top2 最相似chunk内容拼接在一起作为上下文prompt 提供给LLM
        String context = "";
        if(indexOne >= 0){
            context = docs.get(indexOne) + (indexTwo >= 0 ? "\n-----\n" + docs.get(indexTwo) : "");
        }

        // 构建回复
        // 准备prompt
        String prompt = "以下是知识库内容: \n" + context + "\n 请基于上述知识库内容回答用户问题: " + question;

        // 将获取到的top2 文档内容作为提示词交给 chat大模型
        ChatClient.CallResponseSpec spec = chatClient.prompt().system("你是知识助手,结合上下问回答用户问题").user(prompt).call();

        return spec.content();
    }

    // 余弦相似度实现
    private double cosineSimilarity(float[] a, float[] b){

        double dot = 0, na = 0, nb = 0;
        for (int i = 0;i < a.length;i++){
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }

        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
