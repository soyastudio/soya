package soya.framework.dovetails.batch.server;

import com.google.common.io.ByteStreams;
import org.apache.tomcat.util.http.fileupload.FileUtils;
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

                FileUtils.forceDelete(dest);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void commit() {

    }

}
