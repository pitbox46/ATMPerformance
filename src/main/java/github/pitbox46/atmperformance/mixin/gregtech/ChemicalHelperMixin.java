package github.pitbox46.atmperformance.mixin.gregtech;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.UnificationEntry;
import github.pitbox46.atmperformance.ATMPerformance;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(value = ChemicalHelper.class, remap = false)
public class ChemicalHelperMixin {
    @Shadow @Final public static Set<Map.Entry<Supplier<? extends ItemLike>, UnificationEntry>> ITEM_UNIFICATION_ENTRY;
    @Shadow @Final public static Map<ItemLike, UnificationEntry> ITEM_UNIFICATION_ENTRY_COLLECTED;
    @Unique
    private static final Object2ObjectMap<ItemLike, UnificationEntry> FAST_ITEM_UNIFICATION_ENTRY_COLLECTED = new Object2ObjectOpenHashMap<>();

    /**
     * @author pitbox46
     * @reason Complete overhaul
     */
    @Nullable
    @Overwrite
    public static UnificationEntry getUnificationEntry(ItemLike itemLike) {
        for (Map.Entry<Supplier<? extends ItemLike>, UnificationEntry> entry : ITEM_UNIFICATION_ENTRY) {
            FAST_ITEM_UNIFICATION_ENTRY_COLLECTED.put(entry.getKey().get(), entry.getValue());
        }
        ITEM_UNIFICATION_ENTRY.clear();

        return FAST_ITEM_UNIFICATION_ENTRY_COLLECTED.get(itemLike);

        // Default Method, here for performance before/after comparison
//        return ATMPerformance.MAIN_BENCHMARK.benchmarkAndLog(() -> {
//            return ITEM_UNIFICATION_ENTRY_COLLECTED.computeIfAbsent(itemLike, (item) -> {
//                Iterator<Map.Entry<Supplier<? extends ItemLike>, UnificationEntry>> var2 = ITEM_UNIFICATION_ENTRY.iterator();
//
//                Map.Entry<Supplier<? extends ItemLike>, UnificationEntry> entry;
//                do {
//                    if (!var2.hasNext()) {
//                        return null;
//                    }
//
//                    entry = var2.next();
//                } while(entry.getKey().get().asItem() != itemLike.asItem());
//
//                return entry.getValue();
//            });
//        }).value();
    }
}
