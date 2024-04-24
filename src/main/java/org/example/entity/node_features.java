package org.example.entity;

import java.util.ArrayList;
import java.util.List;

public class node_features {
    private String key_word = null;
    private final List<node_feature> features = new ArrayList<>();

    public node_features(String key_word) {
        this.key_word = key_word;
    }


    public List<node_feature> getFeatures() {
        return features;
    }

    public void addFeatures(node_feature feature) {
        if(this.exits_features(feature)) {
           return;
        }
        this.features.add(feature);
    }

    public String getKey_word() {
        return key_word;
    }

    public void setKey_word(String key_word) {
        this.key_word = key_word;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("[node : ");
        str.append(key_word);
        str.append("\n");
        for(node_feature feature : features) {
            str.append(feature.toString());
        }
        str.append("]\n");
        return String.valueOf(str);
    }

    public boolean exits_features(node_feature feature) {
        String key_word = feature.getNode_name();
        List<String> parents_node = feature.getParents_node();
        List<String> brother_node = feature.getBrother_node();
        List<String> child_node = feature.getChild_node();
        for(node_feature feature1 : features) {
            int i = 0;
            if(!feature1.getNode_name().equals(key_word) || parents_node.size() != feature1.getParents_node().size() || brother_node.size() != feature1.getBrother_node().size() || child_node.size() != feature1.getChild_node().size()) {
                continue;
            }
            for(i = 0; i < parents_node.size(); i++) {
                if(!parents_node.get(i).equals(feature1.getParents_node().get(i))) {
                    break;
                }
            }
            if(i < parents_node.size()) {
                continue;
            }
            for(i = 0; i < brother_node.size(); i++) {
                if(!brother_node.get(i).equals(feature1.getBrother_node().get(i))) {
                    break;
                }
            }
            if(i < brother_node.size()) {
                continue;
            }
            for(i = 0; i < child_node.size(); i++) {
                if(!child_node.get(i).equals(feature1.getChild_node().get(i))) {
                    break;
                }
            }
            if(i < child_node.size()) {
                continue;
            }
            feature1.setCount(feature1.getCount() + 1);
            return true;
        }
        return false;
    }

}
