package codes.zucker.DeathChest;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands {

    public static boolean deathChestCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (args.length >= 1) {
            Player pl = CommandHelper.AutoCompleteName(args[0], null);
            Block found = null;
            if (pl != null) {
                for (Entry<Block, PlayerChest> entry : Events.deathChest.entrySet()) {
                    if (pl.getUniqueId().equals(entry.getValue().owner)) {
                        found = entry.getKey();
                        break;
                    }
                }
            }
            if (found == null) {
                p.sendMessage("No death chest found for player \"" + args[0] + "\"");
                return true;
            }
            p.sendMessage("Teleported you to " + pl.getDisplayName() + "'s death chest");
            Location dest = found.getLocation().clone();
            dest.add(0, 1, 0);
            p.teleport(dest);

        } else {
            if (Events.deathChest.size() == 0) {
                p.sendMessage("No death chests currently active");
                return true;
            }
            p.sendMessage("Currently active death chests:");
            for (Entry<Block, PlayerChest> entry : Events.deathChest.entrySet()) {
                Player pl = Bukkit.getPlayer(entry.getValue().owner);
                if (pl != null) {
                    int seconds = (int)(entry.getValue().expire - System.currentTimeMillis()) / 1000;
                    if (seconds > 0)
                        p.sendMessage(pl.getName() + " (" + seconds + " seconds until expiry)");
                    else
                        p.sendMessage(pl.getName() + " (expired " + Math.abs(seconds) + " seconds ago)");
                }
            }
        }
        return true;
    }
}