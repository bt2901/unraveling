package unraveling.mechanics;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
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
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;

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
    // TODO: class "Discovery". Holds information. Not used yet.

    public static class Discovery {
        public ItemStack item;  // relevant item (for description)
        public String researchKey; // relevant research (for effect)
        public boolean overrideRequirements = false;
        public Discovery() {}
        public Discovery(ItemStack is, String key, boolean o) {
            item = is; researchKey = key; overrideRequirements = o;
        }
        public Discovery(ItemStack is, String key) {
            this(is, key, false);
        }
        public static Discovery readFromNBT(NBTTagCompound nbttagcompound) {
            if (nbttagcompound.hasKey("research")) {
                NBTTagCompound tag = nbttagcompound.getCompoundTag("research");
                ItemStack is = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
                String key = tag.getString("researchKey");
                boolean overrideRequirements = tag.getBoolean("override");
                return new Discovery(is, key, overrideRequirements);
            }
            return null;
        }
        public void writeToNBT(NBTTagCompound nbttagcompound) {
            NBTTagCompound res = new NBTTagCompound();
            NBTTagCompound itemNBT = new NBTTagCompound();
            item.writeToNBT(itemNBT);
            res.setString("researchKey", researchKey);
            res.setBoolean("override", overrideRequirements);
            res.setTag("item", itemNBT);
            nbttagcompound.setTag("research", res);
        }
    }
    
    public Integer noteType = -1;
    public Integer value;
    public String aspectTag = null;
    public Discovery ResearchData = null;
    
    public static int ON_ASPECT = 0;
    public static int ON_ITEM = 1;

	public ExaminationData() {}
    
    public static ExaminationData onAspect(Aspect a) {
        ExaminationData ed = new ExaminationData();
        ed.noteType = ON_ASPECT;
        ed.value = 1;
        ed.aspectTag = a.getTag();
        ed.ResearchData = null;
        return ed;
    }
    public static ExaminationData invalid() {
        ExaminationData ed = new ExaminationData();
        ed.noteType = -1;
        ed.value = 0;
        ed.aspectTag = "";
        ed.ResearchData = null;
        return ed;
    }

    public static ExaminationData forResearch(Discovery desc, int value) {
        ExaminationData ed = new ExaminationData();
        ed.noteType = ON_ITEM;
        ed.value = value;
        ed.aspectTag = "";
        ed.ResearchData = desc;
        return ed;
    }

    public String whyICannotUseIt(EntityPlayer player) {
        String playerName = player.getCommandSenderName();
        if (noteType == ON_ASPECT) {
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
        if (noteType == ON_ITEM) {
            if (ResearchCategories.getResearch(ResearchData.researchKey) == null) {
                return StatCollector.translateToLocal("tc.unknownobject");
            }
            boolean overrideRequisites = ResearchData.overrideRequirements; 
            if (!overrideRequisites && !ResearchManager.doesPlayerHaveRequisites(playerName, ResearchData.researchKey)) {
                return StatCollector.translateToLocal("u.note.notready");
            }
            if (ResearchManager.isResearchComplete(playerName, ResearchData.researchKey)) {
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
        if (noteType == ON_ASPECT) {
            details = StatCollector.translateToLocal("tc.aspect.help." + aspectTag);
        } 
        if (noteType == ON_ITEM) {
            // details = ResearchData.researchKey;
            details = ResearchData.item.getDisplayName();
        }
        String desc = new ChatComponentTranslation(prefix, new Object[]{details}).getUnformattedText();
        return desc;
    }
    public String[] getAdvancedDescription() {
        String power_desc = StatCollector.translateToLocal("u.note.description." + noteType + "." + value);
        if (noteType == ON_ITEM) {
            ResearchItem rr = ResearchCategories.getResearch(ResearchData.researchKey);
            if (rr != null) {
                // Aspect primaryaspect = rr.getResearchPrimaryTag();
                return {power_desc, rr.getName()};
            }
            

        }
        return {power_desc};
    }
    public boolean canSeeAdvancedDescription(EntityPlayer player) {
        String playerName = player.getCommandSenderName();
        return ResearchManager.isResearchComplete(playerName, "SCRUTINY_INTUITION");
    }

	public ExaminationData readFromNBT(NBTTagCompound nbttagcompound) {
        noteType = nbttagcompound.getInteger("noteType");
        ResearchData = Discovery.readFromNBT(nbttagcompound);
        value = nbttagcompound.getInteger("value");
        if (nbttagcompound.hasKey("aspect")) {
            aspectTag = nbttagcompound.getString("aspect");
        }
        return this;
    }
    
	public void writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("noteType", noteType);
        if (ResearchData != null) {
            ResearchData.writeToNBT(nbttagcompound);
        }
        nbttagcompound.setInteger("value", value);
        if (aspectTag != "" && aspectTag != null) {
            nbttagcompound.setString("aspect", aspectTag);
        }
    }

    public void onUse(World world, EntityPlayer player) {
        if (world.isRemote) {
            return;
        }
        if (noteType == ON_ASPECT) {
            String playerName = player.getCommandSenderName();
            PlayerKnowledge pk = Thaumcraft.proxy.getPlayerKnowledge();
            int amount = ScanManager.checkAndSyncAspectKnowledge(player, Aspect.getAspect(aspectTag), value);
                //pk.addAspectPool(playerName, aspect, amount);
                //PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), amount, Short.valueOf(pk.getAspectPoolFor(playerName, aspect))), (EntityPlayerMP) player);
            ResearchManager.scheduleSave(player);
            return;
        }
        if (noteType == ON_ITEM) {
            String playerName = player.getCommandSenderName();
            String key = ResearchData.researchKey;
            if (!ResearchManager.isResearchComplete(playerName, key)) {
                PacketHandler.INSTANCE.sendTo((IMessage)new PacketResearchComplete(key), (EntityPlayerMP)player);
                Thaumcraft.proxy.getResearchManager().completeResearch(player, key);
                world.playSoundAtEntity((Entity)player, UnravelingMod.ID + ":research.clue", 1.0f, 1.0f);
            }
            ResearchManager.scheduleSave(player);

        return;
        }
    }
        
}


/*
ResearchItem
    public String getName() {
        return StatCollector.func_74838_a((String)("tc.research_name." + this.key));
    }

    public String getText() {
        return StatCollector.func_74838_a((String)("tc.research_text." + this.key));
    }
*/

