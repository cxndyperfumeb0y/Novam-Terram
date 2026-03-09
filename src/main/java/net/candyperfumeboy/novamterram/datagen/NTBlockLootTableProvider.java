package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.custom.OliveHangingBlock;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class NTBlockLootTableProvider extends BlockLootSubProvider {
    protected NTBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // ==========================================
        // MINERALS & SANDSTONE
        // ==========================================
        dropSelf(NTBlocks.OPAL_BLOCK.get());
        dropSelf(NTBlocks.RHODOCHROSITE_BLOCK.get());
        dropSelf(NTBlocks.JADEITE_BLOCK.get());
        dropSelf(NTBlocks.KYANITE_BLOCK.get());
        dropSelf(NTBlocks.IOLITE_BLOCK.get());
        dropSelf(NTBlocks.LIGNITE_BLOCK.get());

        // Olivine Set
        dropSelf(NTBlocks.OLIVINE_SAND.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE.get());
        dropSelf(NTBlocks.CHISELED_OLIVINE_SANDSTONE.get());
        dropSelf(NTBlocks.CUT_OLIVINE_SANDSTONE.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_STAIRS.get());
        add(NTBlocks.OLIVINE_SANDSTONE_SLAB.get(), block -> createSlabItemTable(NTBlocks.OLIVINE_SANDSTONE_SLAB.get()));
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_BUTTON.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_FENCE.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_WALL.get());
        dropSelf(NTBlocks.OLIVINE_SANDSTONE_PRESSURE_PLATE.get());
        add(NTBlocks.OLIVINE_SANDSTONE_DOOR.get(), block -> createDoorTable(NTBlocks.OLIVINE_SANDSTONE_DOOR.get()));

        // Amber Set
        dropSelf(NTBlocks.AMBER_SAND.get());
        dropSelf(NTBlocks.AMBER_SANDSTONE.get());
        dropSelf(NTBlocks.CHISELED_AMBER_SANDSTONE.get());
        dropSelf(NTBlocks.CUT_AMBER_SANDSTONE.get());
        dropSelf(NTBlocks.AMBER_SANDSTONE_STAIRS.get());
        add(NTBlocks.AMBER_SANDSTONE_SLAB.get(), block -> createSlabItemTable(NTBlocks.AMBER_SANDSTONE_SLAB.get()));
        dropSelf(NTBlocks.AMBER_SANDSTONE_WALL.get());

        // New Blocks
        dropSelf(NTBlocks.SAFFRON_MOSS.get());
        dropSelf(NTBlocks.PETRIFIED_CACTUS.get());

        // ==========================================
        // ORES
        // ==========================================
        add(NTBlocks.LIGNITE_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));
        add(NTBlocks.DEEPSLATE_LIGNITE_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));
        add(NTBlocks.LIGNITE_NETHER_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));
        add(NTBlocks.LIGNITE_END_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));

        add(NTBlocks.OPAL_ORE.get(), block -> createMultipleOreDrops(block, NTItems.RAW_OPAL.get(), 1, 3));
        add(NTBlocks.KYANITE_ORE.get(), block -> createMultipleOreDrops(block, NTItems.RAW_KYANITE.get(), 1, 3));
        add(NTBlocks.JADEITE_ORE.get(), block -> createMultipleOreDrops(block, NTItems.RAW_JADEITE.get(), 1, 3));
        add(NTBlocks.RHODOCHROSITE_ORE.get(), block -> createMultipleOreDrops(block, NTItems.RAW_RHODOCHROSITE.get(), 1, 3));
        add(NTBlocks.IOLITE_ORE.get(), block -> createMultipleOreDrops(block, NTItems.RAW_IOLITE.get(), 1, 3));

        // ==========================================
        // THE WOOD LOOP (Full Wood Sets)
        // ==========================================
        String[] woodNames = {"blue_spruce", "teak", "balsa", "mahogany", "aspen"};

        for (String wood : woodNames) {
            dropSelf(getBlock(wood + "_log"));
            dropSelf(getBlock(wood + "_wood"));
            dropSelf(getBlock("stripped_" + wood + "_log"));
            dropSelf(getBlock("stripped_" + wood + "_wood"));
            dropSelf(getBlock(wood + "_planks"));
            dropSelf(getBlock(wood + "_sapling"));
            dropSelf(getBlock(wood + "_stairs"));
            dropSelf(getBlock(wood + "_button"));
            dropSelf(getBlock(wood + "_trapdoor"));
            dropSelf(getBlock(wood + "_fence"));
            dropSelf(getBlock(wood + "_fence_gate"));
            dropSelf(getBlock(wood + "_pressure_plate"));

            add(getBlock(wood + "_slab"), this::createSlabItemTable);
            add(getBlock(wood + "_door"), this::createDoorTable);

            add(getBlock(wood + "_leaves"), block ->
                    createLeavesDrops(block, getBlock(wood + "_sapling"), NORMAL_LEAVES_SAPLING_CHANCES));

            add(getBlock(wood + "_sign"), block -> createSignTable(block, getItem(wood + "_sign")));
            add(getBlock(wood + "_wall_sign"), block -> createSignTable(getBlock(wood + "_sign"), getItem(wood + "_sign")));
            add(getBlock(wood + "_hanging_sign"), block -> createSignTable(block, getItem(wood + "_hanging_sign")));
            add(getBlock(wood + "_wall_hanging_sign"), block -> createSignTable(getBlock(wood + "_hanging_sign"), getItem(wood + "_hanging_sign")));
        }

        // ==========================================
        // AEGEAN FLORA (Olive)
        // ==========================================
        // We drop oak saplings for now unless you have an olive sapling!
        add(NTBlocks.OLIVE_LEAVES.get(), block -> createLeavesDrops(block, Blocks.OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));

        // Custom hanging fruit loot
        add(NTBlocks.OLIVE_HANGING.get(), block -> LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(NTItems.OLIVE.get())
                                // If Age 2, drop 1-3. Otherwise drop 1.
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F))
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(OliveHangingBlock.AGE, 2))))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(OliveHangingBlock.AGE, 0))))
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))
                                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(OliveHangingBlock.AGE, 1))))
                        )
                )
        );

        // ==========================================
        // HIGHLANDS & TAIGA FLORA
        // ==========================================
        add(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        add(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get());
        dropSelf(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get());
        add(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get(), this::createPetalsDrops);
        add(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get(), this::createPetalsDrops);

        add(NTBlocks.PAULOWNIA_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.PAULOWNIA_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.PAULOWNIA_SAPLING.get());
        add(NTBlocks.PAULOWNIA_LEAF_CARPET.get(), this::createPetalsDrops);

        add(NTBlocks.MYRTLE_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.MYRTLE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.MYRTLE_SAPLING.get());
        add(NTBlocks.MYRTLE_LEAF_CARPET.get(), this::createPetalsDrops);

        // ==========================================
        // FLOWERS & PETALS
        // ==========================================
        dropSelf(NTBlocks.HEATHER.get());
        dropSelf(NTBlocks.LAVENDER.get());
        dropSelf(NTBlocks.ASTER.get());
        dropSelf(NTBlocks.THISTLE.get());
        dropSelf(NTBlocks.TRILLIUM.get());
        dropSelf(NTBlocks.BROMELIAD.get());

        add(NTBlocks.HEATHER_PETALS.get(), this::createPetalsDrops);
        add(NTBlocks.LAVENDER_PETALS.get(), this::createPetalsDrops);
        add(NTBlocks.ASTER_PETALS.get(), this::createPetalsDrops);

        // ==========================================
        // SOILS
        // ==========================================
        dropOther(NTBlocks.BLUE_PODZOL.get(), Blocks.DIRT);


        // ==========================================
        // ROYAL FOREST FLORA
        // ==========================================
        dropSelf(NTBlocks.SILVER_SAGE.get());
        dropSelf(NTBlocks.BLEEDING_HEART.get());

        // For the Double-Tall Iris:
        // We use createDoublePlantWithSeedDrops to ensure it drops itself properly
        // regardless of which half is broken.
        add(NTBlocks.IRIS.get(), block -> createDoublePlantWithSeedDrops(block, block));

    }

    protected LootTable.Builder createPetalsDrops(Block block) {
        LootTable.Builder builder = LootTable.lootTable();
        for (int i = 1; i <= 4; i++) {
            builder.withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
                            .setProperties(StatePropertiesPredicate.Builder.properties()
                                    .hasProperty(PinkPetalsBlock.AMOUNT, i)))
                    .add(LootItem.lootTableItem(block).apply(SetItemCountFunction.setCount(ConstantValue.exactly(i))))
            );
        }
        return builder;
    }

    private Block getBlock(String name) {
        return NTBlocks.BLOCKS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(Holder::value)
                .orElseThrow(() -> new RuntimeException("Could not find block: " + name));
    }

    private Item getItem(String name) {
        return NTItems.ITEMS.getEntries().stream()
                .filter(e -> e.getId().getPath().equals(name))
                .findFirst()
                .map(Holder::value)
                .orElseThrow(() -> new RuntimeException("Could not find item: " + name));
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return NTBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }

    protected LootTable.Builder createSignTable(Block pBlock, Item pItem) {
        return createSingleItemTable(pItem);
    }
}