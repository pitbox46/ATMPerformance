package github.pitbox46.atmperformance.mixin.gregtech;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.UnificationEntry;
import github.pitbox46.atmperformance.ATMPerformance;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(value = ChemicalHelper.class, remap = false)
public class ChemicalHelperMixin {
    @Shadow @Final public static Set<Map.Entry<Supplier<? extends ItemLike>, UnificationEntry>> ITEM_UNIFICATION_ENTRY;
    @Shadow @Final public static Map<ItemLike, UnificationEntry> ITEM_UNIFICATION_ENTRY_COLLECTED;
    @Unique
    private static final Object2ObjectMap<ItemLike, UnificationEntry> FAST_ITEM_UNIFICATION_ENTRY_COLLECTED = new Object2ObjectOpenHashMap<>();
    private static long totalTimeSpent = 0;

    /**
     * @author pitbox46
     * @reason Complete overhaul
     */
    @Nullable
    @Overwrite
    public static UnificationEntry getUnificationEntry(ItemLike itemLike) {
        long start = System.nanoTime();
//        for (Map.Entry<Supplier<? extends ItemLike>, UnificationEntry> entry : ITEM_UNIFICATION_ENTRY) {
//            FAST_ITEM_UNIFICATION_ENTRY_COLLECTED.put(entry.getKey().get(), entry.getValue());
//        }
//        ITEM_UNIFICATION_ENTRY.clear();
//
//        UnificationEntry retValue = FAST_ITEM_UNIFICATION_ENTRY_COLLECTED.get(itemLike);

        UnificationEntry retValue = ITEM_UNIFICATION_ENTRY_COLLECTED.computeIfAbsent(itemLike, (item) -> {
            Iterator<Map.Entry<Supplier<? extends ItemLike>, UnificationEntry>> var2 = ITEM_UNIFICATION_ENTRY.iterator();

            Map.Entry<Supplier<? extends ItemLike>, UnificationEntry> entry;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                entry = var2.next();
            } while(entry.getKey().get().asItem() != itemLike.asItem());

            return entry.getValue();
        });
        long diff = System.nanoTime() - start;
        totalTimeSpent += diff;
        ATMPerformance.LOGGER.debug("Unification - Total Time (ms): {}, Individual Time (ms): {}", totalTimeSpent / 1000000F, diff / 1000000F);
        return retValue;
    }
}
