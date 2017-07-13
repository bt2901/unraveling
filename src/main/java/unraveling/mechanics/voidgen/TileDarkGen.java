package unraveling.mechanics.voidgen;
//package unraveling.tileentity;

import unraveling.UnravelingMod;
import unraveling.UnravelingConfig;

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
        
    // you can switch suction if you have knowledge
    // you can empty a generator: remove a catalyst
    // 
    
    private static final String NBT_ID = "my_cluster_id";
    
    public Aspect drainSelected; 
    public int my_cluster_id = -1;
    public int reserve = 0;
    public int power = 0;
    int drawDelay = 0;
    ItemStack[] inventorySlots = new ItemStack[1];

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        power = nbt.getInteger("power");
        my_cluster_id = nbt.getInteger(NBT_ID);
        reserve = nbt.getInteger( "reserve");
        if (nbt.hasKey("aspect")) {
            drainSelected = Aspect.getAspect(nbt.getString("aspect"));
        } else {
            drainSelected = null;
        }
    
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
        nbt.setInteger( "reserve", reserve);
        nbt.setInteger("power", power);
        nbt.setInteger(NBT_ID, my_cluster_id);
        if (drainSelected != null) {
            nbt.setString("aspect", drainSelected.getTag());
        } 
        
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
    /*

    */
    
	@Override
	public void updateEntity() {
        super.updateEntity();
        if (!this.worldObj.isRemote) {
            this.power = (this.power > 0)? this.power - 1: 0;
            if (this.power < UnravelingConfig.maxVisReserve) {
                if (VisNetHandler.isNodeValid(getParent())) {
                    this.power += VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, Aspect.ENTROPY, 10);
                }
            }

            if (this.power > 0) {
                if (this.reserve < UnravelingConfig.maxEssentiaReserve) {
                    if (drainSelected == null) {
                        drainSelected = Aspect.DARKNESS;
                    }
                    if (this.drawEssentia()) {
                        this.reserve += 1;
                        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    }
                }
                if (my_cluster_id == -1) {
                    my_cluster_id = VoidAggregationHandler.assignId(this, worldObj);
                    System.out.println("my id is " + my_cluster_id);
                }
                VoidAggregationHandler.generatorUpdateCallback(my_cluster_id, this, worldObj);
            }
        }
	}
    
    boolean drawEssentiaFrom(ForgeDirection from) {
        TileEntity te = ThaumcraftApiHelper.getConnectableTile(this.worldObj, xCoord, yCoord, zCoord, from);
        ForgeDirection to = from.getOpposite();
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(to)) {
                return false;
            }
            if (ic.getSuctionAmount(to) < this.getSuctionAmount(from) && 
                ic.takeEssentia(drainSelected, 1, to) == 1) {
                return true;
            }
        }
        return false;
    }
    boolean drawEssentia() {
        if (++this.drawDelay % 5 != 0) {
            return false;
        }
        ForgeDirection[] directions = {ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST};
        for (ForgeDirection dir : directions) {
            if (drawEssentiaFrom(dir)) {
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
        return (face != ForgeDirection.UP && face != ForgeDirection.DOWN);
        // return (face != ForgeDirection.UP);
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return (face != ForgeDirection.UP && face != ForgeDirection.DOWN);
        // return face != ForgeDirection.UP;
        // return face == ForgeDirection.DOWN;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) { return false; }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        drainSelected = aspect;
    }

    @Override
    public boolean renderExtendedTube() {
        return true;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return drainSelected;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        if (face == ForgeDirection.UP || face == ForgeDirection.DOWN) {
            return 0;
        }
        return (this.power > 0 && this.reserve < UnravelingConfig.maxEssentiaReserve) ? 128 : 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection loc) { return null; }

    @Override
    public int getEssentiaAmount(ForgeDirection loc) { return 0; }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection loc) { return 0; }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection loc) { return 0; }    
    
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
    // TODO?
    @Override
	public void parentChanged() { }
    
    @Override
	public void triggerConsumeEffect(Aspect aspect) {	}
    //////////////////////
    // Inventory
    //////////////////////
    
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (UnravelingConfig.getCatalystPower(stack) > 0) {
            return true;
        }
        return false;
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
