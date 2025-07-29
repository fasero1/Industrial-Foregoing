package com.buuz135.industrial.integration.guideme;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "industrialforegoing", value = Dist.CLIENT)
public class KeyHandler {

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event) {
        // Перевіряємо чи натиснута клавіша G (тільки при натисканні, не при відпусканні)
        if (event.getKey() == GLFW.GLFW_KEY_G && event.getAction() == GLFW.GLFW_PRESS) {
            handleGuideKeyPress();
        }
    }

    private static void handleGuideKeyPress() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;

        // Отримуємо предмет на який наведений курсор
        ItemStack hoveredStack = getHoveredItemStack();

        if (hoveredStack != null && !hoveredStack.isEmpty()) {
            String itemName = hoveredStack.getItem().toString();

            // Перевіряємо чи це предмет Industrial Foregoing
            if (itemName.contains("industrialforegoing")) {
                openGuideForItem(hoveredStack);
                return;
            }
        }

        // Якщо не знайдено hovered item, показуємо повідомлення
        mc.player.displayClientMessage(
                Component.literal("§7Hover over an Industrial Foregoing item and press G"),
                true
        );
    }

    /**
     * Отримує ItemStack на який наведений курсор у відкритому GUI
     */
    private static ItemStack getHoveredItemStack() {
        Minecraft mc = Minecraft.getInstance();
        Screen currentScreen = mc.screen;

        if (currentScreen == null) return ItemStack.EMPTY;

        // Перевіряємо чи це контейнер (інвентар, сундук, машина тощо)
        if (currentScreen instanceof AbstractContainerScreen<?> containerScreen) {
            return getHoveredItemFromContainer(containerScreen);
        }

        // TODO: Додати підтримку для JEI/EMI/REI
        // if (isJEIScreen(currentScreen)) {
        //     return getHoveredItemFromJEI(currentScreen);
        // }

        return ItemStack.EMPTY;
    }

    /**
     * Отримує hovered item з контейнера (інвентар, машини тощо)
     */
    private static ItemStack getHoveredItemFromContainer(AbstractContainerScreen<?> screen) {
        try {
            // Використовуємо рефлексію для доступу до hoveredSlot
            java.lang.reflect.Field hoveredSlotField = AbstractContainerScreen.class.getDeclaredField("hoveredSlot");
            hoveredSlotField.setAccessible(true);
            Slot hoveredSlot = (Slot) hoveredSlotField.get(screen);

            if (hoveredSlot != null && hoveredSlot.hasItem()) {
                return hoveredSlot.getItem();
            }
        } catch (Exception e) {
            // Якщо рефлексія не працює, спробуємо альтернативний метод
            return tryAlternativeMethod(screen);
        }

        return ItemStack.EMPTY;
    }

    /**
     * Альтернативний метод для отримання hovered item
     */
    private static ItemStack tryAlternativeMethod(AbstractContainerScreen<?> screen) {
        try {
            // Спробуємо через getSlotUnderMouse (якщо такий метод існує)
            Slot slot = screen.getSlotUnderMouse();
            if (slot != null && slot.hasItem()) {
                return slot.getItem();
            }
        } catch (Exception e) {
            // Ігноруємо помилки
        }

        return ItemStack.EMPTY;
    }

    /**
     * Відкриває guide для конкретного предмета
     */
    private static void openGuideForItem(ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();

        // Визначаємо сторінку guide на основі предмета
        String itemName = stack.getItem().toString();
        String guidePage = determineGuidePage(itemName);

        // Показуємо повідомлення гравцю
        if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.literal("§aOpening guide: " + stack.getHoverName().getString()),
                    true // action bar
            );

            mc.player.displayClientMessage(
                    Component.literal("§7Page: " + guidePage),
                    false
            );
        }

        // ВИПРАВЛЕННЯ: Використовуємо ClientGuideHelper замість старого методу
        ClientGuideHelper.openGuideForItem(itemName, guidePage);
    }

    /**
     * Визначає яку сторінку guide відкривати для конкретного предмета
     */
    private static String determineGuidePage(String itemName) {
        if (itemName.contains("latex_processing")) {
            return "machines/latex_processing_unit.md";  // ПРАВИЛЬНИЙ шлях з machines/
        } else if (itemName.contains("tree_fluid_extractor")) {
            return "machines/tree_fluid_extractor.md";
        } else if (itemName.contains("plant_sower")) {
            return "machines/plant_sower.md";
        } else if (itemName.contains("plant_gatherer")) {
            return "machines/plant_gatherer.md";
        }

        // За замовчуванням - головна сторінка
        return "index.md";
    }
}