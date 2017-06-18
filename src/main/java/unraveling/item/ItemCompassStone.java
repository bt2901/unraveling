
package unraveling.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.utils.EntityUtils;
import net.minecraft.world.ChunkPosition;
import net.minecraft.entity.player.EntityPlayer;

import thaumcraft.client.lib.PlayerNotifications;

import unraveling.UnravelingMod;

public class ItemCompassStone
extends Item {
    public IIcon[] icon = new IIcon[3];
    private IIcon t = null;

    public static HashMap<WorldCoordinates, Long> warpedPortals = new HashMap();
    public static ChunkPosition strongholdPos;
    
    public ItemCompassStone() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        //this.setMaxDurability(0);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon[0] = ir.registerIcon(UnravelingMod.ID + ":sinister_stone_inactive");
        this.icon[1] = ir.registerIcon(UnravelingMod.ID + ":sinister_stone_active");
        this.icon[2] = ir.registerIcon(UnravelingMod.ID + ":sinister_stone_active2");
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return par1 == 1 ? this.icon[1] : (this.t == null ? this.icon[0] : this.t);
    }

    public void onUpdate(ItemStack p_77663_1_, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        if (world.isRemote) {
            ArrayList<WorldCoordinates> del = new ArrayList<WorldCoordinates>();
            this.t = null;
            ChunkPosition pos = world.findClosestStructure("Stronghold", (int)entity.posX, (int)entity.posY, (int)entity.posZ);
            if (pos != null && EntityUtils.isVisibleTo(0.66f, entity, pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ, 512.0f)) {
                this.t = this.icon[2];
                return;
            }
            for (WorldCoordinates wc2 : warpedPortals.keySet()) {
                if (wc2.dim != world.provider.dimensionId || !EntityUtils.isVisibleTo(0.66f, entity, (double)wc2.x + 0.5, (double)wc2.y + 0.5, (double)wc2.z + 0.5, 256.0f)) continue;
                this.t = this.icon[1];
                return;            
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack((Item)this, 1, 0));
    }

    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.rare;
    }
        @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        
        String msg = "pos: " + world.findClosestStructure("Stronghold", (int)player.posX, (int)player.posY, (int)player.posZ);

        PlayerNotifications.addNotification(msg);
        return stack;
    }

}

