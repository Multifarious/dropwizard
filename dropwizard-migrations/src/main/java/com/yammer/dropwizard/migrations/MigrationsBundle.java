package com.yammer.dropwizard.migrations;

import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.ConfigurationStrategy;
import com.yammer.dropwizard.util.Generics;

public abstract class MigrationsBundle<T extends Configuration> implements Bundle, ConfigurationStrategy<T> {
    private final String commandSuffix;
    private final String databaseTitle;

    public <T> MigrationsBundle() {
        this(null,null);
    }

    public <T> MigrationsBundle(String commandSuffix) {
        this(commandSuffix,null);
    }

    /**
     * Constructs a named database migration to be accessed via <em>db_&lt;commandSuffix&gt;</em>
     * @param commandSuffix Defines command used to run this MigrationsBundle
     * @param databaseTitle Human-readable Database title displayed in command help. Defaults to commandSuffix.
     * @param <T>
     */
    public <T> MigrationsBundle(String commandSuffix, String databaseTitle) {
        this.commandSuffix = commandSuffix == null || commandSuffix.isEmpty() ? null : commandSuffix;
        this.databaseTitle = databaseTitle == null || databaseTitle.isEmpty() ? this.commandSuffix : databaseTitle;
    }

    @Override
    public final void initialize(Bootstrap<?> bootstrap) {
        final Class<T> klass = Generics.getTypeParameter(getClass(), Configuration.class);
        bootstrap.addCommand(new DbCommand<T>(this, klass, commandSuffix, databaseTitle));
    }

    @Override
    public final void run(Environment environment) {
        // nothing doing
    }
}
