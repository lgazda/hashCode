package hashcode.key;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class ApacheCommonsHashCodeKey extends KeyData {

    public ApacheCommonsHashCodeKey(int id1, int id2, Date id3) {
        super(id1, id2, id3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        KeyData that = (KeyData) o;

        return new EqualsBuilder()
                .append(getId1(), that.getId1())
                .append(getId2(), that.getId2())
                .append(getId3(), that.getId3())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId1())
                .append(getId2())
                .append(getId3())
                .toHashCode();
    }

    public static ApacheCommonsHashCodeKey of(KeyData key) {
        return new ApacheCommonsHashCodeKey(key.getId1(), key.getId2(), key.getId3());
    }
}
