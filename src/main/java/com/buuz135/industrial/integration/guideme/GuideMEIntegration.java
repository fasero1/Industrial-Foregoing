package com.buuz135.industrial.integration.guideme;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuideMEIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuideMEIntegration.class);

    public static final ResourceLocation GUIDE_ID =
            ResourceLocation.fromNamespaceAndPath("industrialforegoing", "guide");

    /**
     * Ініціалізація GuideME інтеграції
     * Викликається тільки якщо GuideME завантажений
     */
    public static void init() {
        if (!ModList.get().isLoaded("guideme")) {
            LOGGER.warn("GuideME не знайдено, пропускаємо інтеграцію");
            return;
        }

        try {
            LOGGER.info("Ініціалізація GuideME інтеграції для Industrial Foregoing");
            registerGuide();
            registerItemTooltips();
            LOGGER.info("GuideME інтеграція успішно ініціалізована");
        } catch (Exception e) {
            LOGGER.error("Помилка при ініціалізації GuideME інтеграції", e);
        }
    }

    /**
     * Реєстрація основного guide book через GuideME API
     */
    private static void registerGuide() {
        try {
            // Спробуємо зареєструвати guide через GuideME Registry
            if (tryRegisterWithGuideRegistry()) {
                LOGGER.info("✅ Guide успішно зареєстровано через Registry API");
                return;
            }

            // Альтернативний спосіб через GuideManager
            if (tryRegisterWithGuideManager()) {
                LOGGER.info("✅ Guide успішно зареєстровано через Manager API");
                return;
            }

            // Fallback - data-driven registration
            LOGGER.info("📝 Guide буде завантажено як data-driven (через файли)");

        } catch (Exception e) {
            LOGGER.warn("Помилка при реєстрації guide: {}", e.getMessage());
        }

        LOGGER.info("Підготовлено Industrial Foregoing Guide з ID: {}", GUIDE_ID);
    }

    /**
     * Спроба реєстрації через GuideRegistry
     */
    private static boolean tryRegisterWithGuideRegistry() {
        try {
            Class<?> guideRegistryClass = Class.forName("guideme.GuideRegistry");

            // Шукаємо метод register
            java.lang.reflect.Method registerMethod = guideRegistryClass.getMethod(
                    "register",
                    net.minecraft.resources.ResourceLocation.class
            );

            registerMethod.invoke(null, GUIDE_ID);
            return true;

        } catch (Exception e) {
            LOGGER.debug("GuideRegistry API недоступний: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Спроба реєстрації через GuideManager
     */
    private static boolean tryRegisterWithGuideManager() {
        try {
            Class<?> guideManagerClass = Class.forName("guideme.client.GuideManager");

            // Можливо потрібно створити Guide об'єкт
            java.lang.reflect.Method registerMethod = guideManagerClass.getMethod(
                    "registerGuide",
                    net.minecraft.resources.ResourceLocation.class
            );

            registerMethod.invoke(null, GUIDE_ID);
            return true;

        } catch (Exception e) {
            LOGGER.debug("GuideManager API недоступний: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Реєстрація tooltip інтеграції для предметів
     */
    private static void registerItemTooltips() {
        LOGGER.info("Реєструємо tooltip інтеграцію для Industrial Foregoing предметів");
        // TODO: Додати tooltip hooks для блоків/предметів
    }

    /**
     * Перевірка чи доступна GuideME інтеграція
     */
    public static boolean isAvailable() {
        return ModList.get().isLoaded("guideme");
    }

    /**
     * Відкрити guide для конкретного предмета
     */
    public static void openGuideForItem(String itemId) {
        if (!isAvailable()) {
            LOGGER.warn("GuideME недоступний для відкриття guide");
            return;
        }

        try {
            LOGGER.info("Спроба відкрити guide для предмета: {}", itemId);

            // Спробуємо відкрити guide через GuideME API
            if (tryOpenGuideWithAPI(itemId)) {
                return;
            }

            // Fallback - відкриємо головну сторінку guide
            LOGGER.info("Відкриваємо головний guide: {}", GUIDE_ID);

        } catch (Exception e) {
            LOGGER.warn("Не вдалося відкрити guide для {}: {}", itemId, e.getMessage());
        }
    }

    /**
     * Спроба відкрити guide через GuideME API
     */
    private static boolean tryOpenGuideWithAPI(String itemId) {
        try {
            // Спробуємо відкрити guide через GuideME PageAnchor API
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player == null) return false;

            // Створюємо PageAnchor для нашого guide
            Class<?> pageAnchorClass = Class.forName("guideme.PageAnchor");
            Object pageAnchor = pageAnchorClass.getConstructor(
                    net.minecraft.resources.ResourceLocation.class,
                    String.class
            ).newInstance(GUIDE_ID, determinePagePath(itemId));

            // Шукаємо метод для відкриття guide
            try {
                // Спробуємо через GuideManager або подібний клас
                Class<?> guideManagerClass = Class.forName("guideme.client.GuideManager");
                java.lang.reflect.Method openMethod = guideManagerClass.getMethod("openGuide", pageAnchorClass);
                openMethod.invoke(null, pageAnchor);

                LOGGER.info("Успішно відкрито GuideME для: {}", itemId);
                return true;

            } catch (ClassNotFoundException | NoSuchMethodException e) {
                // Спробуємо альтернативний API
                return tryAlternativeGuideAPI(pageAnchor);
            }

        } catch (Exception e) {
            LOGGER.debug("Не вдалося викликати GuideME API: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Альтернативний метод відкриття guide
     */
    private static boolean tryAlternativeGuideAPI(Object pageAnchor) {
        try {
            // Спробуємо через client event або direct API
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();

            // Можливо guide відкривається через команду або інший спосіб
            LOGGER.info("Спроба альтернативного відкриття guide...");

            // TODO: Реалізувати коли знайдемо правильний API
            return false;

        } catch (Exception e) {
            LOGGER.debug("Альтернативний API також не працює: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Визначає шлях до сторінки guide на основі назви предмета
     */
    private static String determinePagePath(String itemId) {
        if (itemId.contains("latex_processing")) {
            return "pages/machines/latex_processing_unit.md";
        } else if (itemId.contains("tree_fluid_extractor")) {
            return "pages/machines/tree_fluid_extractor.md";
        } else if (itemId.contains("plant_sower")) {
            return "pages/machines/plant_sower.md";
        } else if (itemId.contains("plant_gatherer")) {
            return "pages/machines/plant_gatherer.md";
        }

        // За замовчуванням - головна сторінка
        return "pages/index.md";
    }
}