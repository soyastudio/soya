package soya.framework.commons.cli.commands;

import org.apache.commons.io.FileUtils;
import soya.framework.commons.cli.Flow;
import soya.framework.commons.cli.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DefaultFileSystemProcessChain extends FileSystemProcessChain {

    private DefaultFileSystemProcessChain(String baseDir) {
        super(baseDir);
    }

    public static DefaultFileSystemProcessChain instance(String baseDir) {
        return new DefaultFileSystemProcessChain(baseDir);
    }

    public DefaultFileSystemProcessChain mkdir(String path) {
        processor(new Mkdir(path));
        return this;
    }

    public DefaultFileSystemProcessChain createFile(String path, String source, boolean overwrite) {
        processor(new CreateFile(path, source, overwrite));
        return this;
    }

    public DefaultFileSystemProcessChain copyDir(String source, String dest) {
        processor(new CopyDir(source, dest));
        return this;
    }

    public DefaultFileSystemProcessChain copyFile(String source, String dest) {
        processor(new CopyFile(source, dest));
        return this;
    }

    public DefaultFileSystemProcessChain zip(String src, String dest) {
        processor(new Zip(src, dest));
        return this;
    }

    public DefaultFileSystemProcessChain unzip(String zipFile, String destDir) {
        processor(new Unzip(zipFile, destDir));
        return this;
    }

    public DefaultFileSystemProcessChain delete(String path) {
        processor(new Delete(path));
        return this;
    }

    static class Mkdir implements FileSystemProcessor {
        private String path;

        Mkdir(String path) {
            this.path = path;
        }

        @Override
        public void process(File base, Flow.Session session) {
            String dir = Resources.evaluate(path, session.properties());
            File directory = new File(base, dir);
            directory.mkdirs();

        }
    }

    static class CreateFile implements FileSystemProcessor {
        private String fileName;
        private String source;
        private boolean overwrite;

        CreateFile(String fileName, String source, boolean overwrite) {
            this.fileName = fileName;
            this.source = source;
            this.overwrite = overwrite;
        }

        @Override
        public void process(File base, Flow.Session session) {
            String path = Resources.evaluate(fileName, session.properties());
            String contents = (String) session.getResult(source);

            try {
                File file = new File(base, path);
                if(!file.exists()) {
                    file.createNewFile();
                }

                FileUtils.write(file, contents, Charset.defaultCharset());

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    static class CopyDir implements FileSystemProcessor {
        private String source;
        private String destination;

        protected CopyDir(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public void process(File base, Flow.Session session) {
            String srcPath = Resources.evaluate(source, session.properties());
            File src = new File(base, srcPath);

            String destPath = Resources.evaluate(destination, session.properties());
            File dest = new File(base, destPath);

            try {
                FileUtils.copyDirectory(src, dest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class CopyFile implements FileSystemProcessor {
        private String source;
        private String destination;

        protected CopyFile(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public void process(File base, Flow.Session session) {
            String srcPath = Resources.evaluate(source, session.properties());
            File src = new File(base, srcPath);

            String destPath = Resources.evaluate(destination, session.properties());
            File dest = new File(base, destPath);

            try {
                FileUtils.copyFile(src, dest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class Delete implements FileSystemProcessor {
        private String path;

        Delete(String path) {
            this.path = path;
        }

        @Override
        public void process(File base, Flow.Session session) {
            File file = new File(base, Resources.evaluate(path, session.properties()));
            if (file.exists()) {
                try {
                    if (file.isFile()) {
                        FileUtils.forceDelete(file);

                    } else if (file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class Zip extends AbstractZipProcessor {
        protected Zip(String source, String target) {
            super(source, target);
        }

        @Override
        protected void process(File src, File dest) {
            List<String> fileList = getFileList(src);
            try (FileOutputStream fos = new FileOutputStream(dest);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                for (String filePath : fileList) {
                    String name = filePath.substring(src.getAbsolutePath().length() + 1);

                    ZipEntry zipEntry = new ZipEntry(name);
                    zos.putNextEntry(zipEntry);
                    if (!name.endsWith("/")) {
                        // It is empty directory if end with "/".
                        // Read file content and write to zip output stream.
                        try (FileInputStream fis = new FileInputStream(filePath)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, length);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // Close the zip entry.
                    zos.closeEntry();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private List<String> getFileList(File directory) {
            List<String> fileList = new ArrayList<>();

            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file.getAbsolutePath());
                    } else {
                        fileList.addAll(getFileList(file));
                    }
                }
            } else {
                fileList.add(directory.getAbsolutePath() + "/");
            }

            return fileList;

        }
    }

    static class Unzip extends AbstractZipProcessor {

        protected Unzip(String source, String target) {
            super(source, target);
        }

        @Override
        protected void process(File src, File dest) {
            if (!dest.exists()) {
                dest.mkdirs();
            }

            try {
                byte[] buffer = new byte[1024];
                ZipInputStream zis = new ZipInputStream(new FileInputStream(src));
                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    File newFile = newFile(dest, zipEntry);
                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        // fix for Windows-created archives
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("Failed to create directory " + parent);
                        }

                        // write file content
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                    zipEntry = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
            File destFile = new File(destinationDir, zipEntry.getName());

            String destDirPath = destinationDir.getCanonicalPath();
            String destFilePath = destFile.getCanonicalPath();

            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
            }

            return destFile;
        }
    }

    static abstract class AbstractZipProcessor implements FileSystemProcessor {
        protected String source;
        protected String target;

        protected AbstractZipProcessor(String source, String target) {
            this.source = source;
            this.target = target;
        }

        @Override
        public void process(File base, Flow.Session session) {
            String srcPath = Resources.evaluate(source, session.properties());
            File src = new File(base, srcPath);

            String destPath = Resources.evaluate(target, session.properties());
            File dest = new File(base, destPath);

            process(src, dest);

        }

        protected abstract void process(File src, File dest);

    }

}
