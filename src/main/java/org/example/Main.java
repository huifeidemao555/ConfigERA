package org.example;


import org.example.entity.node_feature;
import org.example.entity.node_features;
import org.example.python.PythonInterpret;
import org.example.service.parse_config;
import org.example.service.parse_node;
import org.example.utils.CastToPyArray;
import sun.util.calendar.BaseCalendar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.example.service.calculate_scores.calculate_scores;
import static org.example.service.recommand.recommand_keyword;

public class Main {
//    private static String path = "./数据集思科（Netcomplete综合）/取输入的数据集/";
    private static String path = "./数据集思科（Netcomplete综合）/150设备推荐/";
    private static String saved_path = "saved_out/";
//    private static String file_name = "Ulmi.cfg";
    private static String file_name = "PeerSlough_1.cfg";
    private static final String out_suffix = "_out.out";
    private static List<node_feature> input = new ArrayList<>();

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        muti_parse();
//        //1.解析配置
//        int count = parse_config.parse_cfg(path, file_name, saved_path, out_suffix);
//        System.out.println("三元组的数量为" + count);
//        System.out.println(parse_config.get_key_words_set());
//        System.out.println(parse_config.getTriplets());
//        System.out.println("******************************************************");
//
//
//        //2.从节点中解析出特征
//        parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());
//        parse_config.matrix.printAdjMatrix();
//        parse_config.matrix.printDegreeMatrix();
//        //System.out.println(CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));
//        System.out.println(parse_config.matrix.getCount());
//
//
//        //3.使用python的方法计算矩阵
//        double[][] result = PythonInterpret.pyScript(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()), CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));
//        System.out.println(Arrays.deepToString(result));
//        //System.out.println(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()));
//        //parse_config.matrix.printDegreeMatrix();
//
//
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
    public static void muti_parse() throws IOException, CloneNotSupportedException {
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
                if(features.getKey_word().equals("ip")) {
                    for(node_feature feature : features.getFeatures()) {
                        input.add(feature);
                    }
                    break;
                }
            }
            parse_config.clear();
            parse_node.clear();
        }
        System.out.println(input);

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
                calculate_scores(parse_node.features_list, result, parse_config.matrix);

                //5.计算训练结束的时间
                long end_time = System.currentTimeMillis();
                System.out.println("程序的训练时间为" + (end_time - start_time) + "毫秒");

                //6.取输入的数据集
//                long rec_start_time = System.currentTimeMillis();
                int correct_count = 0;
                for(node_feature feature : input) {
                    //System.out.println("******************************************************");
                    //System.out.println("期望得到的结果是" + feature.getChild_node());
                    //System.out.println("推荐的结果如下:");
                    List<String> rec = recommand_keyword(parse_node.features_list, feature.getParents_node(), feature.getBrother_node(), "ip", 3);
                    //System.out.println(rec);
                    if(rec.contains(feature.getChild_node().get(0))) {
                        correct_count += 1;
                    }
                }
                System.out.println("准确率为" + (correct_count * 100.00 / input.size()) + "%");

//                long rec_end_time = System.currentTimeMillis();
//                System.out.println("程序推荐的时间为" + (rec_end_time - rec_start_time) + "毫秒");

                //使用不同的数据集训练后要将公共的参数清空
                parse_config.clear();
                parse_node.clear();
            }
            count_dir++;
        }
    }
}
