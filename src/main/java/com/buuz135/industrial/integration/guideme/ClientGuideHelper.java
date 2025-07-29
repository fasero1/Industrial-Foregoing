package com.buuz135.industrial.integration.guideme;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OnlyIn(Dist.CLIENT)
public class ClientGuideHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientGuideHelper.class);

    /**
     * Спробувати відкрити guide для предмета
     */
    public static void openGuideForItem(String itemId, String pagePath) {
        try {
            // Пробуємо прямий API виклик
            if (tryDirectGuideAPI(itemId, pagePath)) {
                return;
            }

            // Пробуємо через команду
            if (tryCommandBasedOpening(itemId, pagePath)) {
                return;
            }

            // Fallback - показуємо повідомлення що guide готовий, але API не доступний
            showGuideUnavailableMessage(itemId);

        } catch (Exception e) {
            LOGGER.warn("Помилка при відкритті guide: {}", e.getMessage());
            showGuideUnavailableMessage(itemId);
        }
    }

    /**
     * Прямий виклик GuideME API
     */
    private static boolean tryDirectGuideAPI(String itemId, String pagePath) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return false;

            // Створюємо ResourceLocation для guide
            ResourceLocation guideId = GuideMEIntegration.GUIDE_ID;

            LOGGER.info("Пробуємо відкрити guide: {} сторінка: {}", guideId, pagePath);

            // Метод 1: Через GuidesCommon API (правильний спосіб)
            if (tryGuidesCommonAPI(mc.player, guideId, pagePath)) {
                return true;
            }

            // Метод 2: Простий виклик без сторінки
            if (trySimpleGuideOpen(mc.player, guideId)) {
                return true;
            }

            return false;

        } catch (Exception e) {
            LOGGER.debug("Direct API не працює: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Визначає правильний шлях до сторінки
     */
    private static String determineCorrectPagePath(String itemId) {
        // НЕ ЗМІНЮЄМО pagePath - використовуємо той що прийшов з KeyHandler
        return null; // Не використовується
    }

    /**
     * Спроба через GuidesCommon API (офіційний спосіб)
     */
    private static boolean tryGuidesCommonAPI(net.minecraft.world.entity.player.Player player, ResourceLocation guideId, String pagePath) {
        try {
            // Спробуємо GuidesCommon.openGuide з PageAnchor
            Class<?> guidesCommonClass = Class.forName("guideme.GuidesCommon");
            Class<?> pageAnchorClass = Class.forName("guideme.PageAnchor");

            // Створюємо PageAnchor
            Object pageAnchor = pageAnchorClass.getConstructor(ResourceLocation.class, String.class)
                    .newInstance(guideId, pagePath);

            // Викликаємо GuidesCommon.openGuide(Player, ResourceLocation, PageAnchor)
            java.lang.reflect.Method openMethod = guidesCommonClass.getMethod(
                    "openGuide",
                    net.minecraft.world.entity.player.Player.class,
                    ResourceLocation.class,
                    pageAnchorClass
            );

            openMethod.invoke(null, player, guideId, pageAnchor);

            LOGGER.info("✅ Успішно відкрито через GuidesCommon API!");
            return true;

        } catch (Exception e) {
            LOGGER.debug("GuidesCommon API з PageAnchor недоступний: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Простий виклик GuidesCommon без конкретної сторінки
     */
    private static boolean trySimpleGuideOpen(net.minecraft.world.entity.player.Player player, ResourceLocation guideId) {
        try {
            // Спробуємо простий GuidesCommon.openGuide(Player, ResourceLocation)
            Class<?> guidesCommonClass = Class.forName("guideme.GuidesCommon");

            java.lang.reflect.Method openMethod = guidesCommonClass.getMethod(
                    "openGuide",
                    net.minecraft.world.entity.player.Player.class,
                    ResourceLocation.class
            );

            openMethod.invoke(null, player, guideId);

            LOGGER.info("✅ Успішно відкрито guide через простий GuidesCommon API!");
            return true;

        } catch (Exception e) {
            LOGGER.debug("Простий GuidesCommon API недоступний: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Спроба через PageAnchor API
     */
    private static boolean tryPageAnchorAPI(ResourceLocation guideId, String pagePath) {
        try {
            Class<?> pageAnchorClass = Class.forName("guideme.PageAnchor");
            Object anchor = pageAnchorClass.getConstructor(ResourceLocation.class, String.class)
                    .newInstance(guideId, pagePath);

            // Шукаємо клас для відкриття
            Class<?> guideApiClass = Class.forName("guideme.api.GuideAPI");
            java.lang.reflect.Method openMethod = guideApiClass.getMethod("openGuide", pageAnchorClass);
            openMethod.invoke(null, anchor);

            LOGGER.info("Успішно відкрито через PageAnchor API");
            return true;

        } catch (Exception e) {
            LOGGER.debug("PageAnchor API недоступний: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Спроба відкрити через команду
     */
    private static boolean tryCommandBasedOpening(String itemId, String pagePath) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return false;

            // Згідно з документацією: /give @s guideme:guide[guideme:guide_id="industrialforegoing:guide"]
            String command = String.format("give @s guideme:guide[guideme:guide_id=\"%s\"]",
                    GuideMEIntegration.GUIDE_ID.toString());

            // Виконуємо команду
            if (mc.player.connection != null) {
                mc.player.connection.sendCommand(command);

                mc.player.displayClientMessage(
                        Component.literal("§aGave Industrial Foregoing Guide book"),
                        true
                );

                LOGGER.info("Виконано команду: /{}", command);
                return true;
            }

            return false;

        } catch (Exception e) {
            LOGGER.debug("Command-based opening failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Показує повідомлення що guide недоступний
     */
    private static void showGuideUnavailableMessage(String itemId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(
                    Component.literal("§eGuide available but API connection failed"),
                    true
            );
            mc.player.displayClientMessage(
                    Component.literal("§7Try command: §b/give @s guideme:guide[guideme:guide_id=\"industrialforegoing:guide\"]"),
                    false
            );
            mc.player.displayClientMessage(
                    Component.literal("§7Guide files: §aassets/industrialforegoing/guides/"),
                    false
            );
        }
    }
}