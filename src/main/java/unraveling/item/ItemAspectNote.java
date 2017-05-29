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



public class ItemAspectNote extends Item {
    @SideOnly(Side.CLIENT)
    public IIcon icon;

	protected ItemAspectNote() {
		super();
        this.maxStackSize = 16;
        setHasSubtypes(false);
        
	}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon(UnravelingMod.ID + ":scroll_basic");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int par1) {
        return this.icon;
    }
    private String getAspectDescription(Aspect aspect) {
        if (aspect != null) {
            return StatCollector.translateToLocal("tc.aspect.help." + aspect.getTag());
        }
        return StatCollector.translateToLocal("tc.discoveryunknown");
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        
        Aspect aspect = getAspect(stack);
        // stack.getItemDamage() ;
        if (aspect == null) {
            if (world.isRemote) {
                PlayerNotifications.addNotification(StatCollector.translateToLocal("tc.unknownobject"));
            }
            stack.stackSize--;
            return stack;
        }
        String playerName = player.getCommandSenderName();
        PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
        boolean canRead = pk.hasDiscoveredParentAspects(playerName, aspect);
        if (!canRead) {
            if (world.isRemote) {
                for (Aspect parent : aspect.getComponents()) {
                    if (pk.hasDiscoveredAspect(playerName, parent)) continue;
                    String text = new ChatComponentTranslation(StatCollector.translateToLocal("tc.discoveryerror"), new Object[]{getAspectDescription(parent)}).getUnformattedText();
                    //String text = new ChatComponentTranslation(StatCollector.func_74838_a((String)"tc.discoveryerror"), new Object[]{StatCollector.func_74838_a((String)("tc.aspect.help." + parent.getTag()))}).func_150260_c();
                    
                    PlayerNotifications.addNotification(text);
                    break;
                }
            }
            return stack;
        }
        boolean alreadyKnows = false;
        // alreadyKnows =             if ((list2 = (List)Thaumcraft.proxy.getScannedObjects().get(player.func_70005_c_())) != null && list2.contains(prefix + ScanManager.generateItemHash(Item.func_150899_d((int)scan.id), scan.meta))) {;
        if (alreadyKnows) {
            if (world.isRemote) {
                PlayerNotifications.addNotification(StatCollector.translateToLocal("tc.research.hasnote"));
            }
            return stack;
        }
        if (!world.isRemote) {
            int amount = ScanManager.checkAndSyncAspectKnowledge(player, aspect, 1);
            //pk.addAspectPool(playerName, aspect, amount);
            //PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), amount, Short.valueOf(pk.getAspectPoolFor(playerName, aspect))), (EntityPlayerMP) player);
            ResearchManager.scheduleSave(player);
        }
        stack.stackSize--;
        return stack;
    }

    

	public Aspect getAspect(ItemStack itemstack) {
		if (itemstack.hasTagCompound()) {
			AspectList aspects = new AspectList();
			aspects.readFromNBT(itemstack.getTagCompound());
            if (aspects.size() == 0) {
                return null;
            }
			return aspects.getAspectsSorted()[0];
		}
		return null;
	}
	
	public void setAspects(ItemStack itemstack, AspectList aspects) {
		if (!itemstack.hasTagCompound()) itemstack.setTagCompound(new NBTTagCompound());
		aspects.writeToNBT(itemstack.getTagCompound());
	}
    
	//@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (getAspect(stack) == null) {
            par3List.add(StatCollector.translateToLocal("tc.unknownobject"));
        } else {
            par3List.add("Contains observations about " + getAspectDescription(getAspect(stack)));
        }
	}


}
