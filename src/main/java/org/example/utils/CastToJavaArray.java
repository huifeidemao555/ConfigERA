package org.example.utils;

public class CastToJavaArray {
    public static double[][] convertStringTo2DArray(String input) {
        // 去除字符串两端的方括号
        input = input.substring(1, input.length() - 1);

        // 按行分割字符串
        String[] rows = input.split("\\s+(?=\\[)");

        // 初始化二维数组
        double[][] array = new double[rows.length][];

        for (int i = 0; i < rows.length; i++) {
            // 去除每行两端的方括号
            rows[i] = rows[i].substring(1, rows[i].length() - 1);

            // 按空格分割每行的元素
            String[] elements = rows[i].trim().split("\\s+");

            // 将字符串数组转换为浮点数数组
            double[] elementArray = new double[elements.length];
            for (int j = 0; j < elements.length; j++) {
                elementArray[j] = Double.parseDouble(elements[j]);
            }

            // 将浮点数数组赋值给二维数组
            array[i] = elementArray;
        }

        return array;
    }
}
