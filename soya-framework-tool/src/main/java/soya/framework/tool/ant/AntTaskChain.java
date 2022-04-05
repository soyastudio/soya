package soya.framework.tool.ant;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.*;
import soya.framework.commons.cli.Flow;
import soya.framework.commons.cli.Resources;
import soya.framework.commons.cli.commands.FileSystemProcessChain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class AntTaskChain extends FileSystemProcessChain {

    protected AntTaskChain(String base) {
        super(base);
    }

    public static AntTaskChain instance(String base) {
        return new AntTaskChain(base);
    }

    public AntTaskChain addTask(AntTaskProcessor processor) {
        super.processor(processor);
        return this;
    }

    public AntTaskChain echo(String message) {
        processor(new EchoProcessor(message));
        return this;
    }

    public AntTaskChain copyFile(String src, String dest, boolean overwrite) {
        processor(new CopyFile(src, dest, overwrite));
        return this;
    }

    public AntTaskChain copyDir(String src, String dest, boolean overwrite) {
        processor(new CopyDir(src, dest, overwrite));
        return this;
    }

    public AntTaskChain copyDirIncludes(String src, String dest, boolean overwrite, String includes) {
        processor(new CopyDir(src, dest, overwrite, includes, null));
        return this;
    }

    public AntTaskChain copyDirExcludes(String src, String dest, boolean overwrite, String excludes) {
        processor(new CopyDir(src, dest, overwrite, null, excludes));
        return this;
    }

    public AntTaskChain delete() {
        processor(new DeleteProcessor());
        return this;
    }

    public AntTaskChain deleteIncludes() {
        processor(new DeleteProcessor());
        return this;
    }

    public AntTaskChain deleteExcludes() {
        processor(new DeleteProcessor());
        return this;
    }

    public AntTaskChain mkdir(String dir) {
        processor(new MkdirProcessor(dir));
        return this;
    }

    public AntTaskChain zip() {
        processor(new ZipProcessor());
        return this;
    }

    public static abstract class AntTaskProcessor<T extends Task> extends AntTask<T> implements FileSystemProcessor {
        private File ctx;
        private Flow.Session session;

        @Override
        public void process(File ctx, Flow.Session session) {
            this.ctx = ctx;
            this.session = session;
            init();

            invoke();
        }

        @Override
        protected void init() {
            init(task, ctx, session);
        }

        protected abstract void init(T task, File ctx, Flow.Session session);
    }

    static class EchoProcessor extends AntTaskProcessor<Echo> {
        private String message;

        EchoProcessor(String message) {
            this.message = message;
        }

        @Override
        protected void init(Echo task, File baseDir, Flow.Session session) {
            String msg = message;
            if (message.startsWith("/")) {
                File file = new File(baseDir, Resources.evaluate(message, session.properties()));
                try {
                    msg = IOUtils.toString(new FileInputStream(file), Charset.defaultCharset());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                msg = Resources.evaluate(msg, session.properties());
            }

            task.setMessage(msg);
        }
    }

    static class CopyFile extends AntTaskProcessor<Copy> {
        private String src;
        private String dir;
        private boolean overwrite;

        public CopyFile(String src, String dir, boolean overwrite) {
            this.src = src;
            this.dir = dir;
            this.overwrite = overwrite;
        }


        @Override
        protected void init(Copy task, File ctx, Flow.Session session) {

        }
    }

    static class CopyDir extends AntTaskProcessor<Copy> {
        private String srcDir;
        private String destDir;
        private boolean overwrite;

        private String includes;
        private String excludes;

        CopyDir(String srcDir, String destDir, boolean overwrite) {
            this.srcDir = srcDir;
            this.destDir = destDir;
            this.overwrite = overwrite;
        }

        CopyDir(String srcDir, String destDir, boolean overwrite, String includes, String excludes) {
            this.srcDir = srcDir;
            this.destDir = destDir;
            this.overwrite = overwrite;
            this.includes = includes;
            this.excludes = excludes;
        }

        @Override
        protected void init(Copy task, File ctx, Flow.Session session) {

        }
    }

    static class DeleteProcessor extends AntTaskProcessor<Delete> {

        @Override
        protected void init(Delete task, File ctx, Flow.Session session) {

        }
    }

    static class MkdirProcessor extends AntTaskProcessor<Mkdir> {
        private String dir;

        MkdirProcessor(String dir) {
            this.dir = dir;
        }

        @Override
        protected void init(Mkdir task, File ctx, Flow.Session session) {
            String path = Resources.evaluate(dir, session.properties());
            task.setDir(new File(ctx, path));
        }
    }

    static class ZipProcessor extends AntTaskProcessor<Zip> {


        @Override
        protected void init(Zip task, File ctx, Flow.Session session) {

        }
    }


}
