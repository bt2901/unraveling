package unraveling;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import unraveling.item.TFItems;
import unraveling.block.TFBlocks;
import unraveling.UnravelingMod;

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

import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ConfigBlocks;

import thaumcraft.common.config.ConfigItems;
import net.minecraft.init.Items;


public class EldritchLore {
    public static HashMap recipes = new HashMap();

    public static void addResearch() {
        ResearchCategories.registerCategory("UNRAVELING", new ResourceLocation("unraveling", "textures/tab_unraveling.png"), new ResourceLocation("unraveling", "textures/tab_unraveling.png"));

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

            //).setParents("BRAINCURE","RESEARCHER2").setParentsHidden("INFUSION").setConcealed().registerResearchItem();
            ResearchItem research = new ResearchItem("VOIDORE", "UNRAVELING", 
                new AspectList(), 
                -8, -5, 1, 
                new ItemStack(TFBlocks.voidOre)
            ).registerResearchItem();
            
            ItemStack empty = new ItemStack(ConfigBlocks.blockHole, 1, 15);
            ItemStack iron = new ItemStack(Blocks.iron_ore);
            ItemStack dg = new ItemStack(TFBlocks.darkGen);
            
            registerResearchItemC("CREATEVOIDORE", Arrays.asList(new Object[] { 
    			new AspectList(), 
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
            
            research.setPages(
                new ResearchPage("unraveling.research_text.VOIDORE.1"), 
                //infusionPage(""), 
                //new ResearchPage("2"), 
                constructPage("CREATEVOIDORE")
                ).setStub().setRound().setAutoUnlock();
    }

    public static void explore() {
        (new ResearchItem("INTRO", "UNRAVELING", 
            new AspectList(), 
            -8, -8, 0, 
            new ItemStack(TFItems.necroFocus, 1, 0))
        ).setPages(new ResearchPage[] { 
            new ResearchPage("unraveling.research_text.INTRO.1"), 
            new ResearchPage("unraveling.research_text.INTRO.2") 
        }).setStub().setRound().setAutoUnlock().registerResearchItem();
        
        recipes.put(
            "QBlock", 
            ThaumcraftApi.addArcaneCraftingRecipe(
                "QBLOCK", new ItemStack(TFBlocks.quaesitum), 
                new AspectList().add(Aspect.ENTROPY, 25).add(Aspect.ORDER, 25), 
                new Object[]{"   ", "GSG", "RRR", 
                    Character.valueOf('G'), Items.gold_ingot, 
                    Character.valueOf('S'), new ItemStack(ConfigItems.itemThaumometer), 
                    Character.valueOf('R'), Blocks.stone}));
                    
        (new ResearchItem("Q", "UNRAVELING", 
            new AspectList(), 
            -4, -8, 0, 
            new ItemStack(TFBlocks.quaesitum))
        ).setPages(new ResearchPage[] { 
            new ResearchPage("tc.research_page.unraveling.Q.1"), 
            new ResearchPage((IArcaneRecipe) recipes.get("QBlock")), 
            new ResearchPage("tc.research_page.unraveling.Q.3"), 
            new ResearchPage("tc.research_page.unraveling.Q.4") 
        }).setStub().setRound().setAutoUnlock().registerResearchItem();
    }
}

