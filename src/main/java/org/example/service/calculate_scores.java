package org.example.service;

import org.example.entity.Weight_Matrix;
import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.util.List;

public class calculate_scores {
    public static void calculate_scores(List<node_features> list, double[][] matrix, Weight_Matrix weightMatrix) {
        for(node_features features : list) {
            String key_word = features.getKey_word();
            Integer i = weightMatrix.get_key_word_index(key_word);
            int count = 0;
            for(node_feature feature : features.getFeatures()) {
                count += feature.getCount();
                double scores = 0;
                int score_count = 0;
                for(String parents : feature.getParents_node()) {
                    Integer j = weightMatrix.get_key_word_index(parents);
                    if(j >= 0) {
                        scores += matrix[i][j];
                        score_count += 1;
                    }
                }
                for(String brother : feature.getBrother_node()) {
                    Integer j = weightMatrix.get_key_word_index(brother);
                    scores += matrix[i][j];
                    score_count += 1;
                }
                for(String child : feature.getChild_node()) {
                    Integer j = weightMatrix.get_key_word_index(child);
                    scores += matrix[i][j];
                    score_count += 1;
                }
                feature.setScore(scores / score_count);
            }
            for(node_feature feature : features.getFeatures()) {
                feature.setScore(feature.getScore() * feature.getCount() / count);
            }
        }
    }
}
