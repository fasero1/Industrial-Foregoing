/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.recipe;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class LatexProcessingRecipe implements Recipe<CraftingInput> {

    public static final MapCodec<LatexProcessingRecipe> CODEC = RecordCodecBuilder.mapCodec(in -> in.group(
            SizedFluidIngredient.FLAT_CODEC.fieldOf("latexInput").forGetter(o -> o.latexInput),
            SizedFluidIngredient.FLAT_CODEC.fieldOf("waterInput").forGetter(o -> o.waterInput),
            ItemStack.CODEC.fieldOf("output").forGetter(o -> o.output),
            Codec.INT.optionalFieldOf("processingTime", 200).forGetter(o -> o.processingTime),
            Codec.INT.optionalFieldOf("energyRequired", 1000).forGetter(o -> o.energyRequired),
            Codec.BOOL.optionalFieldOf("defaultRecipe", false).forGetter(o -> o.defaultRecipe)
    ).apply(in, LatexProcessingRecipe::new));

    public SizedFluidIngredient latexInput;
    public SizedFluidIngredient waterInput;
    public ItemStack output;
    public int processingTime;
    public int energyRequired;
    public boolean defaultRecipe;

    public LatexProcessingRecipe(SizedFluidIngredient latexInput, SizedFluidIngredient waterInput, ItemStack output, int processingTime, int energyRequired, boolean defaultRecipe) {
        this.latexInput = latexInput;
        this.waterInput = waterInput;
        this.output = output;
        this.processingTime = processingTime;
        this.energyRequired = energyRequired;
        this.defaultRecipe = defaultRecipe;
    }

    public SizedFluidIngredient getLatexInput() {
        return latexInput;
    }

    public SizedFluidIngredient getWaterInput() {
        return waterInput;
    }

    public int getLatexAmount() {
        return latexInput.amount();
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getEnergyRequired() {
        return energyRequired;
    }

    public boolean matches(FluidStack latex, FluidStack water) {
        return latexInput.test(latex) && waterInput.test(water);
    }

    @Override
    public boolean matches(CraftingInput craftingInput, Level level) {
        return false; // Цей рецепт не для крафтингу
    }

    @Override
    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModuleCore.LATEX_PROCESSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModuleCore.LATEX_PROCESSING_TYPE.get();
    }
}