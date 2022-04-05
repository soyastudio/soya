package soya.framework.tool.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import soya.framework.commons.pattern.ServiceBuilder;

import javax.sql.DataSource;

public abstract class DataSourceBuilder implements ServiceBuilder<DataSource> {

    public static DBCPDataSourceBuilder dbcp(String url, String username, String password) {
        return new DBCPDataSourceBuilder(url, username, password);
    }

    public static HikariCPDataSourceBuilder HikariCP(String url, String username, String password) {
        return new HikariCPDataSourceBuilder(url, username, password);
    }

    public static class DBCPDataSourceBuilder extends DataSourceBuilder {
        private final String url;
        private final String username;
        private final String password;

        private String driverClassName;

        private DBCPDataSourceBuilder(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public DBCPDataSourceBuilder driveClassName(String driverClassName) {
            this.driverClassName = driverClassName;
            return this;
        }

        @Override
        public DataSource build() {
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);

            ds.setDriverClassName(driverClassName);


            return ds;
        }
    }

    public static class HikariCPDataSourceBuilder extends DataSourceBuilder {

        private HikariConfig config;

        private HikariCPDataSourceBuilder(String url, String username, String password) {
            this.config = new HikariConfig();

            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
        }

        public HikariCPDataSourceBuilder driverClassName(String driverClassName) {
            config.setDriverClassName(driverClassName);
            return this;
        }

        public HikariCPDataSourceBuilder autoCommit() {
            config.setAutoCommit(true);
            return this;
        }

        public HikariCPDataSourceBuilder catalog(String catalog) {
            config.setCatalog(catalog);
            return this;
        }

        public HikariCPDataSourceBuilder schema(String schema) {
            config.setSchema(schema);
            return this;
        }

        public HikariCPDataSourceBuilder maximumPoolSize(int maximumPoolSize) {
            config.setMaximumPoolSize(maximumPoolSize);
            return this;
        }

        public HikariCPDataSourceBuilder connectionTimeout(long timeout) {
            config.setConnectionTimeout(timeout);
            return this;
        }

        public HikariCPDataSourceBuilder keepAliveTime(long keepAliveTimeMs) {
            config.setKeepaliveTime(keepAliveTimeMs);
            return this;
        }

        public HikariCPDataSourceBuilder maxLifeTime(long maxLifeTime) {
            config.setMaxLifetime(maxLifeTime);
            return this;
        }

        public HikariCPDataSourceBuilder idleTimeout(long timeout) {
            config.setIdleTimeout(timeout);
            return this;
        }

        public HikariCPDataSourceBuilder connectionInitSql(String sql) {
            config.setConnectionInitSql(sql);
            return this;
        }

        public HikariCPDataSourceBuilder connectionTestQuery(String sql) {
            config.setConnectionTestQuery(sql);
            return this;
        }

        @Override
        public DataSource build() {
            return new HikariDataSource(config);
        }
    }


}
