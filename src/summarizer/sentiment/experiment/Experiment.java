package summarizer.sentiment.experiment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by atone on 16/5/22.
 * Experiment.java
 */
public class Experiment {
    static void expa() throws IOException {
        String dataFileStr = "data/exp_a.txt";
        Path dataPath = Paths.get(dataFileStr);
        List<String> lines = Files.readAllLines(dataPath, StandardCharsets.UTF_8);
        lines.remove(0); // 去掉首行内容
        for (int aspect = 1; aspect <= 17; ++aspect) {
            final int id = aspect;
            List<String> aspectLines = lines.stream().filter(line -> {
                String[] tokens = line.split("\t");
                return tokens.length == 3 && Integer.parseInt(tokens[1]) == id;
            }).collect(Collectors.toList());

        }

    }
}
