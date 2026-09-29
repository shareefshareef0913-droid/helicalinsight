package com.helical.mongodb;

import com.mongodb.jdbc.MongoDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Helical Insight MongoDB JDBC Driver implementation.
 * Wraps com.mongodb.jdbc.MongoDriver to provide seamless MongoDB connectivity
 * for Helical Insight.
 */
public class MongoJdbcDriver extends MongoDriver {

    private static final Logger logger = LoggerFactory.getLogger(MongoJdbcDriver.class);

    static {
        try {
            DriverManager.registerDriver(new MongoJdbcDriver());
            logger.info("Helical MongoJdbcDriver registered successfully.");
        } catch (SQLException e) {
            logger.error("Error registering Helical MongoJdbcDriver", e);
            throw new RuntimeException("Could not register com.helical.mongodb.MongoJdbcDriver", e);
        }
    }

    public MongoJdbcDriver() {
        super();
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if (url == null) {
            return false;
        }
        if (url.startsWith("mongodb://") || url.startsWith("mongodb+srv://")) {
            return true;
        }
        return super.acceptsURL(url);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (url == null) {
            return null;
        }
        String normalizedUrl = url;
        if (url.startsWith("mongodb://") || url.startsWith("mongodb+srv://")) {
            normalizedUrl = "jdbc:" + url;
        }
        if (!acceptsURL(normalizedUrl)) {
            return null;
        }
        try {
            return super.connect(normalizedUrl, info);
        } catch (SQLException e) {
            logger.warn("Direct MongoDriver connection attempt failed: {}", e.getMessage());
            throw e;
        }
    }
}
