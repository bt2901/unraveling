package unraveling.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import unraveling.UnravelingMod;
import unraveling.item.ItemBlockTFMeta;
import cpw.mods.fml.common.registry.GameRegistry;

public class TFBlocks {

    public static Block bossSpawner;
    public static Block fireflyJar;
    public static Block voidOre;
    public static Block darkGen;
    public static Block darkGenMain;
    //public static Block uncraftingTable;
    //public static Block fireJet;

	public static void registerBlocks() {
		
        //firefly = (new BlockTFFirefly()).setBlockName("TFFirefly");
        darkGenMain = (new BlockDarkGenMain()).setBlockName("darkGenMain");
        //bossSpawner = (new BlockTFBossSpawner()).setBlockName("TFBossSpawner");
        fireflyJar = (new BlockTFFireflyJar()).setBlockName("TFFireflyJar");
        voidOre = (new BlockVoidOre()).setBlockName("voidOre");
        darkGen = (new BlockDarkGen()).setBlockName("darkGen");
        //uncraftingTable = (new BlockTFUncraftingTable()).setBlockName("TFUncraftingTable");
        //fireJet = (new BlockTFFireJet()).setBlockName("TFFireJet");
        
		// register blocks with their pickup values
		//registerMyBlock(firefly, ItemBlock.class);
		//registerMyBlock(portal, ItemBlock.class);
		//registerMyBlock(bossSpawner);
		registerMyBlock(fireflyJar, ItemBlock.class);
        registerMyBlock(voidOre, ItemBlock.class);
        registerMyBlock(darkGen, ItemBlock.class);
        registerMyBlock(darkGenMain, ItemBlock.class);

		//registerMyBlock(uncraftingTable, ItemBlock.class);
		//registerMyBlock(fireJet);
        
		// fire info
        //Blocks.fire.setFireInfo(log, 5, 5);
        //Blocks.fire.setFireInfo(leaves, 30, 60);
        //Blocks.fire.setFireInfo(leaves3, 30, 60);
	}

	private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup, BlockSlab singleSlab, BlockSlab doubleSlab, boolean isDouble) {
		GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName(), singleSlab, doubleSlab, isDouble);

	}

	private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup, Block blockAgain, String[] names) {
		GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName(), blockAgain, names);
	}

	private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup) 
	{
		GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName());
	}
	
	private static void registerMyBlock(Block block) 
	{
		GameRegistry.registerBlock(block, ItemBlockTFMeta.class, block.getUnlocalizedName());
	}

}
