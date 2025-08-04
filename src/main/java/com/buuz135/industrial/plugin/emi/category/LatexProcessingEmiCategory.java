package com.buuz135.industrial.plugin.emi.category;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.plugin.emi.recipe.LatexProcessingEmiRecipe;
import com.buuz135.industrial.utils.Reference;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class LatexProcessingEmiCategory extends EmiRecipeCategory {

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "latex_processing");

    public LatexProcessingEmiCategory() {
        super(ID, EmiStack.of(ModuleCore.LATEX_PROCESSING.asItem()));
    }

    @Override
    public @Nullable Comparator<EmiRecipe> getSort() {
        return (o1, o2) -> {
            LatexProcessingEmiRecipe e1 = (LatexProcessingEmiRecipe) o1;
            LatexProcessingEmiRecipe e2 = (LatexProcessingEmiRecipe) o2;
            // Сортуємо за ефективністю (менше енергії = краще)
            int energyCompare = Integer.compare(e1.getRecipe().value().getEnergyRequired(), e2.getRecipe().value().getEnergyRequired());
            if (energyCompare != 0) return energyCompare;
            // Якщо енергія однакова, сортуємо за часом обробки
            return Integer.compare(e1.getRecipe().value().getProcessingTime(), e2.getRecipe().value().getProcessingTime());
        };
    }
}