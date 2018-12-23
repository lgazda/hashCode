package key;

import java.util.Date;
import java.util.Objects;

public class JavaObjectsHashCodeKey extends AttributeBasedKey {

    public JavaObjectsHashCodeKey(int id1, int id2, Date id3) {
        super(id1, id2, id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeBasedKey that = (AttributeBasedKey) o;
        return id1 == that.id1 &&
                id2 == that.id2 &&
                id3 == that.id3;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id1, id2, id3);
    }
}
