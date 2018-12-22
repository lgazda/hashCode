import key.ApacheCommonsHashCodeKey;
import key.JavaObjectsHashCodeKey;
import key.IdeaDefaultHashCodeKey;
import key.StringKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;

public class HashCodeGeneration {
    private static Logger LOG = LogManager.getRootLogger();

    public static void main(String[] args) throws IOException {
        AtomicInteger processedLineCount = new AtomicInteger(0);

        try (Stream<String> stream = Files.lines(Paths.get("W:/hashcode-iv.txt"));
             FileWriter fileWriter = new FileWriter("W:/hashcodes-iv.txt")) {

            fileWriter.write("stringHash,javaHash,apacheHash,ideaHash");
            fileWriter.write(lineSeparator());

            stream.forEach(line -> {
                String[] split1 = line.split(",");
                String[] split = split1[0].split("_");

                int id1 = Integer.parseInt(split[0]);
                int id2 = Integer.parseInt(split[1]);
                long id3 = Long.parseLong(split[2]);

                StringBuilder lineToWrite = new StringBuilder();

                lineToWrite
                        .append(new StringKey(id1, id2, id3).hashCode())
                        .append(",")
                        .append(new JavaObjectsHashCodeKey(id1, id2, id3).hashCode())
                        .append(",")
                        .append(new ApacheCommonsHashCodeKey(id1, id2, id3).hashCode())
                        .append(",")
                        .append(new IdeaDefaultHashCodeKey(id1, id2, id3).hashCode());

                try {
                    fileWriter.append(lineToWrite.toString()).append(lineSeparator());

                    if (processedLineCount.incrementAndGet() % 100_000 == 0) {
                        LOG.info("Processed " + processedLineCount.get());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });

            LOG.info("Total Processed " + processedLineCount.get());
        }
    }
}
