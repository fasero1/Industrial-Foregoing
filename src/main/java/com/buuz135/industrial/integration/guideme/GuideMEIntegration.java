package com.buuz135.industrial.integration.guideme;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuideMEIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuideMEIntegration.class);

    public static final ResourceLocation GUIDE_ID =
            ResourceLocation.fromNamespaceAndPath("industrialforegoing", "guide");

    public static void init() {}

    public static boolean isAvailable() {
        return ModList.get().isLoaded("guideme");
    }
}