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
import unraveling.block.UBlocks;
import cpw.mods.fml.common.registry.GameRegistry;

import thaumcraft.common.items.ItemNugget;
import thaumcraft.common.items.ItemResource;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;


import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.Aspect;

public class URecipes {

	public static void registerRecipes() {
		
		// void ore
        OreDictionary.registerOre("oreVoid", new ItemStack(UBlocks.voidOre, 1, OreDictionary.WILDCARD_VALUE));
        GameRegistry.addSmelting(UBlocks.voidOre, new ItemStack(ConfigItems.itemResource, 1, 16), 0.1F);
        

                /*
            * "[ore item/block id],[ore item/block metadata],[cluster item/block id],[cluster item/block metadata],[chance modifier float]"
            * NOTE: The chance modifier is a multiplier applied to the default chance for that cluster to be produced (default 27.5% for a pickaxe of the core)
            * Example for vanilla iron ore to produce one of my own native iron clusters (assuming default id's) at double the default chance: 
            * FMLInterModComms.sendMessage("Thaumcraft", "nativeCluster","15,0,25016,16,2.0");*/


		GameRegistry.addShapelessRecipe(new ItemStack(UItems.scepterTwilight), new Object[] {new ItemStack(UItems.scepterTwilight, 1, UItems.scepterTwilight.getMaxDamage()), Items.ender_pearl});
		GameRegistry.addShapelessRecipe(new ItemStack(UItems.scepterLifeDrain), new Object[] {new ItemStack(UItems.scepterLifeDrain, 1, UItems.scepterLifeDrain.getMaxDamage()), Items.fermented_spider_eye});
        
		GameRegistry.addShapelessRecipe(new ItemStack(UItems.scepterZombie), new Object[] {new ItemStack(UItems.scepterZombie, 1, UItems.scepterZombie.getMaxDamage()), Items.bone, Items.bone});
	}
}
