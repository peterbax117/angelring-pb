package io.github.peterbax117.angelring.gametest;

import io.github.peterbax117.angelring.AngelRing;
import io.github.peterbax117.angelring.RingFlight;
import io.github.peterbax117.modtestharness.gametest.HarnessTemplates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

import static io.github.peterbax117.modtestharness.gametest.GameTestSupport.survivalPlayer;

/**
 * Server-side tests. They pass with and without Curios (gradlew runGameTestServer -PnoCurios); expectations
 * that differ check RingFlight.curiosLoaded(). FakePlayers do not tick, so tests call RingFlight.update directly.
 */
@GameTestHolder(HarnessTemplates.NAMESPACE)
@PrefixGameTestTemplate(false)
public class RingGameTests
{
    private static final String TEMPLATE = HarnessTemplates.EMPTY_9X5X9;

    @GameTest(template = TEMPLATE)
    public static void noRingNoFlight(GameTestHelper helper) {
        FakePlayer player = player(helper);
        RingFlight.update(player);
        helper.assertFalse(player.mayFly(), "Survival player without a ring must not fly");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void wornRingGrantsFlight(GameTestHelper helper) {
        FakePlayer player = player(helper);
        RingSlot.equip(player);
        RingFlight.update(player);
        helper.assertTrue(player.mayFly(), "Wearing the ring should allow flight");
        helper.assertValueEqual(player.getAttributeValue(NeoForgeMod.CREATIVE_FLIGHT), 1.0, "creative_flight attribute");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void removingRingStopsFlying(GameTestHelper helper) {
        FakePlayer player = player(helper);
        RingSlot.equip(player);
        RingFlight.update(player);
        player.getAbilities().flying = true;

        // Curios caches equip lookups for the current tick, so remove the ring on a later tick as a player would.
        helper.runAfterDelay(1, () -> {
            RingSlot.unequip(player);
            RingFlight.update(player);

            helper.assertFalse(player.mayFly(), "Flight should be removed with the ring (still wearing="
                    + RingFlight.isWearingRing(player) + ", creative_flight=" + player.getAttributeValue(NeoForgeMod.CREATIVE_FLIGHT)
                    + ", found in " + RingSlot.describe(player) + ")");
            helper.assertFalse(player.getAbilities().flying, "Player should stop flying when the ring is removed");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void ringCarriedAnywhereGrantsFlight(GameTestHelper helper) {
        // Main inventory, hotbar, selected hand and off hand all count, with or without Curios.
        for(int slot : new int[]{20, 3, 0}) {
            FakePlayer player = player(helper);
            player.getInventory().selected = 0;
            player.getInventory().setItem(slot, new ItemStack(AngelRing.ANGEL_RING.get()));
            RingFlight.update(player);
            helper.assertTrue(player.mayFly(), "Ring in inventory slot " + slot + " should allow flight");
        }
        FakePlayer offhand = player(helper);
        offhand.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(AngelRing.ANGEL_RING.get()));
        RingFlight.update(offhand);
        helper.assertTrue(offhand.mayFly(), "Ring in the off hand should allow flight");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void droppingCarriedRingStopsFlying(GameTestHelper helper) {
        FakePlayer player = player(helper);
        player.getInventory().setItem(20, new ItemStack(AngelRing.ANGEL_RING.get()));
        RingFlight.update(player);
        player.getAbilities().flying = true;

        helper.runAfterDelay(1, () -> {
            player.getInventory().setItem(20, ItemStack.EMPTY);
            RingFlight.update(player);
            helper.assertFalse(player.mayFly(), "Flight should end when the carried ring is gone");
            helper.assertFalse(player.getAbilities().flying, "Player should stop flying");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void creativeFlightIsNeverRevoked(GameTestHelper helper) {
        FakePlayer player = player(helper);
        player.setGameMode(GameType.CREATIVE);
        RingSlot.equip(player);
        RingFlight.update(player);
        player.getAbilities().flying = true;

        helper.runAfterDelay(1, () -> {
            RingSlot.unequip(player);
            RingFlight.update(player);

            helper.assertTrue(player.mayFly(), "Creative players keep flight");
            helper.assertTrue(player.getAbilities().flying, "Creative players keep flying");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void otherFlightSourcesAreKept(GameTestHelper helper) {
        FakePlayer player = player(helper);
        // Another mod granting flight through the same attribute.
        player.getAttribute(NeoForgeMod.CREATIVE_FLIGHT).addTransientModifier(new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("othermod", "flight"), 1, AttributeModifier.Operation.ADD_VALUE));
        RingSlot.equip(player);
        RingFlight.update(player);

        helper.runAfterDelay(1, () -> {
            RingSlot.unequip(player);
            RingFlight.update(player);

            helper.assertFalse(RingFlight.isWearingRing(player), "Ring should be removed");
            helper.assertTrue(player.mayFly(), "Removing the ring must not remove flight granted by another source");
            helper.succeed();
        });
    }

    @GameTest(template = TEMPLATE)
    public static void ringIsTaggedForCuriosRingSlot(GameTestHelper helper) {
        TagKey<Item> ringTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "ring"));
        helper.assertTrue(new ItemStack(AngelRing.ANGEL_RING.get()).is(ringTag), "Ring should be in the curios:ring tag");
        if(RingFlight.curiosLoaded()) {
            helper.assertTrue(RingSlot.hasRingSlot(player(helper)), "Players should have a Curios ring slot");
        }
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void ringInsideContainerItemsDoesNotGrantFlight(GameTestHelper helper) {
        // Confirmed design: a ring stored inside a backpack, bundle or shulker box does not count.
        FakePlayer bundleCarrier = player(helper);
        ItemStack bundle = new ItemStack(Items.BUNDLE);
        bundle.set(DataComponents.BUNDLE_CONTENTS, new BundleContents(List.of(new ItemStack(AngelRing.ANGEL_RING.get()))));
        bundleCarrier.getInventory().setItem(4, bundle);
        RingFlight.update(bundleCarrier);
        helper.assertFalse(bundleCarrier.mayFly(), "A ring inside a bundle must not grant flight");

        FakePlayer boxCarrier = player(helper);
        ItemStack box = new ItemStack(Items.SHULKER_BOX);
        box.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(new ItemStack(AngelRing.ANGEL_RING.get()))));
        boxCarrier.getInventory().setItem(4, box);
        RingFlight.update(boxCarrier);
        helper.assertFalse(boxCarrier.mayFly(), "A ring inside a shulker box must not grant flight");
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void recipeCraftsRing(GameTestHelper helper) {
        ItemStack glass = new ItemStack(Items.GLASS);
        ItemStack gold = new ItemStack(Items.GOLD_INGOT);
        CraftingInput input = CraftingInput.of(3, 3, List.of(
                glass, gold, glass,
                gold, new ItemStack(Items.NETHER_STAR), gold,
                new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.ELYTRA), new ItemStack(Items.GHAST_TEAR)));

        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
        if(ModList.get().isLoaded("crafttweaker")) {
            // The -Pcrafttweaker run's sample script removes the default recipe.
            helper.assertFalse(recipe.isPresent(), "CraftTweaker script should have removed the default recipe");
            helper.succeed();
            return;
        }
        helper.assertTrue(recipe.isPresent(), "Angel ring recipe should match");
        ItemStack result = recipe.get().value().assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(AngelRing.ANGEL_RING.get()), "Recipe should make the angel ring, made " + result);
        helper.succeed();
    }

    @GameTest(template = TEMPLATE)
    public static void craftTweakerScriptReplacesRecipe(GameTestHelper helper) {
        // Pack makers change recipes with CraftTweaker or KubeJS. src/crafttweaker/scripts/angelring_test.zs adds
        // feather + gold block -> ring; without CraftTweaker that combination must not craft anything.
        CraftingInput input = CraftingInput.of(2, 1, List.of(new ItemStack(Items.FEATHER), new ItemStack(Items.GOLD_BLOCK)));
        var recipe = helper.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
        boolean crafttweaker = ModList.get().isLoaded("crafttweaker");
        helper.assertValueEqual(recipe.isPresent(), crafttweaker, "feather + gold block recipe present");
        if(crafttweaker) {
            ItemStack result = recipe.get().value().assemble(input, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(AngelRing.ANGEL_RING.get()), "Script recipe should make the angel ring, made " + result);
        }
        helper.succeed();
    }

    private static FakePlayer player(GameTestHelper helper) {
        return survivalPlayer(helper, ItemStack.EMPTY, new BlockPos(4, 1, 4), 0, 0);
    }
}
