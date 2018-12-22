package key;

public class GuavaHashCodeKey extends AttributeBasedKey {

    public GuavaHashCodeKey(int id1, int id2, long id3) {
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
        return com.google.common.base.Objects.hashCode(id1, id2, id3);
    }
}
