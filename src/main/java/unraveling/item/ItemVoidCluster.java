package unraveling.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;

import java.util.List;
import unraveling.UnravelingMod;

public class ItemVoidCluster extends Item {

    IIcon icon;
    public ItemVoidCluster() {
        this.setMaxStackSize(64);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon(UnravelingMod.ID + ":clustervoidalt");
    }


    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return this.icon;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName();
    }

    public EnumRarity func_77613_e(ItemStack stack) {
        return EnumRarity.uncommon;
    }
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
    }
}
