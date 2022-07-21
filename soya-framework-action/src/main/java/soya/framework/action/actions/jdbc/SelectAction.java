package soya.framework.action.actions.jdbc;

import soya.framework.action.ActionContext;
import soya.framework.action.Command;

import javax.sql.DataSource;
import java.sql.Connection;

@Command(group = "jdbc", name = "select", httpMethod = Command.HttpMethod.GET, httpResponseTypes = {Command.MediaType.APPLICATION_JSON})
public class SelectAction extends JdbcAction {
    @Override
    protected Object execute() throws Exception {
        DataSource dataSource = ActionContext.getInstance().getService(DataSource.class);

        Connection connection = dataSource.getConnection();

        System.out.println("============== " + connection.getMetaData().getDatabaseProductName());

        connection.close();

        return null;
    }
}
