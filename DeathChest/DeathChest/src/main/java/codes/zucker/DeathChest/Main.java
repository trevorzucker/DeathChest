package codes.zucker.DeathChest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigurationLoader.LoadConfigurationFile(); // Load
        LangLoader.LoadLang();                      // our
        DataLoader.LoadDataFile();                 // files

        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        DataLoader.SaveDataFile();
    }

    // Command handler

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (Map.Entry<String, Method> map : CommandHelper.commands.entrySet()) {
            if (cmd.getName().equalsIgnoreCase(map.getKey())) {
                try {
                    // Here's some dodgy reflection stuff...
                    Method meth = map.getValue();
                    meth.invoke(null, sender, cmd, label, args);
                    return true;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                }
                return true;
            }
        }

        return false;
    }
}