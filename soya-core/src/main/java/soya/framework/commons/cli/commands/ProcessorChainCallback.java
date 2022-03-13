package soya.framework.commons.cli.commands;

import soya.framework.commons.cli.Flow;

import java.util.ArrayList;
import java.util.List;

public abstract class ProcessorChainCallback<T> implements Flow.Callback {

    private T context;
    private List<Processor<T>> chain = new ArrayList<>();

    protected ProcessorChainCallback() {
    }

    protected ProcessorChainCallback<T> processor(Processor<T> processor) {
        chain.add(processor);
        return this;
    }

    @Override
    public void onSuccess(Flow.Session session) throws Exception {
        this.context = init(session);
        chain.forEach(e -> {
            invoke(e, context, session);
        });

    }

    protected void invoke(Processor<T> processor, T ctx, Flow.Session session) {
        processor.process(ctx, session);
    }

    protected abstract T init(Flow.Session session);

    public interface Processor<T> {
        void process(T ctx, Flow.Session session);
    }
}
