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
import unraveling.item.UItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSaprolite extends Block {
	
	public static IIcon iconSide;
	
	protected BlockSaprolite() {
		super(Material.rock);
        this.setStepSound(Block.soundTypeStone);
	}

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
	public IIcon getIcon(int side, int meta) {
    	return iconSide;
    }


    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        BlockSaprolite.iconSide = par1IconRegister.registerIcon(UnravelingMod.ID + ":saprolite");
    }


}
