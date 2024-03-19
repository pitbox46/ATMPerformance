package github.pitbox46.atmperformance;

import com.mojang.logging.LogUtils;
import github.pitbox46.atmperformance.benchmark.Benchmark;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.slf4j.Logger;

/* MISC TODO
 * Add config option for Mimic tick distance
 * Add Mixin config
 */
@Mod(ATMPerformance.MODID)
public class ATMPerformance {
    public static final String MODID = "atmperformance";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Benchmark MAIN_BENCHMARK = new Benchmark("MAIN");

    public ATMPerformance() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }
}
