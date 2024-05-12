package org.example.service;

import org.example.entity.Weight_Matrix;
import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.util.List;

public class calculate_scores {
    public static void calculate_scores_era(List<node_features> list, double[][] matrix, Weight_Matrix weightMatrix) {
        int config_count = weightMatrix.get_config_count();
        for(node_features features : list) {
            String key_word = features.getKey_word();
            Integer i = weightMatrix.get_key_word_index(key_word);
            int count = 0;
            for(node_feature feature : features.getFeatures()) {
                count += feature.getCount();
                double scores = 0;
                double scores1 = 0;
                int score_count = 0;
                //计算s1
                for(String parents : feature.getParents_node()) {
                    scores += (double) weightMatrix.get_config_count(key_word, parents) / weightMatrix.get_config_count(key_word);
                    scores1 += (double) weightMatrix.get_config_count(key_word, parents) / weightMatrix.get_config_count(parents);
                    score_count += 1;
                }
                for(String brother : feature.getBrother_node()) {
                    scores += (double) weightMatrix.get_config_count(key_word, brother) / weightMatrix.get_config_count(key_word);
                    scores += (double) weightMatrix.get_config_count(key_word, brother) / weightMatrix.get_config_count(brother);
                    score_count += 1;
                }
                for(String child : feature.getChild_node()) {
                    scores += (double) weightMatrix.get_config_count(key_word, child) / weightMatrix.get_config_count(key_word);
                    scores += (double) weightMatrix.get_config_count(key_word, child) / weightMatrix.get_config_count(child);
                    score_count += 1;
                }
                feature.setScore((scores + scores1) / score_count);
            }
            //计算s2
            for(node_feature feature : features.getFeatures()) {
                feature.setScore(feature.getScore() * feature.getCount() / count);
            }
        }
    }

    public static void calculate_scores_gat(List<node_features> list, double[][] matrix, Weight_Matrix weightMatrix) {
        int config_count = weightMatrix.get_config_count();
        for(node_features features : list) {
            String key_word = features.getKey_word();
            Integer i = weightMatrix.get_key_word_index(key_word);
            int count = 0;
            int score_count = 0;
            //计算s1
            for(node_feature feature : features.getFeatures()) {
                count += feature.getCount();
                double scores = 0;
                for(String parents : feature.getParents_node()) {
                    scores += (double) weightMatrix.get_config_count(key_word, parents) / weightMatrix.get_config_count(key_word);
                    score_count += 1;
                }
                for(String brother : feature.getBrother_node()) {
                    scores += (double) weightMatrix.get_config_count(key_word, brother) / weightMatrix.get_config_count(key_word);
                    score_count += 1;
                }
                for(String child : feature.getChild_node()) {
                    scores += (double) weightMatrix.get_config_count(key_word, child) / weightMatrix.get_config_count(key_word);
                    score_count += 1;
                }
            }
//            //计算s2
//            for(node_feature feature : features.getFeatures()) {
//                feature.setScore(feature.getScore() * feature.getCount() / count);
//            }
        }
    }
}
