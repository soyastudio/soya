package soya.framework.tasks.apache.poi;


import java.util.ArrayList;
import java.util.List;

public class Table {
    private final Row header;
    private final Row[] rows;

    public Table(Row header, Row[] rows) {
        this.header = header;
        this.rows = rows;
    }

    public String[] header() {
        return header.columns;
    }

    public int rows() {
        return rows.length;
    }

    public String[] row(int index) {
        return rows[index].columns;
    }

    public static Builder builder(String[] header) {
        return new Builder(header);
    }

    private static class Row {
        private String[] columns;
        private Row(String[] columns) {
            this.columns = columns;
        }
    }

    public static class Builder {
        private Row header;
        private List<Row> rows = new ArrayList<>();

        private Builder(String[] header) {
            this.header = new Row(header);
        }

        public Builder header(String[] columns) {
            this.header = new Row(columns);
            return this;
        }

        public Builder addRow(String[] columns) {
            this.rows.add(new Row(columns));
            return this;
        }

        public Table create() {
            return new Table(header, rows.toArray(new Row[rows.size()]));

        }
    }
}
