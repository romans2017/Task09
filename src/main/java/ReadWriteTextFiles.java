

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReadWriteTextFiles {

    public static final String PATTERN_DELIMITER_CSV = "[\\r\\n\\,\\;\\t]";
    public static final String PATTERN_DELIMITER_TXT = "[\\r\\n]";

    public static Set<String> readFile(String fileName, String delimiter) {
        Set<String> set = new HashSet<>();
        try (FileReader fileReader = new FileReader(fileName)) {
            Scanner scanner = new Scanner(fileReader).useDelimiter(delimiter);
            while (scanner.hasNext()) {
                String next = scanner.next();
                if (!"".equals(next)) {
                    set.add(next);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    public static void writeFile(String fileName, Collection<String> collection) {
        String outPath = ".\\src\\main\\resources\\out\\";
        if (!Files.exists(Paths.get(outPath))) {
            try {
                Files.createDirectory(Paths.get(outPath));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try (PrintStream printStream = new PrintStream(outPath + fileName)) {
            printStream.print(collection.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void compareFiles() {
        Set<String> set1 = readFile(".\\src\\main\\resources\\firstNumberFile.txt", PATTERN_DELIMITER_CSV);
        Set<String> set2 = readFile(".\\src\\main\\resources\\secondNumberFile.txt", PATTERN_DELIMITER_CSV);

        //a
        List<String> listTotal = new ArrayList<>(set1);
        listTotal.addAll(set2);
        System.out.println(listTotal);
        writeFile("a_fromTwoFiles.txt", listTotal);

        //b
        Set<String> set = new HashSet<>(set1);
        for (String number : set2) {
            if (set.contains(number)) {
                set.remove(number);
            } else {
                set.add(number);
            }
        }
        System.out.println(set);
        writeFile("b_onlyFromOneOfTwoFiles.txt", set);

        //c
        System.out.println(set1);
        writeFile("c_onlyFromFirstFile.txt", set1);

        //d
        set.clear();
        set.addAll(set1);
        set.addAll(set2);
        System.out.println(set);
        writeFile("a_fromTwoFiles.txt", set);

    }

    public static void statisticByIp() {
        Set<String> set = readFile(".\\src\\main\\resources\\IP.txt", PATTERN_DELIMITER_TXT);
        String stringPatternTime = "\\d{2}\\:\\d{2}\\:\\d{2}";
        String ip = "";
        String time = "";
        String day = "";
        for (String line : set) {
            Pattern pattern = Pattern.compile(".*(?=" + stringPatternTime + ")");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                ip = matcher.group().trim();
            }
            pattern = Pattern.compile(stringPatternTime);
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                time = matcher.group().trim();
            }

            pattern = Pattern.compile("(?<=" + stringPatternTime + ").*");
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                day = matcher.group().trim();
            }

        }
    }

    public static void main(String[] args) {
        //compareFiles();
        statisticByIp();
    }

}
