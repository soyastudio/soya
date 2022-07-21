package soya.framework.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Pipeline {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private transient State state = new State();

    private Bod bod;
    private SourceTable sourceTable;
    private Api api;

    public State state() {
        return state;
    }

    public String getName() {
        return bod.getName();
    }

    public Bod getBod() {
        return bod;
    }

    public SourceTable getSourceTable() {
        return sourceTable;
    }

    public Api getApi() {
        return api;
    }

    public static Pipeline fromJson(String json) {
        return gson.fromJson(json, Pipeline.class);
    }

    public static class Bod {
        private String name;
        private BodPattern pattern;
        private String calendar;
        private long delay;

        public String getName() {
            return name;
        }

        public BodPattern getPattern() {
            return pattern;
        }

        public String getCalendar() {
            return calendar;
        }

        public long getDelay() {
            return delay;
        }
    }

    public static class SourceTable {
        private String schema;
        private String table;
        private String primaryKey;
        private String[] columns;

        private String insert;
        private String update;

        private Map<String, TableColumn> metadata = new ConcurrentHashMap<>();

        public String getSchema() {
            return schema;
        }

        public String getTable() {
            return table;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        public String[] getColumns() {
            return columns;
        }

        public String getInsert() {
            return insert;
        }

        public String getUpdate() {
            return update;
        }

        public Map<String, TableColumn> metadata() {
            return metadata;
        }

        public SourceTable addTableColumn(TableColumn column) {
            metadata.put(column.name.toUpperCase(), column);
            return this;
        }

        public TableColumn getTableColumn(String name) {
            return metadata.get(name.toUpperCase());
        }
    }

    public static class Api {
        private String type = "single";
        private String url;
        private String filter;
        private Shifter[] shifters;

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public String getFilter() {
            return filter;
        }

        public Shifter[] getShifters() {
            return shifters;
        }
    }

    public static class Shifter {
        private DataType type = DataType.String;
        private String from;
        private String to;
        private String expression;

        public DataType getType() {
            return type;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getExpression() {
            return expression;
        }
    }

    public static class State {
        private boolean processing;
        private int round;
        private int cursor = 0;

        private State() {
        }

        public boolean isProcessing() {
            return processing;
        }

        public void setProcessing(boolean processing) {
            this.processing = processing;
        }

        public int getRound() {
            return round;
        }

        public int getCursor() {
            return cursor;
        }

        public State(int cursor) {
            this.cursor = cursor;
        }

        public int next() {
            return cursor ++;
        }

        public void nextRound() {
            cursor = 0;
            round ++;
        }
    }

    public static class TableColumn {
        private String name;
        private String label;
        private String type;

        private TableColumn() {
        }

        public TableColumn name(String name) {
            this.name = name;
            return this;
        }

        public TableColumn label(String label) {
            this.label = label;
            return this;
        }

        public TableColumn type(String type) {
            this.type = type;
            return this;
        }

        public static TableColumn newInstance() {
            return new TableColumn();
        }
    }

    public static enum DataType {
        String, Number, Boolean
    }

    public static enum BodPattern {
        APIMONITOR, CDC;
    }
}
