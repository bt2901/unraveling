package unraveling.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.MapColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import unraveling.UnravelingMod;
import cpw.mods.fml.common.registry.GameRegistry;

public class UBlocks {

    public static Block golemSpawner;
    
    public static Block voidOre;
    public static Block darkGen;
    public static Block darkGenMain;
    public static Block quaesitum;
    public static Block portal;

	public static void registerBlocks() {
		
        darkGenMain = (new BlockDarkGenMain()).setBlockName("darkGenMain");
        voidOre = (new BlockVoidOre()).setBlockName("voidOre");
        darkGen = (new BlockDarkGen()).setBlockName("darkGen");
        quaesitum = (new BlockQuaesitum()).setBlockName("Quaesitum");
        portal = (new BlockVoidPortal()).setBlockName("Portal");
        
		// register blocks with their pickup values
        registerMyBlock(voidOre, ItemBlock.class);
        registerMyBlock(darkGen, ItemBlock.class);
        registerMyBlock(darkGenMain, ItemBlock.class);
        registerMyBlock(quaesitum, ItemBlock.class);
        registerMyBlock(portal, ItemBlock.class);
        
        golemSpawner = (new BlockGolemSpawner()).setBlockName("GolemSpawner");
        registerMyBlock(golemSpawner, ItemBlock.class);

	}

	private static void registerMyBlock(Block block, Class<? extends ItemBlock> pickup) {
		GameRegistry.registerBlock(block, pickup, block.getUnlocalizedName());
	}
}
