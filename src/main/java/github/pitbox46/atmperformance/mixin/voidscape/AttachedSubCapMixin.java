package github.pitbox46.atmperformance.mixin.voidscape;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tamaized.voidscape.capability.SubCapability;

import java.util.*;

/**
 * Avoids unnecessary casting, stream creation, filtering, and array creation
 */
@Mixin(value = SubCapability.AttachedSubCap.class, remap = false)
public abstract class AttachedSubCapMixin implements SubCapability.ISubCap {
    @Unique
    private final List<ISubCapData.ITickHandler> atmPerformance$tickers = new ArrayList<>();
    @Unique
    private final List<ISubCapData.IStorageHandler> atmPerformance$storage = new ArrayList<>();
    @Unique
    private final List<ISubCapData.INetworkHandler> atmPerformance$network = new ArrayList<>();
    @Unique
    private final Map<ResourceLocation, ISubCapData.INetworkHandler> atmPerformance$networkByID = new HashMap<>();
    @Shadow @Final private Map<SubCapKey<?>, ISubCapData> instances;

    @Inject(at = @At(value = "RETURN"), method = "<init>")
    private void onInit(CallbackInfo ci) {
        atmPerformance$populateCaches();
    }

    @Unique
    private void atmPerformance$populateCaches() {
        atmPerformance$tickers.clear();
        atmPerformance$storage.clear();
        atmPerformance$network.clear();
        atmPerformance$networkByID.clear();
        for (ISubCapData data : instances.values()) {
            if (data instanceof ISubCapData.ITickHandler handler) {
                atmPerformance$tickers.add(handler);
            } else if (data instanceof ISubCapData.IStorageHandler handler) {
                atmPerformance$storage.add(handler);
            } else if (data instanceof ISubCapData.INetworkHandler handler) {
                atmPerformance$network.add(handler);
                atmPerformance$networkByID.put(handler.id(), handler);
            }
        }
    }

    /**
     * @author pitbox46
     * @reason Performance optimization
     */
    @Overwrite
    public ISubCapData.ITickHandler[] tickers() {
        return atmPerformance$tickers.toArray(new ISubCapData.ITickHandler[0]);
    }

    /**
     * @author pitbox46
     * @reason Performance optimization
     */
    @Overwrite
    public ISubCapData.IStorageHandler[] storage() {
        return atmPerformance$storage.toArray(new ISubCapData.IStorageHandler[0]);
    }

    /**
     * @author pitbox46
     * @reason Performance optimization
     */
    @Overwrite
    public ISubCapData.INetworkHandler[] network() {
        return atmPerformance$network.toArray(new ISubCapData.INetworkHandler[0]);
    }

    /**
     * @author pitbox46
     * @reason Performance optimization
     */
    @Overwrite
    public void clone(SubCapability.ISubCap old, boolean death) {
        this.instances.forEach((k, v) -> old.get(k).ifPresent(o -> v.clone(o, death)));
        atmPerformance$populateCaches();
    }

    /**
     * @author pitbox46
     * @reason Performance optimization
     */
    @Overwrite
    public Optional<ISubCapData.INetworkHandler> network(ResourceLocation id) {
        return Optional.ofNullable(atmPerformance$networkByID.get(id));
    }
}
