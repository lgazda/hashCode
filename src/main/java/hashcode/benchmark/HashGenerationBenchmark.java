package hashcode.benchmark;

import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.KeyData;
import hashcode.key.JavaObjectsHashCodeKey;
import hashcode.key.StringKey;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HashGenerationBenchmark {

    @State(value = Scope.Benchmark)
    public static class BenchmarkDate {
        private static final int ENTRY_COUNT = 14_000_000;
        private static final int DAY_MILLIS = 86400000;
        private static final int DATE_DAY_INTERVAL = 720;
        private static final Date START_DATE = parseDate("2018-12-28");

        List<Integer> id1s = new ArrayList<>(ENTRY_COUNT);
        List<Integer> id2s = new ArrayList<>(ENTRY_COUNT);
        List<Date> id3s = new ArrayList<>(ENTRY_COUNT);
        List<KeyData> data = new ArrayList<>(ENTRY_COUNT);

        int currentIndexId1 = 0;
        int currentIndexId2 = 0;
        int currentIndexId3 = 0;

        Iterator<KeyData> dataIterator;

        @Setup(Level.Trial)
        public void setUpData() {
            for (int i = 0; i < ENTRY_COUNT; i++) {
                KeyData keyData = new KeyData(
                        i % 50_000,
                        i % 100_000,
                        new Date(START_DATE.getTime() + DAY_MILLIS * (i % DATE_DAY_INTERVAL)));

                data.add(i, keyData);
            }
        }

        @Setup(Level.Iteration)
        public void resetIndexId1() {
            currentIndexId1 = 0;
        }

        @Setup(Level.Iteration)
        public void resetIndexId2() {
            currentIndexId2 = 0;
        }

        @Setup(Level.Iteration)
        public void resetIndexId3() {
            currentIndexId3 = 0;
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

        int nextId1() {
            if (currentIndexId1 >= ENTRY_COUNT) {
                resetIndexId1();
            }

            return id1s.get(currentIndexId1++);
        }

        int nextId2() {
            if (currentIndexId2 >= ENTRY_COUNT) {
                resetIndexId2();
            }

            return id2s.get(currentIndexId2++);
        }

        Date nextId3() {
            if (currentIndexId3 >= ENTRY_COUNT) {
                resetIndexId3();
            }

            return id3s.get(currentIndexId3++);
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
    @Benchmark
    public void stringHashGeneration(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();

        String key = StringKey.stringKey(
                keyData.getId1(),
                keyData.getId2(),
                keyData.getId3());

        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    @Benchmark
    public void javaHashGeneration(BenchmarkDate data, Blackhole blackhole) {


        KeyData key = new JavaObjectsHashCodeKey(data.nextId1(), data.nextId2(), data.nextId3());
        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    @Benchmark
    public void apacheHashGeneration(BenchmarkDate data, Blackhole blackhole) {
        KeyData key = new ApacheCommonsHashCodeKey(data.nextId1(), data.nextId2(), data.nextId3());
        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

}