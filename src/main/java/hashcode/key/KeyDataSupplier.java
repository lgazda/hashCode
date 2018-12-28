package hashcode.key;

import org.apache.commons.lang3.tuple.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface KeyDataSupplier extends Supplier<Stream<Pair<Integer, KeyData>>>, AutoCloseable {
    static Date parseIsoDate(String s) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
