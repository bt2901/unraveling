package unraveling.item;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import unraveling.UnravelingMod;
import unraveling.block.TFBlocks;
import cpw.mods.fml.common.registry.GameRegistry;

import thaumcraft.common.items.ItemNugget;
import thaumcraft.common.items.ItemResource;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;


import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.Aspect;

public class TFRecipes {

	public static void registerRecipes() {
		
		// void ore
        OreDictionary.registerOre("oreVoid", new ItemStack(TFBlocks.voidOre, 1, OreDictionary.WILDCARD_VALUE));
        //GameRegistry.addSmelting(TFBlocks.voidOre, ItemApi.getItem("ItemResource", 16), 0.1F);
        GameRegistry.addSmelting(TFBlocks.voidOre, new ItemStack(ConfigItems.itemResource, 1, 16), 0.1F);
        

                /*
            * "[ore item/block id],[ore item/block metadata],[cluster item/block id],[cluster item/block metadata],[chance modifier float]"
            * NOTE: The chance modifier is a multiplier applied to the default chance for that cluster to be produced (default 27.5% for a pickaxe of the core)
            * Example for vanilla iron ore to produce one of my own native iron clusters (assuming default id's) at double the default chance: 
            * FMLInterModComms.sendMessage("Thaumcraft", "nativeCluster","15,0,25016,16,2.0");*/

        // recipe sorter
        // RecipeSorter.register(TwilightForestMod.ID + ":mapcloning",  TFMapCloningRecipe.class,   SHAPELESS, "after:minecraft:shapeless");
        
		//GameRegistry.addShapelessRecipe(new ItemStack(TFBlocks.fireflyJar, 1, 0), new Object[] {TFBlocks.firefly, Items.glass_bottle});
		GameRegistry.addShapelessRecipe(new ItemStack(TFItems.scepterTwilight), new Object[] {new ItemStack(TFItems.scepterTwilight, 1, TFItems.scepterTwilight.getMaxDamage()), Items.ender_pearl});
		GameRegistry.addShapelessRecipe(new ItemStack(TFItems.scepterLifeDrain), new Object[] {new ItemStack(TFItems.scepterLifeDrain, 1, TFItems.scepterLifeDrain.getMaxDamage()), Items.fermented_spider_eye});
        
		GameRegistry.addShapelessRecipe(new ItemStack(TFItems.scepterZombie), new Object[] {new ItemStack(TFItems.scepterZombie, 1, TFItems.scepterZombie.getMaxDamage()), Items.bone, Items.bone});
        

        /*
		GameRegistry.addShapelessRecipe(new ItemStack(TFItems.magicMapFocus), new Object[] {TFItems.feather, TFItems.torchberries, Items.glowstone_dust});
		GameRegistry.addRecipe(new ItemStack(TFItems.emptyMagicMap), new Object[] {"###", "#X#", "###", '#', Items.paper, 'X', TFItems.magicMapFocus});
		GameRegistry.addRecipe(new ItemStack(TFItems.emptyMazeMap), new Object[] {"###", "#X#", "###", '#', Items.paper, 'X', TFItems.mazeMapFocus});
		GameRegistry.addShapelessRecipe(new ItemStack(TFItems.emptyOreMap), new Object[] {new ItemStack(TFItems.mazeMap, 1, Short.MAX_VALUE), Blocks.gold_block, Blocks.diamond_block, Blocks.iron_block});
		GameRegistry.addShapelessRecipe(new ItemStack(TFItems.emptyOreMap), new Object[] {new ItemStack(TFItems.emptyMazeMap, 1, Short.MAX_VALUE), Blocks.gold_block, Blocks.diamond_block, Blocks.iron_block});

		GameRegistry.addRecipe(new ItemStack(Items.arrow, 4), new Object[] {"X", "#", "Y", 'Y', TFItems.feather, 'X', Items.flint, '#', Items.stick});*/


	}
}
