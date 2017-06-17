package unraveling.mechanics.voidgen;
//package unraveling.tileentity;

import unraveling.UnravelingMod;
import unraveling.mechanics.voidgen.VoidAggregationHandler;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.WorldCoordinates;


import net.minecraft.entity.player.EntityPlayer;
import java.lang.reflect.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import net.minecraftforge.common.util.ForgeDirection;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class TileDarkGen extends TileVisNode implements IEssentiaTransport, IInventory {
        
    private static final String NBT_CHARGES = "charges";
    private static final String NBT_RESERVE = "reserve";
    private static final String NBT_POWER = "power";
    private static final String NBT_ID = "my_cluster_id";
    
    public int my_cluster_id = -1;
    public int charges = 0;
    public boolean reserve = false;
    public int power = 0;
    int drawDelay = 0;
    ItemStack[] inventorySlots = new ItemStack[1];

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        power = nbt.getInteger(NBT_POWER);
        charges = nbt.getInteger(NBT_CHARGES);
        my_cluster_id = nbt.getInteger(NBT_ID);
        reserve = (nbt.getInteger(NBT_RESERVE) != 0) ? true : false;
        
        NBTTagList var2 = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        inventorySlots = new ItemStack[getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < inventorySlots.length)
                inventorySlots[var5] = ItemStack.loadItemStackFromNBT(var4);
        }

    }
    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_RESERVE, reserve? 1 : 0);
        nbt.setInteger(NBT_POWER, power);
        nbt.setInteger(NBT_ID, my_cluster_id);
        nbt.setInteger(NBT_CHARGES, charges);
        
        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < inventorySlots.length; ++var3) {
            if (inventorySlots[var3] != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                inventorySlots[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        nbt.setTag("Items", var2);
    }
    
	@Override
	public void updateEntity() {
        //System.out.println("updating. ");
        /*
        WorldCoordinates drainer = new WorldCoordinates(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
        Field field = VisNetHandler.class.getDeclaredField("nearbyNodes");
        field.setAccessible(true);
        HashMap<WorldCoordinates, ArrayList<WeakReference<TileVisNode>>> value = field.get(null);
        if (!value.containsKey(drainer)) {
            System.out.println("not containsKey");
		}
        */
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            // TODO: count
            // System.out.println("updating. " + this.my_cluster_id + " reserve: " + this.reserve + " power; " + this.power);
            //System.out.println("updating. " + this.my_cluster_id + " power; " + this.power);

            if (this.charges <= 0) {
                if (this.reserve) {
                    this.charges = 100;
                    this.reserve = false;
                    this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                } else if (this.drawEssentia()) {
                    this.charges = 100;
                    this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                }
            }
            if (!this.reserve && this.drawEssentia()) {
                this.reserve = true;
            }
            if (this.charges == 0) {
                this.charges = -1;
                this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
            this.power = (this.power > 0)? this.power - 1: 0;
            if (this.power < 5) {
                if (VisNetHandler.isNodeValid(getParent())) {
                    //System.out.println("drain vis. " + xCoord +" "+ yCoord +" "+ zCoord);
                    this.power += VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, Aspect.ENTROPY, 10);
                } else {
                    //System.out.println("will not drain vis, invalid. ");
                }
            }
            if (this.power > 0) {
                if (my_cluster_id == -1) {
                    my_cluster_id = VoidAggregationHandler.assignId(this, worldObj);
                    System.out.println("my id is " + my_cluster_id);
                }
                VoidAggregationHandler.generatorUpdateCallback(my_cluster_id, this, worldObj);
            }
        }
	}
    
    boolean drawEssentia() {
        if (++this.drawDelay % 5 != 0) {
            return false;
        }
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.worldObj, xCoord, yCoord, zCoord, ForgeDirection.DOWN);
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(ForgeDirection.UP)) {
                return false;
            }
            if (ic.getSuctionAmount(ForgeDirection.UP) < this.getSuctionAmount(ForgeDirection.DOWN) && 
                ic.takeEssentia(Aspect.DARKNESS, 1, ForgeDirection.UP) == 1) {
                return true;
            }
        }
        return false;
    }
    
    //////////////////////
    // Essentia Transport Interface stubs
    //////////////////////
    
    @Override
    public boolean isConnectable(ForgeDirection face) {
        //return face != ForgeDirection.UP;
        return face == ForgeDirection.DOWN;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        // return face != ForgeDirection.UP;
        return face == ForgeDirection.DOWN;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return Aspect.DARKNESS; // or Aspect.VOID or Aspect.ENTROPY
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        if (face == ForgeDirection.UP) {
            return 0;
        }
        return (!this.reserve || this.charges <= 0) ? 128 : 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection loc) {
        return null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection loc) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection loc) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection loc) {
        return 0;
    }    
    //////////////////////
    // Vis Net Member Interface stubs
    //////////////////////
    @Override
	public int getRange() {
        return 1;
    };
	/**
	 * @return true if this is the source or root node of the vis network. 
	 */
    @Override
	public boolean isSource() {
        return false;
    };
    // TODO
    @Override
	public void parentChanged() { }
    
    @Override
	public void triggerConsumeEffect(Aspect aspect) {	}
    //////////////////////
    // Inventory
    //////////////////////
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return (VoidAggregationHandler.getCatalystPower(stack) > 0);
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
}
