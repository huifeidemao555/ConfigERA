import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
    static String python_path = "C:\\Users\\hzx\\anaconda3\\envs\\tf\\python.exe";
    public static void main(String[] args) {
        String pythonArrayString = "[[1, 2,\n 3],     [4, 5, 6], \n[7, 8, 9]]";
        int[][] javaMatrix = convertPythonArrayToJavaMatrix(pythonArrayString);

        // 打印Java格式的矩阵
        for (int i = 0; i < javaMatrix.length; i++) {
            for (int j = 0; j < javaMatrix[i].length; j++) {
                System.out.print(javaMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

}
