package soya.framework.dovetails.maven;

import org.apache.maven.project.MavenProject;
import soya.framework.dovetails.ExternalContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MavenContextWrapper implements ExternalContext {

    private MavenProject project;
    private Properties properties = new Properties();

    private MavenContextWrapper(MavenProject project) {
        this.project = project;
    }

    @Override
    public File getBaseDir() {
        return project.getBasedir();
    }

    @Override
    public String getProperty(String propName) {
        return properties.getProperty(propName);
    }

    @Override
    public <T> T getService(Class<T> type) {
        return null;
    }

    @Override
    public <T> T getService(String name, Class<T> type) {
        return null;
    }

    public static MavenContextBuilder builder(MavenProject project) {
        return new MavenContextBuilder(project);
    }

    static class MavenContextBuilder {
        private MavenProject project;
        private Map<String, BeanBuilder> beanBuilders = new HashMap<>();

        private MavenContextBuilder(MavenProject project) {
            this.project = project;
        }

        public BeanBuilder beanBuilder(String name, Class<?> type) {
            return new BeanBuilder(name, type, this);
        }

        public MavenContextWrapper create() {
            MavenContextWrapper exc = new MavenContextWrapper(project);
            return exc;
        }

    }

    static class BeanBuilder {
        private MavenContextBuilder contextBuilder;
        private String name;
        private Class<?> type;

        private BeanBuilder(String name, Class<?> type, MavenContextBuilder contextBuilder) {
            this.contextBuilder = contextBuilder;
            this.name = name;
            this.type = type;
        }

        public BeanBuilder next(String name, Class<?> type) {
            contextBuilder.beanBuilders.put(this.name, this);
            return new BeanBuilder(name, type, contextBuilder);
        }
    }
}
