package com.buuz135.industrial.integration.guideme;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = "industrialforegoing")
public class GuideCommand {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("if-guide")
                        .requires(source -> source.hasPermission(0)) // Доступно всім гравцям
                        .executes(GuideCommand::openGuide)
                        .then(Commands.literal("test")
                                .executes(GuideCommand::testGuide))
        );
    }

    /**
     * Відкриває Industrial Foregoing guide
     */
    private static int openGuide(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getEntity() instanceof ServerPlayer player) {
            if (GuideMEIntegration.isAvailable()) {
                player.sendSystemMessage(Component.literal("§aOpening Industrial Foregoing Guide..."));

                // Спробуємо серверний виклик GuideME API
                if (tryServerSideGuideOpen(player)) {
                    player.sendSystemMessage(Component.literal("§aGuide opened successfully!"));
                } else {
                    player.sendSystemMessage(Component.literal("§cFailed to open guide - check client log"));
                    player.sendSystemMessage(Component.literal("§7Try pressing G over Industrial Foregoing items"));
                }
            } else {
                player.sendSystemMessage(Component.literal("§cGuideME is not available!"));
                player.sendSystemMessage(Component.literal("§7Please install GuideME mod to use this feature."));
            }
        }

        return 1;
    }

    /**
     * Спробувати відкрити guide з серверного боку
     */
    private static boolean tryServerSideGuideOpen(ServerPlayer player) {
        try {
            // Використовуємо GuidesCommon API для серверного виклику
            Class<?> guidesCommonClass = Class.forName("guideme.GuidesCommon");

            java.lang.reflect.Method openMethod = guidesCommonClass.getMethod(
                    "openGuide",
                    net.minecraft.world.entity.player.Player.class,
                    net.minecraft.resources.ResourceLocation.class
            );

            openMethod.invoke(null, player, GuideMEIntegration.GUIDE_ID);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Тестує GuideME інтеграцію
     */
    private static int testGuide(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getEntity() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal("§6=== Industrial Foregoing Guide Test ==="));
            player.sendSystemMessage(Component.literal("§7GuideME available: " +
                    (GuideMEIntegration.isAvailable() ? "§aYes" : "§cNo")));
            player.sendSystemMessage(Component.literal("§7Guide ID: §b" + GuideMEIntegration.GUIDE_ID));
            player.sendSystemMessage(Component.literal("§7Hold G over Industrial Foregoing items for quick access!"));
        }

        return 1;
    }
    /**
     * Діагностика GuideME інтеграції
     */
    private static int debugGuide(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (source.getEntity() instanceof ServerPlayer player) {
            player.sendSystemMessage(Component.literal("§6=== GuideME Debug Info ==="));
            player.sendSystemMessage(Component.literal("§7Current Guide ID: §b" + GuideMEIntegration.GUIDE_ID));

            // Перевірка файлової структури
            try {
                // TODO: Додати перевірку існування файлів через ResourceManager
                player.sendSystemMessage(Component.literal("§7Expected path: §eassets/industrialforegoing/guides/main_guide/"));
                player.sendSystemMessage(Component.literal("§7Files to check:"));
                player.sendSystemMessage(Component.literal("§7  - guide.json"));
                player.sendSystemMessage(Component.literal("§7  - pages/index.md"));
                player.sendSystemMessage(Component.literal("§7  - pages/machines/latex_processing_unit.md"));

            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("§cError during file check: " + e.getMessage()));
            }

            // Спроба прямого виклику GuideME
            try {
                player.sendSystemMessage(Component.literal("§7Testing direct GuideME call..."));
                // TODO: Додати тест

            } catch (Exception e) {
                player.sendSystemMessage(Component.literal("§cDirect call failed: " + e.getMessage()));
            }
        }

        return 1;
    }
}