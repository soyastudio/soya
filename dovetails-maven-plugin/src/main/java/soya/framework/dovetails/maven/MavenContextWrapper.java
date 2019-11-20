package soya.framework.dovetails.maven;

import org.apache.maven.project.MavenProject;
import soya.framework.dovetails.ExternalContext;

import java.io.File;
import java.util.Properties;

public class MavenContextWrapper implements ExternalContext {

    private MavenProject project;
    private Properties properties = new Properties();

    public MavenContextWrapper(MavenProject project) {
        this.project = project;
    }

    @Override
    public File getBaseDir() {
        return project.getBasedir();
    }

    @Override
    public <T> T getResource(String name, Class<T> type) {
        return null;
    }

    @Override
    public String getProperty(String propName) {
        return properties.getProperty(propName);
    }
}
