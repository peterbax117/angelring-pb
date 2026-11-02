package io.github.peterbax117.angelring;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AngelRingItem extends Item
{
    public AngelRingItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        String where = RingFlight.curiosLoaded() ? "curios" : "inventory";
        lines.add(Component.translatable("item.angelring_pb.angel_ring.tooltip." + where).withStyle(ChatFormatting.GRAY));
    }
}
