import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        int numberOfHashesToCheck = 2;

        List<TreeMap<Long, Integer>> stats = IntStream.range(0, numberOfHashesToCheck)
                .mapToObj(i -> new TreeMap<Long, Integer>())
                .collect(Collectors.toList());

        int interval = 1_000_000;

        final AtomicInteger processed = new AtomicInteger(0);

        long minValue = -2_200_000_000L;
        long maxValue = 2_200_000_000L;

        stats.forEach(map -> {
            map.put(0L, 0);
            for (long i = minValue; i <= maxValue; i += interval) {
                map.put(getIntervalStartIndex(i, interval), 0);
            }
        });


        try (Stream<String> stream = Files.lines(Paths.get("W:/hashcode-iv.txt"))) {
            stream.forEach(line -> {
                String[] split = line.split(",");

                for (int i = 1; i < split.length; i++) {
                    int hashCode = Integer.parseInt(split[i]);
                    long intervalStartIndex = getIntervalStartIndex(hashCode, interval);
                    stats.get(i - 1).compute(intervalStartIndex, (k, v) -> v + 1);

                }

                if (processed.incrementAndGet() % 100_000 == 0) {
                    System.out.println("Processed: " + processed.get());
                }
            });
        }

        System.out.println("Processed total: " + processed.get());

        try (FileWriter fileWriter = new FileWriter("W:/hashcode-distribution.txt")) {
            Iterator<Long> iterator = stats.get(0).keySet().iterator();
            while (iterator.hasNext()) {
                Long key = iterator.next();

                long start = key * interval;
                long end = start + interval - 1;

                String intervalRange = "[" + start + "_" + end + "]";

                System.out.print(intervalRange);
                fileWriter.write(intervalRange);

                for (int i = 0; i < stats.size(); i++) {
                    Integer statValue = stats.get(i).get(key);

                    System.out.print("," + statValue);

                    fileWriter.append(",");
                    fileWriter.write(statValue.toString());
                }

                fileWriter.write("\n");
                System.out.println();
            }
        }

    }

    private static long getIntervalStartIndex(long i, int interval) {
        return i > 0 ? i / interval : (i / interval) - 1;
    }
}
