package com.practicum.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

final class DataSourceInitializers {
    private DataSourceInitializers() {
    }

    static DataSourceInitializer build(DataSource ds, String schema, String data) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource(schema));
        if (data != null && !data.isBlank()) {
            populator.addScript(new ClassPathResource(data));
        }
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(ds);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }
}
