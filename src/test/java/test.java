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

    public static int[][] convertPythonArrayToJavaMatrix(String pythonArrayString) {
        List<List<Integer>> matrixRows = new ArrayList<>();
        Pattern rowPattern = Pattern.compile("\\[(.*?)\\]");
        Matcher rowMatcher = rowPattern.matcher(pythonArrayString);

        while (rowMatcher.find()) {
            String rowString = rowMatcher.group(1);
            List<Integer> row = new ArrayList<>();
            Pattern elementPattern = Pattern.compile("\\b(\\d+)\\b");
            Matcher elementMatcher = elementPattern.matcher(rowString);

            while (elementMatcher.find()) {
                int element = Integer.parseInt(elementMatcher.group(1));
                row.add(element);
            }

            matrixRows.add(row);
        }

        // 转换为二维数组
        int[][] matrix = new int[matrixRows.size()][];
        for (int i = 0; i < matrixRows.size(); i++) {
            List<Integer> row = matrixRows.get(i);
            matrix[i] = new int[row.size()];
            for (int j = 0; j < row.size(); j++) {
                matrix[i][j] = row.get(j);
            }
        }
        return matrix;
    }
}
