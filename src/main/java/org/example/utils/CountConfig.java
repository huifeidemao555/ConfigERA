package org.example.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CountConfig {
//    public static void main(String[] args) throws IOException {
//        String path = "C:\\Users\\hzx\\IdeaProjects\\ConfigERA\\数据集思科（Netcomplete综合）";
//        File dirs = new File(path);
//        int count = 0;
//        for(File dir : dirs.listFiles()) {
//            if(!dir.isDirectory()) {
//                continue;
//            }
//            if(count == 4) {
//                break;
//            }
//            count++;
//            int config_count = 0;
//            for(File file : dir.listFiles()) {
//                BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
//                String line = "";
//                while((line = reader.readLine()) != null) {
//                    if(line.startsWith("!")) {
//                        continue;
//                    }
//                    if(line.startsWith("control-plane")) {
//                        break;
//                    }
//                    String[] strs = line.trim().split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
//                    for(String str : strs) {
//                        if(str != null) {
//                            config_count += 1;
//                        }
//                    }
//                }
//            }
//            System.out.println(dir.getName() + "的配置命令个数为" + config_count);
//        }
//    }
    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\hzx\\IdeaProjects\\ConfigERA\\数据集junniper";
        File dirs = new File(path);
        List<String> regex = new ArrayList<>();
        regex.add("[");
        regex.add("]");
        regex.add("{");
        regex.add("}");
        regex.add(";");
        int count = 0;
        for(File dir : dirs.listFiles()) {
            if(!dir.isDirectory()) {
                continue;
            }
            if(count == 3) {
                break;
            }
            count++;
            int config_count = 0;
            for(File file : dir.listFiles()) {
                BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
                String line = "";
                while((line = reader.readLine()) != null) {
                    if(line.startsWith("#")) {
                        continue;
                    }
                    if(line.startsWith("control-plane")) {
                        break;
                    }
                    String[] strs = line.trim().split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    for(String str : strs) {
                        if(str != null) {
                            if("/*".equals(str)) {
                                break;
                            }
                            if(regex.contains(str)) {
                                continue;
                            }
                            config_count += 1;
                        }
                    }
                }
            }
            System.out.println(dir.getName() + "的配置命令个数为" + config_count);
        }
    }
}
