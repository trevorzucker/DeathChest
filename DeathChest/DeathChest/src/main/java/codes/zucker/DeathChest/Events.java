package codes.zucker.DeathChest;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    final static int EXPIRE_SECONDS = (int)ConfigurationLoader.ConfigValues.get("time_expire");

    public static Map<Block, PlayerChest> deathChest = new HashMap<Block, PlayerChest>();

    @EventHandler
    public static void pDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Inventory drops = Bukkit.createInventory(null, InventoryType.CHEST);
        while(e.getDrops().iterator().hasNext()) {
            ItemStack s = e.getDrops().iterator().next();
            e.getDrops().remove(s);
            drops.addItem(s);
        }
        Block standing = p.getLocation().getBlock();
        standing.setType(Material.ENDER_CHEST);
        long expire = System.currentTimeMillis() + (EXPIRE_SECONDS * 1000);
        deathChest.put(standing, new PlayerChest(drops, p.getUniqueId(), expire));
    }

    public static Map<Player, Inventory> eChest = new HashMap<Player, Inventory>();
    public static Map<Player, Block> openedEChest = new HashMap<Player, Block>();

    public final static String ATTEMPT_OPEN_LOCKED = (String)LangLoader.Values.get("attempt_open_locked");

    @EventHandler
    public static void openEChest(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player p = e.getPlayer();

        Block b = e.getClickedBlock();
        PlayerChest found = null;
        for(Entry<Block, PlayerChest> entry : deathChest.entrySet()) {
            if (entry.getKey().getLocation().equals(b.getLocation())) {
                found = entry.getValue();
                break;
            }
        }
        if (found == null) {
            return;
        }

        if (!found.owner.equals(p.getUniqueId()) && System.currentTimeMillis() < found.expire) {
            int secsRemaining = (int)((found.expire - System.currentTimeMillis()) / 1000);
            String replace = secsRemaining + " seconds";
            if (secsRemaining >= 120) {
                replace = (secsRemaining / 60) + " minutes";
            }
            String msg = ATTEMPT_OPEN_LOCKED.replaceAll("%s", replace);
            p.sendMessage(msg);
            e.setCancelled(true);
            return;
        }

        Inventory plChest = Bukkit.createInventory(null, InventoryType.CHEST);
        Inventory pChest = p.getEnderChest();

        for(ItemStack s : pChest.getContents()) {
            if (s != null && s.getType() != Material.AIR)
            plChest.addItem(s);
        }

        eChest.put(p, plChest);

        pChest.clear();
        for(ItemStack s : found.inventory.getContents()) {
            if (s != null && s.getType() != Material.AIR)
                pChest.addItem(s);
        }
        openedEChest.put(p, b);
    }

    @EventHandler
    public static void closeInv(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        final Inventory contents = p.getEnderChest();
        Inventory pChest = eChest.get(p);
        if (pChest != null) {
            Block closed = openedEChest.get(p);
            Inventory chest = p.getEnderChest();

            int size = 0;
            for(ItemStack i : contents.getContents()) {
                if (i != null && i.getType() != Material.AIR)
                    size++;
            }

            if (size > 0) {
                if (closed != null && deathChest.get(closed) != null) {
                    PlayerChest entry = deathChest.get(closed);
                    Inventory death = entry.inventory;
                    death.clear();
                    for(ItemStack s : contents.getContents())
                        if (s != null && s.getType() != Material.AIR)
                            death.addItem(s);
                }
            }
            else {
                closed.setType(Material.AIR);
                deathChest.remove(closed);
            }

            chest.clear();
            for(ItemStack s : pChest.getContents()) {
                if (s != null && s.getType() != Material.AIR)
                    chest.addItem(s);
            }
            eChest.remove(p);
            openedEChest.remove(p);
        }
    }

    @EventHandler
    public static void blockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if(deathChest.get(b) != null) {
            e.setCancelled(true);
        }
    }

}