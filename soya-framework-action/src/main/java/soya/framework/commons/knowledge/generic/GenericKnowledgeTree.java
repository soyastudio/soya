package soya.framework.commons.knowledge.generic;

import com.google.gson.Gson;
import org.apache.commons.lang3.RandomStringUtils;
import soya.framework.commons.knowledge.KnowledgeNode;
import soya.framework.commons.knowledge.KnowledgeTree;
import soya.framework.commons.knowledge.KnowledgeTreeNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GenericKnowledgeTree<K, T> implements KnowledgeTree<K, T>, KnowledgeNode<K> {

    private DefaultKnowledgeNode<K> knowledgeNode;
    private KnowledgeTreeNode<T> root;
    private Map<String, KnowledgeTreeNode<T>> treeNodeMap;

    protected GenericKnowledgeTree(K knowledge, DefaultTreeNode<T> root) {
        this.knowledgeNode = new DefaultKnowledgeNode<>(knowledge);
        this.root = root;
        this.treeNodeMap = new LinkedHashMap<>();
        treeNodeMap.put(root.path, root);
    }

    @Override
    public KnowledgeTreeNode<T> root() {
        return root;
    }

    @Override
    public KnowledgeTreeNode<T> create(KnowledgeTreeNode<T> parent, Object data) {
        DefaultTreeNode node = new DefaultTreeNode(parent, data);
        while (treeNodeMap.containsKey(node.path)) {
            node = new DefaultTreeNode(parent, RandomStringUtils.randomAlphanumeric(10), data);
        }

        treeNodeMap.put(node.path, node);
        return node;
    }

    @Override
    public KnowledgeTreeNode<T> create(KnowledgeTreeNode<T> parent, String name, Object data) {
        DefaultTreeNode node = new DefaultTreeNode(parent, name, data);
        treeNodeMap.put(node.path, node);
        return node;
    }

    @Override
    public boolean contains(String path) {
        return treeNodeMap.containsKey(path);
    }

    public KnowledgeTreeNode<T> get(String path) {
        return treeNodeMap.get(path);
    }

    public Iterator<String> paths() {
        return treeNodeMap.keySet().iterator();
    }

    public Iterator<KnowledgeTreeNode<T>> nodes() {
        return treeNodeMap.values().iterator();
    }

    public static <K, T> GenericKnowledgeTree<K, T> newInstance(K k, T t) {
        return new GenericKnowledgeTree<>(k, new DefaultTreeNode<T>(null, t));
    }

    public static <K, T> GenericKnowledgeTree<K, T> newInstance(K k, String name, T t) {
        return new GenericKnowledgeTree<>(k, new DefaultTreeNode<T>(null, name, t));
    }

    @Override
    public K origin() {
        return knowledgeNode.origin();
    }

    @Override
    public void annotate(String namespace, Object annotation) {
        knowledgeNode.annotate(namespace, annotation);
    }

    @Override
    public Object getAnnotation(String namespace) {
        return knowledgeNode.getAnnotation(namespace);
    }

    @Override
    public <A> A getAnnotation(String namespace, Class<A> annotationType) {
        return knowledgeNode.getAnnotation(namespace, annotationType);
    }

    static class DefaultTreeNode<T> implements KnowledgeTreeNode<T> {

        private KnowledgeTreeNode parent;
        private List<KnowledgeTreeNode> children = new ArrayList<>();

        private String name;
        private String path;

        private DefaultKnowledgeNode<T> defaultKnowledgeNode;
        private Map<String, Object> annotations = new LinkedHashMap<>();

        protected DefaultTreeNode(KnowledgeTreeNode parent, T data) {
            this.parent = parent;
            this.name = RandomStringUtils.randomAlphanumeric(10);
            this.defaultKnowledgeNode = new DefaultKnowledgeNode<>(data);

            if (parent != null) {
                parent.getChildren().add(this);
                this.path = parent.getPath() + "/" + name;
            } else {
                this.path = name;
            }
        }

        protected DefaultTreeNode(KnowledgeTreeNode parent, String name, T data) {
            this.parent = parent;
            this.name = name;
            this.defaultKnowledgeNode = new DefaultKnowledgeNode<>(data);

            if (parent != null) {
                parent.getChildren().add(this);
                this.path = parent.getPath() + "/" + name;
            } else {
                this.path = name;
            }
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public KnowledgeTreeNode getParent() {
            return parent;
        }

        @Override
        public List<KnowledgeTreeNode> getChildren() {
            return children;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public KnowledgeNode<T> getData() {
            return defaultKnowledgeNode;
        }

        @Override
        public T origin() {
            return defaultKnowledgeNode.origin();
        }

        @Override
        public void annotate(String namespace, Object annotation) {
            defaultKnowledgeNode.annotate(namespace, annotation);
        }

        @Override
        public Object getAnnotation(String namespace) {
            return defaultKnowledgeNode.getAnnotation(namespace);
        }

        @Override
        public <A> A getAnnotation(String namespace, Class<A> annotationType) {
            return defaultKnowledgeNode.getAnnotation(namespace, annotationType);
        }
    }

    static class DefaultKnowledgeNode<T> implements KnowledgeNode<T> {
        private static Gson gson = new Gson();

        private final T origin;
        protected Map<String, Object> annotations = new ConcurrentHashMap<>();

        protected DefaultKnowledgeNode(T origin) {
            this.origin = origin;
        }

        public T origin() {
            return origin;
        }

        @Override
        public void annotate(String namespace, Object annotation) {
            if (annotation == null) {
                annotations.remove(namespace);
            } else {
                annotations.put(namespace, annotation);
            }
        }

        @Override
        public Object getAnnotation(String namespace) {
            return annotations.get(namespace);
        }

        @Override
        public <A> A getAnnotation(String namespace, Class<A> annotationType) {
            if (!annotations.containsKey(namespace)) {
                return null;
            }

            Object value = annotations.get(namespace);

            return (A) value;
        }
    }

}
