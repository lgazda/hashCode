package key;

import java.util.Objects;

public abstract class AttributeBasedKey {
    protected final int id1;
    protected final int id2;
    protected final long id3;

    protected AttributeBasedKey(int id1, int id2, long id3) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
    }

    public String stringHashCode() {
        return Objects.toString(hashCode());
    }
}
