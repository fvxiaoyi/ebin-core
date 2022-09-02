package core.framework;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author ebin
 */
@Mojo(name = "docker-package", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class PackageMojo extends AbstractMojo {
    public static final String DOCKER_DIR = "docker";
    public static final String DEFAULT_BASE_IMAGE = "openjdk:17-jdk-alpine";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "baseImage", defaultValue = DEFAULT_BASE_IMAGE)
    protected String baseImage;

    public void execute() {
        try {
            File workingDirectory = processWorkingDirectory();
            String jarName = project.getArtifactId() + "-" + project.getVersion() + ".jar";
            String[] args = new String[]{"java", "-Djarmode=layertools", "-jar", jarName, "extract", "--destination", DOCKER_DIR};
            ProcessBuilder builder = new ProcessBuilder();
            builder.directory(workingDirectory);
            builder.command(args);
            builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            Process exec = builder.start();
            exec.waitFor();
            getLog().info("Cmd exec success.");

            createShFile(workingDirectory.getPath());
            createDockerFile(workingDirectory.getPath());
        } catch (IOException e) {
            getLog().info(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private File processWorkingDirectory() {
        return ensureDirectory(new File(project.getBuild().getDirectory()));
    }

    protected static File ensureDirectory(File dir) {
        if (!dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("Couldn't create directories: " + dir);
        }
        return dir;
    }

    private void createShFile(String workingDirectoryPath) throws IOException {
        getLog().info("create sh file.");
        File shFile = new File(workingDirectoryPath + "/" + DOCKER_DIR, "run.sh");
        if (!shFile.exists()) {
            if (!shFile.createNewFile()) {
                getLog().error("Create sh failed.");
                return;
            }
        }
        if (shFile.canWrite()) {
            BufferedWriter out = new BufferedWriter(new FileWriter(shFile));
            out.write("#!/bin/sh\n");
            out.write("exec java $JAVA_OPTS org.springframework.boot.loader.JarLauncher $@");
            out.close();
            getLog().info("Write sh success.");
        } else {
            getLog().error("Write sh failed.");
        }
    }

    private void createDockerFile(String workingDirectoryPath) throws IOException {
        getLog().info("create Dockerfile.");
        File dockerFile = new File(workingDirectoryPath + "/" + DOCKER_DIR, "Dockerfile");
        if (!dockerFile.exists()) {
            if (!dockerFile.createNewFile()) {
                getLog().error("create Dockerfile failed.");
                return;
            }
        }
        if (dockerFile.canWrite()) {
            BufferedWriter out = new BufferedWriter(new FileWriter(dockerFile));
            out.write("FROM " + baseImage + "\n");
            out.write("RUN addgroup --system app && adduser --system --no-create-home --ingroup app app\n");
            out.write("USER app\n");
            out.write("VOLUME /app\n");
            out.write("COPY run.sh ./\n");
            out.write("COPY dependencies/ ./\n");
            out.write("COPY spring-boot-loader/ ./\n");
            out.write("COPY snapshot-dependencies/ ./\n");
            out.write("COPY application/ ./\n");
            out.write("ENTRYPOINT [\"sh\",\"./run.sh\"]");
            out.close();

            getLog().info("Write Dockerfile success.");
        } else {
            getLog().error("Write Dockerfile failed.");
        }
    }
}
