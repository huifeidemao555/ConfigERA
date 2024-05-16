package org.example;


import jnr.ffi.annotations.In;
import org.example.entity.Triplet;
import org.example.entity.node_feature;
import org.example.entity.node_features;
import org.example.python.PythonInterpret;
import org.example.service.calculate_scores;
import org.example.service.parse_config;
import org.example.service.parse_node;
import org.example.service.useARC;
import org.example.utils.CastToPyArray;
import org.python.core.AstList;
import sun.util.calendar.BaseCalendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.service.recommand.get_ret_spss;
import static org.example.service.recommand.recommand_keyword;

public class Main {
//    private static String path = "./数据集思科（Netcomplete综合）/取输入的数据集/";
    private static String path = "./数据集思科（Netcomplete综合）/150设备推荐/";
    private static String saved_path = "saved_out/";
//    private static String file_name = "Ulmi.cfg";
    private static String file_name = "PeerSlough_1.cfg";
    private static final String out_suffix = "_out.out";
    private static List<node_feature> input = new ArrayList<>();
    private static Integer topN = 3;
    private static Integer ARC_capacity = 1;
    private static List<node_feature> low_freq_input = null;
    private static List<node_feature> high_freq_input = null;


    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        get_input();
        split_input(input);
        input = high_freq_input;
//        System.out.println("低频的数据集：");
//        System.out.println(low_freq_input);
//        System.out.println("高频的数据集：");
//        System.out.println(high_freq_input);
        ConfigERA();
//        traditional_statistic();
//        spss();
//        ConfigERA_ARC();
//        GAT();
        //1.解析配置
//        int count = parse_config.parse_cfg(path, file_name, saved_path, out_suffix);
//        System.out.println("三元组的数量为" + count);
//        System.out.println(parse_config.get_key_words_set());
//        System.out.println(parse_config.getTriplets());
//        System.out.println("******************************************************");
//        //2.从节点中解析出特征
//        parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());
//        parse_config.matrix.printAdjMatrix();
//        parse_config.matrix.printDegreeMatrix();
//        System.out.println(parse_node.features_list);
//        //System.out.println(CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));
//        System.out.println(parse_config.matrix.getCount());

//        //3.使用python的方法计算矩阵
//        double[][] result = PythonInterpret.pyScript(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()), CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));
//        System.out.println(Arrays.deepToString(result));
//        //System.out.println(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()));
//        //parse_config.matrix.printDegreeMatrix();

//        //4.计算每一个特征的得分
//        calculate_scores(parse_node.features_list, result, parse_config.matrix);
//        System.out.println(parse_node.features_list);
//
//        //5.测试推荐能得到什么结果
//        List<String> parents = new ArrayList<>();
//        parents.add("interface");
//        List<String> brother = new ArrayList<>();
//        String node = "interfaceAttr";
//        System.out.println("******************************************************");
//        System.out.println("推荐的结果如下:");
//        System.out.println(recommand_keyword(parse_node.features_list, parents, brother, node, 2, false));
    }

    public static void get_input() throws IOException, CloneNotSupportedException {
        //先读取输入片段备用，这里读取了所有ip的特征
        String input_path = "./数据集思科（Netcomplete综合）/取输入的数据集";
        File input_dir = new File(input_path);
        File[] input_files = input_dir.listFiles();
        for(File input_file : input_files) {
            if(!input_file.isFile()) {
                continue;
            }
            parse_config.parse_cfg(input_path + "/", input_file.getName(), saved_path + input_dir.getName() + "/", out_suffix);
            parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());
            for(node_features features : parse_node.features_list) {
                input.addAll(features.getFeatures());
            }
            parse_config.clear();
            parse_node.clear();
        }
//        System.out.println(input);
    }
    public static void ConfigERA() throws IOException, CloneNotSupportedException {
        /*
         * ConfigERA的方法
         */
        String dir_path = "./数据集思科（Netcomplete综合）/";
        File dir = new File(dir_path);
        File[] sub_dirs = dir.listFiles();
        int count_dir = 0;
        for(File sub_dir : sub_dirs) {
            //System.out.println("当前访问的目录是" + sub_dir.getName());
            System.out.println(sub_dir.getPath());
            if(count_dir == 4) {
                break;
            }
            if(sub_dir.isDirectory()) {
                long start_time = System.currentTimeMillis();
                File[] files = sub_dir.listFiles();
                for (File file : files) {
                    //1.解析配置
                    int count = parse_config.parse_cfg(sub_dir.getPath() + "/", file.getName(), saved_path + sub_dir.getName() + "/", out_suffix);
                }
                //2.解析特征
                parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());
                System.out.println("当前数据集的配置对个数" + parse_config.getTriplets().size());

                //3.使用python方法计算矩阵
                double[][] result = PythonInterpret.pyScript(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()), CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));

                //4.计算特征得分
                calculate_scores.calculate_scores_era(parse_node.features_list, result, parse_config.matrix);

                //5.计算训练结束的时间
                long end_time = System.currentTimeMillis();
                System.out.println("程序的训练时间为" + (end_time - start_time) + "ms");

                //6.取输入的数据集
