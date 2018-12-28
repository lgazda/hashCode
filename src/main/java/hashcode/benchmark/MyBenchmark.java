package hashcode.benchmark;

import hashcode.key.ApacheCommonsHashCodeKey;
import hashcode.key.KeyData;
import hashcode.key.JavaObjectsHashCodeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MyBenchmark {

    private static Logger LOG = LogManager.getRootLogger();

    public static void main(String[] args) {

        String inputIV = "W:/hashcodes/inventory.iv.csv";
        String inputSH = "W:/hashcodes/inventory.sh.csv";
        String inputTS = "W:/hashcodes/inventory.ts.csv";

        List<KeyData> keyData = new ArrayList<>(38_000_000);
        List<JavaObjectsHashCodeKey> javaObjectsHashCodeKeys  = new ArrayList<>(38_000_000);
        List<ApacheCommonsHashCodeKey> apacheCommonsHashCodeKeys  = new ArrayList<>(38_000_000);

        LOG.info("Populating key data");

        Stream.of(inputTS, inputIV, inputSH)
                .forEach(inputPath -> {
                    try (Stream<String> lines = Files.lines(Paths.get(inputPath))) {
                        lines.map(KeyData::parse)
                                .forEach(key -> {
                                    //keyData.add(key);
                                    javaObjectsHashCodeKeys.add(JavaObjectsHashCodeKey.of(key));
                                    apacheCommonsHashCodeKeys.add(ApacheCommonsHashCodeKey.of(key));

                                    if (javaObjectsHashCodeKeys.size() % 1_000_000 == 0) {
                                        LOG.info("1_000_000 processed");
                                    }
                                });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        LOG.info("Populated");
    }

}