package dev.fralo.bookflix.easyj.bootstrappers;

import dev.fralo.bookflix.easyj.db.Database;
import dev.fralo.bookflix.easyj.orm.Model;

public class OrmBootstrapper extends Bootstrapper {
    @Override
    public void bootstrap() throws Exception {
        Model.setDatabase(Database.getConnection());
    }
}
