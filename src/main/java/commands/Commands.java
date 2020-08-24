package {{.Package}}.commands;

import com.mitchdennett.framework.commands.Command;
import com.mitchdennett.framework.commands.MakeMigration;
import com.mitchdennett.framework.commands.MigrateCommand;
import com.mitchdennett.framework.commands.MigrateRollbackCommand;

public class Commands {

    public static final Class<Command>[] COMMANDS = new Class[]{
            MakeMigration.class,
            MigrateCommand.class,
            MigrateRollbackCommand.class
    };
}
