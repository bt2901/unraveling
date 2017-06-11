package unraveling.block;

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
import unraveling.item.UItems;
import unraveling.tileentity.TileQuaesitum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import thaumcraft.api.research.ResearchPage;


public class BlockQuaesitum extends BlockContainer {
	
	public static IIcon foundationSide;
	public static IIcon foundation;
	public static IIcon pillar;
	public static IIcon slabSide;
	public static IIcon slabTop;
	public static IIcon slabBottom;
	
    public Random random = new Random();
	protected BlockQuaesitum() {
		super(Material.rock);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, (10.0F/16.0F), 1.0F);
		this.setHardness(1.0F);
		this.setStepSound(Block.soundTypeStone);
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
    	return UnravelingMod.proxy.getQRenderID();
    }
    
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
	public IIcon getIcon(int side, int meta) {
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
	public int getLightValue(IBlockAccess world, int x, int y, int z) 
    {
    	return 2;
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
        return new TileQuaesitum();
    }
  
    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockQuaesitum.pillar = par1IconRegister.registerIcon(UnravelingMod.ID + ":quaesitum_pillar");
        BlockQuaesitum.foundationSide = par1IconRegister.registerIcon(UnravelingMod.ID + ":quaesitum_f_side");
        BlockQuaesitum.foundation = par1IconRegister.registerIcon(UnravelingMod.ID + ":quaesitum_f_bottom");
        BlockQuaesitum.slabSide = par1IconRegister.registerIcon(UnravelingMod.ID + ":quaesitum_slabside");
        BlockQuaesitum.slabTop = par1IconRegister.registerIcon(UnravelingMod.ID + ":quaesitum_top");
        BlockQuaesitum.slabBottom = par1IconRegister.registerIcon(UnravelingMod.ID + ":quaesitum_bottom");
    }

    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
        TileQuaesitum analyzer = (TileQuaesitum) par1World.getTileEntity(par2, par3, par4);

        if (analyzer != null) {
            for (int j1 = 0; j1 < analyzer.getSizeInventory(); ++j1) {
                ItemStack itemstack = analyzer.getStackInSlot(j1);

                if (itemstack != null) {
                    float f = random.nextFloat() * 0.8F + 0.1F;
                    float f1 = random.nextFloat() * 0.8F + 0.1F;
                    EntityItem entityitem;

                    for (float f2 = random.nextFloat() * 0.8F + 0.1F; itemstack.stackSize > 0; par1World.spawnEntityInWorld(entityitem)) {
                        int k1 = random.nextInt(21) + 10;

                        if (k1 > itemstack.stackSize)
                            k1 = itemstack.stackSize;

                        itemstack.stackSize -= k1;
                        entityitem = new EntityItem(par1World, par2 + f, par3 + f1, par4 + f2, new ItemStack(itemstack.getItem(), k1, itemstack.getItemDamage()));
                        float f3 = 0.05F;
                        entityitem.motionX = (float) random.nextGaussian() * f3;
                        entityitem.motionY = (float) random.nextGaussian() * f3 + 0.2F;
                        entityitem.motionZ = (float) random.nextGaussian() * f3;

                        if (itemstack.hasTagCompound())
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                    }
                }
            }

            par1World.func_147453_f(par2, par3, par4, par5);
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }



    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        if (!par1World.isRemote) {
            TileQuaesitum tile = (TileQuaesitum) par1World.getTileEntity(par2, par3, par4);
            if (tile != null) {
                par5EntityPlayer.openGui(UnravelingMod.instance, UnravelingMod.proxy.GUI_ID_Q, par1World, par2, par3, par4);
            }
        }
        return true;
    }
}
