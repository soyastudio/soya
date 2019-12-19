package soya.framework.dovetails.batch.server;

import com.google.common.io.ByteStreams;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GithubService {

    private final String uri;
    private final File directory;
    private final File workspace;

    private Repository repository;
    private Map<String, String> checkouts = new ConcurrentHashMap<>();

    protected GithubService(String uri, File directory, File workspace) {
        this.uri = uri;
        this.directory = directory;
        this.workspace = workspace;

        try {
            File gitMeta = new File(directory, ".git");
            if (!gitMeta.exists()) {
                Git git = Git.cloneRepository()
                        .setURI(uri)
                        .setDirectory(directory)
                        .call();
            }

            FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            repository = repositoryBuilder.setGitDir(gitMeta)
                    .readEnvironment() // scan environment GIT_* variables
                    .findGitDir() // scan up the file system tree
                    .setMustExist(true)
                    .build();

        } catch (GitAPIException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkOut(String user, String path) {
        File source = new File(directory, path);

        File userDir = new File(workspace, user);
        if (!userDir.exists()) {
            userDir.mkdir();
        }

        File dest = new File(userDir, path);
        if (!dest.exists()) {
            if (dest.isDirectory()) {
                copyFolder(source, dest);

            } else {
                File parent = dest.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    dest.createNewFile();
                    InputStream is = new FileInputStream(source);
                    OutputStream os = new FileOutputStream(dest);
                    ByteStreams.copy(is, os);
                    os.close();
                    is.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (Exception e) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void addNew(String user, String path, boolean checkout) {
        File source = new File(directory, path);
        if (source.exists()) {
            throw new IllegalStateException("File '" + path + "' already exist");
        }

        File parent = source.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
            try {
                source.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (checkout) {
            checkOut(user, path);
        }
    }

    public void saveContents(String user, String path, String contents) {
        File userDir = new File(workspace, user);
        if (!userDir.exists()) {
            userDir.mkdir();
        }

        File dest = new File(userDir, path);
        if (dest.exists()) {
            try {
                dest.createNewFile();
                InputStream is = new ByteArrayInputStream(contents.getBytes());
                OutputStream os = new FileOutputStream(dest);
                ByteStreams.copy(is, os);
                os.close();
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkIn(String user, String path) {

        File source = new File(directory, path);

        File userDir = new File(workspace, user);
        if (!userDir.exists()) {
            userDir.mkdir();
        }

        File dest = new File(userDir, path);
        if (dest.exists()) {
            try {
                dest.createNewFile();
                InputStream is = new FileInputStream(dest);
                OutputStream os = new FileOutputStream(source);
                ByteStreams.copy(is, os);
                os.close();
                is.close();

                File parent = dest.getParentFile();
                FileUtils.forceDelete(dest);
                while (parent.listFiles().length == 0) {
                    File forDelete = parent;
                    parent = parent.getParentFile();
                    FileUtils.forceDelete(forDelete);

                    if (parent.equals(workspace)) {
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanWorkspace(String user) {
        File dir = new File(workspace, user);
        if (dir.exists()) {
            try {
                FileUtils.forceDelete(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearWorkspace() {
        File[] dirs = workspace.listFiles();
        for (File dir : dirs) {
            if (dir.exists() && dir.isDirectory()) {
                try {
                    FileUtils.forceDelete(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void importToWorkspace(String user, String path, String uri) {
        File dir = new File(workspace, user);
        dir = new File(dir, path);

        dir.mkdirs();
        try {
            Git.cloneRepository()
                    .setURI(uri)
                    .setDirectory(dir)
                    .call();
            File gitMeta = new File(dir, ".git");
            FileUtils.forceDelete(gitMeta);

        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
        }
    }

    public void commit() {

    }

}
