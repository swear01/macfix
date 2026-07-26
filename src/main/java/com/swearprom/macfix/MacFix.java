package com.swearprom.macfix;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(value = MacFix.MOD_ID, dist = Dist.CLIENT)
public final class MacFix {
    public static final String MOD_ID = "macfix";
    static final Logger LOGGER = LogUtils.getLogger();

    public MacFix(IEventBus modEventBus) {
        LOGGER.info("macfix 0.1.0 loaded (client-only macOS fixes)");
    }
}
