package soya.framework.commandline.tasks.jdbc;

import soya.framework.commandline.CommandGroup;
import soya.framework.commandline.TaskExecutionContext;
import soya.framework.commandline.Task;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@CommandGroup(group = "jdbc", title = "JDBC Service", description = "Toolkit for JDBC command.")
public abstract class JdbcTask<T> extends Task<T> {

    protected void close(Closeable closeable) throws IOException {
        closeable.close();
    }

    protected Connection connection() throws SQLException {
        DataSource dataSource = TaskExecutionContext.getInstance().getService(DataSource.class);
        return dataSource.getConnection();
    }
}
