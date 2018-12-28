package hashcode.key;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static hashcode.key.KeyDataSupplier.parseIsoDate;
import static java.util.Objects.nonNull;

public class FileBasedKeyDataSupplier implements KeyDataSupplier {
    private final String filePath;
    private Stream<String> fileLines;

    public FileBasedKeyDataSupplier(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void close() {
        if (nonNull(fileLines)) {
            fileLines.close();
        }
    }

    @Override
    public Stream<Pair<Integer, KeyData>> get() {
        try {
            fileLines = Files.lines(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicInteger index = new AtomicInteger(0);
        return fileLines.map(line -> {
            String[] lineSplit = line.split(",");

            int id1 = Integer.parseInt(lineSplit[0]);
            int id2 = Integer.parseInt(lineSplit[1]);
            Date id3 = parseIsoDate(lineSplit[2]);

            return Pair.of(index.getAndIncrement(), new KeyData(id1, id2, id3));
        });
    }
}
