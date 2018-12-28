package hashcode.key;

import java.util.Date;

public class IdeaDefaultHashCodeKey extends KeyData {

    public IdeaDefaultHashCodeKey(int id1, int id2, Date id3) {
        super(id1, id2, id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyData that = (KeyData) o;

        if (getId1() != that.getId1()) return false;
        if (getId2() != that.getId2()) return false;
        return getId3().equals(that.getId3());
    }

    @Override
    public int hashCode() {
        int result = getId1();
        result = 31 * result + getId2();
        result = 31 * result + getId3().hashCode();
        return result;
    }

    public static IdeaDefaultHashCodeKey of(KeyData key) {
        return new IdeaDefaultHashCodeKey(key.getId1(), key.getId2(), key.getId3());
    }
}
