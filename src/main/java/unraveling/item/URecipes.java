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
import unraveling.EldritchLore;
import unraveling.block.UBlocks;
import cpw.mods.fml.common.registry.GameRegistry;

import thaumcraft.common.items.ItemNugget;
import thaumcraft.common.items.ItemResource;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.common.config.ConfigItems;

import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigBlocks;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.Aspect;

public class URecipes {

	public static void registerRecipes() {
		
		// void ore
        ItemStack voidIngot = new ItemStack(ConfigItems.itemResource, 1, 16);
        OreDictionary.registerOre("oreVoid", new ItemStack(UBlocks.voidOre, 1, OreDictionary.WILDCARD_VALUE));
        GameRegistry.addSmelting(UBlocks.voidOre, voidIngot, 0.1F);
        ThaumcraftApi.addSmeltingBonus(new ItemStack(UBlocks.voidOre), new ItemStack(ConfigItems.itemNugget, 1, 7));

        // FMLInterModComms.sendMessage("Thaumcraft", "nativeCluster","15,0,25016,16,2.0");

                /*
            * "[ore item/block id],[ore item/block metadata],[cluster item/block id],[cluster item/block metadata],[chance modifier float]"
            * NOTE: The chance modifier is a multiplier applied to the default chance for that cluster to be produced (default 27.5% for a pickaxe of the core)
            * Example for vanilla iron ore to produce one of my own native iron clusters (assuming default id's) at double the default chance: 
            * */


		GameRegistry.addShapelessRecipe(new ItemStack(UItems.scepterTwilight), new Object[] {new ItemStack(UItems.scepterTwilight, 1, UItems.scepterTwilight.getMaxDamage()), Items.ender_pearl});
		GameRegistry.addShapelessRecipe(new ItemStack(UItems.scepterLifeDrain), new Object[] {new ItemStack(UItems.scepterLifeDrain, 1, UItems.scepterLifeDrain.getMaxDamage()), Items.fermented_spider_eye});
        
		GameRegistry.addShapelessRecipe(new ItemStack(UItems.scepterZombie), new Object[] {new ItemStack(UItems.scepterZombie, 1, UItems.scepterZombie.getMaxDamage()), Items.bone, Items.bone});
        
        EldritchLore.recipes.put("QBlock", 
            ThaumcraftApi.addArcaneCraftingRecipe(
                "QBLOCK", new ItemStack(UBlocks.quaesitum), 
                new AspectList().add(Aspect.ENTROPY, 25).add(Aspect.ORDER, 25), 
                new Object[]{"   ", "GSG", "RRR", 
                    Character.valueOf('G'), Items.gold_ingot, 
                    Character.valueOf('S'), new ItemStack(ConfigItems.itemThaumometer), 
                    Character.valueOf('R'), Blocks.stone}));

        EldritchLore.recipes.put("WarpLocator", ThaumcraftApi.addArcaneCraftingRecipe("ASTRALSNARE", 
            new ItemStack(UItems.ender_compass, 1), 
            (new AspectList()).add(Aspect.ENTROPY, 20).add(Aspect.ORDER, 20), 
            new Object[]{" E ", "ESE", " L ", 
                Character.valueOf('R'), new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), 
                Character.valueOf('E'), new ItemStack(Items.ender_eye), 
                Character.valueOf('L'), new ItemStack(ConfigItems.itemCompassStone)}
            )
        );
        EldritchLore.recipes.put("VoidPortal", ThaumcraftApi.addArcaneCraftingRecipe("VOIDPORTAL", 
            new ItemStack(UBlocks.portal, 1, 0), 
            (new AspectList()).add(Aspect.ENTROPY, 20).add(Aspect.ORDER, 20), 
            new Object[]{"RWR", "WXW", "RWR", 
                Character.valueOf('R'), new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2), 
                Character.valueOf('W'), new ItemStack(ConfigBlocks.blockCosmeticOpaque, 1, 2), 
                Character.valueOf('X'), new ItemStack(UItems.ender_compass, 1)}
            )
        );
        EldritchLore.recipes.put("VoidAgg", ThaumcraftApi.addArcaneCraftingRecipe("VOIDAGG", 
            new ItemStack(UBlocks.darkGen, 1, 1), 
            (new AspectList()).add(Aspect.ENTROPY, 20).add(Aspect.ORDER, 20), 
            new Object[]{" S ", " I ", "III", 
                Character.valueOf('S'), new ItemStack(ConfigItems.itemResource, 1, 17), 
                Character.valueOf('I'), voidIngot}
            )
        );
	}
}
