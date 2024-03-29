package soya.framework.action.actions.jdbc;

import soya.framework.action.Action;
import soya.framework.action.ActionContext;
import soya.framework.action.Domain;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Domain(group = "jdbc", title = "JDBC Service", description = "Toolkit for JDBC command.")
public abstract class JdbcAction<T> extends Action<T> {

    protected void close(Closeable closeable) throws IOException {
        closeable.close();
    }

    protected Connection connection() throws SQLException {
        DataSource dataSource = ActionContext.getInstance().getService(DataSource.class);
        return dataSource.getConnection();
    }
}
