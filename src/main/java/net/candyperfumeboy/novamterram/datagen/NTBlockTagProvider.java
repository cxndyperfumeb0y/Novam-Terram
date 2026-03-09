package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class NTBlockTagProvider extends BlockTagsProvider {
    public NTBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NovamTerram.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // --- PICKAXE MINEABLE (Stones, Ores, Sandstone) ---
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(NTBlocks.LIGNITE_BLOCK.get(),
                        NTBlocks.OPAL_BLOCK.get(),
                        NTBlocks.OPAL_ORE.get(),
                        NTBlocks.RHODOCHROSITE_BLOCK.get(),
                        NTBlocks.RHODOCHROSITE_ORE.get(),
                        NTBlocks.KYANITE_BLOCK.get(),
                        NTBlocks.KYANITE_ORE.get(),
                        NTBlocks.IOLITE_BLOCK.get(),
                        NTBlocks.IOLITE_ORE.get(),
                        NTBlocks.JADEITE_BLOCK.get(),
                        NTBlocks.JADEITE_ORE.get(),
                        NTBlocks.DEEPSLATE_LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_END_ORE.get(),
                        NTBlocks.LIGNITE_NETHER_ORE.get(),
                        NTBlocks.CUT_OLIVINE_SANDSTONE.get(),
                        NTBlocks.OLIVINE_SANDSTONE.get(),
                        NTBlocks.CHISELED_OLIVINE_SANDSTONE.get(),
                        NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(),
                        NTBlocks.OLIVINE_SANDSTONE_SLAB.get(),
                        NTBlocks.OLIVINE_SANDSTONE_WALL.get(),
                        NTBlocks.PETRIFIED_CACTUS.get(), // NEW
                        NTBlocks.AMBER_SANDSTONE.get(), // NEW
                        NTBlocks.CUT_AMBER_SANDSTONE.get(), // NEW
                        NTBlocks.CHISELED_AMBER_SANDSTONE.get(), // NEW
                        NTBlocks.AMBER_SANDSTONE_STAIRS.get(), // NEW
                        NTBlocks.AMBER_SANDSTONE_SLAB.get(), // NEW
                        NTBlocks.AMBER_SANDSTONE_WALL.get()); // NEW

        // --- SHOVEL MINEABLE ---
        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(NTBlocks.OLIVINE_SAND.get(),
                        NTBlocks.BLUE_PODZOL.get(),
                        NTBlocks.SAFFRON_MOSS.get(), // NEW
                        NTBlocks.AMBER_SAND.get()); // NEW

        // --- HOE MINEABLE (Leaves & Crops) ---
        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(),
                        NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(),
                        NTBlocks.PAULOWNIA_LEAVES.get(),
                        NTBlocks.MYRTLE_LEAVES.get(),
                        NTBlocks.OLIVE_LEAVES.get(),
                        NTBlocks.OLIVE_HANGING.get());

        // --- TOOL LEVELS ---
        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(NTBlocks.DEEPSLATE_LIGNITE_ORE.get(),
                        NTBlocks.LIGNITE_END_ORE.get(),
                        NTBlocks.LIGNITE_NETHER_ORE.get(),
                        NTBlocks.OPAL_ORE.get(),
                        NTBlocks.OPAL_BLOCK.get(),
                        NTBlocks.IOLITE_ORE.get(),
                        NTBlocks.IOLITE_BLOCK.get(),
                        NTBlocks.KYANITE_ORE.get(),
                        NTBlocks.KYANITE_BLOCK.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(NTBlocks.JADEITE_ORE.get(),
                        NTBlocks.JADEITE_BLOCK.get(),
                        NTBlocks.RHODOCHROSITE_ORE.get(),
                        NTBlocks.RHODOCHROSITE_BLOCK.get());

        // --- SAND & DIRT ---
        tag(BlockTags.SAND).add(NTBlocks.OLIVINE_SAND.get(), NTBlocks.AMBER_SAND.get()); // NEW
        tag(BlockTags.CAMEL_SAND_STEP_SOUND_BLOCKS).add(NTBlocks.OLIVINE_SAND.get(), NTBlocks.AMBER_SAND.get()); // NEW
        tag(BlockTags.DIRT).add(NTBlocks.BLUE_PODZOL.get(), NTBlocks.SAFFRON_MOSS.get()); // NEW

        // --- INFRASTRUCTURE (Non-wood) ---
        tag(BlockTags.FENCES).add(NTBlocks.OLIVINE_SANDSTONE_FENCE.get());
        tag(BlockTags.FENCE_GATES).add(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get());
        tag(BlockTags.WALLS).add(NTBlocks.OLIVINE_SANDSTONE_WALL.get(), NTBlocks.AMBER_SANDSTONE_WALL.get()); // NEW
        tag(BlockTags.STAIRS).add(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get(), NTBlocks.AMBER_SANDSTONE_STAIRS.get()); // NEW
        tag(BlockTags.SLABS).add(NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), NTBlocks.AMBER_SANDSTONE_SLAB.get()); // NEW
        tag(BlockTags.DOORS).add(NTBlocks.OLIVINE_SANDSTONE_DOOR.get()); // NEW
        tag(BlockTags.TRAPDOORS).add(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get()); // NEW
        tag(BlockTags.PRESSURE_PLATES).add(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get()); // NEW
        tag(BlockTags.BUTTONS).add(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get()); // NEW


        // --- BEACON BASES ---
        tag(BlockTags.BEACON_BASE_BLOCKS).add(
                NTBlocks.OPAL_BLOCK.get(),
                NTBlocks.IOLITE_BLOCK.get(),
                NTBlocks.KYANITE_BLOCK.get(),
                NTBlocks.JADEITE_BLOCK.get(),
                NTBlocks.RHODOCHROSITE_BLOCK.get()
        );

        // --- NEOFORGE TAGS ---
        tag(Tags.Blocks.ORES).add(
                NTBlocks.OPAL_ORE.get(),
                NTBlocks.IOLITE_ORE.get(),
                NTBlocks.KYANITE_ORE.get(),
                NTBlocks.JADEITE_ORE.get(),
                NTBlocks.RHODOCHROSITE_ORE.get(),
                NTBlocks.LIGNITE_ORE.get()
        );
        tag(Tags.Blocks.STORAGE_BLOCKS).add(
                NTBlocks.OPAL_BLOCK.get(),
                NTBlocks.IOLITE_BLOCK.get(),
                NTBlocks.KYANITE_BLOCK.get(),
                NTBlocks.JADEITE_BLOCK.get(),
                NTBlocks.RHODOCHROSITE_BLOCK.get()
        );

        // --- FLORA & FLOWERS ---
        tag(BlockTags.SMALL_FLOWERS).add(
                NTBlocks.HEATHER.get(),
                NTBlocks.LAVENDER.get(),
                NTBlocks.ASTER.get(),
                NTBlocks.THISTLE.get(),
                NTBlocks.BROMELIAD.get(),
                NTBlocks.TRILLIUM.get(),
                NTBlocks.SILVER_SAGE.get(),
                NTBlocks.BLEEDING_HEART.get()
        );

        tag(BlockTags.TALL_FLOWERS).add(
                NTBlocks.IRIS.get()
        );
        tag(BlockTags.FLOWER_POTS).add(
                NTBlocks.HEATHER.get(),
                NTBlocks.LAVENDER.get(),
                NTBlocks.ASTER.get(),
                NTBlocks.TRILLIUM.get(),
                NTBlocks.THISTLE.get(),
                NTBlocks.BROMELIAD.get(),
                NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get(),
                NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get(),
                NTBlocks.PAULOWNIA_SAPLING.get(),
                NTBlocks.MYRTLE_SAPLING.get(),
                NTBlocks.IRIS.get()
        );

        tag(BlockTags.SAPLINGS).add(
                NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get(),
                NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get(),
                NTBlocks.PAULOWNIA_SAPLING.get(),
                NTBlocks.MYRTLE_SAPLING.get()
        );

        tag(BlockTags.LEAVES).add(
                NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(),
                NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(),
                NTBlocks.PAULOWNIA_LEAVES.get(),
                NTBlocks.MYRTLE_LEAVES.get(),
                NTBlocks.OLIVE_LEAVES.get()
        );

        tag(BlockTags.REPLACEABLE).add(
                NTBlocks.HEATHER_PETALS.get(),
                NTBlocks.LAVENDER_PETALS.get(),
                NTBlocks.ASTER_PETALS.get(),
                NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get(),
                NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get(),
                NTBlocks.PAULOWNIA_LEAF_CARPET.get(),
                NTBlocks.MYRTLE_LEAF_CARPET.get(),
                NTBlocks.OLIVE_HANGING.get(),
                NTBlocks.SILVER_SAGE.get(),
                NTBlocks.BLEEDING_HEART.get(),
                NTBlocks.IRIS.get()
        );

        // ==========================================
        // THE WOOD LOOP
        // ==========================================
        String[] woodNames = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};

        for (String wood : woodNames) {
            tag(BlockTags.MINEABLE_WITH_AXE).add(
                    getBlock(wood + "_log"),
                    getBlock(wood + "_wood"),
                    getBlock("stripped_" + wood + "_log"),
                    getBlock("stripped_" + wood + "_wood"),
                    getBlock(wood + "_planks"),
                    getBlock(wood + "_stairs"),
                    getBlock(wood + "_slab"),
                    getBlock(wood + "_fence"),
                    getBlock(wood + "_fence_gate"),
                    getBlock(wood + "_door"),
                    getBlock(wood + "_trapdoor"),
                    getBlock(wood + "_pressure_plate"),
                    getBlock(wood + "_button"),
                    getBlock(wood + "_sign"),
                    getBlock(wood + "_wall_sign"),
                    getBlock(wood + "_hanging_sign"),
                    getBlock(wood + "_wall_hanging_sign")
            );

            tag(BlockTags.LOGS_THAT_BURN).add(
                    getBlock(wood + "_log"),
                    getBlock(wood + "_wood"),
                    getBlock("stripped_" + wood + "_log"),
                    getBlock("stripped_" + wood + "_wood")
            );

            tag(BlockTags.PLANKS).add(getBlock(wood + "_planks"));
            tag(BlockTags.SAPLINGS).add(getBlock(wood + "_sapling"));
            tag(BlockTags.LEAVES).add(getBlock(wood + "_leaves"));
            tag(BlockTags.MINEABLE_WITH_HOE).add(getBlock(wood + "_leaves"));

            tag(BlockTags.FENCES).add(getBlock(wood + "_fence"));
            tag(BlockTags.FENCE_GATES).add(getBlock(wood + "_fence_gate"));
            tag(BlockTags.STAIRS).add(getBlock(wood + "_stairs"));
            tag(BlockTags.SLABS).add(getBlock(wood + "_slab"));
            tag(BlockTags.DOORS).add(getBlock(wood + "_door"));
            tag(BlockTags.TRAPDOORS).add(getBlock(wood + "_trapdoor"));
            tag(BlockTags.PRESSURE_PLATES).add(getBlock(wood + "_pressure_plate"));
            tag(BlockTags.BUTTONS).add(getBlock(wood + "_button"));

            tag(BlockTags.STANDING_SIGNS).add(getBlock(wood + "_sign"));
            tag(BlockTags.WALL_SIGNS).add(getBlock(wood + "_wall_sign"));
            tag(BlockTags.CEILING_HANGING_SIGNS).add(getBlock(wood + "_hanging_sign"));
            tag(BlockTags.WALL_HANGING_SIGNS).add(getBlock(wood + "_wall_hanging_sign"));
        }
    }

    private Block getBlock(String name) {
        return NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(DeferredHolder::get)
                .orElseThrow(() -> new RuntimeException("Block not found for tag: " + name));
    }
}