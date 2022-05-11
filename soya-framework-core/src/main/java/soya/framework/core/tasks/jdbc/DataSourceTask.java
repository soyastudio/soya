package soya.framework.core.tasks.jdbc;

import soya.framework.core.TaskExecutionContext;
import soya.framework.core.Task;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceTask<T> extends Task<T> {

    protected void close(Closeable closeable) throws IOException {
        closeable.close();
    }

    protected Connection connection() throws SQLException {
        DataSource dataSource = TaskExecutionContext.getInstance().getService(DataSource.class);
        return dataSource.getConnection();
    }
}
