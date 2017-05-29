package unraveling.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockContainer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import unraveling.UnravelingMod;
import unraveling.item.TFItems;
import unraveling.tileentity.TileDarkGenMain;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDarkGenMain extends BlockContainer {
	
	public static IIcon fullSide;
	public static IIcon slabTop;
	public static IIcon slabBottom;
    
	protected BlockDarkGenMain() {
		super(Material.rock);
		//this.setBlockBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 1.0F, 0.8125F);
		this.setHardness(1.0F);
		this.setStepSound(Block.soundTypeStone);
        }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    @Override
	public boolean renderAsNormalBlock()
    {
        return true;
    }

	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    @Override
	public boolean isOpaqueCube()
    {
        return true;
    }
    
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
	public IIcon getIcon(int side, int meta)
    {
        if (side == 0)
            return slabBottom;
        if (side == 1)
            return slabTop;
        return fullSide;
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
	public int getLightValue(IBlockAccess world, int x, int y, int z) 
    {
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
    public boolean isBlockNormalCube(World world, int x, int y, int z) 
    {
    	return false;
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDarkGenMain();
    }
  
    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        BlockDarkGenMain.fullSide = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgen_fullside");
        BlockDarkGenMain.slabTop = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgen_top");
        BlockDarkGenMain.slabBottom = par1IconRegister.registerIcon(UnravelingMod.ID + ":darkgen_bottom");
    }
    @Override
    public void onNeighborBlockChange(World worldObj, int x, int y, int z, Block p_149695_5_) {
        TileDarkGenMain tileentity = (TileDarkGenMain) worldObj.getTileEntity(x, y, z);
        
        if(tileentity != null) {
            tileentity.onNeighborBlockChange();
        }
    }



}
