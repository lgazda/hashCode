package hashcode.key;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hashcode.key.KeyDataSupplier.parseIsoDate;

public class GenerateKeyDataSupplier implements KeyDataSupplier {
    private static final int DAY_MILLIS = 86400000;
    private static final int DATE_DAY_INTERVAL = 720;
    private static final Date START_DATE = parseIsoDate("2018-12-28");

    private final int entryCount;

    public GenerateKeyDataSupplier(int entryCount) {
        this.entryCount = entryCount;
    }

    @Override
    public Stream<Pair<Integer, KeyData>> get() {
         Stream<Pair<Integer, KeyData>> stream1 = IntStream.range(0, entryCount / 3)
                .mapToObj(i -> Pair.of(i, new KeyData(
                        i % 50_000,
                        i % 100_000,
                        new Date(START_DATE.getTime() + DAY_MILLIS * (i % DATE_DAY_INTERVAL)))));

        Stream<Pair<Integer, KeyData>> stream2 = IntStream.range(entryCount / 3, 2 * entryCount / 3)
                .mapToObj(i -> Pair.of(i, new KeyData(
                        203_000_000 + i % 50_00,
                        1_002_000_000 + i % 100_000,
                        new Date(START_DATE.getTime() + DAY_MILLIS * (i % DATE_DAY_INTERVAL)))));

        Stream<Pair<Integer, KeyData>> stream3 = IntStream.range(2 * entryCount / 3, entryCount)
                .mapToObj(i -> Pair.of(i, new KeyData(
                        503_000_000 + i % 50_00,
                        1_010_000_000 + i % 100_000,
                        new Date(START_DATE.getTime() + DAY_MILLIS * (i % DATE_DAY_INTERVAL)))));

        Stream<Pair<Integer, KeyData>> stream12 = Stream.concat(stream1, stream2);
        return Stream.concat(stream12, stream3);
    }

    @Override
    public void close() {

    }
}