//                long rec_start_time = System.currentTimeMillis();
                long rec_time_count = 0;
                double[] list = new double[4];
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                for(int k = 0; k < 4; k++) {
                    list[k] = 0.00;
                }
                for(int j = 0; j < 3; j++) {
                    int correct_count = 0;
                    double rec_time = 0.00;
                    for(int i = 0; i < 2000; i++) {
                        Random random = new Random();
                        long rec_start_time = System.nanoTime();
                        node_feature feature = input.get(random.nextInt(input.size()));
                        if(feature.getChild_node().isEmpty()) {
                            i--;
                            continue;
                        }
                        //System.out.println("******************************************************");
                        //System.out.println("期望得到的结果是" + feature.getChild_node());
                        //System.out.println("推荐的结果如下:");
                        List<String> rec = recommand_keyword(parse_node.features_list, feature.getParents_node(), feature.getBrother_node(), feature.getNode_name(), topN);
                        //System.out.println(rec);
                        long rec_end_time = System.nanoTime();
                        rec_time += (rec_end_time - rec_start_time);
                        if(rec.contains(feature.getChild_node().get(0))) {
                            correct_count += 1;
                        }
                        if(i > 0 && (i % 500 == 0 || i == 2000 - 1)) {
                            if(i % 500 == 0) {
                                list[i / 500 - 1] += (correct_count * 100.00 / 2000);
                            } else {
                                list[3] += (correct_count * 100.00 / 2000);
                            }
                        }
                    }
                    rec_time_count += (long) (rec_time / 2000);
//                    System.out.println("程序推荐的平均时间为" + rec_time / 2000 + "ns");
//                    System.out.println(Arrays.toString(list));
//                    System.out.println("准确率为" + (correct_count * 100.00 / 2000) + "%");
                }

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");
                List<String> ret = new ArrayList<>();
                for(int k = 0; k < 4; k++) {
                    list[k] /= 3;
                    ret.add(decimalFormat.format(list[k]));
                }
                System.out.println("准确率变化为" + ret);
                System.out.println("程序推荐的平均时间为" + rec_time_count / 3 + "ns");

                //使用不同的数据集训练后要将公共的参数清空
                parse_config.clear();
                parse_node.clear();
            }
            count_dir++;
        }
    }

    public static void traditional_statistic() throws IOException {
        /*
         * 传统统计的方法
         */
        String dir_path = "./数据集思科（Netcomplete综合）/";
        File dir = new File(dir_path);
        File[] sub_dirs = dir.listFiles();
        int count_dir = 0;
        for(File sub_dir : sub_dirs) {
            //System.out.println("当前访问的目录是" + sub_dir.getName());
            System.out.println(sub_dir.getPath());
            if(count_dir == 4) {
                break;
            }
            if(sub_dir.isDirectory()) {
                long start_time = System.currentTimeMillis();
                File[] files = sub_dir.listFiles();
                for (File file : files) {
                    //1.解析配置
                    int count = parse_config.parse_cfg(sub_dir.getPath() + "/", file.getName(), saved_path + sub_dir.getName() + "/", out_suffix);
                }
                long end_time = System.currentTimeMillis();
                System.out.println("程序训练时间为" + (end_time - start_time) + "ms");
                long rec_time_count = 0;
                double[] list = new double[4];
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                for(int k = 0; k < 4; k++) {
                    list[k] = 0.00;
                }
                for(int j = 0; j < 3; j++) {
                    int correct_count = 0;
                    double rec_time = 0.00;
                    for(int i = 0; i < 2000; i++) {
                        Random random = new Random();
                        long rec_start_time = System.nanoTime();
                        node_feature feature = input.get(random.nextInt(input.size()));
                        if(feature.getChild_node().isEmpty()) {
                            i--;
                            continue;
                        }
                        //System.out.println("******************************************************");
                        //System.out.println("期望得到的结果是" + feature.getChild_node());
                        //System.out.println("推荐的结果如下:");
                        List<String> ret = get_most_n(parse_config.getTriplets(), topN, feature.getNode_name());
                        if(ret.contains(feature.getChild_node().get(0))) {
                            correct_count += 1;
                        }
                        if(i > 0 && (i % 500 == 0 || i == 2000 - 1)) {
                            if(i % 500 == 0) {
                                list[i / 500 - 1] += (correct_count * 100.00 / 2000);
                            } else {
                                list[3] += (correct_count * 100.00 / 2000);
                            }
                        }
                    }
                    rec_time_count += (long) (rec_time / 2000);
//                    System.out.println("程序推荐的平均时间为" + rec_time / 2000 + "ns");
//                    System.out.println(Arrays.toString(list));
//                    System.out.println("准确率为" + (correct_count * 100.00 / 2000) + "%");
                }

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");
                List<String> ret = new ArrayList<>();
                for(int k = 0; k < 4; k++) {
                    list[k] /= 3;
                    ret.add(decimalFormat.format(list[k]));
                }
                System.out.println("准确率变化为" + ret);
                System.out.println("程序推荐的平均时间为" + rec_time_count / 3 + "ns");

                //使用不同的数据集训练后要将公共的参数清空
                parse_config.clear();
                parse_node.clear();
            }
            count_dir++;
        }
    }

    private static List<String> get_most_n(List<Triplet> triplets, Integer n, String key_word) {
        Map<String, Integer> map = new HashMap<>();
        for(Triplet triplet : triplets) {
            if(triplet.getStart().equals(key_word)) {
                map.merge(triplet.getEnd(), 1, Integer::sum);
            }
        }
//        for(Map.Entry<String, Integer> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + "  " + entry.getValue());
//        }
        List<String> list = map.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed())
                .map(Map.Entry::getKey).collect(Collectors.toList());
        if(list.size() < n) {
            return list;
        }
