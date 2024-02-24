package github.pitbox46.atmperformance;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ATMPerformance.MODID)
public class ATMPerformance {
    public static final String MODID = "atmperformance";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ATMPerformance() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}
