package hashcode.benchmark;

import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.KeyData;
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

public class ApacheHashBenchmark {

    @State(value = Scope.Benchmark)
    public static class BenchmarkData extends GeneralBenchmarkData {
        @Override
        @Setup(Level.Trial)
        public void generateData() {
            super.generateData();
        }

        @Override
        protected ApacheCommonsHashCodeKey createBenchmarkKey(KeyData keyData) {
            return ApacheCommonsHashCodeKey.of(keyData);
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
    public void apacheHashGeneration(BenchmarkData data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        KeyData key = data.createBenchmarkKey(keyData);

        blackhole.consume(key.hashCode());
        blackhole.consume(key);
    }

    @Fork(value = 1)
    @Warmup(iterations = 3, time = 5)
    @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    @Measurement(iterations = 5)
    @Benchmark
    public void apacheHashMapAccess(BenchmarkData data, Blackhole blackhole) {
        KeyData keyData = data.nextDataElement();
        KeyData key = data.createBenchmarkKey(keyData);

        blackhole.consume(data.keyElementMap.get(key));
    }


}