package org.example.service;

import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.util.ArrayList;
import java.util.List;

public class recommand {
    public static List<String> recommand_keyword(List<node_features> list, List<String> parents, List<String> brother, String key_word, int topN) {
        /**
         * 根据输入的input信息推荐出合适的关键字
         * list为存放特征的list，[[no:], [ip:]]这种格式
         * parents，传入其所有的父节点
         * brother，传入其所有的兄弟节点
         * key_word,当前的关键字
         * topN,控制topn_list缓存的大小
         * ARC，是否启用ARC算法
         *
         * 这个需要实现使用ARC算法和不启用ARC算法两种情况，我已经实现了启用的情况，需要实现没有启用的情况
         * 主要的逻辑是输入上面的参数，在所有的特征中寻找和当前关键字一样的一个list，再在list中根据parents和brother寻找匹配的，
         * 可能会返回多个结果，
         */
        List<node_feature> topn_list = new ArrayList<>();
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
                    if(!feature.getBrother_node().containsAll(brother)) {
                        continue;
                    }
                    add_to_topn_list(topn_list, topN, feature);
                }
                return get_ret_from_topn_list(topn_list);
            }
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
            if(feature.getChild_node().isEmpty()) {
                continue;
            }
            ret.add(feature.getChild_node().get(0));
        }
        return ret;
    }
}
