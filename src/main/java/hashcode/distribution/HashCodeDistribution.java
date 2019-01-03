package hashcode.distribution;

import com.google.common.collect.ImmutableMap;
import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.FileBasedKeyDataSupplier;
import hashcode.key.GeneratedKeyDataSupplier;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

public class HashCodeDistribution {

    private static final int ENTRY_COUNT = 25_000_000;
    private static final int HASH_CODE_STATS_DISTRIBUTION_INTERVAL = 10_000_000;

    private static Logger LOG = LogManager.getRootLogger();

    public static final Map<String, Function<KeyData, Integer>> HASH_FUNCTION_MAP = ImmutableMap.<String, Function<KeyData, Integer>>builder()
            .put("stringHash", keyData -> StringKey.stringKey(keyData).hashCode())
            .put("javaHash", keyData -> JavaObjectsHashCodeKey.of(keyData).hashCode())
            .put("apacheHash", keyData -> ApacheCommonsHashCodeKey.of(keyData).hashCode())
            .put("ideaHash", keyData -> IdeaDefaultHashCodeKey.of(keyData).hashCode())
            .build();

    public static void main(String[] args) throws Exception {

        Stats stats = new Stats(HASH_CODE_STATS_DISTRIBUTION_INTERVAL);

        try(KeyDataSupplier fileSupplier = new FileBasedKeyDataSupplier("W:/hashcodes/inventory.ts.csv");
            KeyDataSupplier generatedDataSupplier = new GeneratedKeyDataSupplier(ENTRY_COUNT)) {
            collectStats(stats, generatedDataSupplier);
        }

        writeStats("W:/hashcodes/hashcode.distribution.generated.csv",
                stats,
                HASH_CODE_STATS_DISTRIBUTION_INTERVAL);
    }

    public static class Stats {
        private final Map<String, ConcurrentSkipListMap<Long, Integer>> stats;
        private final Map<String, Set<Integer>> hashCodes;
        private final int hashCodeStatsDistributionInterval;

        public Stats(int hashCodeStatsDistributionInterval) {
            this.hashCodeStatsDistributionInterval = hashCodeStatsDistributionInterval;
            this.stats = createStats(hashCodeStatsDistributionInterval);
            this.hashCodes = HASH_FUNCTION_MAP.keySet().stream()
                    .collect(Collectors.toMap(Function.identity(), name -> new HashSet<>(ENTRY_COUNT)));
        }

        private static Map<String, ConcurrentSkipListMap<Long, Integer>> createStats(int interval) {
            LOG.info("Creating stats");
            Map<String, ConcurrentSkipListMap<Long, Integer>> map = HASH_FUNCTION_MAP.keySet().stream()
                    .collect(Collectors.toMap(Function.identity(), name -> new ConcurrentSkipListMap<>()));

            initializeStats(map, interval);

            return map;
        }

        public void collectHashCodeStat(String hashName, int hashCode) {
            long statsBucket = getIntervalStartIndex(hashCode, hashCodeStatsDistributionInterval);
            stats.get(hashName).compute(statsBucket, (k, v) -> defaultIfNull(v, 0) + 1);
            hashCodes.get(hashName).add(hashCode);
        }

        public List<String> getHashNames() {
            List<String> hashColumns = new ArrayList<>(stats.keySet());
            hashColumns.sort(String::compareTo);

            return hashColumns;
        }

        public Collection<Long> getStatIntervals() {
            return stats.values().stream().findFirst().map(ConcurrentSkipListMap::keySet).orElse(null);
        }

        public int getStatsForInterval(String hashName, Long currentInterval) {
            ConcurrentSkipListMap<Long, Integer> stat = stats.get(hashName);
            return stat.get(currentInterval);
        }

        public int getUniqueHashCodesCount(String hashName) {
            return hashCodes.get(hashName).size();
        }

        public int getCollectedHashCodesCount(String hashName) {
            return stats.get(hashName)
                    .values()
                    .stream()
                    .mapToInt(i -> i)
                    .sum();
        }
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

    private static void collectStats(Stats stats, KeyDataSupplier dataSupplier) {
        final AtomicInteger processed = new AtomicInteger(0);

        dataSupplier.get().forEach(data -> {
            HASH_FUNCTION_MAP.forEach((hashName, hashFunction) -> {
                Integer hashCode = hashFunction.apply(data.getValue());
                stats.collectHashCodeStat(hashName, hashCode);
            });

            if (processed.incrementAndGet() % 1_000_000 == 0) {
                LOG.info("Processed: " + processed.get());
            }
        });

        LOG.info("Processed total: " + processed.get());
    }

    private static void writeStats(String distributionOutputFilePath, Stats stats, int distributionInterval) throws IOException {

        try (FileWriter fileWriter = new FileWriter(distributionOutputFilePath)) {
            fileWriter.write("interval,");
            fileWriter.write(String.join(",", stats.getHashNames()));
            fileWriter.write(lineSeparator());

            for (Long currentInterval : stats.getStatIntervals()) {
                long start = currentInterval * distributionInterval;
                long end = start + distributionInterval - 1;

                StringBuilder line = new StringBuilder().append("[").append(start).append("_").append(end).append("]");

                for (String hashName : stats.getHashNames()) {
                    Integer intervalStats = stats.getStatsForInterval(hashName, currentInterval);
                    line.append(',').append(intervalStats);
                }

                fileWriter.append(line.toString()).append(lineSeparator());
                LOG.info(line.toString());
            }
        }

        stats.getHashNames()
                .stream()
                .map(name -> name + " total: " +  stats.getCollectedHashCodesCount(name) + " unique: " + stats.getUniqueHashCodesCount(name))
                .forEach(System.out::println);
    }

    private static long getIntervalStartIndex(long i, int interval) {
        return i > 0 ? i / interval : (i / interval) - 1;
    }
}