//        System.out.println(list);
        return list.subList(0, n);
    }

    public static void ConfigERA_ARC() throws IOException, CloneNotSupportedException {
        /*
         * ConfigERA的方法
         */
        String dir_path = "./数据集思科（Netcomplete综合）/";
        File dir = new File(dir_path);
        File[] sub_dirs = dir.listFiles();
        int count_dir = 0;
        for(File sub_dir : sub_dirs) {
            //System.out.println("当前访问的目录是" + sub_dir.getName());
            System.out.println(sub_dir.getPath());
            if(count_dir == 4) {
                break;
            }
            if(sub_dir.isDirectory()) {
                long start_time = System.currentTimeMillis();
                File[] files = sub_dir.listFiles();
                for (File file : files) {
                    //1.解析配置
                    int count = parse_config.parse_cfg(sub_dir.getPath() + "/", file.getName(), saved_path + sub_dir.getName() + "/", out_suffix);
                }
                //2.解析特征
                parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());

                //3.使用python方法计算矩阵
                double[][] result = PythonInterpret.pyScript(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()), CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));

                //4.计算特征得分
                calculate_scores.calculate_scores_era(parse_node.features_list, result, parse_config.matrix);

                //5.计算训练结束的时间
                long end_time = System.currentTimeMillis();
                System.out.println("程序的训练时间为" + (end_time - start_time) + "ms");

                //6.取输入的数据集
