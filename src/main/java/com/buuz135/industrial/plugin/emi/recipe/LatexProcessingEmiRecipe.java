package com.buuz135.industrial.plugin.emi.recipe;

import com.buuz135.industrial.plugin.emi.IFEmiPlugin;
import com.buuz135.industrial.recipe.LatexProcessingRecipe;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.client.screen.asset.DefaultAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeHolder;

public class LatexProcessingEmiRecipe extends CustomEmiRecipe {

    private final RecipeHolder<LatexProcessingRecipe> recipe;

    public LatexProcessingEmiRecipe(RecipeHolder<LatexProcessingRecipe> recipe) {
        super(recipe.id(), IFEmiPlugin.LATEX_PROCESSING_EMI_CATEGORY,
                combineIng(
                        fromInputSingle(recipe.value().getLatexInput()),
                        fromInputSingle(recipe.value().getWaterInput())
                ),
                fromOutput(EmiStack.of(recipe.value().getResultItem(null))));
        this.recipe = recipe;
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        try {
            if (this.getInputs().size() >= 2 && this.getOutputs().size() >= 1) {
                // Латекс танк (ліворуч)
                widgets.addTank(this.getInputs().get(0), 10, 3, 14, 52, 10)
                        .backgroundTexture(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3)
                        .drawBack(false);

                // Вода танк (по центру)
                widgets.addTank(this.getInputs().get(1), 35, 3, 14, 52, 10)
                        .backgroundTexture(DefaultAssetProvider.DEFAULT_LOCATION, 177 + 3, 1 + 3)
                        .drawBack(false);

                // Вихідний слот (праворуч)
                widgets.addSlot(this.getOutputs().get(0), 80, 17).recipeContext(this);

                // Стрілка обробки
                widgets.addFillingArrow(55, 17, 5000); // Фіксована швидкість

                widgets.addDrawable(0, 0, 0, 0, (draw, mouseX, mouseY, delta) -> {
                    // Tank backgrounds
                    AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 8, 1);
                    AssetUtil.drawAsset(draw, Minecraft.getInstance().screen, DefaultAssetProvider.DEFAULT_PROVIDER.getAsset(AssetTypes.TANK_NORMAL), 33, 1);
                });
            } else {
                System.out.println("ERROR: Not enough inputs/outputs! Inputs: " + this.getInputs().size() + ", Outputs: " + this.getOutputs().size());
            }

        } catch (Exception e) {
            System.out.println("ERROR in addWidgets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public RecipeHolder<LatexProcessingRecipe> getRecipe() {
        return recipe;
    }
}