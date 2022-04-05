package soya.framework.transform.schema.avro;

import org.apache.avro.Schema;

import java.util.*;

// Iterates over an Avro schema
public class AvroSchemaIterator implements Iterable<Schema>,
        Iterator<Schema> {

    private Schema currentSchema;
    private final Deque<Schema> nodesToIterate;
    private final Set<Integer> iteratedNodes;

    public AvroSchemaIterator(Schema rootSchema) {
        nodesToIterate = new LinkedList<>();
        iteratedNodes = new HashSet<>();
        nodesToIterate.addFirst(rootSchema);
    }

    @Override
    public Iterator<Schema> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        while (!nodesToIterate.isEmpty()) {
            currentSchema = nodesToIterate.removeFirst();
            Integer objectNum = System.identityHashCode(currentSchema);
            if (!iteratedNodes.contains(objectNum)) {
                iteratedNodes.add(objectNum);
                nodesToIterate.addAll(getChildNodes(currentSchema));
                return true;
            }
        }
        return false;
    }

    @Override
    public Schema next() {
        return currentSchema;
    }

    // Returns the child nodes of schema
    private static List<Schema> getChildNodes(Schema schema) {
        List<Schema> children = new ArrayList<>();

        switch (schema.getType()) {
            case RECORD:
                for (Schema.Field field : schema.getFields()) {
                    children.add(field.schema());
                }
                break;
            case UNION:
                children.addAll(schema.getTypes());
                break;
            case ARRAY:
                children.add(schema.getElementType());
                break;
            case MAP:
                children.add(schema.getValueType());
                break;
        }
        return children;
    }
}
