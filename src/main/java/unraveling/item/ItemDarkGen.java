package unraveling.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.IIcon;
import unraveling.UnravelingMod;

// stub: I have no idea how to render dark gen in inventory

public class ItemDarkGen extends ItemBlock {

  private IIcon icon;

  public ItemDarkGen(Block b) {
    super(b);
    setMaxStackSize(1);
    setUnlocalizedName("darkGenStub");
    }

  @Override
  public void registerIcons(IIconRegister iconRegister) {
    this.itemIcon = iconRegister.registerIcon(UnravelingMod.ID + ":" + "darkGenStub");
  }
}