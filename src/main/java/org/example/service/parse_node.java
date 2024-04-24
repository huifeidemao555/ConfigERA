package org.example.service;


import org.example.entity.Triplet;
import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class parse_node {
    public static List<node_features> features_list = new ArrayList<>();

    public static void parse(List<Triplet> triplets, Set<String> key_word_set) throws CloneNotSupportedException {
        for(String key_word : key_word_set) {
            //对于每一个关键字都遍历一遍语义分析生成的三元组
            node_features features = new node_features(key_word);
            node_feature feature = null;

            boolean is_interface = false;
            String parents_node = null;
            for(Triplet triplet : triplets) {
                String start = triplet.getStart();
                String relation = triplet.getRelation();
                String end = triplet.getEnd();

                if("interface".equals(start) || "router".equals(start)) {
                    is_interface = true;
                }

                if(!is_interface && start.equals(key_word) && "".equals(parents_node)) {
                    if(feature != null && !features.exits_features(feature)) {
                        features.addFeatures(feature);
                    }
                    feature = new node_feature(key_word);
                }

                if("interface".equals(start) || "router".equals(start)) {
                    if(feature != null && !features.exits_features(feature)) {
                        features.addFeatures(feature);
                    }
                    feature = null;
                }

                if(key_word.equals(start) && relation.equals("sameLevel")) {        //如果处于同级关系，则将添加到兄弟集合中去
                    feature.addBrother_node(end);
                } else if(key_word.equals(start) && !relation.equals("sameLevel")) {                                 //如果是当前节点位于起始，则将后面节点添加到子节点集合中去
                    if(feature == null) {
                        feature = new node_feature(key_word);
                    }
                    feature.addChild_node(end);
                    parents_node = "";
                } else if(end.equals(key_word) && !relation.equals("sameLevel")) {                                   //如果当前节点位于末尾，则将前面节点添加到双亲结点集合中去
                    if(feature != null && !features.exits_features(feature)) {
                        features.addFeatures(feature);
                    }
                    feature = new node_feature(key_word);
                    feature.addParents_node(start);
                    parents_node = start;
                }

            }
            if(feature != null && !features.exits_features(feature)) {
                features.addFeatures(feature);
            }

            features = parse_features(features);
            //System.out.println(features);
            features_list.add(features);
        }
    }
    public static node_features parse_features(node_features features) throws CloneNotSupportedException {
        node_features features1 = new node_features(features.getKey_word());
        for(node_feature feature : features.getFeatures()) {
            if(feature.getChild_node().isEmpty()) {
                features1.addFeatures(feature);
                continue;
            }
            for(String child : feature.getChild_node()) {
                node_feature feature1 = feature.clone();
                feature1.addChild_node(child);
                features1.addFeatures(feature1);
            }
        }
        return features1;
    }

}
