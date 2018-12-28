package hashcode.key;

import java.util.Date;
import java.util.Objects;

public class JavaObjectsHashCodeKey extends KeyData {

    public JavaObjectsHashCodeKey(int id1, int id2, Date id3) {
        super(id1, id2, id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyData that = (KeyData) o;
        return getId1() == that.getId1() &&
                getId2() == that.getId2() &&
                getId3() == that.getId3();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId1(), getId2(), getId3());
    }

    public static JavaObjectsHashCodeKey of(KeyData key) {
        return new JavaObjectsHashCodeKey(key.getId1(), key.getId2(), key.getId3());
    }
}
