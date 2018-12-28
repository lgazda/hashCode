package hashcode.distribution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class HashCodeDistribution {

    private static Logger LOG = LogManager.getRootLogger();

    public static void main(String[] args) throws IOException {
        String hashCodesInputFilePath = "W:/hashcodes/hashcodes.ts.csv";
        String distributionOutputFilePath = "W:/hashcodes/hashcode.distribution.ts.csv";

        int numberOfHashesToCheck = 4;
        int hashCodeStatsDistributionInterval = 10_000_000;

        List<ConcurrentSkipListMap<Long, Integer>> stats = createStats(numberOfHashesToCheck, hashCodeStatsDistributionInterval);

        collectStats(hashCodesInputFilePath, numberOfHashesToCheck, stats, hashCodeStatsDistributionInterval);
        writeStats(distributionOutputFilePath, stats, hashCodeStatsDistributionInterval);

    }

    private static List<ConcurrentSkipListMap<Long, Integer>> createStats(int numberOfHashesToCheck, int interval) {
        LOG.info("Creating stats");
        List<ConcurrentSkipListMap<Long, Integer>> collect = IntStream.range(0, numberOfHashesToCheck)
                .mapToObj(i -> new ConcurrentSkipListMap<Long, Integer>())
                .collect(Collectors.toList());

        initializeStats(collect, interval);

        return collect;
    }

    private static void initializeStats(List<ConcurrentSkipListMap<Long, Integer>> stats, int interval) {
        long minValue = -2_147_000_000L;
        long maxValue = 2_147_000_000L;

        LOG.info("Initializing stats intervals");
        stats.stream().parallel().forEach(map -> {
            map.put(0L, 0);
            for (long i = minValue; i <= maxValue; i += interval) {
                map.put(getIntervalStartIndex(i, interval), 0);
            }
        });
    }

    private static void collectStats(String hashCodesInputFilePath, int numberOfHashesToCheck, List<ConcurrentSkipListMap<Long, Integer>> stats, int interval) throws IOException {
        final AtomicInteger processed = new AtomicInteger(0);

        try (Stream<String> stream = Files.lines(Paths.get(hashCodesInputFilePath))) {
            stream.skip(1).parallel().forEach(line -> {
                String[] split = line.split(",");

                if (split.length < numberOfHashesToCheck) {
                    throw new IllegalArgumentException("File line doesn't contain required number of values " + numberOfHashesToCheck);
                }

                for (int i = 0; i < split.length; i++) {
                    int hashCode = parseInt(split[i]);
                    long intervalStartIndex = getIntervalStartIndex(hashCode, interval);

                    stats.get(i).compute(intervalStartIndex, (k, v) -> defaultIfNull(v, 0) + 1);
                }

                if (processed.incrementAndGet() % 100_000 == 0) {
                    LOG.info("Processed: " + processed.get());
                }
            });
        }

        LOG.info("Processed total: " + processed.get());
    }

    private static void writeStats(String distributionOutputFilePath, List<ConcurrentSkipListMap<Long, Integer>> stats, int interval) throws IOException {
        try (FileWriter fileWriter = new FileWriter(distributionOutputFilePath)) {
            fileWriter.write("interval,stringHash,javaHash,apacheHash,ideaHash");
            fileWriter.write(lineSeparator());

            for (Long key : stats.get(0).keySet()) {
                long start = key * interval;
                long end = start + interval - 1;

                StringBuilder line = new StringBuilder().append("[").append(start).append("_").append(end).append("]");

                for (ConcurrentSkipListMap<Long, Integer> stat : stats) {
                    Integer statValue = stat.get(key);
                    line.append(',').append(statValue);
                }

                fileWriter.append(line.toString()).append(lineSeparator());
                LOG.info(line.toString());
            }
        }
    }

    private static long getIntervalStartIndex(long i, int interval) {
        return i > 0 ? i / interval : (i / interval) - 1;
    }
}
