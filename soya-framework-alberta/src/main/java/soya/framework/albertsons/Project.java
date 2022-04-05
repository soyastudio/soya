package soya.framework.albertsons;

public class Project {

    private String name;
    private String[] source;
    private String[] consumer;

    private String application;
    private Flow[] flows;

    private String egNumber;

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getSource() {
        return source;
    }

    public void setSource(String[] source) {
        this.source = source;
    }

    public String[] getConsumer() {
        return consumer;
    }

    public void setConsumer(String[] consumer) {
        this.consumer = consumer;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Flow[] getFlows() {
        return flows;
    }

    public void setFlows(Flow[] flows) {
        this.flows = flows;
    }

    public String getEgNumber() {
        return egNumber;
    }

    public void setEgNumber(String egNumber) {
        this.egNumber = egNumber;
    }

    public static class Flow {
        private String name;
        private String namespace;

        private Destination[] input;
        private Destination[] output;
        private Node[] nodes;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public Destination[] getInput() {
            return input;
        }

        public void setInput(Destination[] input) {
            this.input = input;
        }

        public Destination[] getOutput() {
            return output;
        }

        public void setOutput(Destination[] output) {
            this.output = output;
        }

        public Node[] getNodes() {
            return nodes;
        }

        public void setNodes(Node[] nodes) {
            this.nodes = nodes;
        }
    }

    public static class Destination {
        private String name;
        private String namespace;
        private String location;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public static class Node {
        private String name;
        private String namespace;
        private Property[] properties;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public Property[] getProperties() {
            return properties;
        }

        public void setProperties(Property[] properties) {
            this.properties = properties;
        }
    }

    public static class Property {
        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
