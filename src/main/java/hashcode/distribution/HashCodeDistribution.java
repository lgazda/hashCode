package hashcode.distribution;

import com.google.common.collect.ImmutableMap;
import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.FileBasedKeyDataSupplier;
import hashcode.key.GenerateKeyDataSupplier;
import hashcode.key.IdeaDefaultHashCodeKey;
import hashcode.key.JavaObjectsHashCodeKey;
import hashcode.key.KeyData;
import hashcode.key.KeyDataSupplier;
import hashcode.key.StringKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class HashCodeDistribution {

    private static Logger LOG = LogManager.getRootLogger();

    public static final Map<String, Function<KeyData, Integer>> HASH_FUNCTION_MAP = ImmutableMap.<String, Function<KeyData, Integer>>builder()
            .put("stringHash", keyData -> StringKey.stringKey(keyData).hashCode())
            .put("javaHash", keyData -> JavaObjectsHashCodeKey.of(keyData).hashCode())
            .put("apacheHash", keyData -> ApacheCommonsHashCodeKey.of(keyData).hashCode())
            .put("ideaHash", keyData -> IdeaDefaultHashCodeKey.of(keyData).hashCode())
            .build();

    public static void main(String[] args) throws Exception {
        int hashCodeStatsDistributionInterval = 10_000_000;

        Map<String, ConcurrentSkipListMap<Long, Integer>> stats = createStats(hashCodeStatsDistributionInterval);

        try(KeyDataSupplier fileSupplier = new FileBasedKeyDataSupplier("W:/hashcodes/inventory.ts.csv");
            KeyDataSupplier generatedDataSupplier = new GenerateKeyDataSupplier(37_000_000)) {
            collectStats(stats, hashCodeStatsDistributionInterval, generatedDataSupplier);
        }

        writeStats("W:/hashcodes/hashcode.distribution.generated.csv",
                stats,
                hashCodeStatsDistributionInterval);
    }

    private static Map<String, ConcurrentSkipListMap<Long, Integer>> createStats(int interval) {
        LOG.info("Creating stats");
        Map<String, ConcurrentSkipListMap<Long, Integer>> map = HASH_FUNCTION_MAP.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), name -> new ConcurrentSkipListMap<>()));

        initializeStats(map, interval);

        return map;
    }

    private static void initializeStats(Map<String, ConcurrentSkipListMap<Long, Integer>> stats, int interval) {
        long minValue = -2_147_000_000L;
        long maxValue = 2_147_000_000L;

        LOG.info("Initializing stats intervals");
        stats.values().stream().parallel().forEach(map -> {
            map.put(0L, 0);
            for (long i = minValue; i <= maxValue; i += interval) {
                map.put(getIntervalStartIndex(i, interval), 0);
            }
        });

        LOG.info("Stats intervals initialized");
    }

    private static void collectStats(Map<String, ConcurrentSkipListMap<Long, Integer>> stats, int distributionInterval, KeyDataSupplier pairStream) {
        final AtomicInteger processed = new AtomicInteger(0);

        pairStream.get().forEach(data -> {
            HASH_FUNCTION_MAP.forEach((hashName, hashFunction) -> {
                ConcurrentSkipListMap<Long, Integer> singleHashStats = stats.get(hashName);
                Integer hashCode = hashFunction.apply(data.getValue());
                long statsBucket = getIntervalStartIndex(hashCode, distributionInterval);

                singleHashStats.compute(statsBucket, (k, v) -> defaultIfNull(v, 0) + 1);
            });

            if (processed.incrementAndGet() % 1_000_000 == 0) {
                LOG.info("Processed: " + processed.get());
            }
        });

        LOG.info("Processed total: " + processed.get());
    }

    private static void writeStats(String distributionOutputFilePath, Map<String, ConcurrentSkipListMap<Long, Integer>> stats, int distributionInterval) throws IOException {
        List<String> hashColumns = new ArrayList<>(stats.keySet());
        hashColumns.sort(String::compareTo);

        try (FileWriter fileWriter = new FileWriter(distributionOutputFilePath)) {
            fileWriter.write("interval,");
            fileWriter.write(String.join(",", hashColumns));
            fileWriter.write(lineSeparator());

            for (Long currentInterval : stats.get(hashColumns.get(0)).keySet()) {
                long start = currentInterval * distributionInterval;
                long end = start + distributionInterval - 1;

                StringBuilder line = new StringBuilder().append("[").append(start).append("_").append(end).append("]");

                for (String hashName : hashColumns) {
                    ConcurrentSkipListMap<Long, Integer> stat = stats.get(hashName);
                    Integer statValue = stat.get(currentInterval);

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
