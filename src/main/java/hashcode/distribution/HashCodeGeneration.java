package hashcode.distribution;

import hashcode.key.FileBasedKeyDataSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static hashcode.distribution.HashCodeDistribution.HASH_FUNCTION_MAP;
import static java.lang.System.lineSeparator;

public class HashCodeGeneration {
    private static Logger LOG = LogManager.getRootLogger();

    public static void main(String[] args) throws IOException {
        String inputFilePath = "W:/hashcodes/inventory.sh.csv";
        String outputFilePath = "W:/hashcodes/hashcodes.sh.csv";
        boolean appendToOutput = false;

        AtomicInteger processedLineCount = new AtomicInteger(0);

        try (FileBasedKeyDataSupplier fileBasedKeyDataSupplier = new FileBasedKeyDataSupplier(inputFilePath);
             FileWriter fileWriter = new FileWriter(outputFilePath, appendToOutput)) {

            List<String> hashColumns = new ArrayList<>(HASH_FUNCTION_MAP.keySet());
            hashColumns.sort(String::compareTo);

            fileWriter.write(String.join(",", hashColumns));
            fileWriter.write(lineSeparator());

            fileBasedKeyDataSupplier.get().forEach(keyData -> {
                StringBuilder lineToWrite = new StringBuilder();

                hashColumns.forEach(hashColumn -> {
                    Integer hashCode = HASH_FUNCTION_MAP.get(hashColumn).apply(keyData.getValue());
                    lineToWrite.append(hashCode).append(",");
                });

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
