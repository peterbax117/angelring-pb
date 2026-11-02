package io.github.peterbax117.angelring.clienttest;

import io.github.peterbax117.angelring.AngelRing;
import io.github.peterbax117.angelring.RingFlight;
import io.github.peterbax117.modtestharness.client.ClientTest;
import io.github.peterbax117.modtestharness.client.ClientTestContext;
import io.github.peterbax117.modtestharness.client.ClientTestHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import top.theillusivec4.curios.api.CuriosApi;

import static io.github.peterbax117.modtestharness.client.ClientTestContext.check;

/** Client tests: real double-tap-jump flight in survival, with and without Curios. */
@ClientTestHolder
public final class RingClientTests
{
    private static final BlockPos STAND = new BlockPos(0, -60, 0);

    @ClientTest(value = "worn ring lets a survival player double-tap jump to fly", order = 1)
    public static void ringGrantsFlight(ClientTestContext ctx) {
        prepareSurvival(ctx);
        check(!ctx.onClient(() -> ctx.mc().player.mayFly()), "Survival player should not fly without the ring");

        ctx.onServerDo(RingClientTests::equip);
        ctx.await("client to see flight from the synced attribute", () -> ctx.mc().player.mayFly());

        doubleTapJump(ctx);
        ctx.await("client player to start flying", () -> ctx.mc().player.getAbilities().flying);
        ctx.await("server to accept flying", () -> ctx.onServer(p -> p.getAbilities().flying));
    }

    @ClientTest(value = "removing the ring ends flight and the player falls", order = 2)
    public static void removingRingEndsFlight(ClientTestContext ctx) {
        prepareSurvival(ctx);
        ctx.onServerDo(RingClientTests::equip);
        ctx.await("client to see flight", () -> ctx.mc().player.mayFly());
        doubleTapJump(ctx);
        ctx.await("client player to start flying", () -> ctx.mc().player.getAbilities().flying);

        // Rise a few blocks so a fall is observable.
        ctx.onClient(() -> ctx.mc().options.keyJump.setDown(true));
        ctx.waitTicks(15);
        ctx.onClient(() -> ctx.mc().options.keyJump.setDown(false));
        double airborneY = ctx.onClient(() -> ctx.mc().player.getY());
        check(airborneY > STAND.getY() + 1, "Player should be airborne, y=" + airborneY);

        ctx.onServerDo(RingClientTests::unequip);
        ctx.await("client flight to end", () -> !ctx.mc().player.mayFly() && !ctx.mc().player.getAbilities().flying);
        ctx.await("player to land", 10, () -> ctx.mc().player.onGround());
        check(ctx.onClient(() -> ctx.mc().player.getY()) < airborneY, "Player should have fallen");
    }

    // Helpers

    private static void prepareSurvival(ClientTestContext ctx) {
        ctx.fill(new BlockPos(-3, -60, -3), new BlockPos(3, -50, 3), Blocks.AIR.defaultBlockState());
        ctx.onServerDo(p -> {
            unequip(p);
            p.getInventory().clearContent();
        });
        ctx.setGameMode(GameType.SURVIVAL);
        ctx.teleport(STAND, 180, 0);
        ctx.await("player standing on the ground", () -> ctx.mc().player.onGround());
    }

    /** Presses jump twice on exact ticks, inside vanilla's 7 tick double-tap window. */
    private static void doubleTapJump(ClientTestContext ctx) {
        var jump = ctx.mc().options.keyJump;
        ctx.perTick(4, i -> jump.setDown(i % 2 == 0));
        ctx.waitTicks(2);
    }

    private static void equip(ServerPlayer player) {
        ItemStack ring = new ItemStack(AngelRing.ANGEL_RING.get());
        if(RingFlight.curiosLoaded()) Curios.set(player, ring);
        else player.getInventory().setItem(8, ring);
    }

    private static void unequip(ServerPlayer player) {
        if(RingFlight.curiosLoaded()) Curios.set(player, ItemStack.EMPTY);
        else player.getInventory().setItem(8, ItemStack.EMPTY);
    }

    /** Only loaded when Curios is installed. */
    private static final class Curios
    {
        static void set(ServerPlayer player, ItemStack stack) {
            CuriosApi.getCuriosInventory(player).orElseThrow().setEquippedCurio("ring", 0, stack);
        }
    }
}
