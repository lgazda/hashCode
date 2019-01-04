package hashcode.benchmark;

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

import java.util.concurrent.TimeUnit;

public class StringHashBenchmark {

    @State(value = Scope.Benchmark)
    public static class BenchmarkData extends GeneralBenchmarkData {
        @Override
        @Setup(Level.Trial)
        public void generateData() {
            super.generateData();
        }

        @Override
        protected String createBenchmarkKey(KeyData keyData) {
            return StringKey.stringKey(keyData);
        }

        @Override
        @Setup(Level.Iteration)
        public void resetDataIterator() {
            super.resetDataIterator();
        }
    }

    @Fork(value = 1)
    @Warmup(iterations = 3, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    @Benchmark
    public void stringHashGeneration(BenchmarkData data, Blackhole blackhole) {
        Object key = data.getNextKey();

        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 5, time = 5)
    @BenchmarkMode({Mode.Throughput}) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    @Benchmark
    public void stringMapAccess(BenchmarkData data, Blackhole blackhole) {
        Object key = data.getNextKey();

        blackhole.consume(data.keyElementMap.get(key));
    }

}