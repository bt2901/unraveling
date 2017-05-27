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

import unraveling.item.ItemAspectNote;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import unraveling.UnravelingMod;
import unraveling.item.TFItems;
import net.minecraft.entity.item.EntityItem;
import java.util.Random;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class TileQuaesitum extends TileEntity implements IInventory  {

    static Random rand = new Random();
    ItemStack[] inventorySlots = new ItemStack[4];
    public int researchtime = 0;
    public int bonus = 0;
    public int progress = 0;

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

    public void startResearch(EntityPlayer player) {
        ItemStack thingResearched = getStackInSlot(0);
        if (thingResearched != null && researchtime <= 0 && canResearch()) {
            bonus = 0;
            progress = 0;
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
            int meta = 0;
            ItemAspectNote note = (ItemAspectNote)TFItems.aspectNote;
            ItemStack finishedResearch = new ItemStack(note, 1, meta);

            Aspect[] al = new AspectList(thingResearched).getAspects();
            int randomIndex = rand.nextInt(al.length);
            note.setAspects(finishedResearch, new AspectList().add(al[randomIndex], 1));
            
            ItemStack stack = getStackInSlot(3);
            if (stack == null || stack.isItemEqual(finishedResearch)) {
                finishedResearch.stackSize += (stack == null) ? 0 : stack.stackSize;
                setInventorySlotContents(3, finishedResearch);
                return;
            }
            
            EntityItem entityitem = new EntityItem(worldObj, xCoord, yCoord + 1, zCoord, new ItemStack(note, 1, meta));                
            entityitem.getEntityItem().setTagCompound((NBTTagCompound) finishedResearch.getTagCompound().copy());
            worldObj.spawnEntityInWorld(entityitem);
        }
    }
    
    @SideOnly(value=Side.CLIENT)
    public int getResearchTimeScaled(int par1) {
        return par1 * progress / researchtime;
    }
    private boolean canResearch() {
        if (getStackInSlot(0) == null) {
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

/*
package thaumcraft.common.tiles;


import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ResearchManager;

public class TileDeconstructionTable
extends TileThaumcraft
implements ISidedInventory {
    public Aspect aspect;
    public int breaktime;
    private ItemStack[] itemStacks = new ItemStack[1];
    private String customName;
    private static final int[] sides = new int[]{0};

    public int func_70302_i_() {
        return 1;
    }

    public ItemStack func_70301_a(int par1) {
        return this.itemStacks[par1];
    }

    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.itemStacks[par1] != null) {
            ItemStack itemstack = this.itemStacks[par1];
            this.itemStacks[par1] = null;
            this.func_70296_d();
            return itemstack;
        }
        return null;
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.itemStacks[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.field_77994_a > this.func_70297_j_()) {
            par2ItemStack.field_77994_a = this.func_70297_j_();
        }
        this.func_70296_d();
    }

    public String func_145825_b() {
        return this.func_145818_k_() ? this.customName : "container.decontable";
    }

    public boolean func_145818_k_() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setGuiDisplayName(String par1Str) {
        this.customName = par1Str;
    }

    public void openChest() {}
    public void closeChest() {}

    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
        AspectList al = ThaumcraftCraftingManager.getObjectTags(par2ItemStack);
        if ((al = ThaumcraftCraftingManager.getBonusTags(par2ItemStack, al)) != null && al.size() > 0) {
            return true;
        }
        return false;
    }

    public int[] getSlotsForFace(int par1) {
        return par1 != 1 ? sides : new int[]{};
    }

    public boolean canInsertItem(int slot, ItemStack par2ItemStack, int face) {
        return face == 1 ? false : this.func_94041_b(slot, par2ItemStack);
    }
    public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
        return true;
    }


}

*/