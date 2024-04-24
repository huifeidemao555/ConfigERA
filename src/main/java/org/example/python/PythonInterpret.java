package org.example.python;

import org.example.utils.CastToJavaArray;

import java.io.*;

public class PythonInterpret {
    public static double[][] pyScript(StringBuilder DegreeMatrix, StringBuilder AdjMatrix) {
        /**
         *  传入邻接矩阵和度矩阵，调用python的方法计算结果
         */
        Process proc;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/java/org/example/python/test.py"));
            writer.write("import numpy as np\n");
            writer.write("from scipy.linalg import fractional_matrix_power\n");
            writer.write("np.set_printoptions(threshold=np.inf)\n");
            writer.write("a = np.array(" + DegreeMatrix + ")\n");
            writer.write("a = fractional_matrix_power(a, -0.5)\n");
            writer.write("b = np.array(" + AdjMatrix + ")\n");
            writer.write("c = a @ b @ a\n");
            writer.write("c = np.around(c, decimals = 2)\n");
            //writer.write("print(a)\n");
            //writer.write("b = " + AdjMatrix + "\n");
            writer.write("print(c)\n");
            //writer.write("print(type(a))\n");
            writer.close();


            proc = Runtime.getRuntime().exec("python src/main/java/org/example/python/test.py");
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            //String line = null;
            //while ((line = in.readLine()) != null) {
            //    System.out.println(line);
            //}
            //使用这种方法读取输出
            StringBuilder builder = new StringBuilder();
            int ch;
            while ((ch = in.read()) != -1) { // 使用read()方法按字符读取
                char currentChar = (char) ch;
                // 如果到达行尾（例如，在Unix系统中是'\n'，在Windows中是'\r\n'）
                if (currentChar != '\n' && currentChar != '\r') {
                    builder.append(currentChar);
                }
            }
            //System.out.println(builder);
            double[][] result = CastToJavaArray.convertStringTo2DArray(String.valueOf(builder));

            in.close();
            proc.waitFor();
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
