package soya.framework.commandline.tasks.jdbc;

import soya.framework.commandline.Command;
import soya.framework.commandline.TaskExecutionContext;

import javax.sql.DataSource;
import java.sql.Connection;

@Command(group = "jdbc", name = "select", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SelectTask extends JdbcTask {
    @Override
    protected Object execute() throws Exception {
        DataSource dataSource = TaskExecutionContext.getInstance().getService(DataSource.class);

        Connection connection = dataSource.getConnection();

        System.out.println("============== " + connection.getMetaData().getDatabaseProductName());

        connection.close();

        return null;
    }
}
