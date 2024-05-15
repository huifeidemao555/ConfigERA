package org.example.service;

import org.example.entity.Triplet;
import org.example.entity.Weight_Matrix;
import org.example.entity.node_feature;
import org.example.entity.node_features;
import org.example.service.parse_node;
import org.example.utils.CastToPyArray;

import java.io.*;
import java.util.*;

public class parse_juniper {

    private static Integer config_count = 0;
    private static final Set<String> key_words_set = new HashSet<>();
    private static final List<Triplet> triplets = new ArrayList<>();
    public static final Weight_Matrix matrix = new Weight_Matrix();

    public static List<node_features> features_list = new ArrayList<>();


    public static void parse_cfg(String path,  String out_suffix) throws IOException{
        Stack<String> stack = new Stack<>();
        List<String> myList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = "";
        String s2 = "";
        String a="";
        String sinterface = "interfaces";
        String b="";
        String c="";
        int ccc=0;
        try {
            while((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() >= 2 && line.charAt(0) == '#' && line.charAt(1) == '#') continue;//跳注释
                if(line.startsWith("version")) {
                    continue;
                }
                for (int i = line.length() - 2; i >= 0; i--) {
                    if (line.charAt(i) == '#' && line.charAt(i + 1) == '#') line = line.substring(0, i);
                    else if (line.charAt(i) == '/' && line.charAt(i + 1) == '*') line = line.substring(0, i);
                }
                // line = line.trim();
                if (line.charAt(line.length() - 1) == '{') { //小节入栈
                    if (ccc >= 1) ccc++;
                    if (ccc == 2) continue;
                    line = line.substring(0, line.length() - 1);
                    line = line.trim();
                    for (int i = 0; i < line.length(); i++) {
                        if (line.charAt(i) == ' ') {
                            line = line.substring(0, i);
                        }
                    }
                    stack.push(line);
                    if (stack.peek().equals(sinterface)) ccc = 1;
                } else if (line.charAt(0) == '}') {//弹出
                    if (ccc == 2) {
                        ccc--;
                        continue;
                    }
                    if (ccc >= 1) ccc--;
                    stack.pop();
                } else if (line.charAt(line.length() - 1) == ';') {//单句去参
                    line = line.substring(0, line.length() - 1);//去掉;
                    int blank = 0;
                    int n = line.length();
                    for (int i = 0; i < n; i++) {
                        if (line.charAt(i) == 32)
                            blank++;
                    }
                    line = line.replaceAll("\\[.*\\w.*\\]", "parameter");
                    line = line.replaceAll("\".*?\\w.*?\"", "parameter");
                    String[] ans = new String[blank + 1];
                    int j = 0;
                    int k = 0;
                    for (int i = 0; i < line.length() - 1; i++) {
                        if (line.charAt(i) == ' ') {
                            s2 = line.substring(k, i);
                            if (s2.matches(".*[0-9].*")) {
                                if (s2.matches("[0-9]+")) {
                                    ans[j] = "value";
                                } else if (s2.matches("\\:{3,}\\::") || s2.matches("\\.{3,}/")) {
                                    ans[j] = "Submask";
                                } else if (s2.matches("\\.{3,}")) {
                                    ans[j] = "IP_Address";
                                } else {
                                    ans[j] = "parameter";
                                }
                            }
                            else {
                                ans[j] = s2;
                            }
                            k = i + 1;
                            j = j + 1;
                        }
                    }
                    s2 = line.substring(k, line.length());
                    if (s2.matches(".*[0-9].*")) {
                        if (s2.matches("[0-9]+")) {
                            ans[j] = "value";
                        } else if (s2.matches("\\:{3,}\\::") || s2.matches("\\.{3,}/")) {
                            ans[j] = "Submask";
                        } else if (s2.matches("\\.{3,}")) {
                            ans[j] = "IP_Address";
                        } else {
                            ans[j] = "parameter";
                        }
                    }
                    else {
                        ans[j] = s2;
                    }
                    ans[0] = ans[0].trim();
                    c=ans[0];
                    b="internalCMD";
                    a=stack.peek();
                    myList.add(a+"_"+b+"_"+c);
                    stack.push(ans[0]);
                    for (int i = 1; i < j + 1; i = i + 1) {
                        ans[i] = ans[i].trim();
                        c=ans[i];
                        b="attrCMD";
                        a=stack.peek();
                        myList.add(a+"_"+b+"_"+c);
                        stack.push(ans[i]);
                    }
                    while (!stack.peek().equals(ans[0])) {
                        stack.pop();
                    }
                    stack.pop();
                }
            }
            reader.close();
        } catch (Exception e) { }
//        System.out.println(myList);
        for(String A:myList){
            String[] parts = A.split("_");
            if(Objects.equals(parts[2], "Submask")||Objects.equals(parts[2], "IP_Address")||Objects.equals(parts[2], "parameter")||Objects.equals(parts[2], "value")){
                parts[2]=parts[0]+"Attr";
            }
            if(!Objects.equals(parts[0], "Submask") && !Objects.equals(parts[0], "IP_Address") && !Objects.equals(parts[0], "parameter") && !Objects.equals(parts[0], "value")) {
                triplets.add(new Triplet(parts[0], parts[1], parts[2]));
            }
            if(!(parts[0].endsWith("Attr"))&&!Objects.equals(parts[0], "Submask") && !Objects.equals(parts[0], "IP_Address") && !Objects.equals(parts[0], "parameter") && !Objects.equals(parts[0], "value")) {
                key_words_set.add(parts[0]);
            }
            if(!(parts[2].endsWith("Attr"))) {
                key_words_set.add(parts[2]);
            }
            insert_to_matrix(parts[0], parts[2]);
        }
        write_same_level(myList);
    }
    private static void write_same_level(List<String> myList) {
        //获取行列索引
        List<List<String>> a = new ArrayList<>();
        for (String A:myList){
            String[] sublist2 = A.split("_");
            a.add(Arrays.asList(sublist2)) ;
        }
        for (String b:key_words_set){
            Set<String> d = new HashSet<String>();
            for (List<String> c:a){
                if(c.get(0).equals(b)){
                    d.add(c.get(2));
                }
            }
            for (String DD:d){
                for (String cc:d){
                    if(!Objects.equals(cc, DD)){
                        triplets.add(new Triplet(cc, "sameLevel", DD));
                    }
                }
            }

        }
    }
    private static void insert_to_matrix(String start, String end) {
        //获取行列索引
        if(matrix.get_key_word_index(start) == -1) {
            matrix.insert_key_word_index(start);
        }
        int row = matrix.get_key_word_index(start);
        if(matrix.get_key_word_index(end) == -1) {
            matrix.insert_key_word_index(end);
        }
        int column = matrix.get_key_word_index(end);

        //对度加一
        matrix.setMatrix(row, column);
    }
    public static Set<String> get_key_words_set() {
        return key_words_set;
    }
    public static List<Triplet> getTriplets() {
        return triplets;
    }


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

                if(feature != null && key_word.equals(start) && relation.equals("sameLevel")) {        //如果处于同级关系，则将添加到兄弟集合中去
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

    public static void clear() {
        features_list.clear();
        triplets.clear();
        matrix.clear();
        key_words_set.clear();
    }


}
