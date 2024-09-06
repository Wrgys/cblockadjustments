package com.wrgy.cblockadjustments;

import com.mojang.logging.LogUtils;
import com.wrgy.cblockadjustments.client.CustomCommandBlockScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CBlockAdjustments.MOD_ID)
public class CBlockAdjustments {
    public static final String MOD_ID = "cblockadjustments";

    public static final Logger LOGGER = LogUtils.getLogger();

    public CBlockAdjustments() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        CustomCommandBlockScreen.clientSetup(event);
    }
}
