package io.github.peterbax117.angelring.gametest;

import io.github.peterbax117.angelring.AngelRing;
import io.github.peterbax117.angelring.RingFlight;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

/** Equips the ring the way a player would in the current setup. Only touches Curios when it is loaded. */
final class RingSlot
{
    private RingSlot() {
    }

    static void equip(Player player) {
        if(RingFlight.curiosLoaded()) Curios.set(player, new ItemStack(AngelRing.ANGEL_RING.get()));
        else player.getInventory().setItem(8, new ItemStack(AngelRing.ANGEL_RING.get()));
    }

    static void unequip(Player player) {
        if(RingFlight.curiosLoaded()) Curios.set(player, ItemStack.EMPTY);
        else player.getInventory().setItem(8, ItemStack.EMPTY);
    }

    static boolean hasRingSlot(Player player) {
        return Curios.hasRingSlot(player);
    }

    /** Where Curios finds a ring, for failure messages. */
    static String describe(Player player) {
        return RingFlight.curiosLoaded() ? Curios.describe(player) : "inventory";
    }

    private static final class Curios
    {
        static String describe(Player player) {
            return CuriosApi.getCuriosInventory(player)
                    .flatMap(inventory -> inventory.findFirstCurio(AngelRing.ANGEL_RING.get()))
                    .map(result -> result.slotContext().identifier() + "#" + result.slotContext().index()
                            + (result.slotContext().cosmetic() ? " (cosmetic)" : ""))
                    .orElse("none");
        }

        static void set(Player player, ItemStack stack) {
            CuriosApi.getCuriosInventory(player).orElseThrow(() -> new AssertionError("Player has no Curios inventory"))
                    .setEquippedCurio("ring", 0, stack);
        }

        static boolean hasRingSlot(Player player) {
            return CuriosApi.getCuriosInventory(player).flatMap(inventory -> inventory.getStacksHandler("ring")).isPresent();
        }
    }
}
