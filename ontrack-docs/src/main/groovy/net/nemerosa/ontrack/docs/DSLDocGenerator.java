package net.nemerosa.ontrack.docs;

import net.nemerosa.ontrack.dsl.Ontrack;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Generation of DSL documentation
 */
public class DSLDocGenerator {

    public static void main(String[] args) throws IOException {
        String sourcePath = args[0];
        String outputPath = args[1];

        File dir = new File(outputPath);
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        DSLDoc doc = new DSLDocExtractor(sourcePath).generate(Ontrack.class);

        File adocFile = new File(dir, "dsl-generated.adoc");
        System.out.format("[doc] Writing AsciiDoc at %s%n", adocFile);
        try (PrintWriter writer = new PrintWriter(adocFile)) {
            new AsciiDocGenerator().generate(writer, doc);
        }
    }

}
