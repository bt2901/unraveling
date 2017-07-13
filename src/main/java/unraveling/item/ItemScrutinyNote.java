
package unraveling.item;

import java.util.List;

import net.minecraft.item.Item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import unraveling.UnravelingMod;
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

import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigItems;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;

import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.lib.research.PlayerKnowledge;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import unraveling.mechanics.ExaminationData;
import unraveling.mechanics.ExaminationData.Discovery;

public class ItemScrutinyNote extends Item {
    @SideOnly(Side.CLIENT)
    public IIcon iconAspect;
    public IIcon iconItem;
    public IIcon iconTorn;
    public IIcon iconFancy;

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
        this.iconTorn = ir.registerIcon(UnravelingMod.ID + ":vellum");
        this.iconFancy = ir.registerIcon(UnravelingMod.ID + ":scroll_fancy");
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
        ItemScrutinyNote note = (ItemScrutinyNote)UItems.scrutinyNote;
        ExaminationData ed = ExaminationData.onAspect(aspect);
        ItemStack finishedResearch = new ItemStack(note, 1, ed.noteType);
        setData(finishedResearch, ed);
        return finishedResearch;
    }
    public static ItemStack createEmptyNote() {
        ItemScrutinyNote note = (ItemScrutinyNote)UItems.scrutinyNote;
        ExaminationData ed = ExaminationData.invalid();
        ItemStack finishedResearch = new ItemStack(note, 1, ed.noteType);
        setData(finishedResearch, ed);
        return finishedResearch;
    }
    public static ItemStack createNoteOnResearch(Discovery which, int value) {
        ItemScrutinyNote note = (ItemScrutinyNote)UItems.scrutinyNote;
        ExaminationData ed = ExaminationData.forResearch(which, value);
        ItemStack finishedResearch = new ItemStack(note, 1, ed.noteType);
        setData(finishedResearch, ed);
        return finishedResearch;
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        ItemStack lodestone = new ItemStack(ConfigItems.itemCompassStone, 1, 0);
        Discovery sinstone = new Discovery(lodestone, "SINSTONE", true);
        ItemStack torch = new ItemStack(Blocks.torch, 1, 0);
        Discovery nitor = new Discovery(torch, "NITOR", false);
        list.add(createNoteOnResearch(nitor, 3));
        list.add(createNoteOnResearch(sinstone, 1));

        list.add(createNoteOnResearch(new Discovery(torch, "SCRUTINY_INTUITION", false), 5));
        list.add(createNoteOnResearch(new Discovery(torch, "SCRUTINY_SILKTOUCH", false), 5));
        list.add(createNoteOnResearch(new Discovery(torch, "SCRUTINY_RECYCLING", false), 5));

        list.add(createNoteOnAspect(Aspect.MAGIC));
        list.add(createNoteOnAspect(Aspect.MAN));
        list.add(createNoteOnAspect(Aspect.VOID));
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
            if (!world.isRemote) {
                ed.onUse(world, player);
            }
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
                for (String desc : ed.getAdvancedDescription()) {
                    par3List.add(desc);
                }
            }
        }
	}

	public static void setData(ItemStack itemstack, ExaminationData ed) {
		if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new NBTTagCompound());
        }
		ed.writeToNBT(itemstack.getTagCompound());
	}    
}
