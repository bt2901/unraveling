package unraveling.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import unraveling.UnravelingMod;
import unraveling.item.TFItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidOre extends Block {
	
	public static IIcon iconSide;
	
	protected BlockVoidOre() {
		super(Material.rock);
		this.setHardness(1.5F);
		this.setResistance(1.0F);
        this.setStepSound(Block.soundTypeStone);
	}

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
	public IIcon getIcon(int side, int meta)
    {
    	return iconSide;
    }
       
    /**
     * A randomly called display update to be able to add particles or other items for display
    @Override
	@SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand)
    {

    	double dx = x + ((rand.nextFloat() - rand.nextFloat()) * 0.3F + 0.5F);
    	double dy = y - 0.1F + ((rand.nextFloat() - rand.nextFloat()) * 0.4F);
    	double dz = z + ((rand.nextFloat() - rand.nextFloat()) * 0.3F + 0.5F);

    	EntityTFTinyFirefly tinyfly = new EntityTFTinyFirefly(world, dx, dy, dz);
    	world.addWeatherEffect(tinyfly);

    	dx = x + ((rand.nextFloat() - rand.nextFloat()) * 0.3F + 0.5F);
    	dy = y - 0.1F + ((rand.nextFloat() - rand.nextFloat()) * 0.4F);
    	dz = z + ((rand.nextFloat() - rand.nextFloat()) * 0.3F + 0.5F);

    	tinyfly = new EntityTFTinyFirefly(world, dx, dy, dz);
    	world.addWeatherEffect(tinyfly);
    }
     */


    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        BlockVoidOre.iconSide = par1IconRegister.registerIcon(UnravelingMod.ID + ":voidore");
    }


}
