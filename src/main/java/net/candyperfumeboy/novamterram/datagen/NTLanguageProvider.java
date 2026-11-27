package net.candyperfumeboy.novamterram.datagen;

import net.candyperfumeboy.novamterram.NovamTerram;
import net.candyperfumeboy.novamterram.block.NTBlocks;
import net.candyperfumeboy.novamterram.entities.NTEntities;
import net.candyperfumeboy.novamterram.entities.variants.NTBoatEntities;
import net.candyperfumeboy.novamterram.item.NTItems;
import net.candyperfumeboy.novamterram.worldgen.biome.NTBiomes;
import net.minecraft.Util;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds en_us.json when you run datagen.
 *
 * Every block, item, entity and biome gets a name made from its ID, so
 * "purple_myrtle_sapling" becomes "Purple Myrtle Sapling" with nothing to type.
 *
 * You only need to edit this file when:
 *  - a name should be different from its ID: add it to addSpecialNames()
 *  - you add a chat message or subtitle: add it to addText()
 */
public class NTLanguageProvider extends LanguageProvider {

    private final Set<String> named = new HashSet<>();

    public NTLanguageProvider(PackOutput output) {
        super(output, NovamTerram.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // These two go first, so the automatic names don't replace them
        addSpecialNames();
        addText();

        NTBlocks.BLOCKS.getEntries().forEach(block -> addAutomatic(block.get().getDescriptionId(), block.getId()));
        // Items that place a block (including signs) share the block's name, so they are skipped here
        NTItems.ITEMS.getEntries().forEach(item -> addAutomatic(item.get().getDescriptionId(), item.getId()));
        NTEntities.ENTITY_TYPES.getEntries().forEach(entity -> addAutomatic(entity.get().getDescriptionId(), entity.getId()));
        NTBoatEntities.ENTITY_TYPES.getEntries().forEach(entity -> addAutomatic(entity.get().getDescriptionId(), entity.getId()));
        NTBiomes.ALL.forEach(biome -> addAutomatic(Util.makeDescriptionId("biome", biome.location()), biome.location()));
    }

    private void addSpecialNames() {
        addBlock(NTBlocks.OPAL_BLOCK, "Block of Opal");
        addBlock(NTBlocks.RHODOCHROSITE_BLOCK, "Block of Rhodochrosite");
        addBlock(NTBlocks.IOLITE_BLOCK, "Block of Iolite");
        addBlock(NTBlocks.JADEITE_BLOCK, "Block of Jadeite");
        addBlock(NTBlocks.KYANITE_BLOCK, "Block of Kyanite");
        addBlock(NTBlocks.LIGNITE_BLOCK, "Block of Lignite");
        addBlock(NTBlocks.LIGNITE_NETHER_ORE, "Nether Lignite Ore");
        addBlock(NTBlocks.LIGNITE_END_ORE, "End Lignite Ore");
        addItem(NTItems.LIGNITE_COAL, "Lignite");
        addBlock(NTBlocks.OLIVINE_SANDSTONE_FENCE_GATE, "Olivine Sandstone Fence Gate");
        addBlock(NTBlocks.OLIVINE_SANDSTONE_TRAP_DOOR, "Olivine Sandstone Trapdoor");
        addBlock(NTBlocks.OLIVE_HANGING, "Hanging Olives");
    }

    private void addText() {
        // Creative tab
        add("creativetab.novam_terram_tab", "Novam Terram");

        // Ocelot
        add("entity.novamterram.ocelot.tame_success", "This ocelot trusts you completely.");
        add("entity.novamterram.ocelot.trust_gained", "This ocelot seems wary of your presence...");
        add("entity.novamterram.ocelot.play", "The ocelot is ready to play!");
        add("entity.novamterram.ocelot.sitting", "The ocelot is now sitting.");
        add("entity.novamterram.ocelot.standing", "The ocelot is now following.");

        // Fox
        add("message.novamterram.fox.trust_success", "This fox trusts you completely.");
        add("message.novamterram.fox.trust_fail", "This fox seems wary of your presence...");

        // Hyena
        add("message.novamterram.hyena.trust_success", "This hyena trusts you completely.");
        add("message.novamterram.hyena.trust_fail", "This hyena seems wary of your presence...");
        add("message.novamterram.hyena.waiting", "Hyena is waiting.");
        add("message.novamterram.hyena.following", "Hyena is following!");

        // Songbird
        add("message.novamterram.songbird.trust_success", "This songbird trusts you completely.");
        add("message.novamterram.songbird.trust_fail", "This songbird seems wary of your presence...");
        add("message.novamterram.songbird.waiting", "Songbird is waiting.");
        add("message.novamterram.songbird.following", "Songbird is following!");
        add("message.novamterram.songbird.gift", "Your songbird brought you a seed.");
        add("message.novamterram.songbird.on_head", "Double-tap sneak to let your songbird hop off.");
        add("message.novamterram.songbird.alarm", "Your songbird is sounding the alarm!");

        // Faun
        add("message.novamterram.faun.trust_success", "This faun trusts you completely.");
        add("message.novamterram.faun.trust_fail", "This faun seems wary of your presence...");
        add("message.novamterram.faun.trust_lost", "The faun no longer trusts you.");
        add("message.novamterram.faun.gift", "A faun left you a gift.");
        add("message.novamterram.faun.alarm", "A faun sounds the alarm!");
        add("message.novamterram.faun.guide_start", "The faun wants to show you something...");
        add("message.novamterram.faun.guide_none", "The faun finds nothing to show you here.");
        add("message.novamterram.faun.guide_wait", "The faun needs to rest before leading you again.");
        add("message.novamterram.faun.guide_done", "The faun has found something!");
        add("message.novamterram.faun.guide_lost", "The faun stopped leading you.");

        // Subtitles
        add("subtitles.entity.novamterram.fox.death_silver", "Silver Fox shatters");
        add("subtitles.entity.novamterram.fox.death_desert", "Desert Fox crumbles");
        add("subtitles.entity.novamterram.faun.ambient", "Faun bleats");
        add("subtitles.entity.novamterram.faun.alert", "Faun sounds alarm");
        add("subtitles.entity.novamterram.songbird.ambient", "Songbird chirps");
        add("subtitles.entity.novamterram.hyena.ambient", "Hyena giggles");
    }

    @Override
    public void add(String key, String value) {
        super.add(key, value);
        named.add(key);
    }

    private void addAutomatic(String key, ResourceLocation id) {
        if (!named.contains(key)) {
            add(key, toTitle(id.getPath()));
        }
    }

    /** "stripped_teak_log" -> "Stripped Teak Log" */
    private static String toTitle(String path) {
        return Arrays.stream(path.split("_"))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}
