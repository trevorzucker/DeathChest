package codes.zucker.DeathChest;

import java.util.UUID;

import org.bukkit.inventory.Inventory;

public class PlayerChest {
    public Inventory inventory;
    public UUID owner;
    public long expire;
    
    public PlayerChest(Inventory i, UUID u, long e) {
        inventory = i;
        owner = u;
        expire = e;
    }
}