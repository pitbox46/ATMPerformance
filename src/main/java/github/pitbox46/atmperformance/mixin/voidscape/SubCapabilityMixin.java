package github.pitbox46.atmperformance.mixin.voidscape;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tamaized.voidscape.capability.SubCapability;

import java.util.function.Consumer;

@Mixin(value = SubCapability.class, remap = false)
public class SubCapabilityMixin {
    @ModifyArg(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/eventbus/api/IEventBus;addListener(Ljava/util/function/Consumer;)V",
                    ordinal = 1
            ),
            method = "init"
    )
    private static Consumer<LivingEvent.LivingTickEvent> replaceLivingTickHandler(Consumer<LivingEvent.LivingTickEvent> consumer){
        return event -> {
            Entity entity = event.getEntity();
            if (!entity.canUpdate())
                return;
            entity.getCapability(SubCapability.CAPABILITY).ifPresent(cap -> {
                for (SubCapability.ISubCap.ISubCapData.ITickHandler ticker : cap.tickers()) {
                    ticker.tick(entity);
                }

                if (entity instanceof ServerPlayer serverPlayer) {
                    if (cap.getLastWorld() != serverPlayer.level().dimension().location()) {
                        for (SubCapability.ISubCap.ISubCapData.INetworkHandler network : cap.network()) {
                            network.sendToClient(serverPlayer);
                        }
                        cap.setLastWorld(serverPlayer.level().dimension().location());
                    }
                }
            });
        };
    }
}
