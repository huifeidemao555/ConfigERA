package org.example.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Weight_Matrix {
    Map<String, Integer> key_word_index;
    Integer count;
    Integer[][] matrix;
    public Weight_Matrix() {
        this.key_word_index = new HashMap<>();
        this.count = 0;
        this.matrix = new Integer[10000][]; // 只初始化外部数组

        // 初始化每一个内部的数组
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = new Integer[10000];
            Arrays.fill(matrix[i], 0);
        }
    }

    public void insert_key_word_index(String key_word) {
        if(!key_word_index.containsKey(key_word)) {
            key_word_index.put(key_word, count++);
        }
    }

    public Integer get_key_word_index(String key_word) {
        if(key_word_index.containsKey(key_word)) {
            return key_word_index.get(key_word);
        }
        return -1;
    }

    public void setMatrix(Integer row, Integer column){
        this.matrix[row][column]++;
    }

    public Integer getMatrix(Integer row, Integer column) {
        return this.matrix[row][column];
    }

    public Integer[][] getAdjMatrix() {
        Integer[][] newMatrix = new Integer[this.count][this.count];
        for(int i = 0; i < this.count; i++) {
            System.arraycopy(this.matrix[i], 0, newMatrix[i], 0, this.count);
        }
        return newMatrix;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void printAdjMatrix() {
        Integer[][] matrix1 = this.getAdjMatrix();
        for(int i = 0; i < matrix1.length; i++) {
            for(int j = 0; j < matrix1.length; j++) {
                System.out.print(matrix1[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printDegreeMatrix() {
        Integer[][] matrix1 = this.getDegreeMatrix();
        for (Integer[] integers : matrix1) {
            for (int j = 0; j < matrix1.length; j++) {
                System.out.print(integers[j] + " ");
            }
            System.out.println();
        }
    }

    public Integer[][] getDegreeMatrix() {
        Integer[][] degreeMatrix = new Integer[this.count][];
        for (int i = 0; i < degreeMatrix.length; i++) {
            degreeMatrix[i] = new Integer[this.count];
            Arrays.fill(degreeMatrix[i], 0);
        }
        for(int i = 0; i < this.count; i++) {
            for(int j = 0; j < this.count; j++) {
                if(this.matrix[i][j] > 0) {
                    degreeMatrix[i][i] += 1;
                    degreeMatrix[j][j] += 1;
                }
            }
        }
        return degreeMatrix;
    }

    public void clear() {
        this.count = 0;
        this.key_word_index.clear();
        for(int i = 0; i < this.matrix.length; i++) {
            Arrays.fill(this.matrix[i], 0);
        }
    }
    public int get_config_count() {
        /*
         * 获取全部配置对的数量
         */
        int config_count = 0;
        for(int i = 0; i < this.count; i++) {
            for(int j = 0; j < this.count; j++) {
                config_count += this.matrix[i][j];
            }
        }
        return config_count;
    }

    public int get_config_count(String key_word) {
        /*
         * 获取指定配置对的数量
         */
        int config_count = 0;
        int index = this.get_key_word_index(key_word);
        for(int i = 0; i < this.count; i++) {
            config_count += this.matrix[index][i];
        }
        for(int j = 0; j < this.count; j++) {
            config_count += this.matrix[j][index];
        }
        return config_count;
    }

    public int get_config_count(String key1, String key2) {
        int index_i = this.get_key_word_index(key1);
        int index_j = this.get_key_word_index(key2);
        return this.matrix[index_i][index_j];
    }
}
