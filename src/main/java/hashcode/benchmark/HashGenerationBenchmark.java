package hashcode.benchmark;

import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.GeneratedKeyDataSupplier;
import hashcode.key.JavaObjectsHashCodeKey;
import hashcode.key.KeyData;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HashGenerationBenchmark {

    @State(value = Scope.Benchmark)
    public static class BenchmarkDate {
        private static final int ENTRY_COUNT = 12_000_000;

        List<KeyData> keyDataList = new LinkedList<>();
        Map<Object, Integer> keyElementMap = new ConcurrentHashMap<>(ENTRY_COUNT);

        private Iterator<KeyData> dataIterator;

        @Setup(Level.Trial)
        public void generateData() {
            new GeneratedKeyDataSupplier(ENTRY_COUNT)
                .get()
                .forEach(keyData -> {
                    keyDataList.add(keyData.getKey(), keyData.getValue());
                    keyElementMap.put(ApacheCommonsHashCodeKey.of(keyData.getValue()), keyData.getKey());
                    //keyElementMap.put(JavaObjectsHashCodeKey.of(keyData.getValue()), keyData.getKey());
                    //keyElementMap.put(StringKey.stringKey(keyData.getValue()), keyData.getKey());
                });
        }

        @Setup(Level.Iteration)
        public void resetDataIterator() {
            dataIterator = keyDataList.iterator();
        }

        public KeyData nextDataElement() {
            if (!dataIterator.hasNext()) {
                resetDataIterator();
            }

            return dataIterator.next();
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
    //@Benchmark
    public void stringMapAccess(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        String key = StringKey.stringKey(keyData);

        blackhole.consume(data.keyElementMap.get(key));
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
    @Benchmark
    public void apacheHashMapAccess(BenchmarkDate data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        KeyData key = ApacheCommonsHashCodeKey.of(keyData);

        blackhole.consume(data.keyElementMap.get(key));
    }

}