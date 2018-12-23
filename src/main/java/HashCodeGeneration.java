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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;

public class HashCodeGeneration {
    private static Logger LOG = LogManager.getRootLogger();

    public static void main(String[] args) throws IOException {
        String inputFilePath = "W:/hashcodes/inventory.sh.csv";
        String outputFilePath = "W:/hashcodes/hashcodes.sh.csv";
        boolean appendToOutput = false;

        AtomicInteger processedLineCount = new AtomicInteger(0);

        try (Stream<String> stream = Files.lines(Paths.get(inputFilePath));
             FileWriter fileWriter = new FileWriter(outputFilePath, appendToOutput)) {

            fileWriter.write("stringHash,javaHash,apacheHash,ideaHash");
            fileWriter.write(lineSeparator());

            stream.forEach(line -> {
                String[] lineSplit = line.split(",");

                int id1 = Integer.parseInt(lineSplit[0]);
                int id2 = Integer.parseInt(lineSplit[1]);
                Date id3 = parseDate(lineSplit[2]);

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

    private static Date parseDate(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return simpleDateFormat.parse(s);
        } catch (ParseException e) {
            throw new IllegalStateException("Unable to parse date from: " + s);
        }
    }
}
