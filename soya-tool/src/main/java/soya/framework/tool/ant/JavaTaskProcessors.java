package soya.framework.tool.ant;

import com.sun.tools.xjc.XJC2Task;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Ear;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.War;
import soya.framework.commons.cli.Flow;
import soya.framework.commons.cli.Resources;

import java.io.File;
import java.net.MalformedURLException;

public class JavaTaskProcessors {

    public static JavaTaskProcessor<? extends Task> jar() {
        return new JarProcessor();
    }

    public static JavaTaskProcessor<? extends Task> war() {
        return new WarProcessor();
    }

    public static JavaTaskProcessor<? extends Task> ear() {
        return new EarProcessor();
    }

    public static JavaTaskProcessor<? extends Task> xjc(String schema, String target, String pkg) {
        return new XJC2Processor(schema, target, pkg);
    }

    abstract static class JavaTaskProcessor<T extends Task> extends AntTaskChain.AntTaskProcessor<T> {
    }

    static class JarProcessor extends JavaTaskProcessor<Jar> {

        @Override
        protected void init(Jar task, File ctx, Flow.Session session) {

        }
    }

    static class WarProcessor extends JavaTaskProcessor<War> {

        @Override
        protected void init(War task, File ctx, Flow.Session session) {

        }
    }

    static class EarProcessor extends JavaTaskProcessor<Ear> {

        @Override
        protected void init(Ear task, File ctx, Flow.Session session) {

        }
    }

    static class XJC2Processor extends JavaTaskProcessor<XJC2Task> {
        private String schema;
        private String dest;
        private String packageName;

        XJC2Processor(String schema, String dest, String packageName) {
            this.schema = schema;
            this.dest = dest;
            this.packageName = packageName;
        }

        @Override
        protected void init(XJC2Task task, File ctx, Flow.Session session) {
            File sch = new File(ctx, Resources.evaluate(schema, session.properties()));
            File dir = new File(ctx, Resources.evaluate(dest, session.properties()));
            if(!dir.exists()) {
                dir.mkdirs();
            }

            task.setPackage(Resources.evaluate(packageName, session.properties()));
            task.setDestdir(dir);

            try {
                task.setSchema(sch.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