//                long rec_start_time = System.currentTimeMillis();
                useARC cache = new useARC(ARC_capacity);
                long rec_time_count = 0;
                double[] list = new double[4];
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                for(int k = 0; k < 4; k++) {
                    list[k] = 0.00;
                }
                for(int j = 0; j < 3; j++) {
                    int correct_count = 0;
                    double rec_time = 0.00;
                    for(int i = 0; i < 2000; i++) {
                        Random random = new Random();
                        long rec_start_time = System.nanoTime();
                        node_feature feature = input.get(random.nextInt(input.size()));
                        if(feature.getChild_node().isEmpty()) {
                            i--;
                            continue;
                        }
                        //System.out.println("******************************************************");
                        //System.out.println("期望得到的结果是" + feature.getChild_node());
                        //System.out.println("推荐的结果如下:");
                        List<String> rec = recommand_keyword(parse_node.features_list, feature.getParents_node(), feature.getBrother_node(), feature.getNode_name(), topN);
                        //System.out.println(rec);
                        long rec_end_time = System.nanoTime();
                        rec_time += (rec_end_time - rec_start_time);
                        List<String> cache_list = cache.get_ARC_list();
                        if(cache_list.contains(feature.getChild_node().get(0)) || rec.contains(feature.getChild_node().get(0))) {
                            correct_count += 1;
                        }
                        if(!feature.getChild_node().isEmpty()) {
                            cache.saveToARC(feature.getChild_node().get(0));
                        }
                        if(i > 0 && (i % 500 == 0 || i == 2000 - 1)) {
                            if(i % 500 == 0) {
                                list[i / 500 - 1] += (correct_count * 100.00 / 2000);
                            } else {
                                list[3] += (correct_count * 100.00 / 2000);
                            }
                        }
                    }
                    rec_time_count += (long) (rec_time / 2000);
                }
                List<String> ret = new ArrayList<>();
                for(int k = 0; k < 4; k++) {
                    list[k] /= 3;
                    ret.add(decimalFormat.format(list[k]));
                }
                System.out.println("准确率变化为" + ret);
                System.out.println("程序推荐的平均时间为" + rec_time_count / 3 + "ns");

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");

                //使用不同的数据集训练后要将公共的参数清空
                parse_config.clear();
                parse_node.clear();
            }
            count_dir++;
        }
    }
    public static void GAT() throws IOException, CloneNotSupportedException {
        /*
         * 单层神经网络 + 注意力机制的方法
         */
        String dir_path = "./数据集思科（Netcomplete综合）/";
        File dir = new File(dir_path);
        File[] sub_dirs = dir.listFiles();
        int count_dir = 0;
        for(File sub_dir : sub_dirs) {
            //System.out.println("当前访问的目录是" + sub_dir.getName());
            System.out.println(sub_dir.getPath());
            if(count_dir == 4) {
                break;
            }
            if(sub_dir.isDirectory()) {
                long start_time = System.currentTimeMillis();
                File[] files = sub_dir.listFiles();
                for (File file : files) {
                    //1.解析配置
                    int count = parse_config.parse_cfg(sub_dir.getPath() + "/", file.getName(), saved_path + sub_dir.getName() + "/", out_suffix);
                }
                //2.解析特征
                parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());

                //3.使用python方法计算矩阵
                double[][] result = PythonInterpret.pyScript(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()), CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));

                //4.计算特征得分
                calculate_scores.calculate_scores_gat(parse_node.features_list, result, parse_config.matrix);

                //5.计算训练结束的时间
                long end_time = System.currentTimeMillis();
                System.out.println("程序的训练时间为" + (end_time - start_time) + "ms");

                //6.取输入的数据集
