package soya.framework.dovetails.batch.server;

import java.util.UUID;

public abstract class SecurityService {
    private String secretKey;

    protected SecurityService() {
    }

    public void refresh() {
        System.out.println("--------------------- refreshing the security key: " + keygen());
    }

    private String keygen() {
        return UUID.randomUUID().toString();
    }
}
