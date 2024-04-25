package org.example;


import org.example.python.PythonInterpret;
import org.example.service.parse_config;
import org.example.service.parse_node;
import org.example.utils.CastToPyArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.example.service.calculate_scores.calculate_scores;
import static org.example.service.recommand.recommand_keyword;

public class Main {
    private static String path = "./数据集思科（Netcomplete综合）/取输入的数据集/";
    private static String saved_path = "saved_out/";
    private static String file_name = "Ulmi.cfg";
    private static final String out_suffix = "_out.out";

    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        //1.解析配置
        int count = parse_config.parse_cfg(path, file_name, saved_path, out_suffix);
        System.out.println("三元组的数量为" + count);
        System.out.println(parse_config.get_key_words_set());
        System.out.println(parse_config.getTriplets());
        System.out.println("******************************************************");


        //2.从节点中解析出特征
        parse_node.parse(parse_config.getTriplets(), parse_config.get_key_words_set());
        //parse_config.matrix.printAdjMatrix();
        //System.out.println(CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));
        System.out.println(parse_config.matrix.getCount());


        //3.使用python的方法计算矩阵
        double[][] result = PythonInterpret.pyScript(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()), CastToPyArray.cast(parse_config.matrix.getAdjMatrix()));
        System.out.println(Arrays.deepToString(result));
        //System.out.println(CastToPyArray.cast(parse_config.matrix.getDegreeMatrix()));
        //parse_config.matrix.printDegreeMatrix();


        //4.计算每一个特征的得分
        calculate_scores(parse_node.features_list, result, parse_config.matrix);
        System.out.println(parse_node.features_list);

        //5.测试推荐能得到什么结果
        List<String> parents = new ArrayList<>();
        parents.add("interface");
        List<String> brother = new ArrayList<>();
        String node = "interfaceAttr";
        System.out.println("******************************************************");
        System.out.println("推荐的结果如下:");
        System.out.println(recommand_keyword(parse_node.features_list, parents, brother, node, 2, false));

    }
}
