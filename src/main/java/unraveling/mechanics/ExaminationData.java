package unraveling.mechanics;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;

import unraveling.UnravelingMod;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
// import thaumcraft.common.lib.network.playerdata.PacketAspectPool;

import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.lib.research.PlayerKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.research.ResearchCategories;


public class ExaminationData {
    
    public Integer noteType = -1;
    public Integer value;
    public String aspectTag = null;
    public String ResearchData = null;
    

	public ExaminationData() {}
    
    public static ExaminationData onAspect(Aspect a) {
        ExaminationData ed = new ExaminationData();
        ed.noteType = 0;
        ed.value = 1;
        ed.aspectTag = a.getTag();
        ed.ResearchData = "";
        return ed;
    }

    public static ExaminationData forResearch(String desc, int value) {
        ExaminationData ed = new ExaminationData();
        ed.noteType = 1;
        ed.value = value;
        ed.aspectTag = "";
        ed.ResearchData = desc;
        return ed;
    }


    public String whyICannotUseIt(EntityPlayer player) {
        String playerName = player.getCommandSenderName();
        if (noteType == 0) {
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspect == null) {
                return StatCollector.translateToLocal("tc.unknownobject");
            }
            PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
            boolean canRead = pk.hasDiscoveredParentAspects(playerName, aspect);
            if (!canRead) {
                for (Aspect parent : aspect.getComponents()) {
                    if (!pk.hasDiscoveredAspect(playerName, parent)) {
                        String desc = StatCollector.translateToLocal("tc.aspect.help." + parent.getTag());
                        return new ChatComponentTranslation(StatCollector.translateToLocal("tc.discoveryerror"), new Object[]{desc}).getUnformattedText();
                    }
                }
            }
            boolean alreadyKnows = false;
            // alreadyKnows =             if ((list2 = (List)Thaumcraft.proxy.getScannedObjects().get(player.func_70005_c_())) != null && list2.contains(prefix + ScanManager.generateItemHash(Item.func_150899_d((int)scan.id), scan.meta))) {;
            if (alreadyKnows) {
                return StatCollector.translateToLocal("u.note.hasread");
            }
            return null;
        }
        if (noteType == 1) {
            //System.out.println("doesPlayerHaveRequisites: " + ResearchData);
            //System.out.println("Research: " + );
            if (ResearchCategories.getResearch(ResearchData) == null) {
                return StatCollector.translateToLocal("tc.unknownobject");
            }
            if (!ResearchManager.doesPlayerHaveRequisites(playerName, ResearchData)) {
                return StatCollector.translateToLocal("u.note.notready");
            }
            if (ResearchManager.isResearchComplete(playerName, ResearchData)) {
                return StatCollector.translateToLocal("u.note.hasread");
            }
            return null;
        }
        return null;
    }
    
    public String getDescription() {
        String details = StatCollector.translateToLocal("tc.discoveryunknown");
        if (noteType < 0) {
            return details;
        }
        String prefix = StatCollector.translateToLocal("u.note." + noteType);
        if (noteType == 0) {
            details = StatCollector.translateToLocal("tc.aspect.help." + aspectTag);
        } 
        if (noteType == 1) {
            details = ResearchData;
        }
        String desc = new ChatComponentTranslation(prefix, new Object[]{details}).getUnformattedText();
        return desc;
    }
    public String getAdvancedDescription() {
        return StatCollector.translateToLocal("u.note.description." + noteType + "." + value);
    }
    public boolean canSeeAdvancedDescription(EntityPlayer player) {
        String playerName = player.getCommandSenderName();
        return ResearchManager.isResearchComplete(playerName, "RESEARCHER2");
        // return true;
    }

    

	
	public ExaminationData readFromNBT(NBTTagCompound nbttagcompound) {
        noteType = nbttagcompound.getInteger("noteType");
        ResearchData = nbttagcompound.getString("ResearchData");
        value = nbttagcompound.getInteger("value");
        if (nbttagcompound.hasKey("aspect")) {
            aspectTag = nbttagcompound.getString("aspect");
        }
        return this;
    }
    
	public void writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("noteType", noteType);
        nbttagcompound.setString("ResearchData", ResearchData);
        nbttagcompound.setInteger("value", value);
        if (aspectTag != "" && aspectTag != null) {
            nbttagcompound.setString("aspect", aspectTag);
        }
    }
    // assuming !world.isRemote
    public void onUse(World world, EntityPlayer player) {
        
        System.out.println("use note: "  + noteType + "|" + aspectTag + "|" + ResearchData);
        if (world.isRemote) {
            return;
        }
        if (noteType == 0) {
            String playerName = player.getCommandSenderName();
            PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
            int amount = ScanManager.checkAndSyncAspectKnowledge(player, Aspect.getAspect(aspectTag), value);
                //pk.addAspectPool(playerName, aspect, amount);
                //PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), amount, Short.valueOf(pk.getAspectPoolFor(playerName, aspect))), (EntityPlayerMP) player);
            ResearchManager.scheduleSave(player);
            return;
        }
        if (noteType == 1) {
            String playerName = player.getCommandSenderName();
            // String key = "CRIMSON";
            String key = ResearchData;
            if (!ResearchManager.isResearchComplete(playerName, key)) {
                PacketHandler.INSTANCE.sendTo((IMessage)new PacketResearchComplete(key), (EntityPlayerMP)player);
                Thaumcraft.proxy.getResearchManager().completeResearch(player, key);
                world.playSoundAtEntity((Entity)player, "thaumcraft:learn", 0.75f, 1.0f);
            }
            ResearchManager.scheduleSave(player);

        return;
        }
    }
        
}




