package org.example.service;

import org.example.entity.Triplet;
import org.example.entity.Weight_Matrix;
import org.example.entity.node_feature;
import org.example.entity.node_features;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
    public static List<String> listFiles(String folderPath) {
        List<String> fileNames = new ArrayList<>();

        // 创建 File 对象，表示文件夹路径
        File folder = new File(folderPath);

        // 检查文件夹是否存在
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定路径不是一个有效的文件夹！");
            return fileNames;
        }

        // 获取文件夹中的所有文件
        File[] files = folder.listFiles();

        // 遍历文件夹中的每个文件，并将文件名添加到列表中
        for (File file : files) {
            if (file.isFile()) {
                fileNames.add(file.getName());
            }
        }

        return fileNames;
    }

    public static Map<String, Integer> count_nums(List<Triplet> triplets) {

        // 使用 Map 统计每个元素出现的次数
        Map<String, Integer> counts = new HashMap<>();
        for (Triplet triplet : triplets) {
            String firstValue = triplet.getStart();
            counts.put(firstValue, counts.getOrDefault(firstValue, 0) + 1);
            String lastValue = triplet.getEnd();
            counts.put(lastValue, counts.getOrDefault(lastValue, 0) + 1);
        }
        System.out.println("元素出现的次数：" + counts);
        return counts;
    }

    public static Map<Triplet, Double> calculates_scores_spss(String path) throws IOException {
        /*
         * 这个函数需要完成对 数据集思科/取输入的数据集/卡方检验的数据集   这个目录中的AB两个数据集进行训练
         * 调用parse_config中的parse_cfg方法可以对指定文件进行解析，parse_config中的triplets就是保存的解析后所有的三元组集合，
         * 对A目录解析后的三元组list进行深拷贝后就能调用clear清除以便解析下一个目录
         * 这里进行卡方运算后保存好变量，可以更改返回类型
         */
        String path_a = "./数据集思科（Netcomplete综合）/取输入的数据集/卡方检验的数据集/A/";
//        String path_b = "./数据集思科（Netcomplete综合）/取输入的数据集/卡方检验的数据集/B/";
        String path_b = path;
        List<String> file_names_a = listFiles(path_a);
        List<String> file_names_b = listFiles(path_b);
        String saved_path = "";
        String out_suffix = "_out.out";

        Set<Triplet> trips_all = new HashSet<>();

        //A数据集中的次数
        parse_config cfg_a = new parse_config();
        for (String name_a : file_names_a) {
            cfg_a.parse_cfg(path_a, name_a, saved_path, out_suffix);
        }
        List<Triplet> trips_a = cfg_a.getTriplets();
        Map<String, Integer> nums_a = count_nums(trips_a);

        trips_all.addAll(trips_a);
        cfg_a.clear();

        //B数据集中的次数
        parse_config cfg_b = new parse_config();
        for (String name_b : file_names_b) {
            cfg_b.parse_cfg(path_b, name_b, saved_path, out_suffix);
        }
        List<Triplet> trips_b = cfg_b.getTriplets();
        Map<String, Integer> nums_b = count_nums(trips_b);

        for (Triplet triplet : trips_b) {
            trips_all.add(triplet);
        }

        Map<Triplet, Double> x_2 = new HashMap<>();

        for (Triplet triplet : trips_all) {
            String first = triplet.getStart();
            String last = triplet.getEnd();
            if (nums_a.containsKey(first) && nums_b.containsKey(first)&&nums_a.containsKey(last)&&nums_b.containsKey(last)) {
                int a = nums_a.get(first);
                int b = nums_b.get(first);
                int c = nums_a.get(last);
                int d = nums_b.get(last);
                int n = a + b + c + d;
                double s = a * d - b * c;
                //int Math.abs(s);
                double s1 = Math.abs(s);
                double x1 = s1 * s1 * n;
                double x2 = (a + b) * (c + d) * (a + c) * (b + d);
                double x = Math.abs(x1 / x2);
                x_2.put(triplet, x);
            }
        }

        return x_2;
    }

    public static Map<Triplet, Double> calculates_scores_spss_junniper(String path) throws IOException {
        String path_a = "./数据集junniper/固定取输入/";
//        String path_b = "./数据集junniper/卡方取输入/B/";
        String path_b = path;
        List<String> file_names_a = listFiles(path_a);
        List<String> file_names_b = listFiles(path_b);
        String saved_path = "";
        String out_suffix = "_out.out";

        Set<Triplet> trips_all = new HashSet<>();

        //A数据集中的次数
        parse_juniper cfg_a = new parse_juniper();
        for (String name_a : file_names_a) {
            cfg_a.parse_cfg(path_a + name_a, out_suffix);
        }
        List<Triplet> trips_a = parse_juniper.getTriplets();
        Map<String, Integer> nums_a = count_nums(trips_a);

        trips_all.addAll(trips_a);
        parse_juniper.clear();

        //B数据集中的次数
        parse_juniper cfg_b = new parse_juniper();
        for (String name_b : file_names_b) {
            cfg_b.parse_cfg(path_b + name_b, out_suffix);
        }
        List<Triplet> trips_b = parse_juniper.getTriplets();
        Map<String, Integer> nums_b = count_nums(trips_b);
        parse_juniper.clear();

        for (Triplet triplet : trips_b) {
            trips_all.add(triplet);
        }

        Map<Triplet, Double> x_2 = new HashMap<>();

        for (Triplet triplet : trips_all) {
            String first = triplet.getStart();
            String last = triplet.getEnd();
            if (nums_a.containsKey(first) && nums_b.containsKey(first)&&nums_a.containsKey(last)&&nums_b.containsKey(last)) {
                int a = nums_a.get(first);
                int b = nums_b.get(first);
                int c = nums_a.get(last);
                int d = nums_b.get(last);
                int n = a + b + c + d;
                double s = a * d - b * c;
                //int Math.abs(s);
                double s1 = Math.abs(s);
                double x1 = s1 * s1 * n;
                double x2 = (a + b) * (c + d) * (a + c) * (b + d);
                double x = Math.abs(x1 / x2);
                x_2.put(triplet, x);
            }
        }

        return x_2;
    }
}
