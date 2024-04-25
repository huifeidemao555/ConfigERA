package org.example.service;

import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.util.ArrayList;
import java.util.List;

public class recommand {
    public static List<String> recommand_keyword(List<node_features> list, List<String> parents, List<String> brother, String key_word, int topN, boolean ARC) {
        /**
         * 根据输入的input信息推荐出合适的关键字
         */
        List<node_feature> topn_list = new ArrayList<>();
        if(!ARC) { //没有采用ARC的情况
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
                        add_to_topn_list(topn_list, topN, feature);
                    }
                    return get_ret_from_topn_list(topn_list);
                }
            }
        } else {    ////采用topN的情况
            return null;
        }
        return null;
    }


    private static void add_to_topn_list(List<node_feature> list, int topN, node_feature feature) {
        if(list.size() < topN) {
            list.add(feature);
        } else {
            int min_index = 0;
            for(int i = 1; i < list.size(); i++) {
                if(list.get(min_index).getScore() >= list.get(i).getScore()) {
                    min_index = i;
                }
            }
            if(list.get(min_index).getScore() < feature.getScore()) {
                list.remove(min_index);
                list.add(feature);
            }
        }
    }

    private static List<String> get_ret_from_topn_list(List<node_feature> list) {
        List<String> ret = new ArrayList<>();
        for(node_feature feature : list) {
            ret.add(feature.getChild_node().toString());
        }
        return ret;
    }
}
