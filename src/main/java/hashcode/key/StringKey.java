package hashcode.key;

import java.util.Date;

public class StringKey {
    private final String key;

    public StringKey(int id1, int id2, Date id3) {
        this.key = id1 + "_" + id2 + "_" + id3.getTime();
    }

    @Override
    public boolean equals(Object o) {
        return key.equals(o);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public static StringKey of(KeyData key) {
        return new StringKey(key.id1, key.id2, key.id3);
    }

    public static String stringKey(int id1, int id2, Date id3) {
        return (id1 + "_" + id2 + "_" + id3.getTime());
    }

    public static String stringKey(KeyData key) {
        return (key.getId1() + "_" + key.getId2() + "_" + key.getId3().getTime());
    }
}
