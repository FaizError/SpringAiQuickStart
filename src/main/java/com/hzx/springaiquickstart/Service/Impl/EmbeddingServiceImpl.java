package com.hzx.springaiquickstart.Service.Impl;

import com.hzx.springaiquickstart.Service.EmbeddingService;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.zhipuai.ZhiPuAiEmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private List<String> docs = List.of("我爱杨赏","杨赏是小猪","小美女是杨赏");

    private EmbeddingModel embeddingModel;

    private List<float[]> embedList;

    public EmbeddingServiceImpl(ZhiPuAiEmbeddingModel zhiPuAiEmbeddingModel) {

        this.embeddingModel = zhiPuAiEmbeddingModel;

        this.embedList = this.embeddingModel.embed(docs);
    }

    @Override
    public String findSimText(String message) {

        // 1.向量化message
        float[] embed = embeddingModel.embed(message);

        // 2.两个向量计算余弦相似度
        double maxSim = -1;
        int index = -1;
        for (int i = 0; i < embedList.size(); i++){

            // -1~1
            double sim = cosineSimilarity(embed, embedList.get(i));

            if(sim > maxSim){
                maxSim = sim;
                index = i;
            }

        }

        return docs.get(index);
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
