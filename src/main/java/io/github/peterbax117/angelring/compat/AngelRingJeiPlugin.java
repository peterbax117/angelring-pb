package io.github.peterbax117.angelring.compat;

import io.github.peterbax117.angelring.AngelRing;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** JEI info page. JEI discovers this class itself, so it is never loaded when JEI is absent. */
@JeiPlugin
public class AngelRingJeiPlugin implements IModPlugin
{
    @Override
    public ResourceLocation getPluginUid() {
        return AngelRing.loc("jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(AngelRing.ANGEL_RING.get(), Component.translatable("jei.angelring_pb.angel_ring.info"));
    }
}
