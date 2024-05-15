package org.example.service;


import org.example.entity.Triplet;
import org.example.entity.Weight_Matrix;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class parse_config {

    private static final String path = "./数据集思科（Netcomplete综合）/取输入的数据集/";
    private static final String saved_path = "";
    private static final String file_name = "Ulmi.cfg";
    private static final String out_suffix = "_out.out";
    private static Integer config_count = 0;
    private static final Set<String> key_words_set = new HashSet<>();

    private static final List<Triplet> triplets = new ArrayList<>();

    public static final Weight_Matrix matrix = new Weight_Matrix();

    public static void main(String[] args) throws IOException {
//        String cfg_path = path + file_name;
//        parse_cfg(cfg_path);
//        File file = new File(cfgPath);
//        File out_file = new File(saved_path);
//        if(!out_file.exists()) {
//            file.createNewFile();
//            System.out.println("成功创建新文件！");
//        }
//        if(!file.isFile()) {
//            System.out.println("请输入有效的文件路径！");
//        } else {
//            System.out.println("成功读取" + file.getName());
//        }
    }

    public static int parse_cfg(String path, String file_name, String saved_path, String out_suffix) throws IOException {
        /**
         * 读取文件的每一行并且解析
         */
        /*****************************1.设置好保存目录以及保存名称*****************************/
        String filepath = path + file_name;                                                     //读取路径 + 文件名
        String parse_out_path = saved_path + file_name.split("\\.")[0] + out_suffix;      //保存目录 + 文件名 + 后缀

        File out_dir = new File(saved_path);
        if(!out_dir.isDirectory()) out_dir.mkdirs();

        BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
        FileWriter writer = new FileWriter(new File(parse_out_path));

//        System.out.println(parse_out_path);
        /*****************************2.对文件的每一行进行解析*****************************/
        try {
//            config_count = 0;
//            key_words_set.clear();
//            triplets.clear();
//            matrix.clear();
            String line = "";
            String curAttr = "";
            String lineAttr = "";
            Set<String> attrs = new HashSet<>();
            int i = 0;
            while((line = reader.readLine()) != null) {
                // 跳过配置无关的信息
                if(line.startsWith("control-plane")){
                    break;
                }
                if(line.startsWith("!") || line.startsWith("service") || line.startsWith("boot")) {
                    continue;
                }
                // 对每行读取到的数据进行处理
                boolean tab = line.startsWith(" ");             // 本行是否有缩进
                String[] strs = line.trim().split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); //使用正则表达式匹配分割，并且保证不会将双引号中间的空格分割开
                lineAttr = "";
                if(tab) {
                    if(!attrs.contains(strs[0])) {
                        write_result(writer, curAttr, "internalCMD", strs[0]);
                        attrs.add(strs[0]);
                    } else {
                        i = 2;
                        write_result(writer, strs[0] + "Attr", "internalCMD", strs[i]);
                    }
                } else {
                    write_same_level(attrs, writer);
                    curAttr = "";
                    attrs.clear();
                }
                for(; i < strs.length - 1; i++) {
                    if(i > 0 && isIPAddr(strs[i]) && (isIPAddr(strs[i - 1]) || isIPAddr(strs[i + 1]))) {
                        continue;
                    } else if(isFloat(strs[i]) || isInteger(strs[i]) || isIPAddr(strs[i]) || isValue(strs[i])) {
                        write_result(writer, lineAttr, "internalCMD", strs[i + 1]);
                    } else if(strs[i].equals("interface") || strs[i].equals("hostname") || isFloat(strs[i + 1]) || isInteger(strs[i + 1]) || isIPAddr(strs[i + 1]) || isValue(strs[i + 1])) {
                        write_result(writer, strs[i], "attrCMD", strs[i] + "Attr");
                        if(!tab) {
                            curAttr = strs[i] + "Attr";
                        } else {
                            lineAttr = strs[i] + "Attr";
                        }
                    } else {
                        write_result(writer, strs[i], "internalCMD", strs[i + 1]);
                    }
                }
                i = 0;
            }
            write_same_level(attrs, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        reader.close();
        writer.close();

        return config_count;
    }

    public static Set<String> get_key_words_set() {
        return key_words_set;
    }
    public static List<Triplet> getTriplets() {
        return triplets;
    }
    private static void write_same_level(Set<String> attrs, FileWriter writer) throws IOException {
        /**
         * 从记录的同级属性数组中建立同级属性关系
         */
        List<String> list = new ArrayList<>(attrs);
        for(int i = 0; i < list.size() - 1; i++) {
            for(int j = i + 1; j < list.size(); j++) {
                write_result(writer, list.get(i), "sameLevel", list.get(j));
                write_result(writer, list.get(j), "sameLevel", list.get(i));
            }
        }
    }

    private static void write_result(FileWriter writer, String start, String relation, String end) throws IOException {
        /**
         * 将三元组写入文件中
         */
        String triplet = "(" + start + "," + relation + "," + end + ")\n";
        //将三元组结果保存进list里面
        triplets.add(new Triplet(start, relation, end));
        config_count += 1;
        //将每个关键字都保存到set里面
        if(!key_words_set.contains(start)) {
            key_words_set.add(start);
        }
        if(!key_words_set.contains(end)) {
            key_words_set.add(end);
        }

//        try {
//            writer.write(triplet);
//        } catch (IOException e) {
//            System.out.println("写入结果出现错误！");
//            throw new RuntimeException(e);
//        }
        insert_to_matrix(start, end);
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

    private static boolean isIPAddr(String str) {
        //判断是否是ip地址的正则表达式
        String reg = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$";
//        String reg = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}(?:/(\\d{1,2}|3[0-2]))?$";
        return Pattern.matches(reg, str);
    }
    private static boolean isInteger(String str) {
        // 整数的正则表达式
        String regex = "\\d+";
        return str.matches(regex);
    }

    private static boolean isFloat(String str) {
        // 浮点数的正则表达式
        String regex = "\\d+\\.\\d+";
        return str.matches(regex);
    }

    private static boolean isValue(String str) {
        return str.startsWith("\"") && str.endsWith("\"");
    }

    public static void clear() {
        config_count = 0;
        key_words_set.clear();
        matrix.clear();
        triplets.clear();
    }
}

