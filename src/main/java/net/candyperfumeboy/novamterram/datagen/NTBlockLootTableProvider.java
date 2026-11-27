package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.block.custom.OliveHangingBlock;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
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

        // White Sandstone Set
        dropSelf(NTBlocks.WHITE_SAND.get());
        dropSelf(NTBlocks.WHITE_SANDSTONE.get());
        dropSelf(NTBlocks.CHISELED_WHITE_SANDSTONE.get());
        dropSelf(NTBlocks.CUT_WHITE_SANDSTONE.get());
        dropSelf(NTBlocks.WHITE_SANDSTONE_STAIRS.get());
        add(NTBlocks.WHITE_SANDSTONE_SLAB.get(), block -> createSlabItemTable(NTBlocks.WHITE_SANDSTONE_SLAB.get()));
        dropSelf(NTBlocks.WHITE_SANDSTONE_WALL.get());

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
        // WOODS & FLORA
        // ==========================================
        for (NTWoodSet set : NTBlocks.WOOD_SETS) {
            dropSelf(set.log.get());
            dropSelf(set.wood.get());
            dropSelf(set.strippedLog.get());
            dropSelf(set.strippedWood.get());
            dropSelf(set.planks.get());
            dropSelf(set.sapling.get());
            dropSelf(set.stairs.get());
            dropSelf(set.button.get());
            dropSelf(set.trapdoor.get());
            dropSelf(set.fence.get());
            dropSelf(set.fenceGate.get());
            dropSelf(set.pressurePlate.get());
            add(set.slab.get(), this::createSlabItemTable);
            add(set.door.get(), this::createDoorTable);
            add(set.leaves.get(), block -> createLeavesDrops(block, set.sapling.get(), NORMAL_LEAVES_SAPLING_CHANCES));
            add(set.sign.get(), block -> createSingleItemTable(set.signItem.get()));
            add(set.wallSign.get(), block -> createSingleItemTable(set.signItem.get()));
            add(set.hangingSign.get(), block -> createSingleItemTable(set.hangingSignItem.get()));
            add(set.wallHangingSign.get(), block -> createSingleItemTable(set.hangingSignItem.get()));
        }

        add(NTBlocks.OLIVE_LEAVES.get(), block -> createLeavesDrops(block, Blocks.OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES));
        add(NTBlocks.OLIVE_HANGING.get(), block -> LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(NTItems.OLIVE.get()).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(OliveHangingBlock.AGE, 2)))).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(OliveHangingBlock.AGE, 0)))).apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(OliveHangingBlock.AGE, 1)))))));

        add(NTBlocks.ORANGE_AUTUMNAL_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        add(NTBlocks.YELLOW_AUTUMNAL_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.ORANGE_AUTUMNAL_PINE_SAPLING.get());
        dropSelf(NTBlocks.YELLOW_AUTUMNAL_PINE_SAPLING.get());
        add(NTBlocks.ORANGE_AUTUMNAL_LEAF_CARPET.get(), this::createPetalsDrops);
        add(NTBlocks.YELLOW_AUTUMNAL_LEAF_CARPET.get(), this::createPetalsDrops);
        add(NTBlocks.PAULOWNIA_LEAVES.get(), block -> createLeavesDrops(block, NTBlocks.PAULOWNIA_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.PAULOWNIA_SAPLING.get());
        add(NTBlocks.PAULOWNIA_LEAF_CARPET.get(), this::createPetalsDrops);
        add(NTBlocks.WHITE_MYRTLE_LEAVES.get(), block ->
                createLeavesDrops(block, NTBlocks.WHITE_MYRTLE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.WHITE_MYRTLE_SAPLING.get());

// --- RED MYRTLE ---
        add(NTBlocks.RED_MYRTLE_LEAVES.get(), block ->
                createLeavesDrops(block, NTBlocks.RED_MYRTLE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.RED_MYRTLE_SAPLING.get());

// --- PURPLE MYRTLE ---
        add(NTBlocks.PURPLE_MYRTLE_LEAVES.get(), block ->
                createLeavesDrops(block, NTBlocks.PURPLE_MYRTLE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        dropSelf(NTBlocks.PURPLE_MYRTLE_SAPLING.get());
        add(NTBlocks.MYRTLE_LEAF_CARPET.get(), this::createPetalsDrops);

        dropSelf(NTBlocks.HEATHER.get());
        dropSelf(NTBlocks.LAVENDER.get());
        dropSelf(NTBlocks.ASTER.get());
        dropSelf(NTBlocks.THISTLE.get());
        dropSelf(NTBlocks.TRILLIUM.get());
        dropSelf(NTBlocks.BROMELIAD.get());
        dropSelf(NTBlocks.BLUEBELL.get());
        dropSelf(NTBlocks.PURPLE_BLUEBELL.get());

        add(NTBlocks.HEATHER_PETALS.get(), this::createPetalsDrops);
        add(NTBlocks.LAVENDER_PETALS.get(), this::createPetalsDrops);
        add(NTBlocks.ASTER_PETALS.get(), this::createPetalsDrops);

        dropOther(NTBlocks.BLUE_PODZOL.get(), Blocks.DIRT);
        dropSelf(NTBlocks.SILVER_SAGE.get());
        dropSelf(NTBlocks.BLEEDING_HEART.get());
        add(NTBlocks.IRIS.get(), block -> createDoublePlantWithSeedDrops(block, block));
    }

    protected LootTable.Builder createPetalsDrops(Block block) {
        LootTable.Builder builder = LootTable.lootTable();
        for (int i = 1; i <= 4; i++) {
            builder.withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PinkPetalsBlock.AMOUNT, i))).add(LootItem.lootTableItem(block).apply(SetItemCountFunction.setCount(ConstantValue.exactly(i)))));
        }
        return builder;
    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock, this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops))).apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return NTBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
