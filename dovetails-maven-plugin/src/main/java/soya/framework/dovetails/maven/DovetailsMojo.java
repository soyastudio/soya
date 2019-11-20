package soya.framework.dovetails.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import soya.framework.dovetails.Dovetail;
import soya.framework.dovetails.ExternalContext;
import soya.framework.dovetails.support.DefaultDovetail;
import soya.framework.dovetails.support.DefaultTaskFlowController;

import java.io.File;
import java.io.FileInputStream;

@Mojo(name = "run", threadSafe = true, defaultPhase = LifecyclePhase.INSTALL)
public class DovetailsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter()
    private PlexusConfiguration configuration;

    @Parameter(defaultValue = "${project.basedir}/dovetails.yaml", property = "conf")
    private File yamlFile;

    @Parameter(defaultValue = "${project.basedir}/target/generated-sources/dovetails")
    private File destination;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            DefaultTaskFlowController defaultTaskFlowController = DefaultTaskFlowController.builder().create();
            ExternalContext externalContext = new MavenContextWrapper(mavenProject);
            Dovetail dovetail = new DefaultDovetail(new FileInputStream(yamlFile), externalContext, defaultTaskFlowController);

            dovetail.run();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MojoFailureException(e.getMessage());
        }
    }
}
