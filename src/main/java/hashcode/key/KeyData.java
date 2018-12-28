package hashcode.key;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KeyData {
    private final int id1;
    private final int id2;
    private final Date id3;

    public KeyData(int id1, int id2, Date id3) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
    }

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }

    public Date getId3() {
        return id3;
    }

    public static Date parseDate(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return simpleDateFormat.parse(s);
        } catch (ParseException e) {
            throw new IllegalStateException("Unable to parse date from: " + s);
        }
    }

    public static KeyData parse(String line) {
        String[] lineSplit = line.split(",");

        int id1 = Integer.parseInt(lineSplit[0]);
        int id2 = Integer.parseInt(lineSplit[1]);
        Date id3 = parseDate(lineSplit[2]);

        return new KeyData(id1, id2, id3);
    }
}
