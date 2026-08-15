package com.smartschool.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP-based connection pool manager.
 * Singleton pattern ensures one pool for the entire application lifetime.
 * Thread-safe via double-checked locking.
 */
public final class DatabaseConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionManager.class);
    private static volatile DatabaseConnectionManager instance;
    private final HikariDataSource dataSource;

    private DatabaseConnectionManager() {
        AppConfig cfg = AppConfig.getInstance();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(cfg.get("db.url"));
        hikariConfig.setUsername(cfg.get("db.username"));
        hikariConfig.setPassword(cfg.get("db.password"));
        hikariConfig.setMaximumPoolSize(cfg.getInt("db.pool.max-size", 10));
        hikariConfig.setMinimumIdle(cfg.getInt("db.pool.min-idle", 2));
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(600000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setPoolName("SmartSchoolPool");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(hikariConfig);
        logger.info("Database connection pool initialized: {}", cfg.get("db.url"));
    }

    public static DatabaseConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnectionManager.class) {
                if (instance == null) {
                    instance = new DatabaseConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Returns a connection from the pool. Caller is responsible for closing.
     * Use try-with-resources to ensure proper release back to pool.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Database connection pool closed.");
        }
    }
}
