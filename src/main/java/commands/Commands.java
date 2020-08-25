package {{.Package}}.commands;

import com.mitchdennett.framework.commands.*;

public class Commands {

    public static final Class<Command>[] COMMANDS = new Class[]{
            MakeMigration.class,
            MigrateCommand.class,
            MigrateRollbackCommand.class,
            MakeCommand.class,
            MakeController.class
    };
}
