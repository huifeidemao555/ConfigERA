package org.example.utils;

public class CastToPyArray {
    public static StringBuilder cast(Integer[][] matrix) {
        // 将矩阵转换为字符串形式
        StringBuilder matrixString = new StringBuilder("[");
        for (int i = 0; i < matrix.length; i++) {
            matrixString.append("[");
            for (int j = 0; j < matrix[i].length; j++) {
                matrixString.append(matrix[i][j]);
                if (j < matrix[i].length - 1) {
                    matrixString.append(", ");
                }
            }
            matrixString.append("]");
            if (i < matrix.length - 1) {
                matrixString.append(", ");
            }
        }
        matrixString.append("]");
        return matrixString;
    }
}
