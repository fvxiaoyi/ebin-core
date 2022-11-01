package core.framework;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ebin
 */
@Mojo(name = "ddd-checkstyle-file", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
public class CheckStyleMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    public void execute() {
        File workingDirectory = processWorkingDirectory();
        createFile(workingDirectory.getPath(), "checkstyle-application-import-control.xml");
        createFile(workingDirectory.getPath(), "checkstyle-domain-import-control.xml");
        createFile(workingDirectory.getPath(), "checkstyle-interface-import-control.xml");
    }

    private void createFile(String workingDirectoryPath, String filename) {
        getLog().info("create file :" + filename + ".");
        File file = new File(workingDirectoryPath, filename);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    getLog().error("Create file:" + filename + " failed.");
                    return;
                }
            } catch (IOException e) {
                getLog().error("Create file:" + filename + " failed.");
            }
        }
        if (file.canWrite()) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream(filename);
                 OutputStream out = new FileOutputStream(file)) {
                IOUtils.copy(in, out);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            getLog().info("Write file :" + filename + " succeed.");
        } else {
            getLog().info("Write file :" + filename + " failed.");
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
}
