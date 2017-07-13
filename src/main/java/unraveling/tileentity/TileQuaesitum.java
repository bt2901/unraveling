package unraveling.tileentity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import unraveling.UnravelingMod;
import unraveling.item.UItems;
import unraveling.item.ItemScrutinyNote;
import unraveling.UnravelingConfig;
import unraveling.mechanics.ScrutinyHandler;
import unraveling.mechanics.ExaminationData;
import unraveling.mechanics.ExaminationData.Discovery;

import net.minecraft.entity.item.EntityItem;
import java.util.Random;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ResearchManager;


public class TileQuaesitum extends TileEntity implements IInventory  {

    static Random rand = new Random();
    public ItemStack[] inventorySlots = new ItemStack[4];
    public int researchtime = 0;
    public int bonus = 0;
    public int progress = 0;
    String playerName = "";

	public TileQuaesitum() {
	}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }


    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        NBTTagList var2 = par1NBTTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        inventorySlots = new ItemStack[getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < inventorySlots.length)
                inventorySlots[var5] = ItemStack.loadItemStackFromNBT(var4);
        }
        //isResearching = par1NBTTagCompound.getBoolean("isResearching");
        progress = par1NBTTagCompound.getInteger("progress");
        researchtime = par1NBTTagCompound.getInteger("researchtime");
        bonus = par1NBTTagCompound.getInteger("bonus");
        playerName = par1NBTTagCompound.getString("playerName");
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);

        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < inventorySlots.length; ++var3) {
            if (inventorySlots[var3] != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                inventorySlots[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        par1NBTTagCompound.setTag("Items", var2);
        //par1NBTTagCompound.setBoolean("isResearching", isResearching);
        par1NBTTagCompound.setInteger("researchtime", researchtime);
        par1NBTTagCompound.setInteger("progress", progress);
        par1NBTTagCompound.setInteger("bonus", bonus);
        par1NBTTagCompound.setString("playerName", playerName);
    }


    @Override
    public String getInventoryName() {
        return "TODO";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }
    /////// IInventory boilerplate

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
    }
    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (inventorySlots[i] != null) {
            ItemStack stackAt;

            if (inventorySlots[i].stackSize <= j) {
                stackAt = inventorySlots[i];
                inventorySlots[i] = null;
                return stackAt;
            } else {
                stackAt = inventorySlots[i].splitStack(j);

                if (inventorySlots[i].stackSize == 0)
                    inventorySlots[i] = null;

                return stackAt;
            }
        }

        return null;
    }
    @Override
    public int getSizeInventory() {
        return inventorySlots.length;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return inventorySlots[i];
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return getStackInSlot(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        inventorySlots[i] = itemstack;
    }
    @Override
    public boolean canUpdate() {
        return true;
    }

    public int bonusFrom(String key) {
        if (ResearchManager.isResearchComplete(playerName, key)) {
            return 1;
        }
        return 0;
    }
    public void startResearch(EntityPlayer player) {
        ItemStack thingResearched = getStackInSlot(0);
        if (thingResearched != null && researchtime <= 0 && canResearch()) {
            progress = 0;
            playerName = player.getCommandSenderName();
            bonus = 0;
            bonus += bonusFrom("SCRUTINY_INTUITION") + bonusFrom("SCRUTINY_RECYCLING") + bonusFrom("SCRUTINY_SILKTOUCH");
            
            AspectList al = new AspectList(thingResearched);
            // simple items are easy to study
            if (al.size() <= 2 || al.visSize() < 5) {
                researchtime = 40;
            } else {
                // complex things take longer
                researchtime = 10 * (al.visSize() + al.size());
            }
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            this.markDirty();
        }
    }
    public void finishResearch() {
        ItemStack thingResearched = getStackInSlot(0);
        progress = 0;
        researchtime = 0;
        
        if (thingResearched != null) {
            ResearchManager.consumeInkFromTable(this.inventorySlots[2], true);
            this.inventorySlots[1].stackSize--;
            if(this.inventorySlots[1].stackSize == 0) {
                this.inventorySlots[1] = (ItemStack)null;
            }

            ItemStack finishedResearch = ScrutinyHandler.finishResearch(thingResearched, playerName, bonus);
            boolean consumed = ScrutinyHandler.maybeConsumeItem(thingResearched, playerName, bonus, finishedResearch);
            if (consumed) {
                // System.out.println("Consuming " + inventorySlots[0]);
                this.inventorySlots[0].stackSize--;
                if(this.inventorySlots[0].stackSize == 0) {
                    this.inventorySlots[0] = (ItemStack)null;
                }
                // System.out.println("Now " + this.inventorySlots[0].stackSize);
            }
            if (finishedResearch == null) {
                return;
            }
            ItemStack stack = getStackInSlot(3);
            if (stack==null) {
                setInventorySlotContents(3, finishedResearch);
                return;
            }
            
            EntityItem entityitem = new EntityItem(worldObj, xCoord, yCoord + 1, zCoord, finishedResearch);                
            worldObj.spawnEntityInWorld(entityitem);
        }
        playerName = "";

    }
    
    @SideOnly(value=Side.CLIENT)
    public int getResearchTimeScaled(int par1) {
        return par1 * progress / researchtime;
    }
    private boolean canResearch() {
        if (getStackInSlot(0) == null || getStackInSlot(1) == null) {
            return false;
        }
        if (!ResearchManager.consumeInkFromTable(this.inventorySlots[2], false)) {
            return false;
        }
        AspectList al = ThaumcraftCraftingManager.getObjectTags(getStackInSlot(0));
        if (al == null || al.size() == 0) {
            return false;
        }
        return true;
    }
    
    @Override
    public void updateEntity() {
        boolean flag1 = false;
        if (!worldObj.isRemote) {
            /*if (this.researchtime == 0 && this.canResearch()) {
                this.researchtime = 40;
                flag1 = true;
            }*/
            if (this.progress < this.researchtime && this.canResearch()) {
                ++this.progress;
                
                if (this.progress == this.researchtime) {
                    finishResearch();
                    flag1 = true;
                }
            } else {
                this.researchtime = 0;
                this.progress = 0;
            }
        }
        if (flag1) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            this.markDirty();
        }
    }

    
}

