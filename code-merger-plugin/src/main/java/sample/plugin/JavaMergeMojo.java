package sample.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

@Mojo(name = "merge")
public class JavaMergeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}")
    private File buildDirectory;

    @Override
    public void execute() throws MojoExecutionException {

        try {

            StringBuilder merged = new StringBuilder();

            Files.walk(sourceDirectory.toPath())
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {

                        try {

                            merged.append("// File: ")
                                    .append(path.getFileName())
                                    .append("\n");

                            merged.append(Files.readString(path));

                            merged.append("\n\n");

                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }

                    });

            File output =
                    new File(buildDirectory,
                            "merged/MergedCode.java");

            output.getParentFile().mkdirs();

            Files.writeString(output.toPath(),
                    merged.toString());

            getLog().info("Created: " + output);

        } catch (Exception e) {

            throw new MojoExecutionException(
                    "Error while merging java files", e);
        }
    }
}