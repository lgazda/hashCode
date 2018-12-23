package key;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class ApacheCommonsHashCodeKey extends AttributeBasedKey {

    public ApacheCommonsHashCodeKey(int id1, int id2, Date id3) {
        super(id1, id2, id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AttributeBasedKey that = (AttributeBasedKey) o;

        return new EqualsBuilder()
                .append(id1, that.id1)
                .append(id2, that.id2)
                .append(id3, that.id3)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id1)
                .append(id2)
                .append(id3)
                .toHashCode();
    }
}
