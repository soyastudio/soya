package soya.framework.tasks.markdown;

import java.util.EventListener;

public interface MarkdownEventListener extends EventListener {

    void onEvent(MarkdownEvent event);
}
