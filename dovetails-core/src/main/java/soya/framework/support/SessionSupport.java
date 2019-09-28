package soya.framework.support;

import soya.framework.DataObject;
import soya.framework.Evaluation;
import soya.framework.Session;
import soya.framework.util.GsonUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SessionSupport implements Session {

    private final String id;
    private final long createdTime;

    private DataObject currentState;
    private Map<String, ObjectWrapper> attributes = new ConcurrentHashMap<>();

    private long lastUpdatedTime;

    private transient Evaluation evaluation;

    protected SessionSupport() {
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
        this.lastUpdatedTime = System.currentTimeMillis();

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public Object get(String attrName) {
        if (!attributes.containsKey(attrName)) {
            return null;

        } else {
            return attributes.get(attrName).getWrapperedObject();
        }
    }

    @Override
    public synchronized void set(String attrName, Object attrValue) throws IllegalStateException {
        if (attributes.containsKey(attrName) && attributes.get(attrName).immutable) {
            throw new IllegalStateException("Attribute '" + attrName + "' is immutable.");

        } else if (attrValue == null) {
            attributes.remove(attrName);

        } else {
            attributes.put(attrName, new ObjectWrapper(attrValue));

        }
    }

    @Override
    public synchronized void setImmutable(String attrName, Object attrValue) throws IllegalStateException {
        if (attributes.containsKey(attrName) && attributes.get(attrName).immutable) {
            throw new IllegalStateException("Attribute '" + attrName + "' is immutable.");

        } else if (attrValue == null) {
            throw new IllegalArgumentException("Immutable attribute cannot be null.");

        } else if (attrValue instanceof Cloneable) {
            throw new IllegalArgumentException("Immutable attribute should be cloneable.");

        } else {
            attributes.put(attrName, new ObjectWrapper(attrValue, true));
        }
    }

    @Override
    public DataObject getCurrentState() {
        return currentState;
    }

    @Override
    public synchronized void updateState(DataObject state) throws IllegalStateException {
        if (evaluation != null) {
            throw new IllegalStateException("Illegal state: session is in evaluation state.");
        }

        this.currentState = state;
        this.lastUpdatedTime = System.currentTimeMillis();
    }

    @Override
    public long getLastUpdatedTime() {
        return this.lastUpdatedTime;
    }

    @Override
    public void startEvaluation() {
        if (evaluation != null) {
            throw new IllegalStateException("Evaluation is already started.");
        }

        this.evaluation = new DefaultEvaluation(this);
    }

    @Override
    public void endEvaluation() {
        this.evaluation = null;
    }

    static class ObjectWrapper {
        private Object wrapperedObject;
        private boolean immutable;

        public ObjectWrapper(Object wrapperedObject) {
            this.wrapperedObject = wrapperedObject;
        }

        public ObjectWrapper(Object wrapperedObject, boolean immutable) {
            this.wrapperedObject = wrapperedObject;
            this.immutable = immutable;
        }

        public Object getWrapperedObject() {
            if (!immutable) {
                return wrapperedObject;
            } else {
                return GsonUtils.deepCopy(wrapperedObject);
            }
        }
    }

    static class DefaultEvaluation implements Evaluation {
        private final Session session;
        private DataObject value;
        private Map<String, Object> attributes;

        private DefaultEvaluation(Session session) {
            this.session = session;

            this.value = GsonUtils.deepCopy(session.getCurrentState());
            this.attributes = new ConcurrentHashMap<>();
        }

        @Override
        public Session getSession() {
            return session;
        }

        @Override
        public Object get(String attrName) {
            return attributes.get(attrName);
        }

        @Override
        public void set(String attrName, Object attrValue) {
            attributes.put(attrName, attrValue);
        }

        public DataObject getValue() {
            return value;
        }

        public void setValue(DataObject value) {
            this.value = value;
        }
    }
}
