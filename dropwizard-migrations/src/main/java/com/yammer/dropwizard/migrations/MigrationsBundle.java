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

    /**
     * Constructs a named database migration to be accessed via <tt>db</tt>.
     */
    public <T> MigrationsBundle() {
        this(null,null);
    }

    /**
     * Constructs a named database migration to be accessed via <tt>db_<em>&lt;commandSuffix&gt;</em></tt>.
     * @param commandSuffix Defines command used to run this MigrationsBundle
     */
    public <T> MigrationsBundle(String commandSuffix) {
        this(commandSuffix,null);
    }

    /**
     * Constructs a named database migration to be accessed via <tt>db_<em>&lt;commandSuffix&gt;</em></tt>.
     * @param commandSuffix Defines command used to run this MigrationsBundle
     * @param databaseTitle Human-readable Database title displayed in command help. Defaults to commandSuffix.
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
