package github.pitbox46.atmperformance;

import com.mojang.logging.LogUtils;
import github.pitbox46.atmperformance.benchmark.Benchmark;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    static class ClientEvents {
        static long worldJoinTime = 0;

        @SubscribeEvent
        public static void onChangeScreen(ScreenEvent.Opening event) {
            if (event.getNewScreen() != null && event.getNewScreen().getTitle().getString().equals("Reading world data...")) {
                worldJoinTime = System.nanoTime();
            }
        }

        @SubscribeEvent
        public static void onRenderLevelStage(RenderLevelStageEvent event) {
            if (worldJoinTime != -1) {
                LOGGER.info("Time to join world (seconds): {}", (System.nanoTime() - worldJoinTime) / 1_000_000_000F);
                worldJoinTime = -1;
            }
        }
    }
}
