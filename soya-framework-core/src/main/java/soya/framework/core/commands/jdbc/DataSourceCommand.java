package soya.framework.core.commands.jdbc;

import soya.framework.core.CommandCallable;
import soya.framework.core.CommandExecutionContext;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataSourceCommand<T> implements CommandCallable<T> {

    protected void close(Closeable closeable) throws IOException {
        closeable.close();
    }

    protected Connection connection() throws SQLException {
        DataSource dataSource = CommandExecutionContext.getInstance().getService(DataSource.class);
        return dataSource.getConnection();
    }
}
