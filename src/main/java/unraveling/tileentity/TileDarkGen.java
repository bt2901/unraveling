package unraveling.tileentity;

import unraveling.UnravelingMod;
import unraveling.mechanics.VoidAggregationHandler;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.WorldCoordinates;

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


public class TileDarkGen extends TileVisNode implements IEssentiaTransport {
        
    private static final String NBT_CHARGES = "charges";
    private static final String NBT_RESERVE = "reserve";
    private static final String NBT_POWER = "power";
    private static final String NBT_ID = "my_cluster_id";
    
    public int my_cluster_id = -1;
    public int charges = 0;
    public boolean reserve = false;
    public int power = 0;
    int drawDelay = 0;

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        power = nbt.getInteger(NBT_POWER);
        charges = nbt.getInteger(NBT_CHARGES);
        my_cluster_id = nbt.getInteger(NBT_ID);
        reserve = (nbt.getInteger(NBT_RESERVE) != 0) ? true : false;
    }
    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_RESERVE, reserve? 1 : 0);
        nbt.setInteger(NBT_POWER, power);
        nbt.setInteger(NBT_ID, my_cluster_id);
        nbt.setInteger(NBT_CHARGES, charges);
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

}
