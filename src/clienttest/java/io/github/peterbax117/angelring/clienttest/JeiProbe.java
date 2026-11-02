package io.github.peterbax117.angelring.clienttest;

import io.github.peterbax117.angelring.AngelRing;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

/** Test-only JEI plugin that exposes JEI's runtime to client tests. Only loaded when JEI is installed. */
@JeiPlugin
public class JeiProbe implements IModPlugin
{
    private static volatile IJeiRuntime runtime;

    @Override
    public ResourceLocation getPluginUid() {
        return AngelRing.loc("jei_test_probe");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    static boolean ready() {
        return runtime != null;
    }

    static boolean ringHasInfoPage() {
        return runtime.getRecipeManager().createRecipeLookup(RecipeTypes.INFORMATION).get()
                .anyMatch(info -> info.getIngredients().stream()
                        .anyMatch(ingredient -> ingredient.getItemStack().map(s -> s.is(AngelRing.ANGEL_RING.get())).orElse(false)));
    }
}
