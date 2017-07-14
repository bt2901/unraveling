package unraveling.mechanics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.EnumRarity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import unraveling.UnravelingConfig;
import unraveling.UnravelingMod;
import net.minecraft.tileentity.TileEntity;
import unraveling.tileentity.TileQuaesitum;

import thaumcraft.common.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;

import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.PlayerKnowledge;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.HexUtils;

import thaumcraft.common.items.ItemResearchNotes;
import unraveling.item.ItemScrutinyNote;

import unraveling.mechanics.ExaminationData;
import unraveling.mechanics.ExaminationData.Discovery;


public class ScrutinyHandler {
    static Random rand = new Random();
    public static Aspect selectRandom(Set<Aspect> set) {
        System.out.println("Selecting from set");
        int index = rand.nextInt(set.size());
        Iterator<Aspect> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
    public static Aspect selectRandom(AspectList asps) {
        System.out.println("Selecting from AL");

        Aspect[] al = asps.getAspects();
        System.out.println(al);
        int randomIndex = rand.nextInt(al.length);
        Aspect chosen = al[randomIndex];
        System.out.println(randomIndex + " out of " + al.length);
        return chosen;
    }
    
    public static Aspect selectAspect(ItemStack thingResearched, String playerName) {
        Set<Aspect> aspects_here = new HashSet<Aspect>();
        Set<Aspect> unknown_aspects_here = new HashSet<Aspect>();
        PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
        for (Aspect asp : new AspectList(thingResearched).aspects.keySet()) {
            if (asp != null) {
                aspects_here.add(asp);
                if (!pk.hasDiscoveredAspect(playerName, asp)) {
                    unknown_aspects_here.add(asp);
                }
            }
        }
        System.out.println("aspects_here.size() == " + aspects_here.size());
        if (aspects_here.size() == 0) {
            return null;
        }
        if (ResearchManager.isResearchComplete(playerName, "SCRUTINY_INTUITION")) {
            System.out.println("unknown_aspects_here.size() == " + aspects_here.size());
            if (unknown_aspects_here.size() > 0) {
                return selectRandom(unknown_aspects_here);
            }
        }
        System.out.println("Selecting absolutely random aspect");
        
        return selectRandom(aspects_here);
    }
    public static Aspect recycleResearchNote(ItemStack thingResearched, String playerName) {
        ResearchNoteData note = ResearchManager.getData(thingResearched);
        Set<Aspect> aspects_here = new HashSet();
        for (HexUtils.Hex hex : note.hexes.values()) {
            Aspect aspect1 = note.hexEntries.get((Object)hex.toString()).aspect;
            // 1 is a starting aspect
            // 2 is player-placed aspect
            if (note.hexEntries.get((Object)hex.toString()).type == 2){ 
                aspects_here.add(aspect1);
            }
        }
        if (aspects_here.size() > 0) {
            return selectRandom(aspects_here);
        }
        return null;
    }
    
    public static Aspect reduceResearchNote(ItemStack thingResearched, String playerName) {
        ResearchNoteData note = ResearchManager.getData(thingResearched);
        ResearchItem rr = ResearchCategories.getResearch(note.key);
        Set<Aspect> unknown_aspects_here = new HashSet();
        PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
        for (Aspect a : rr.tags.getAspects()) {
            if (!pk.hasDiscoveredAspect(playerName, a)) {
                unknown_aspects_here.add(a);
            }
        }
        if (unknown_aspects_here.size() > 0) {
            return selectRandom(unknown_aspects_here);
        }
        return null;
    }

    public static Aspect reduceScrutinyNote(ItemStack thingResearched, String playerName) {
        
        ExaminationData ed = new ExaminationData().readFromNBT(thingResearched.getTagCompound());
        Aspect aspect = Aspect.getAspect(ed.aspectTag);
        if (aspect == null || aspect.isPrimal()) {
            return null;
        }

        if (ResearchManager.isResearchComplete(playerName, "SCRUTINY_INTUITION")) {
            PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
            for (Aspect parent : aspect.getComponents()) {
                if (!pk.hasDiscoveredAspect(playerName, parent)) {
                    return parent;
                }
            }
        }
        return selectRandom(new AspectList().add(aspect.getComponents()[0], 1).add(aspect.getComponents()[1], 1));
    }

    public static ItemStack outputAspectNote(ItemStack thingResearched, String playerName, int bonuses) {
        Aspect chosen = examineAspect(thingResearched, playerName, bonuses);
        if (chosen == null) {
            System.out.println("chosen == null");
            return null;
        }
        PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
        // losing unknown aspect is annoying
        if (!pk.hasDiscoveredAspect(playerName, chosen)) {
            return ItemScrutinyNote.createNoteOnAspect(chosen);
        }
        int success = rand.nextInt(10) + bonuses;
        if (success > 7) {
            return ItemScrutinyNote.createNoteOnAspect(chosen);
        }
        System.out.println("Wanted to create note on " + chosen.getTag() + " but failed!");
        return null; // failure
    }
    public static boolean isScrutinyReport(ItemStack thingResearched) {
        Class reportClass = ItemScrutinyNote.class;
        return reportClass.isAssignableFrom(thingResearched.getItem().getClass());
    }
    public static boolean isResearchNote(ItemStack thingResearched) {
        Class noteClass = ItemResearchNotes.class;
        return noteClass.isAssignableFrom(thingResearched.getItem().getClass());
    }
    public static Aspect examineAspect(ItemStack thingResearched, String playerName, int bonuses) {
        Aspect chosen = null;
        if (isResearchNote(thingResearched) ) {
            if (ResearchManager.isResearchComplete(playerName, "SCRUTINY_INTUITION")) {
                chosen = reduceResearchNote(thingResearched, playerName);
            }
            if (chosen == null) {
                if (ResearchManager.isResearchComplete(playerName, "SCRUTINY_RECYCLING")) {
                    chosen = recycleResearchNote(thingResearched, playerName);
                }
            }
            return chosen;
        }
        if (isScrutinyReport(thingResearched)){
            if (ResearchManager.isResearchComplete(playerName, "SCRUTINY_RECYCLING")) {
                chosen = reduceScrutinyNote(thingResearched, playerName);
            }
            return chosen;
        }
        System.out.println("chosing at random");
        return selectAspect(thingResearched, playerName);
    }
    
    
    public static ItemStack finishResearch(ItemStack thingResearched, String playerName, int bonuses) {

        ItemStack finishedResearch = null;
        Discovery r = UnravelingConfig.RelatedResearch(thingResearched);
        if (r != null) {
            int choice = rand.nextInt(10) + bonuses;
            if (choice > 8) {
                int power = Math.min(rand.nextInt(3) + bonuses/3, 5);
                finishedResearch = ItemScrutinyNote.createNoteOnResearch(r, power);
            }
        }
        if (finishedResearch == null) {
            finishedResearch = outputAspectNote(thingResearched, playerName, bonuses);
        }
        return finishedResearch;
    }
    
    public static boolean maybeConsumeItem (ItemStack thingResearched, String playerName, int bonuses, ItemStack finishedResearch) {
        // always consume scrutiny reports
        if (isScrutinyReport(thingResearched)) {
            return true;
        }
        // never consume very valuable items
        EnumRarity rarity = thingResearched.getItem().getRarity(thingResearched);
        if (rarity == EnumRarity.rare || rarity == EnumRarity.epic) {
            return false;
        }
        if (finishedResearch == null && ResearchManager.isResearchComplete(playerName, "SCRUTINY_SILKTOUCH")) {
            return false;
        }
        int failure = rand.nextInt(10) - bonuses;
        if (failure < 4) {
            return true;
        }
        return false;
    }
}

/*

    public static int[] calculateResearchBoosters(xd world, int x, int y, int z) {
        int bookshelves = 0;
        int brains = 0;
        for (int k = -1; k <= 1; ++k) {
            for (int i1 = -1; i1 <= 1; ++i1) {
                if (k == 0 && i1 == 0 || !world.i(x + i1, y, z + k) || !world.i(x + i1, y + 1, z + k)) continue;
                if (world.a(x + i1 * 2, y, z + k * 2) == pb.an.bO) {
                    ++bookshelves;
                }
                if (world.a(x + i1 * 2, y, z + k * 2) == mod_ThaumCraft.blockAppFragile.bO && world.e(x + i1 * 2, y, z + k * 2) == 4) {
                    ++brains;
                }
                if (world.a(x + i1 * 2, y + 1, z + k * 2) == pb.an.bO) {
                    ++bookshelves;
                }
                if (world.a(x + i1 * 2, y + 1, z + k * 2) == mod_ThaumCraft.blockAppFragile.bO && world.e(x + i1 * 2, y + 1, z + k * 2) == 4) {
                    ++brains;
                }
                if (i1 == 0 || k == 0) continue;
                if (world.a(x + i1 * 2, y, z + k) == pb.an.bO) {
                    ++bookshelves;
                }
                if (world.a(x + i1 * 2, y, z + k) == mod_ThaumCraft.blockAppFragile.bO && world.e(x + i1 * 2, y, z + k) == 4) {
                    ++brains;
                }
                if (world.a(x + i1 * 2, y + 1, z + k) == pb.an.bO) {
                    ++bookshelves;
                }
                if (world.a(x + i1 * 2, y + 1, z + k) == mod_ThaumCraft.blockAppFragile.bO && world.e(x + i1 * 2, y + 1, z + k) == 4) {
                    ++brains;
                }
                if (world.a(x + i1, y, z + k * 2) == pb.an.bO) {
                    ++bookshelves;
                }
                if (world.a(x + i1, y, z + k * 2) == mod_ThaumCraft.blockAppFragile.bO && world.e(x + i1, y, z + k * 2) == 4) {
                    ++brains;
                }
                if (world.a(x + i1, y + 1, z + k * 2) == pb.an.bO) {
                    ++bookshelves;
                }
                if (world.a(x + i1, y + 1, z + k * 2) != mod_ThaumCraft.blockAppFragile.bO || world.e(x + i1, y + 1, z + k * 2) != 4) continue;
                ++brains;
            }
        }
        return new int[]{bookshelves, brains};
    }
*/