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

import java.util.List;
/*
import thaumcraft.api.research.ResearchPage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;
import thaumcraft.common.lib.research.ResearchManager;
*/

import unraveling.UnravelingMod;

public class ItemArtifact extends Item {

    final int subtypes = 10;
    IIcon[] icon;
    public ItemArtifact() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        //this.setMaxDurability(0);
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon[0] = ir.registerIcon(UnravelingMod.ID + ":artifacts/circuit");
        this.icon[1] = ir.registerIcon(UnravelingMod.ID + ":artifacts/ancient_seal");
        this.icon[2] = ir.registerIcon(UnravelingMod.ID + ":artifacts/eldritch_mechanism");
        this.icon[3] = ir.registerIcon(UnravelingMod.ID + ":artifacts/shard_of_strange_metal");
        this.icon[4] = ir.registerIcon(UnravelingMod.ID + ":artifacts/disturbing_mirror");
        this.icon[5] = ir.registerIcon(UnravelingMod.ID + ":artifacts/engine");
        this.icon[6] = ir.registerIcon(UnravelingMod.ID + ":artifacts/gear1");
        this.icon[7] = ir.registerIcon(UnravelingMod.ID + ":artifacts/gear2");
        this.icon[8] = ir.registerIcon(UnravelingMod.ID + ":artifacts/glowing_eldritch_device");
        this.icon[9] = ir.registerIcon(UnravelingMod.ID + ":artifacts/eldritch_repository");
    }


    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        if (par1 < this.icon.length) {
            return this.icon[par1];
        }
        return this.icon[0];
    }

    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName() + "." + par1ItemStack.getItemDamage();
    }

    public EnumRarity func_77613_e(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case 2: {
                return EnumRarity.rare;
            }
            case 3: {
                return EnumRarity.epic;
            }
        }
        return EnumRarity.uncommon;
    }
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        super.addInformation(stack, player, list, par4);
        if (stack != null) {
            list.add((Object)EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal((String)UnravelingMod.ID + "item.Artifact.text"));
        }
    }
}