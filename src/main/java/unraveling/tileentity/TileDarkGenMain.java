package unraveling.tileentity;

import java.lang.Math;
import java.awt.Color;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import unraveling.block.TFBlocks;
import unraveling.mechanics.VoidAggregationHandler;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;

import java.util.HashMap;
import java.lang.ref.WeakReference;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.WorldCoordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
//import thaumcraft.api.ThaumcraftA;
import thaumcraft.common.Thaumcraft;


import unraveling.UnravelingMod;

public class TileDarkGenMain extends TileVisNode {

    //private static final String NBT_CATALYST = "enchantsIntArray";
    private static final String NBT_CONVERSION_TIME_LEFT = "conversion_time_left";
    private static final String NBT_WORKING = "working";
    public static final String NBT_GEN_ID = "my_generators_id";
    public int ticksExisted = 0;
    public int my_generators_id;
    public WeakReference<TileVisNode> above = null;
    // TODO:
    // send infusion data
    // last block updated
	    
    @Override
	public int getRange() {
        return 1;
    };
    @Override
	public boolean isSource() {
        return true;
        //return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    };
    public boolean isWorking() {
        // TileEntity tileDirectlyAbove = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
        TileEntity tileDirectlyAbove = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
        boolean redstone = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
        return redstone && (tileDirectlyAbove != null) && (tileDirectlyAbove instanceof TileVisNode);
    }
    
    @Override
	public int consumeVis(Aspect aspect, int amount) {
        if (!isWorking()) {
            return 0;
        };
        if (aspect != Aspect.ENTROPY) {
            return 0;
        };

        /* int drain = Math.min(this.vis.getAmount(aspect), amount);
        if (drain > 0) {
            this.vis.reduce(aspect, drain);
        }*/
        int drain = Math.min(4, amount);
        //System.out.println("DGM: in consumeVis(). " + aspect + " amount: " + amount + " returning: " + drain);
        return drain;
	}
        

    public void readCustomNBT(NBTTagCompound nbt) {

        my_generators_id = nbt.getInteger(NBT_GEN_ID);
        //NBTTagList list = nbt.getTagList("my_generators", VoidAggregationHandler.GEN_NUM);
        //int old_size = myGenerators.size();
        
        /*
        myGenerators = new ArrayList();
        for(int i = 0; i < VoidAggregationHandler.GEN_NUM; i++) {
            NBTTagCompound compound = (NBTTagCompound) list.getCompoundTagAt(i);
            Vec3 genPos = Vec3.createVectorHelper(compound.getDouble("posX"), compound.getDouble("posY"), compound.getDouble("posZ"));
            System.out.println("reading NBT: " + genPos + " " + getMyPos() + " condition: " + genPos != getMyPos() + "size: " + myGenerators.size());
            if (genPos != getMyPos()) {
                myGenerators.add(genPos);
                if (old_size == 0) {
                    Color color = new Color(Aspect.DARKNESS.getColor());
                    Thaumcraft.proxy.arcLightning(worldObj, 
                        xCoord, yCoord, zCoord, 
                        genPos.xCoord, genPos.yCoord, genPos.zCoord, 
                        (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f,
                        10.0F);
                }
            }
        }*/
    }
    
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_GEN_ID, my_generators_id);
        /*
        NBTTagList list = new NBTTagList();
		for(int i = 0; i < VoidAggregationHandler.GEN_NUM; i++) {
            Vec3 genPos = getMyPos();
            if (i < myGenerators.size()) {
                // System.out.println("myGenerators.size() < i" + myGenerators.size() + " " + i)
                genPos = myGenerators.get(i);
            }
            NBTTagCompound compound = new NBTTagCompound();
            compound.setDouble("posX", genPos.xCoord);
            compound.setDouble("posY", genPos.yCoord);
            compound.setDouble("posZ", genPos.zCoord);
            list.appendTag(compound);
        }*/
    }

    public void onNeighborBlockChange() {
        if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
            HashMap<WorldCoordinates, WeakReference<TileVisNode>> sourcelist = VisNetHandler.sources.get(worldObj.provider.dimensionId);
            if (sourcelist != null && sourcelist.containsKey(getLocation())) {
                System.out.println("DGM: removing node . . .");
                invalidate();
                // removeThisNode();
                nodeRefresh = true;
            }
        }        
    }
    @Override
    public void updateEntity() {

        // if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
        if (isWorking()) {
            super.updateEntity();
            if (!worldObj.isRemote && (nodeCounter % 40==0 || nodeRefresh) && (getChildren().size() > 1)) {
                if (above == null) {
                    TileEntity tileDirectlyAbove = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);
                    above = new WeakReference((TileVisNode)tileDirectlyAbove);
                }
                
                for (WeakReference<TileVisNode> child : getChildren()) {
                    TileVisNode n = child.get();

                    if (n != null && n.getParent() != null && n.getParent().get() == (TileVisNode)this) {
                    //if (n != null) {
                        boolean isDirectlyAbove = (n.xCoord == xCoord && n.yCoord == yCoord + 1 && n.zCoord == zCoord);
                        if (!isDirectlyAbove) {
                            // move the vis source
                            System.out.println("rerouting from " + n.getParent() + " to " + above);
                            n.setParent(above);
                            //n.nodeRefresh = true;
                            System.out.println("now parent is " + n.getParent());
                            worldObj.markBlockForUpdate(n.xCoord, n.yCoord, n.zCoord);
                            //n.parentChanged();
                        }
                    }
                }
                //nodeRefresh = true;
            }
        }
    }


}