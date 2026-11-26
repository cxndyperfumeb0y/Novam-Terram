package net.candyperfumeboy.novamterram.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Hands part of a vanilla biome's climate slots to a Novam Terram biome.
 * Whatever a rule does not cover stays vanilla.
 */
public final class NTSlotRules {

    private static final int T = 0, H = 1, C = 2, E = 3, W = 4;

    private final List<Rule> rules = new ArrayList<>();

    /** Starts a rule: "part of these vanilla biomes becomes this biome". Earlier rules win. */
    @SafeVarargs
    public final Rule give(ResourceKey<Biome> replacement, BooleanSupplier enabled, ResourceKey<Biome>... vanilla) {
        Rule rule = new Rule(replacement, enabled, Set.of(vanilla));
        rules.add(rule);
        return rule;
    }

    public static final class Rule {
        private final ResourceKey<Biome> replacement;
        private final BooleanSupplier enabled;
        private final Set<ResourceKey<Biome>> vanilla;
        private final Climate.Parameter[] limits = new Climate.Parameter[5];

        private Rule(ResourceKey<Biome> replacement, BooleanSupplier enabled, Set<ResourceKey<Biome>> vanilla) {
            this.replacement = replacement;
            this.enabled = enabled;
            this.vanilla = vanilla;
        }

        public Rule temperature(float min, float max)     { return limit(T, min, max); }
        public Rule humidity(float min, float max)        { return limit(H, min, max); }
        public Rule continentalness(float min, float max) { return limit(C, min, max); }
        public Rule erosion(float min, float max)         { return limit(E, min, max); }
        public Rule weirdness(float min, float max)       { return limit(W, min, max); }

        private Rule limit(int axis, float min, float max) {
            limits[axis] = Climate.Parameter.span(min, max);
            return this;
        }
    }

    /** Passes one vanilla slot to the mapper, split up according to the rules. */
    public void apply(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                      Climate.ParameterPoint point, ResourceKey<Biome> biome) {
        Climate.Parameter[] box = {point.temperature(), point.humidity(), point.continentalness(),
                point.erosion(), point.weirdness()};
        apply(mapper, point, biome, box, 0);
    }

    private void apply(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                       Climate.ParameterPoint original, ResourceKey<Biome> biome,
                       Climate.Parameter[] box, int first) {
        for (int i = first; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            if (!rule.enabled.getAsBoolean() || !rule.vanilla.contains(biome) || !overlaps(box, rule)) continue;

            Climate.Parameter[] inside = box.clone();
            for (int axis = 0; axis < 5; axis++) {
                Climate.Parameter limit = rule.limits[axis];
                if (limit == null) continue;
                long min = inside[axis].min(), max = inside[axis].max();
                // The parts outside the rule are offered to the later rules.
                if (min < limit.min()) {
                    Climate.Parameter[] below = inside.clone();
                    below[axis] = new Climate.Parameter(min, limit.min());
                    apply(mapper, original, biome, below, i + 1);
                    min = limit.min();
                }
                if (max > limit.max()) {
                    Climate.Parameter[] above = inside.clone();
                    above[axis] = new Climate.Parameter(limit.max(), max);
                    apply(mapper, original, biome, above, i + 1);
                    max = limit.max();
                }
                inside[axis] = new Climate.Parameter(min, max);
            }
            emit(mapper, original, inside, rule.replacement);
            return;
        }
        emit(mapper, original, box, biome);
    }

    private static boolean overlaps(Climate.Parameter[] box, Rule rule) {
        for (int axis = 0; axis < 5; axis++) {
            Climate.Parameter limit = rule.limits[axis];
            if (limit != null && (box[axis].max() <= limit.min() || box[axis].min() >= limit.max())) return false;
        }
        return true;
    }

    private static void emit(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper,
                             Climate.ParameterPoint original, Climate.Parameter[] box, ResourceKey<Biome> biome) {
        mapper.accept(Pair.of(new Climate.ParameterPoint(box[T], box[H], box[C], box[E],
                original.depth(), box[W], original.offset()), biome));
    }
}
