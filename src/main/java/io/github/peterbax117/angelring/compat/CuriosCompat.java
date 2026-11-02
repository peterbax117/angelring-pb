package io.github.peterbax117.angelring.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

/** The only class that touches the Curios API; RingFlight loads it only when Curios is installed. */
public final class CuriosCompat
{
    private CuriosCompat() {
    }

    public static boolean isEquipped(Player player, Item item) {
        return CuriosApi.getCuriosInventory(player).map(inventory -> inventory.isEquipped(item)).orElse(false);
    }
}
