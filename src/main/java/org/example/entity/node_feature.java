package org.example.entity;

import java.util.ArrayList;
import java.util.List;

public class node_feature implements Cloneable {
    private String node_name = "";
    private Double score = 0.00;
    private Integer count = 1;
    private final List<String> parents_node = new ArrayList<>();
    private final List<String> brother_node = new ArrayList<>();
    private final List<String> child_node = new ArrayList<>();

    public List<String> getParents_node() {
        return parents_node;
    }

    public void addParents_node(String parents) {
        this.parents_node.add(parents);
    }

    public List<String> getBrother_node() {
        return brother_node;
    }

    public void addBrother_node(String brother) {
        this.brother_node.add(brother);
    }

    public List<String> getChild_node() {
        return child_node;
    }

    public String getNode_name() {
        return node_name;
    }

    public void setNode_name(String node_name) {
        this.node_name = node_name;
    }

    public void addChild_node(String child) {
        this.child_node.add(child);
    }


    public node_feature(String node_name) {
        this.node_name = node_name;
    }

    public Double getScore() {
        return score;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "node_feature{" +
                "node_name='" + node_name + '\'' +
                ", parents_node=" + parents_node +
                ", brother_node=" + brother_node +
                ", child_node=" + child_node +
                ", count=" + count +
                ", score=" + score +
                "}\n";
    }

    @Override
    public node_feature clone() throws CloneNotSupportedException {
        node_feature newNode = new node_feature(this.node_name);
        newNode.parents_node.addAll(this.parents_node);
        newNode.brother_node.addAll(this.brother_node);
        return newNode;
    }
}
