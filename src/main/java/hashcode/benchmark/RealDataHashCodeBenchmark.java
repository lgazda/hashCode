package hashcode.benchmark;

import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.KeyData;
import hashcode.key.JavaObjectsHashCodeKey;
import hashcode.key.StringKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class RealDataHashCodeBenchmark {
    private static Logger LOG = LogManager.getRootLogger();


    //@State(Scope.Benchmark)
    public static class BenchmarkData {
        public static final int initSize = 14_000_000;
        public Map<JavaObjectsHashCodeKey, Integer> javaObjectsHashCodeKeys  = new ConcurrentHashMap<>(initSize);
        public Map<ApacheCommonsHashCodeKey, Integer> apacheCommonsHashCodeKeys  = new ConcurrentHashMap<>(initSize);
        public Map<StringKey, Integer> stringKeys  = new ConcurrentHashMap<>(initSize);

        //@Setup(Level.Trial)
        public void loadKeys() {
            LOG.info("Populating key keyDataList");

            String inputIV = "W:/hashcodes/inventory.iv.csv";
            String inputSH = "W:/hashcodes/inventory.sh.csv";
            String inputTS = "W:/hashcodes/inventory.ts.csv";

            AtomicInteger processed = new AtomicInteger(0);

            Stream.of(inputIV)
                    .forEach(inputPath -> {
                        try (Stream<String> lines = Files.lines(Paths.get(inputPath))) {
                            lines.parallel()
                                    //.limit(1_000_000)
                                    .map(KeyData::parse)
                                    .forEach(key -> {
                                        //stringKeys.put(StringKey.of(key), stringKeys.size());
                                        javaObjectsHashCodeKeys.put(JavaObjectsHashCodeKey.of(key), javaObjectsHashCodeKeys.size());
                                        //apacheCommonsHashCodeKeys.put(ApacheCommonsHashCodeKey.of(key), apacheCommonsHashCodeKeys.size());

                                        if (processed.incrementAndGet() % 1_000_000 == 0) {
                                            LOG.info("1_000_000 processed");
                                        }
                                    });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            LOG.info("Populated");
        }
    }

    //@Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 1, time = 5)
    @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 3)
    public void javaObjectsHashCodeGeneration(BenchmarkData benchmarkData, Blackhole blackhole) {
        generateHashCodes(blackhole, benchmarkData.javaObjectsHashCodeKeys.keySet());
    }

    //@Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 1, time = 5)
    @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 3)
    public void javaHashAccess(BenchmarkData benchmarkData, Blackhole blackhole) {
        accessMap(blackhole, benchmarkData.javaObjectsHashCodeKeys);
    }

    //@Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 1, time = 5)
    @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 3)
    public void stringHashCodeGeneration(BenchmarkData benchmarkData, Blackhole blackhole) {
        generateHashCodes(blackhole, benchmarkData.stringKeys.keySet());
    }

    //@Benchmark
    @Fork(value = 1)
    @Warmup(iterations = 1, time = 5)
    @BenchmarkMode(Mode.AverageTime) @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Measurement(iterations = 3)
    public void stringHashAccess(BenchmarkData benchmarkData, Blackhole blackhole) {
        accessMap(blackhole, benchmarkData.stringKeys);
    }

    private void generateHashCodes(Blackhole blackhole, Collection elements) {
        for (Object o : elements) {
            blackhole.consume(o.hashCode());
        }
    }

    private void accessMap(Blackhole blackhole, Map<?, ?> map) {
        for (Object o : map.keySet()) {
            blackhole.consume(map.get(o));
        }
    }
}