//                long rec_start_time = System.currentTimeMillis();
                long rec_time_count = 0;
                double[] list = new double[4];
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                for(int k = 0; k < 4; k++) {
                    list[k] = 0.00;
                }
                for(int j = 0; j < 3; j++) {
                    int correct_count = 0;
                    double rec_time = 0.00;
                    for(int i = 0; i < 2000; i++) {
                        Random random = new Random();
                        long rec_start_time = System.nanoTime();
                        node_feature feature = input.get(random.nextInt(input.size()));
                        if(feature.getChild_node().isEmpty()) {
                            i--;
                            continue;
                        }
                        //System.out.println("******************************************************");
                        //System.out.println("期望得到的结果是" + feature.getChild_node());
                        //System.out.println("推荐的结果如下:");
                        List<String> rec = recommand_keyword(parse_node.features_list, feature.getParents_node(), feature.getBrother_node(), feature.getNode_name(), topN);
                        //System.out.println(rec);
                        long rec_end_time = System.nanoTime();
                        rec_time += (rec_end_time - rec_start_time);
                        if(rec.contains(feature.getChild_node().get(0))) {
                            correct_count += 1;
                        }
                        if(i > 0 && (i % 500 == 0 || i == 2000 - 1)) {
                            if(i % 500 == 0) {
                                list[i / 500 - 1] += (correct_count * 100.00 / 2000);
                            } else {
                                list[3] += (correct_count * 100.00 / 2000);
                            }
                        }
                    }
                    rec_time_count += (long) (rec_time / 2000);
//                    System.out.println("程序推荐的平均时间为" + rec_time / 2000 + "ns");
//                    System.out.println(Arrays.toString(list));
//                    System.out.println("准确率为" + (correct_count * 100.00 / 2000) + "%");
                }

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");
                List<String> ret = new ArrayList<>();
                for(int k = 0; k < 4; k++) {
                    list[k] /= 3;
                    ret.add(decimalFormat.format(list[k]));
                }
                System.out.println("准确率变化为" + ret);
                System.out.println("程序推荐的平均时间为" + rec_time_count / 3 + "ns");

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");

                //使用不同的数据集训练后要将公共的参数清空
                parse_config.clear();
                parse_node.clear();
            }
            count_dir++;
        }
    }
    public static void split_input(List<node_feature> input) {
        // 使用Map来统计每个node_feature的出现次数
        Map<node_feature, Integer> frequencyMap = new HashMap<>();
        for (node_feature nf : input) {
            frequencyMap.put(nf, frequencyMap.getOrDefault(nf, 0) + 1);
        }

        // 将Map的entry转换为List，以便排序
        List<Map.Entry<node_feature, Integer>> entries = new ArrayList<>(frequencyMap.entrySet());
        entries.sort(Map.Entry.comparingByValue()); // 按值（频率）排序

        input.sort(Comparator.comparingInt(node_feature::getCount));

        // 计算中位数
        int medianIndex = input.size() / 2;

        // 初始化两个列表来保存结果
        List<node_feature> lowFrequency = new ArrayList<>();
        List<node_feature> highFrequency = new ArrayList<>();

        // 遍历input，根据频率划分到两个列表中
        int count = 0;
        for (node_feature feature : input) {
            if (count < medianIndex) {
                lowFrequency.add(feature);
            } else {
                highFrequency.add(feature);
            }
            count++;
        }
        low_freq_input = lowFrequency;
        high_freq_input = highFrequency;
    }

    public static void spss() throws IOException {
        File dirs = new File("./数据集思科（Netcomplete综合）/");
        File[] sub_dirs = dirs.listFiles();
        for(int p = 1; p <= 5; p++) {
            System.out.println("当前的topN为" + p);
            int count = 0;
            for(File dir : sub_dirs) {
                if(!dir.isDirectory()) {
                    continue;
                }
                if(count == 4) {
                    break;
                }
                count++;
                System.out.println("当前目录为" + dir.getName());

                long start_time = System.currentTimeMillis();
                Map<Triplet, Double> map = calculate_scores.calculates_scores_spss(dir.getPath() + "/");
                long end_time = System.currentTimeMillis();
                System.out.println("程序训练时间为" + (end_time - start_time) + "ms");
//        List<String> ret = get_ret_spss(map, "ip", 3);
//        System.out.println(ret);
                long rec_time_count = 0;
                double[] list = new double[4];
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                for(int k = 0; k < 4; k++) {
                    list[k] = 0.00;
                }
                for(int j = 0; j < 3; j++) {
                    int correct_count = 0;
                    double rec_time = 0.00;
                    for(int i = 0; i < 2000; i++) {
                        Random random = new Random();
                        long rec_start_time = System.nanoTime();
                        node_feature feature = input.get(random.nextInt(input.size()));
                        if(feature.getChild_node().isEmpty()) {
                            i--;
                            continue;
                        }
                        //System.out.println("******************************************************");
                        //System.out.println("期望得到的结果是" + feature.getChild_node());
                        //System.out.println("推荐的结果如下:");
                        List<String> rec = get_ret_spss(map, feature.getNode_name(), topN);
                        //System.out.println(rec);
                        long rec_end_time = System.nanoTime();
                        rec_time += (rec_end_time - rec_start_time);
                        if(rec.contains(feature.getChild_node().get(0))) {
                            correct_count += 1;
                        }
                        if(i > 0 && (i % 500 == 0 || i == 2000 - 1)) {
                            if(i % 500 == 0) {
                                list[i / 500 - 1] += (correct_count * 100.00 / 2000);
                            } else {
                                list[3] += (correct_count * 100.00 / 2000);
                            }
                        }
                    }
                    rec_time_count += (long) (rec_time / 2000);
//                    System.out.println("程序推荐的平均时间为" + rec_time / 2000 + "ns");
//                    System.out.println(Arrays.toString(list));
//                    System.out.println("准确率为" + (correct_count * 100.00 / 2000) + "%");
                }

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");
                List<String> ret = new ArrayList<>();
                for(int k = 0; k < 4; k++) {
                    list[k] /= 3;
                    ret.add(decimalFormat.format(list[k]));
                }
                System.out.println("准确率变化为" + ret);
                System.out.println("程序推荐的平均时间为" + rec_time_count / 3 + "ns");
            }
        }


    }
}
