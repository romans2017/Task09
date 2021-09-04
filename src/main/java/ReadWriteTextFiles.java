

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    private static class IpData {
        private Integer numberVisits = 1;
        private final Map<String, Integer> mapNumberVisitsPerHour = new HashMap<>();
        private final Map<String, Integer> mapNumberVisitsPerDay = new HashMap<>();
        private String popularHour;
        private String popularDay;
    }

    public static void statisticByIp() {
        Set<String> setLines = readFile(".\\src\\main\\resources\\IP.txt", PATTERN_DELIMITER_TXT);
        Map<String, IpData> mapIpNumberVisits = new HashMap<>();
        Map<String, Integer> numberVisitsPerHour = new HashMap<>();
        /*Map<String, Integer> mapIpNumberVisits = new HashMap<>();
        Map<Ip, Integer> mapIpNumberDays = new HashMap<>();
        Map<Ip, Integer> mapIpNumberHours = new HashMap<>();*/
        /*String stringPatternTime = "\\d{2}\\:\\d{2}\\:\\d{2}";
        String ip = "";
        String time = "";
        String day = "";*/
        for (String line : setLines) {
            /*Pattern pattern = Pattern.compile(".*(?=" + stringPatternTime + ")");
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
            }*/
            String[] splitLine = line.split(" ");
            String ip = splitLine[0].trim();
            String time = splitLine[1].trim();
            String day = splitLine[2].trim();
            IpData curValue = mapIpNumberVisits.merge(ip, new IpData(), (oldValue, newValue) -> {
                oldValue.numberVisits += newValue.numberVisits;
                return oldValue;
            });
            curValue.mapNumberVisitsPerHour.merge(day, 1, Integer::sum);
            curValue.mapNumberVisitsPerDay.merge(time.substring(0, 2), 1, Integer::sum);

            numberVisitsPerHour.merge(time.substring(0, 2), 1, Integer::sum);

            /*mapIpNumberVisits.merge(ip, 1, Integer::sum);
            mapIpNumberDays.merge(new Ip(ip, "", day), 1, Integer::sum);
            mapIpNumberHours.merge(new Ip(ip, time.substring(0, 2), ""), 1, Integer::sum);*/
        }
        TreeSet<Map.Entry<String, Integer>> set = new TreeSet<>(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        set.addAll(numberVisitsPerHour.entrySet());
        String popularHourInTotal = set.first().getKey();

        List<String> resultList = new ArrayList<>();
        mapIpNumberVisits.forEach((key, value) -> {
            TreeSet<Map.Entry<String, Integer>> setValue = new TreeSet<>(Map.Entry.comparingByValue(Comparator.reverseOrder()));
            setValue.addAll(value.mapNumberVisitsPerHour.entrySet());
            value.popularDay = setValue.first().getKey();
            setValue.clear();
            setValue.addAll(value.mapNumberVisitsPerDay.entrySet());
            value.popularHour = setValue.first().getKey();
            resultList.add(key + " " + value.numberVisits + " " + value.popularDay + " " + value.popularHour);
        });
        resultList.add(popularHourInTotal);
        System.out.println(resultList);
        writeFile("statisticByIp.txt", resultList);
    }

    public static void main(String[] args) {
        compareFiles();
        statisticByIp();
    }

}
