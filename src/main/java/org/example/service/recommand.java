package org.example.service;

import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.util.List;

public class recommand {
    public static List<String> recommand_keyword(List<node_features> list, List<String> parents, String key_word, List<String> brother, int topN) {
        /**
         * 根据输入的input信息推荐出合适的关键字
         */
        if(topN == 0) { //没有采用topN的情况
            for(node_features features : list) {
                if(features.getKey_word().equals(key_word)) {
                    List<String> ret = null;
                    double score = 0.0;
                    int i = 0;
                    for(node_feature feature : features.getFeatures()) {
                        if(parents.size() != feature.getParents_node().size() || feature.getBrother_node().size() != brother.size()) {
                            continue;
                        }
                        for(i = 0; i < parents.size(); i++) {
                            if(!parents.get(i).equals(feature.getParents_node().get(i))) {
                                break;
                            }
                        }
                        if(i < parents.size()) {
                            continue;
                        }
                        for(i = 0; i < brother.size(); i++) {
                            if(!brother.get(i).equals(feature.getBrother_node().get(i))) {
                                break;
                            }
                        }
                        if(i < brother.size()) {
                            continue;
                        }

                        ret = feature.getScore() > score ? feature.getChild_node() : ret;
                        score = feature.getScore() > score ? feature.getScore() : score;
                    }
                    return ret;
                    break;
                }
            }
        } else {    ////采用topN的情况
            return null;
        }
    }
}
