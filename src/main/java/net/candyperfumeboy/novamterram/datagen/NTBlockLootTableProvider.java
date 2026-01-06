package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.item.NTItems;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class NTBlockLootTableProvider extends BlockLootSubProvider {
    protected NTBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // --- BASIC BLOCKS ---
        dropSelf(NTBlocks.OPAL_BLOCK.get());
        dropSelf(NTBlocks.LIGNITE_BLOCK.get());
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

        // --- ORES (Corrected Drops) ---
        // Lignite Ores: Drop Lignite Coal
        add(NTBlocks.LIGNITE_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));
        add(NTBlocks.DEEPSLATE_LIGNITE_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));
        add(NTBlocks.LIGNITE_NETHER_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));
        add(NTBlocks.LIGNITE_END_ORE.get(), block -> createOreDrop(block, NTItems.LIGNITE_COAL.get()));

        // Opal Ore: Drop 1-3 Raw Opal (Using your custom helper)
        add(NTBlocks.OPAL_ORE.get(), block -> createMultipleOreDrops(block, NTItems.RAW_OPAL.get(), 1, 3));

        // --- BLUE SPRUCE WOOD ---
        dropSelf(NTBlocks.BLUE_SPRUCE_LOG.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_WOOD.get());
        dropSelf(NTBlocks.STRIPPED_BLUE_SPRUCE_LOG.get());
        dropSelf(NTBlocks.STRIPPED_BLUE_SPRUCE_WOOD.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_PLANKS.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_SAPLING.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_STAIRS.get());
        add(NTBlocks.BLUE_SPRUCE_SLAB.get(), block -> createSlabItemTable(NTBlocks.BLUE_SPRUCE_SLAB.get()));
        dropSelf(NTBlocks.BLUE_SPRUCE_BUTTON.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_TRAP_DOOR.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_FENCE.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_FENCE_GATE.get());
        dropSelf(NTBlocks.BLUE_SPRUCE_PRESSURE_PLATE.get());
        add(NTBlocks.BLUE_SPRUCE_DOOR.get(), block -> createDoorTable(NTBlocks.BLUE_SPRUCE_DOOR.get()));

        // Leaves: Chance to drop Saplings
        this.add(NTBlocks.BLUE_SPRUCE_LEAVES.get(), block ->
                createLeavesDrops(block, NTBlocks.BLUE_SPRUCE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));

        // Signs: Drop the Item version of the sign
        this.add(NTBlocks.BLUE_SPRUCE_SIGN.get(), block -> createSignTable(block, NTItems.BLUE_SPRUCE_SIGN_ITEM.get()));
        this.add(NTBlocks.BLUE_SPRUCE_WALL_SIGN.get(), block -> createSignTable(NTBlocks.BLUE_SPRUCE_SIGN.get(), NTItems.BLUE_SPRUCE_SIGN_ITEM.get()));

        this.add(NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get(), block -> createSignTable(block, NTItems.BLUE_SPRUCE_HANGING_SIGN_ITEM.get()));
        this.add(NTBlocks.BLUE_SPRUCE_WALL_HANGING_SIGN.get(), block -> createSignTable(NTBlocks.BLUE_SPRUCE_HANGING_SIGN.get(), NTItems.BLUE_SPRUCE_HANGING_SIGN_ITEM.get()));

        // Podzol: Drops Dirt (unless using Silk Touch, which dropOther handles internally)
        dropOther(NTBlocks.BLUE_PODZOL.get(), Blocks.DIRT);
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
