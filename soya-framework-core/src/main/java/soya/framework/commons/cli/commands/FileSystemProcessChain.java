package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Flow;
import soya.framework.commons.cli.Resources;

import java.io.File;

public class FileSystemProcessChain extends ProcessorChainCallback<File> {

    private String baseDir;

    public FileSystemProcessChain(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    protected File init(Flow.Session session) {
        String basePath = Resources.evaluate(baseDir, session.properties());
        File base = new File(basePath);
        return base;
    }

    public FileSystemProcessChain addProcessor(FileSystemProcessor processor) {
        processor(processor);
        return this;
    }

    public interface FileSystemProcessor extends ProcessorChainCallback.Processor<File> {
    }
}
