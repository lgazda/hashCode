package hashcode.benchmark;

import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.KeyData;
import hashcode.key.JavaObjectsHashCodeKey;
import hashcode.key.StringKey;
import org.apache.commons.lang3.tuple.Pair;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class HashGenerationBenchmark {

    @State(value = Scope.Benchmark)
    public static class BenchmarkDate {
        private static final int ENTRY_COUNT = 37_000_000;
        private static final int DAY_MILLIS = 86400000;
        private static final int DATE_DAY_INTERVAL = 720;
        private static final Date START_DATE = parseDate("2018-12-28");

        List<KeyData> data = new ArrayList<>(ENTRY_COUNT);
        Map<Object, Integer> elementMap = new ConcurrentHashMap<>(ENTRY_COUNT);

        Iterator<KeyData> dataIterator;

        @Setup(Level.Trial)
        public void generateData() {
            generateKeyDataStream()
                .forEach(keyData -> {
                    data.add(keyData.getKey(), keyData.getValue());
                    //elementMap.put(ApacheCommonsHashCodeKey.of(keyData), i);
                    elementMap.put(StringKey.stringKey(keyData.getValue()), keyData.getKey());
                });
        }

        public static Stream<Pair<Integer, KeyData>> generateKeyDataStream() {
            return IntStream.range(0, ENTRY_COUNT)
                    .mapToObj(i -> Pair.of(i, new KeyData(
                            i % 50_000,
                            i % 100_000,
                            new Date(START_DATE.getTime() + DAY_MILLIS * (i % DATE_DAY_INTERVAL)))));
        }

        public interface KeyDataSupplier extends Supplier<Stream<Pair<Integer, KeyData>>>, AutoCloseable { }

        public static class FileBasedKeyDataSupplier implements KeyDataSupplier {
            private final String filePath;
            private Stream<String> fileLines;

            public FileBasedKeyDataSupplier(String filePath) {
                this.filePath = filePath;
            }

            @Override
            public void close() {
                if (nonNull(fileLines)) {
                    fileLines.close();
                }
            }

            @Override
            public Stream<Pair<Integer, KeyData>> get() {
                try {
                    fileLines = Files.lines(Paths.get(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                AtomicInteger index = new AtomicInteger(0);
                return fileLines.map(line -> {
                    String[] lineSplit = line.split(",");

                    int id1 = Integer.parseInt(lineSplit[0]);
                    int id2 = Integer.parseInt(lineSplit[1]);
                    Date id3 = parseDate(lineSplit[2]);

                    return Pair.of(index.getAndIncrement(), new KeyData(id1, id2, id3));
                });
            }
        }

        public static class GeneratedKeyDataSupplier implements KeyDataSupplier {
            private static final int DAY_MILLIS = 86400000;
            private static final int DATE_DAY_INTERVAL = 720;
            private static final Date START_DATE = parseDate("2018-12-28");

            private final int entryCount;

            public GeneratedKeyDataSupplier(int entryCount) {
                this.entryCount = entryCount;
            }

            @Override
            public Stream<Pair<Integer, KeyData>> get() {
                return IntStream.range(0, entryCount)
                        .mapToObj(i -> Pair.of(i, new KeyData(
                                i % 50_000,
                                i % 100_000,
                                new Date(START_DATE.getTime() + DAY_MILLIS * (i % DATE_DAY_INTERVAL)))));
            }

            @Override
            public void close() {

            }
        }

        @Setup(Level.Iteration)
        public void resetDataIterator() {
            dataIterator = data.iterator();
        }

        KeyData nextDataElement() {
            if (!dataIterator.hasNext()) {
                resetDataIterator();
            }

            return dataIterator.next();
        }


        private static Date parseDate(String s) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(s);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    //@Benchmark
    public void stringHashGeneration(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        String key = StringKey.stringKey(keyData);

        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode({Mode.Throughput}) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    @Benchmark
    public void stringMapAccess(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        String key = StringKey.stringKey(keyData);

        blackhole.consume(data.elementMap.get(key));
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
   //@Benchmark
    public void javaHashGeneration(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        KeyData key = JavaObjectsHashCodeKey.of(keyData);

        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    //@Benchmark
    public void apacheHashGeneration(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        KeyData key = ApacheCommonsHashCodeKey.of(keyData);

        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    //@Benchmark
    public void apacheHashMapAccess(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        KeyData key = ApacheCommonsHashCodeKey.of(keyData);

        blackhole.consume(data.elementMap.get(key));
    }

}