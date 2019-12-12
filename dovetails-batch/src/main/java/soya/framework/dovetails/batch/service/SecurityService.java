package soya.framework.dovetails.batch.service;

import java.io.File;

public abstract class SecurityService {
    public static String SECURITY_KEY_FILE = "security.key";

    private File file;

    protected SecurityService(File file) {
        this.file = file;
    }

    public void refresh() {
        System.out.println("------------------- refresh security key... ");
    }
}
