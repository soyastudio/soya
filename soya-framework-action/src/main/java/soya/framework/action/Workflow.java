package soya.framework.action;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Workflow {

    private static Map<URI, Workflow> workflows = new ConcurrentHashMap<>();

    private final URI uri;
    private final Map<String, Node> nodeMap;

    private Workflow(URI uri, List<Node> nodes) {
        this.uri = uri;
        Map<String, Node> map = new HashMap<>();
        nodes.forEach(n -> {
            if(map.containsKey(n.name)) {
                throw new IllegalArgumentException("Node is already defined: " + n.name);
            }
            map.put(n.name, n);
        });
        this.nodeMap = Collections.unmodifiableMap(map);
    }

    public void onEvent(ActionEvent event) {
        if(workflows.containsKey(event.getUri())) {
            ActionContext.getInstance().getExecutorService().execute(new ActionEventHandler(event));
        } else {
            // DeadEvent
        }
    }

    public static void post(ActionEvent event) {
        workflows.get(event.getUri()).onEvent(event);
    }

    public static Builder builder() {
        return new Builder();
    }

    static class Node {
        private final String name;
        private final URI action;
        private final int startPoint;
        private final ResultEvaluator evaluator;

        public Node(String name, URI action, int startPoint, ResultEvaluator evaluator) {
            this.name = name;
            this.action = action;
            this.startPoint = startPoint;
            this.evaluator = evaluator;
        }
    }

    interface ResultEvaluator {
        int evaluate();
    }

    static class ActionEventHandler implements Runnable {
        private final ActionEvent event;

        ActionEventHandler(ActionEvent event) {
            this.event = event;
        }

        @Override
        public void run() {
            Session session = new Session(event);

        }
    }

    static class Session {
        private ActionEvent event;
        Session(ActionEvent event) {
            this.event = event;
        }
    }

    public static class NodeBuilder {
        private final Builder builder;

        private String name;
        private URI action;
        private int startPoint;
        private ResultEvaluator evaluator;

        private NodeBuilder(Builder builder) {
            this.builder = builder;
        }

        public NodeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public NodeBuilder action(URI action) {
            this.action = action;
            return this;
        }

        public NodeBuilder startPoint(int startPoint) {
            this.startPoint = startPoint;
            return this;
        }

        public NodeBuilder evaluator(ResultEvaluator evaluator) {
            this.evaluator = evaluator;
            return this;
        }

        public Builder create() {
            builder.nodes.add(new Node(name, action, startPoint, evaluator));
            return builder;
        }
    }

    public static class Builder {
        private List<Node> nodes = new ArrayList<>();

        private Builder() {
        }

        public NodeBuilder node() {
            return new NodeBuilder(this);
        }

        public URI create(URI uri) {
            Workflow workflow = new Workflow(uri, nodes);
            workflows.put(workflow.uri, workflow);
            return workflow.uri;
        }

    }

}
