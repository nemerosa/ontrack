package net.nemerosa.ontrack.docs;

import net.nemerosa.ontrack.dsl.Ontrack;

import java.io.File;
import java.io.IOException;

/**
 * Generation of DSL documentation
 */
public class DSLDocGenerator {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String sourcePath = args[0];
        String outputPath = args[1];

        File outputDir = new File(outputPath);
        //noinspection ResultOfMethodCallIgnored
        outputDir.mkdirs();

        DSLDoc doc = new DSLDocExtractor(sourcePath).generate(Ontrack.class);

        System.out.format("[doc] Writing AsciiDoc files in %s%n", outputDir);
        new AsciiDocGenerator().generate(outputDir, doc);
    }

}
