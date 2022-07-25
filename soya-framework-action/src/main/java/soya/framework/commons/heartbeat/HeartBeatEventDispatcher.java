package soya.framework.commons.heartbeat;

import soya.framework.commons.eventbus.Event;

import java.net.URI;

public class HeartBeatEventDispatcher implements Heartbeat.HeartbeatListener {
    @Override
    public void onEvent(Heartbeat.HeartbeatEvent event) {
        Event.builder(URI.create("heartbeat://" + event.getName()), event).create();
    }
}
