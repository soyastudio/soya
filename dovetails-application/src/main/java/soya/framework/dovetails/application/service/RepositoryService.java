package soya.framework.dovetails.application.service;

import java.io.File;

public abstract class RepositoryService {
    protected static RepositoryService instance;

    private File home;

    protected RepositoryService(File home) {
        this.home = home;
    }

    public void refresh() {
        System.out.println("---------------- refresh repository: " + home.exists());
        File[] files = home.listFiles();
        for (File dir : files) {
            System.out.println("---------------- refresh repository: " + dir.getName());
        }
    }

    public static RepositoryService getInstance() {
        return instance;
    }
}
