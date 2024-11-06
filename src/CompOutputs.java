import java.io.*;
import java.util.*;

public class CompOutputs {

    public static void main(String[] args) throws IOException {
        File file1 = new File("output.txt");
        File file2 = new File("correctOutput.txt");

        List<String> lines1 = readLines(file1);
        List<String> lines2 = readLines(file2);

        compareFiles(lines1, lines2);
    }

    public static List<String> readLines(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static void compareFiles(List<String> lines1, List<String> lines2) {
        boolean areIdentical = true;
        int maxSize = Math.max(lines1.size(), lines2.size());

        for (int i = 0; i < maxSize; i++) {
            String line1 = i < lines1.size() ? lines1.get(i) : "";
            String line2 = i < lines2.size() ? lines2.get(i) : "";

            if (!line1.equals(line2)) {
                areIdentical = false;
                System.out.println("Difference at line " + (i + 1) + ":");
                System.out.println("File1: " + line1);
                System.out.println("File2: " + line2);
            }
        }

        if (areIdentical) {
            System.out.println("The files are identical.");
        }
    }
}
