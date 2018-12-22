package key;

public class IdeaDefaultHashCodeKey extends AttributeBasedKey {

    public IdeaDefaultHashCodeKey(int id1, int id2, long id3) {
        super(id1, id2, id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttributeBasedKey that = (AttributeBasedKey) o;

        if (id1 != that.id1) return false;
        if (id2 != that.id2) return false;
        return id3 == that.id3;
    }

    @Override
    public int hashCode() {
        int result = id1;
        result = 31 * result + id2;
        result = 31 * result + (int) (id3 ^ (id3 >>> 32));
        return result;
    }
}
