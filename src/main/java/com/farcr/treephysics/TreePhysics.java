package com.farcr.treephysics;

import com.farcr.treephysics.event.CommonEvents;
import com.farcr.treephysics.index.TreePhysicsConfig;
import com.farcr.treephysics.index.TreePhysicsParticleTypes;
import com.farcr.treephysics.networking.TreePhysicsPackets;
import com.mojang.logging.LogUtils;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(TreePhysics.MOD_ID)
public class TreePhysics {
    public static final String MOD_ID = "treephysics";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TreePhysics(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(CommonEvents.class);
        SableEventPlatform.INSTANCE.onSubLevelContainerReady(CommonEvents::containerReady);
        SableEventPlatform.INSTANCE.onPostPhysicsTick(CommonEvents::postPhysicsTick);
        modContainer.registerConfig(ModConfig.Type.COMMON, TreePhysicsConfig.SPEC);
        if (FMLEnvironment.dist.isClient()) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
        TreePhysicsPackets.init();
        TreePhysicsParticleTypes.init(modEventBus);
    }

    public static ResourceLocation path(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

}
