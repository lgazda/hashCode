package hashcode.key;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hashcode.key.KeyDataSupplier.parseIsoDate;

public class GeneratedKeyDataSupplier implements KeyDataSupplier {
    private static final int DAY_MILLIS = 86400000;
    private static final int DAYS_PER_ID2 = 1000;
    private static final Date START_DATE = parseIsoDate("2018-12-28");
    public static final int ID2_PER_ID1 = 25;

    private final int entryCount;

    public GeneratedKeyDataSupplier(int entryCount) {
        this.entryCount = entryCount;
    }

    @Override
    public Stream<Pair<Integer, KeyData>> get() {
        AtomicInteger id1Counter_1 = new AtomicInteger(1);
        AtomicInteger id2Counter_1 = new AtomicInteger(0);
        AtomicInteger id3Counter_1 = new AtomicInteger(1);

        Stream<Pair<Integer, KeyData>> stream1 = IntStream.range(0, entryCount / 3)
                .mapToObj(i -> {
                    setCounters(id1Counter_1, id2Counter_1, id3Counter_1);

                    return Pair.of(i, new KeyData(
                            id1Counter_1.get(),
                            id2Counter_1.get(),
                            new Date(START_DATE.getTime() + DAY_MILLIS * id3Counter_1.getAndIncrement())));
                    });

        AtomicInteger id1Counter_2 = new AtomicInteger(1);
        AtomicInteger id2Counter_2 = new AtomicInteger(0);
        AtomicInteger id3Counter_2 = new AtomicInteger(1);

        Stream<Pair<Integer, KeyData>> stream2 = IntStream.range(entryCount / 3, 2 * entryCount / 3)
                .mapToObj(i -> {
                    setCounters(id1Counter_2, id2Counter_2, id3Counter_2);

                    return Pair.of(i, new KeyData(
                            203_000_000 + id1Counter_2.get(),
                            1_002_000_000 + id2Counter_2.get(),
                            new Date(START_DATE.getTime() + DAY_MILLIS * id3Counter_2.getAndIncrement())));
                });

        AtomicInteger id1Counter_3 = new AtomicInteger(1);
        AtomicInteger id2Counter_3 = new AtomicInteger(0);
        AtomicInteger id3Counter_3 = new AtomicInteger(1);

        Stream<Pair<Integer, KeyData>> stream3 = IntStream.range(2 * entryCount / 3, entryCount)
                .mapToObj(i -> {
                    setCounters(id1Counter_3, id2Counter_3, id3Counter_3);

                    return Pair.of(i, new KeyData(
                            503_000_000 + id1Counter_3.get(),
                            1_010_000_000 + id2Counter_3.get(),
                            new Date(START_DATE.getTime() + DAY_MILLIS * id3Counter_3.getAndIncrement())));
                });

        Stream<Pair<Integer, KeyData>> stream12 = Stream.concat(stream1, stream2);
        return Stream.concat(stream12, stream3);
    }

    private void setCounters(AtomicInteger cId1, AtomicInteger crtId1, AtomicInteger dayCounter1) {
        if (dayCounter1.get() == DAYS_PER_ID2) {
            crtId1.incrementAndGet();
            dayCounter1.set(1);
        }

        if (crtId1.get() == ID2_PER_ID1) {
            cId1.incrementAndGet();
            crtId1.set(0);
        }
    }

    @Override
    public void close() {

    }
}
