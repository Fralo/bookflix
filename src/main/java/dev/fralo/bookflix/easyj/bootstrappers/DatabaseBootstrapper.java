package dev.fralo.bookflix.easyj.bootstrappers;

import dev.fralo.bookflix.easyj.db.MigrationManager;

public class DatabaseBootstrapper extends Bootstrapper {
    @Override
    public void bootstrap() throws Exception {
        MigrationManager migrationManager = new MigrationManager();
        migrationManager.runMigrations();
    }
}
