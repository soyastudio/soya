package soya.framework.transform.schema.support;

import soya.framework.transform.schema.Annotatable;
import soya.framework.transform.schema.KnowledgeTree;
import soya.framework.transform.schema.KnowledgeTreeNode;
import soya.framework.transform.schema.Tree;

import java.util.*;

public class MoneyTree<K, T> extends Feature<K> implements KnowledgeTree<K, T> {

    private KnowledgeTreeNode root;
    private Map<String, KnowledgeTreeNode<T>> treeNodeMap;

    protected MoneyTree(K knowledge, DefaultTreeNode<T> root) {
        super(knowledge);
        this.root = root;
        this.treeNodeMap = new LinkedHashMap<>();
        treeNodeMap.put(root.path, root);

    }

    @Override
    public KnowledgeTreeNode<T> root() {
        return root;
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

    @Override
    public Set<KnowledgeTreeNode<T>> find(Selector selector) {
        return selector.select();
    }

    @Override
    public Tree filterIn(Selector selector) {
        return null;
    }

    @Override
    public Tree filterOut(Selector selector) {
        return null;
    }

    public static <K, T> MoneyTree<K, T> newInstance(K k, String name, T t) {
        return new MoneyTree<>(k, new DefaultTreeNode<T>(null, name, t));
    }
/*
    public static <K, T> KnowledgeTreeBuilder<K, T> builder() {
        return new MoneyTreeBuilder<K, T>();
    }


    static class MoneyTreeBuilder<K, T> implements KnowledgeTreeBuilder<K, T> {
        private K knowledgeBase;
        private KnowledgeDigester<K, T> knowledgeDigester;

        private MoneyTreeBuilder() {
        }

        @Override
        public KnowledgeTreeBuilder<K, T> knowledgeBase(K knowledgeBase) {
            this.knowledgeBase = knowledgeBase;
            return this;
        }

        @Override
        public KnowledgeTreeBuilder<K, T> knowledgeDigester(KnowledgeDigester<K, T> digester) {
            this.knowledgeDigester = digester;
            return this;
        }

        @Override
        public KnowledgeTree<K, T> create() {
            return new MoneyTree(knowledgeBase, (DefaultTreeNode) knowledgeDigester.digester(knowledgeBase));
        }
    }
*/

    static class DefaultTreeNode<T> implements KnowledgeTreeNode<T> {

        private KnowledgeTreeNode parent;
        private List<KnowledgeTreeNode> children = new ArrayList<>();

        private String name;
        private String path;

        private DataWrapper<T> dataWrapper;
        private Map<String, Object> annotations = new LinkedHashMap<>();

        protected DefaultTreeNode(KnowledgeTreeNode parent, String name, T data) {
            this.parent = parent;
            this.name = name;
            this.dataWrapper = new DataWrapper<>(data);

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
        public Annotatable<T> getData() {
            return dataWrapper;
        }

        @Override
        public T origin() {
            return dataWrapper.origin();
        }

        @Override
        public void annotate(String namespace, Object annotation) {
            dataWrapper.annotate(namespace, annotation);
        }

        @Override
        public Object getAnnotation(String namespace) {
            return dataWrapper.getAnnotation(namespace);
        }

        @Override
        public <A> A getAnnotation(String namespace, Class<A> annotationType) {
            return dataWrapper.getAnnotation(namespace, annotationType);
        }
    }

    static class DataWrapper<T> extends Feature<T> {
        protected DataWrapper(T origin) {
            super(origin);
        }
    }

}
