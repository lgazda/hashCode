import key.ApacheCommonsHashCodeKey;
import key.GuavaHashCodeKey;
import key.IdeaDefaultHashCodeKey;
import key.StringKey;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.lineSeparator;
import static java.lang.System.setOut;

public class HashCodeGeneration {

    public static void main(String[] args) throws IOException {
        AtomicInteger processedLineCount = new AtomicInteger(0);

        try (Stream<String> stream = Files.lines(Paths.get("W:/hashcode-iv.txt"));
             FileWriter fileWriter = new FileWriter("W:/hashcodes-iv.txt")) {

            fileWriter.write("stringHash,guavaHash,apacheHash,ideaHash");
            fileWriter.write(lineSeparator());

            stream.forEach(line -> {
                String[] split1 = line.split(",");
                String[] split = split1[0].split("_");

                int id1 = Integer.parseInt(split[0]);
                int id2 = Integer.parseInt(split[1]);
                long id3 = Long.parseLong(split[2]);

                try {
                    fileWriter.write(new StringKey(id1, id2, id3).stringHashCode());
                    fileWriter.write(",");
                    fileWriter.write(new GuavaHashCodeKey(id1, id2, id3).stringHashCode());
                    fileWriter.write(",");
                    fileWriter.write(new ApacheCommonsHashCodeKey(id1, id2, id3).stringHashCode());
                    fileWriter.write(",");
                    fileWriter.write(new IdeaDefaultHashCodeKey(id1, id2, id3).stringHashCode());
                    fileWriter.write(lineSeparator());

                    if (processedLineCount.incrementAndGet() % 100_000 == 0) {
                        System.out.println("Processed " + processedLineCount.get());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });

            System.out.println("Total Processed " + processedLineCount.get());
        }
    }
}
