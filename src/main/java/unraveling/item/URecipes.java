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
import thaumcraft.common.lib.utils.Utils;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.Aspect;

public class URecipes {

	public static void registerRecipes() {
		
		// void ore
        ItemStack voidIngot = new ItemStack(ConfigItems.itemResource, 1, 16);
        ItemStack voidNugget = new ItemStack(ConfigItems.itemNugget, 1, 7);
        ItemStack voidIngotDoubled = new ItemStack(ConfigItems.itemResource, 2, 16);
        ItemStack voidOre = new ItemStack(UBlocks.voidOre);
        ItemStack voidCluster = new ItemStack(UItems.voidCluster);
        
        OreDictionary.registerOre("oreVoid", new ItemStack(UBlocks.voidOre, 1, OreDictionary.WILDCARD_VALUE));
        GameRegistry.addSmelting(UBlocks.voidOre, voidIngot, 0.1F);
        ThaumcraftApi.addSmeltingBonus(voidOre, voidNugget);

        OreDictionary.registerOre("clusterVoid", new ItemStack(UItems.voidCluster, 1, OreDictionary.WILDCARD_VALUE));
        GameRegistry.addSmelting(UItems.voidCluster, voidIngotDoubled, 0.1F);
        ThaumcraftApi.addSmeltingBonus(voidCluster, voidNugget);
        Utils.addSpecialMiningResult(voidOre, voidCluster, 0.5F);
        

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
                Character.valueOf('S'), new ItemStack(ConfigItems.itemSanityChecker), 
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
