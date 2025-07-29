package com.buuz135.industrial.integration.guideme;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "industrialforegoing")
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        // Перевіряємо чи це предмет Industrial Foregoing
        if (stack.getItem().toString().contains("industrialforegoing")) {
            // Додаємо підказку про натискання G
            event.getToolTip().add(Component.literal("")); // Порожній рядок

            if (GuideMEIntegration.isAvailable()) {
                event.getToolTip().add(Component.literal("§7Press §eG §7for guide")
                        .withStyle(ChatFormatting.GRAY));
                event.getToolTip().add(Component.literal("§aGuideME ready")
                        .withStyle(ChatFormatting.GREEN));
            } else {
                event.getToolTip().add(Component.literal("§7Install GuideME for guide support")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }
}