package io.github.peterbax117.angelring;

import io.github.peterbax117.angelring.compat.CuriosCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Grants flight through NeoForge's {@code neoforge:creative_flight} attribute while the ring is worn.
 * An attribute modifier stacks cleanly with creative mode and other mods' flight: removing ours never takes
 * away flight someone else granted.
 */
@EventBusSubscriber(modid = AngelRing.MODID)
public final class RingFlight
{
    public static final ResourceLocation MODIFIER_ID = AngelRing.loc("angel_ring_flight");
    private static final AttributeModifier MODIFIER =
            new AttributeModifier(MODIFIER_ID, 1, AttributeModifier.Operation.ADD_VALUE);

    private static Boolean curiosLoaded;

    private RingFlight() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        update(event.getEntity());
    }

    /** Adds or removes the flight modifier to match whether the player wears a ring. Server side only. */
    public static void update(Player player) {
        if(player.level().isClientSide) return;
        AttributeInstance flight = player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT);
        if(flight == null) return;

        boolean wearing = isWearingRing(player);
        boolean granted = flight.hasModifier(MODIFIER_ID);
        if(wearing && !granted) {
            flight.addTransientModifier(MODIFIER);
        }
        else if(!wearing && granted) {
            flight.removeModifier(MODIFIER_ID);
            // Stop flying if nothing else lets the player fly; they fall as with a removed angel ring.
            if(player.getAbilities().flying && !player.mayFly()) {
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }
    }

    /** With Curios, only the curio slots count; without it, anywhere in the inventory including the off hand. */
    public static boolean isWearingRing(Player player) {
        if(curiosLoaded()) return CuriosCompat.isEquipped(player, AngelRing.ANGEL_RING.get());
        for(ItemStack stack : player.getInventory().items) if(stack.is(AngelRing.ANGEL_RING)) return true;
        for(ItemStack stack : player.getInventory().offhand) if(stack.is(AngelRing.ANGEL_RING)) return true;
        return false;
    }

    public static boolean curiosLoaded() {
        if(curiosLoaded == null) curiosLoaded = ModList.get().isLoaded("curios");
        return curiosLoaded;
    }
}
