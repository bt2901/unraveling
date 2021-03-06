package unraveling.block;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import unraveling.UnravelingMod;
import unraveling.item.UItems;
import unraveling.mechanics.voidgen.TileDarkGen;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import unraveling.mechanics.voidgen.VoidAggregationHandler;

public class BlockDarkGen extends BlockStrangeDevice {
	
	public static IIcon gemDark;
	public static IIcon slabSide;
	public static IIcon slabTop;
	public static IIcon slabBottom;
	
	protected BlockDarkGen() {
		super();
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
	public boolean renderAsNormalBlock() {
        return false;
    }

	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
	public boolean isOpaqueCube() {
        return false;
    }
    
    /**
     * The type of render function that is called for this block
     */
    @Override
	public int getRenderType() {
    	return UnravelingMod.proxy.getDarkGenRenderID();
    }
    
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
	public IIcon getIcon(int side, int meta) {
        // the darkness gem will be rendered separately
        if (side == 0)
            return slabBottom;
        if (side == 1)
            return slabTop;
        return slabSide;
    }

    /**
     * Get a light value for this block, normal ranges are between 0 and 15
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return The light value
     */
    @Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
    	return 0;
    }
    
    /**
     * Return true if the block is a normal, solid cube.  This
     * determines indirect power state, entity ejection from blocks, and a few
     * others.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block is a full cube
     */
    public boolean isBlockNormalCube(World world, int x, int y, int z) {
    	return false;
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDarkGen();
    }
  
    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockDarkGen.gemDark = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgem");
        BlockDarkGen.slabSide = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgen_slabside");
        BlockDarkGen.slabTop = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgen_top");
        BlockDarkGen.slabBottom = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgen_bottom");
    }
    
    public void breakBlock(World world, int par2, int par3, int par4, int par5, int par6) {
        TileDarkGen tileentity = (TileDarkGen) world.getTileEntity(par2, par3, par4);
        
        if(tileentity != null) {
            if (!world.isRemote) {
                // VoidAggregationHandler.notifyOfDestruction((TileEntity)tileentity);
                tileentity.removeThisNode();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        if (!par1World.isRemote) {
            TileDarkGen tile = (TileDarkGen) par1World.getTileEntity(par2, par3, par4);
            if (tile != null) {
                System.out.println("activated!");
                par5EntityPlayer.openGui(UnravelingMod.instance, UnravelingMod.proxy.GUI_ID_DG, par1World, par2, par3, par4);
            }
        }
        return true;
    }
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int md, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        if (md == 1) {
            ret.add(new ItemStack(UBlocks.darkGen, 1, 0));
        } else if (md == 0) {
            ret.add(new ItemStack(UItems.artifact, 1 + fortune, 3));
        }
        return ret;
    }

}



