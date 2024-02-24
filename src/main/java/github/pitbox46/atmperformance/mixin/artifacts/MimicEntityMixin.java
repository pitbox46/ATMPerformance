package github.pitbox46.atmperformance.mixin.artifacts;

import artifacts.entity.MimicEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mimics don't despawn, so we end up accumulating them. Luckily, they also don't really do anything if a
 * player is not nearby. Because of this, we just skip ticking mimics that are too far away from a player.
 */
@Mixin(MimicEntity.class)
public abstract class MimicEntityMixin extends Mob {
    /**
     * Radius needed to be able to tick {@link #aiStep()}
     */
    @Unique
    private static final int RADIUS_TO_PLAYER = 32;

    @Shadow(remap = false) public boolean isDormant;

    @Shadow public abstract void setDormant(boolean isDormant);

    protected MimicEntityMixin(EntityType<? extends Mob> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void skipTick(CallbackInfo ci) {
        if (!this.level().hasNearbyAlivePlayer(this.getX(), this.getY(), this.getZ(), RADIUS_TO_PLAYER)) {
            if (!isDormant) {
                setDormant(true);
            }
            ci.cancel();
        }
    }
}
