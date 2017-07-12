package unraveling;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import unraveling.item.UItems;
import unraveling.block.UBlocks;
import unraveling.UnravelingMod;
import unraveling.item.ItemArtifact;


import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.api.research.ResearchItem;

import unraveling.mechanics.UResearchItem;

import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ConfigBlocks;

import thaumcraft.common.config.ConfigItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public class EldritchLore {
    public static HashMap recipes = new HashMap();

    public static void addResearch() {
        ResourceLocation background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png");
        // ResourceLocation background = new ResourceLocation("unraveling", "textures/tab_unraveling.png");
        ResourceLocation icon = new ResourceLocation("unraveling", "textures/tab.png");
        ResearchCategories.registerCategory("UNRAVELING", icon, background);
        
        explore();
        studyUndead();
        studyVoid();
        
    }
    
    public static void studyUndead() {}

	private static void registerResearchItemC(String string, List<Object> asList) {
		ConfigResearch.recipes.put(string, asList);
    }    

	private static ResearchPage constructPage(String name) {
        return new ResearchPage((List)ConfigResearch.recipes.get(name));
    }    
    public static void studyVoid() {

            ResearchItem researchVoidGen = new UResearchItem("VOIDGEN", "UNRAVELING", 
                new AspectList().add(Aspect.EXCHANGE, 5).add(Aspect.VOID,  12), 
                0, 8, 1, 
                new ItemStack(UBlocks.voidOre)
            ).registerResearchItem();
            
            ItemStack empty = new ItemStack(ConfigBlocks.blockHole, 1, 15);
            ItemStack iron = new ItemStack(Blocks.iron_ore);
            ItemStack dg = new ItemStack(UBlocks.darkGen);
            
            registerResearchItemC("CREATEVOIDORE", Arrays.asList(new Object[] { 
    			new AspectList().add(Aspect.DARKNESS, UnravelingConfig.baseVoidProductionCost).add(Aspect.VOID, UnravelingConfig.baseVoidProductionCost), 
    			4, 2, 4, 
    			Arrays.asList(new ItemStack[] { 
    					 empty, empty, empty, empty, 
    					 empty, empty, empty, empty, 
    					 empty, empty, iron, empty, 
    					 empty, empty, empty, empty, 
                         
    					 dg, empty, empty, dg,
    					 empty, iron, iron, empty,
    					 empty, iron, iron, empty,
    					 dg, empty, empty, dg
                     }) 
                }));
            
            researchVoidGen.setPages(
                new ResearchPage("1"), 
                constructPage("CREATEVOIDORE")
                ).setStub().setRound();
                
        (new UResearchItem("VOIDAGG", "UNRAVELING", 
            new AspectList().add(Aspect.EXCHANGE, 5).add(Aspect.VOID,  12), 
            3, 8, 1, 
            new ItemStack(UBlocks.darkGen))
        ).setPages(new ResearchPage[] { 
            new ResearchPage("1"), 
            new ResearchPage((IArcaneRecipe) recipes.get("VoidAgg")), 
            }).setStub().setRound().setParents(new String[] { "VOIDGEN" }).registerResearchItem();
    }
    
    public static void explore() {       
        // --------- Quaesitum ------------
        (new UResearchItem("Q", "UNRAVELING", 
            new AspectList(), 
            -4, -4, 0, 
            new ItemStack(UBlocks.quaesitum))
        ).setPages(new ResearchPage[] { 
            new ResearchPage("1"), 
            new ResearchPage((IArcaneRecipe) recipes.get("QBlock")), 
            new ResearchPage("3"), 
            new ResearchPage("4") 
        }).setSpecial().setAutoUnlock().registerResearchItem();
        
        (new UResearchItem("SCRUTINY_INTUITION", "UNRAVELING", 
            new AspectList().add(Aspect.MIND, 5).add(Aspect.ORDER, 2).add(Aspect.ELDRITCH, 1).add(Aspect.SENSES, 1), 
            -2, -2, 1, 
            new ResourceLocation("unraveling", "textures/scrutiny_intuition.png"))
        ).setPages(new ResearchPage[] {
            new ResearchPage("1"), 
        }).setRound().setParents(new String[] { "Q" }).registerResearchItem();
        
        (new UResearchItem("SCRUTINY_RECYCLING", "UNRAVELING", 
            new AspectList().add(Aspect.MIND, 5).add(Aspect.GREED, 2).add(Aspect.ORDER, 1), 
            -2, -4, 1, 
            new ResourceLocation("unraveling", "textures/vellum_quill.png"))
        ).setPages(new ResearchPage[] {
            new ResearchPage("1"), 
        }).setRound().setParents(new String[] { "Q" }).registerResearchItem();
        
        (new UResearchItem("SCRUTINY_SILKTOUCH", "UNRAVELING", 
            new AspectList().add(Aspect.MIND, 5).add(Aspect.GREED, 2).add(Aspect.ENTROPY, 1).add(Aspect.HEAL, 1), 
            -2, -6, 1, 
            new ResourceLocation("unraveling", "textures/scrutiny_silk.png"))
        ).setPages(new ResearchPage[] {
            new ResearchPage("1"), 
        }).setRound().setParents(new String[] { "Q" }).registerResearchItem();
        // ---------- SAN -------------
        (new UResearchItem("ASTRALSNARE", "UNRAVELING", 
            new AspectList().add(Aspect.MIND, 5).add(Aspect.ENTROPY, 5).add(Aspect.SENSES, 7), 
            0, 0, 1, 
            new ResourceLocation("unraveling", "textures/items/artifacts/singularity.png"))
        ).setPages(new ResearchPage[] { 
            new ResearchPage("1"), 
            new ResearchPage("2"), 
            new ResearchPage("3"), 
            new ResearchPage((IArcaneRecipe) recipes.get("WarpLocator")), 
            }).setStub().setRound().registerResearchItem();
        
        (new UResearchItem("VOIDPORTAL", "UNRAVELING", 
            new AspectList().add(Aspect.ELDRITCH, 5).add(Aspect.TRAVEL, 5), 
            0, 2, 1, 
            new ResourceLocation("unraveling", "textures/eldritchIcon.png"))
        ).setPages(new ResearchPage[] { 
            new ResearchPage("1"), 
            new ResearchPage("2") 
        }).setSpecial().setParents(new String[] { "ASTRALSNARE" }).setConcealed().registerResearchItem();
                
        (new UResearchItem("lost", "UNRAVELING", 
            new AspectList(), 
            -3, 6, 0, 
            new ResourceLocation("unraveling", "textures/lostIcon.png"))
        ).setPages(new ResearchPage[] {
            new ResearchPage("1"), 
        }).setStub().setRound().registerResearchItem();

    }
    
    

}

/*
new ResearchItem("COREFISHING", "GOLEMANCY", 
    new AspectList().add(Aspect.WATER, 3).add(Aspect.HARVEST, 3).add(Aspect.BEAST, 3).add(Aspect.HUNGER, 3), 
    -2, 7, 2, 
    new ItemStack(ConfigItems.itemGolemCore, 1, 11)
    ).setPages(
        new ResearchPage("tc.research_page.COREFISHING.1"), 
        new ResearchPage((InfusionRecipe)recipes.get("CoreFishing")), 
        new ResearchPage("UPGRADEAIR", "tc.research_page.COREFISHING.2"), 
        new ResearchPage("UPGRADEFIRE", "tc.research_page.COREFISHING.3"), 
        new ResearchPage("UPGRADEORDER", "tc.research_page.COREFISHING.4"), 
        new ResearchPage("UPGRADEENTROPY", "tc.research_page.COREFISHING.5")
    ).setConcealed().setSecondary().setParents("COREHARVEST", "INFUSION").registerResearchItem();

*/
