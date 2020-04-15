package codes.zucker.DeathChest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {
    
    public static boolean command(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player)sender;
        return true;
    }
}