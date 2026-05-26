package sample.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "check")
public class CodeCheckMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}/src/main/java", readonly = true)
    private java.io.File sourceDirectory;

    @Override
    public void execute() throws MojoExecutionException {

        List<String> violations = new ArrayList<>();

        try {

            Files.walk(sourceDirectory.toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(path -> checkFile(path, violations));

        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read source directory", e);
        }

        if (!violations.isEmpty()) {
            for (String v : violations) {
                getLog().error(v);
            }
            throw new MojoExecutionException(
                    "Code check failed. Found " + violations.size() + " violation(s)."
            );
        }

        getLog().info("Code check passed - no violations found");
    }

    private void checkFile(Path file, List<String> violations) {

        try {
            String content = Files.readString(file);

            if (content.contains("System.out.println")) {
                violations.add("System.out.println found in: " + file.getFileName());
            }

            if (content.contains("TODO")) {
                violations.add("TODO found in: " + file.getFileName());
            }

        } catch (IOException e) {
            violations.add("Cannot read file: " + file + " (" + e.getMessage() + ")");
        }
    }
}