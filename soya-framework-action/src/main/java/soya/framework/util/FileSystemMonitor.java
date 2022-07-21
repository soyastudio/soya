package soya.framework.util;

public class FileSystemMonitor implements Heartbeat.HeartbeatListener {

    @Override
    public void onEvent(Heartbeat.HeartbeatEvent event) {
        System.out.println("============================ scan...");

    }
}
