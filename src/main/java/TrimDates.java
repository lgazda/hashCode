import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class TrimDates {
    public static void main(String[] args) throws IOException {
        String inputFilePath = "W:/hashcodes/inventory.iv.csv";
        String outputFilePath = "W:/hashcodes/inventory.iv.2.csv";


        try (Stream<String> stream = Files.lines(Paths.get(inputFilePath));
             FileWriter fileWriter = new FileWriter(outputFilePath)) {

            stream.forEach(line -> {
                try {
                    fileWriter.append(line.replace(" 00:00:00.000", "")).append(System.lineSeparator());
                } catch (IOException e) {
                    throw new IllegalStateException();                }
            });
        }
    }
}
