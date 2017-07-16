package unraveling.item;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBook;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import unraveling.UnravelingMod;

public class ItemThaumiumBook extends ItemBook {
    IIcon icon;

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon(UnravelingMod.ID + ":thaumbook_alt");
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return this.icon;
    }
    public EnumRarity func_77613_e(ItemStack stack) {
        return EnumRarity.uncommon;
    }
    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    @Override
    public int getItemEnchantability() {
        return 5;
    }
    
}