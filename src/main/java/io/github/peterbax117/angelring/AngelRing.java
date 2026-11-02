package io.github.peterbax117.angelring;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(AngelRing.MODID)
public final class AngelRing
{
    public static final String MODID = "angelring_pb";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<AngelRingItem> ANGEL_RING = ITEMS.register("angel_ring",
            () -> new AngelRingItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public AngelRing(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(AngelRing::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) event.accept(ANGEL_RING);
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
