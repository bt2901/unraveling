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
import net.minecraft.world.IBlockAccess;
import java.util.Random;

public class BlockEldritchBricks extends Block {
	
    private static int N = 3;
	public static IIcon icon_top;
	public static IIcon[] icons = new IIcon[N * 2];
	
	protected BlockEldritchBricks() {
		super(Material.rock);
		this.setHardness(1.5F);
		this.setResistance(1.0F);
        this.setStepSound(Block.soundTypeStone);
	}

    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    @Override
	public IIcon getIcon(int side, int meta) {
    	return icons[0];
    }


    @Override
	@SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        icons[0] = par1IconRegister.registerIcon(UnravelingMod.ID + ":eb_1");
        icons[1] = par1IconRegister.registerIcon(UnravelingMod.ID + ":eb_2");
        icons[2] = par1IconRegister.registerIcon(UnravelingMod.ID + ":eb_3");
        icons[3] = par1IconRegister.registerIcon(UnravelingMod.ID + ":ebi_1");
        icons[4] = par1IconRegister.registerIcon(UnravelingMod.ID + ":ebi_2");
        icons[5] = par1IconRegister.registerIcon(UnravelingMod.ID + ":ebi_3");
        icon_top = par1IconRegister.registerIcon("thaumcraft" + ":es_p");
    }
    @SideOnly(value=Side.CLIENT)
    public IIcon getIcon(IBlockAccess ba, int x, int y, int z, int side) {
        String l = "" + x + "" + y + "" + z;
        Random r1 = new Random(Math.abs(l.hashCode() * 100) + 1);
        int i = r1.nextInt(12345 + side) % N;
        if (side < 2) {
            return icon_top;
        }
        int sideMod = (side < 4) ? 0 : 1;
        return this.icons[i + (sideMod * N)];
    }

}
