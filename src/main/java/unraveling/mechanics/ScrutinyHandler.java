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

import unraveling.item.ItemScrutinyNote;
import unraveling.mechanics.ExaminationData;
import unraveling.mechanics.ExaminationData.Discovery;


public class ScrutinyHandler {
    static Random rand = new Random();
    public static Aspect selectRandom(Set<Aspect> set) {
        int index = rand.nextInt(set.size());
        Iterator<Aspect> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
    public static Aspect selectRandom(AspectList asps) {
        Aspect[] al = asps.getAspects();
        int randomIndex = rand.nextInt(al.length);
        Aspect chosen = al[randomIndex];
        return chosen;
    }
    
    public static Aspect selectAspect(ItemStack thingResearched, String playerName) {
        if (ResearchManager.isResearchComplete(playerName, "SCRUTINY_INTUITION")) {
            PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
            Set<Aspect> aspects_here = new AspectList(thingResearched).aspects.keySet();
            AspectList aspects_known = pk.aspectsDiscovered.get(playerName);
            aspects_here.removeAll(aspects_known.aspects.keySet());
            if (aspects_here.size() > 0) {
                return selectRandom(aspects_here);
            }
        }
        return selectRandom(new AspectList(thingResearched));
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
    
    public static ItemStack finishResearch(TileQuaesitum q) {
        ItemStack thingResearched = q.getStackInSlot(0);
        if (thingResearched != null) {
            //--this.inventorySlots[2].stackSize;
            ResearchManager.consumeInkFromTable(q.inventorySlots[2], true);
            q.inventorySlots[1].stackSize--;
            
            ItemStack finishedResearch;
            Discovery r = UnravelingConfig.RelatedResearch(thingResearched);
            if (r != null) {
                finishedResearch = ItemScrutinyNote.createNoteOnResearch(r, rand.nextInt(5));
            } else {
                Aspect[] al = new AspectList(thingResearched).getAspects();
                int randomIndex = rand.nextInt(al.length);
                Aspect chosen = al[randomIndex];
                if (chosen == null) {
                    System.out.println(al);
                    System.out.println(randomIndex);
                    finishedResearch = ItemScrutinyNote.createEmptyNote();
                } else {
                    finishedResearch = ItemScrutinyNote.createNoteOnAspect(chosen);
                }
            }
            return finishedResearch;
        }
        return null;
    }
    
    public void maybeConsumeItem(int bonuses, TileQuaesitum q) {
    }
}
