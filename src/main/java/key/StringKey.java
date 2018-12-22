package key;

import java.util.Objects;

public class StringKey {
    private final String key;

    public StringKey(int id1, int id2, long id3) {
        this.key = id1 + "_" + id2 + "_" + id3;
    }

    @Override
    public boolean equals(Object o) {
        return key.equals(o);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public String stringHashCode() {
        return Objects.toString(hashCode());
    }
}
