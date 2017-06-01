
package unraveling.item;

import java.util.List;

import net.minecraft.item.Item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import unraveling.UnravelingMod;
import unraveling.entity.EntityTFLoyalZombie;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Iterator;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import thaumcraft.api.IScribeTools;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.StatCollector;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;

import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.lib.research.PlayerKnowledge;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import unraveling.mechanics.ExaminationData;

public class ItemScrutinyNote extends Item {
    @SideOnly(Side.CLIENT)
    public IIcon iconAspect;
    public IIcon iconItem;
    public IIcon iconTorn;

	protected ItemScrutinyNote() {
		super();
        this.maxStackSize = 1;
        setHasSubtypes(true);
	}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir) {
        this.iconAspect = ir.registerIcon(UnravelingMod.ID + ":scroll_basic");
        this.iconItem = ir.registerIcon(UnravelingMod.ID + ":scroll_advanced");
        this.iconTorn = ir.registerIcon(UnravelingMod.ID + ":emptyMazeMap");
    }
    /*
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconIndex(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return this.iconTorn;
        }
        ExaminationData ed = new ExaminationData().readFromNBT(stack.getTagCompound());
        return (ed.noteType == "A") ? this.iconAspect : this.iconItem;
    }*/
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int meta) {
        if (meta == 0) {
            return this.iconAspect;
        }
        if (meta == 1) {
            return this.iconItem;
        }
        return this.iconTorn;
    }
    public static ItemStack createNoteOnAspect(Aspect aspect) {
        int meta = 0;
        ItemScrutinyNote note = (ItemScrutinyNote)TFItems.scrutinyNote;
        ExaminationData ed = ExaminationData.onAspect(aspect);
        System.out.println("ed asp: " + ed.aspectTag + " " + ed.noteType);
        ItemStack finishedResearch = new ItemStack(note, 1, meta);
        setData(finishedResearch, ed);
        return finishedResearch;
    }
    public static ItemStack createNoteOnResearch(String which, int value) {
        int meta = 1;
        ItemScrutinyNote note = (ItemScrutinyNote)TFItems.scrutinyNote;
        ExaminationData ed = ExaminationData.forResearch(which, value);
        System.out.println("ed item: " + ed.ResearchData + " " + ed.noteType);
        ItemStack finishedResearch = new ItemStack(note, 1, meta);
        setData(finishedResearch, ed);
        return finishedResearch;
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        ItemStack finishedResearch = new ItemStack(item, 1, 1);
        setData(finishedResearch, ExaminationData.forResearch("NITOR", 3));
        list.add(finishedResearch);
        
        ItemStack finishedResearch2 = new ItemStack(item, 1, 1);
        setData(finishedResearch2, ExaminationData.forResearch("SINSTONE", 5));
        list.add(finishedResearch2);
        
        ItemStack finishedResearch3 = new ItemStack(item, 1, 0);
        setData(finishedResearch3, ExaminationData.onAspect(Aspect.MAGIC));
        list.add(finishedResearch3);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.hasTagCompound()) {
            ExaminationData ed = new ExaminationData().readFromNBT(stack.getTagCompound());
            String reason = ed.whyICannotUseIt(player);
            if (reason != null) {
                if (world.isRemote) {
                    PlayerNotifications.addNotification(reason);
                }
                return stack;
            }
            ed.onUse(world, player);
            stack.stackSize--;
        }
        return stack;
    }
    

    
	//@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (stack.hasTagCompound()) {
            ExaminationData ed = new ExaminationData().readFromNBT(stack.getTagCompound());
            par3List.add(ed.getDescription());
            if (ed.canSeeAdvancedDescription(par2EntityPlayer)) {
                par3List.add(ed.getAdvancedDescription());
            }
            
        }
	}

	public static void setData(ItemStack itemstack, ExaminationData ed) {
		if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
        }
		ed.writeToNBT(itemstack.getTagCompound());
        System.out.println("SetData " + ed + " at " + itemstack);
	}    
}
