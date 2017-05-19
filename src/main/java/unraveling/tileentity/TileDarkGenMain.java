package unraveling.tileentity;

import java.lang.Math;
import java.awt.Color;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import unraveling.block.TFBlocks;
import unraveling.VoidAggregationHandler;
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
        System.out.println("DGM: in consumeVis(). " + aspect + " amount: " + amount + " returning: " + drain);
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

                TileEntity tileDirectlyAbove = worldObj.getTileEntity(xCoord, yCoord + 1, zCoord);

                System.out.println("children: " + getChildren().size());
                for (WeakReference<TileVisNode> child : getChildren()) {
                    TileVisNode n = child.get();
                
                    if (n != null) {
                        boolean isDirectlyAbove = (n.xCoord == xCoord && n.yCoord == yCoord + 1 && n.zCoord == zCoord);
                        if (!isDirectlyAbove) {
                            // move the vis source
                            System.out.println("rerouting");
                            n.setParent(new WeakReference((TileVisNode)tileDirectlyAbove));
                            n.nodeRefresh = true;
                        }
                    }
                }
                // nodeRefresh = true;
            }

        }
    }
        /*
        if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
            // System.out.println("update . . ." + myGenerators.size() + " " + ticksExisted);
            if (myGenerators.isEmpty()) {
                if (!worldObj.isRemote) {
                    if (++ticksExisted % 40 == 0) {
                        if (searchForGenerators()) {
                            getDescriptionPacket();
                        };
                    }
                }
            } else {
                int genStatus = checkGeneratorsAlive();
                boolean ok = describeSquareOrDie();
                if (genStatus == -2 || !ok) {
                    myGenerators.clear();
                } else {
                    processInfusion();
                }
            }
        }*/



    public void makeLinkageFX(){
    }
    public void makeInfusionFX(){
    }
    /*
    public void processInfusion(){
        int squareSide = maxx-minx;
        for (int x = minx; x <= maxx; x++) {
            for (int z = minz; z <= maxz; z++) {
                for (int y = cury; y <= cury + squareSide; y++) {
                    Block id = worldObj.getBlock(x, y, z);
                    int meta = worldObj.getBlockMetadata(x, y, z);
                    if (id == Blocks.iron_ore && meta == 0) {
                        worldObj.setBlock(x, y, z, TFBlocks.voidOre);
                        worldObj.markBlockForUpdate(x, y, z);
                        Thaumcraft.proxy.nodeBolt(worldObj, x, y, z, x+1.0F, y+1.0F, z+1.0F);
                        return;
                    }
                    // check all gens
                    // if they aren't valid: knows_generators = false
                    // if they has no power
                }
            }
        }
    }*/
    
}
/*
    public void readCustomNBT(NBTTagCompound par1NBTTagCompound) {
        working = par1NBTTagCompound.getBoolean(TAG_WORKING);
        currentAspects.readFromNBT(par1NBTTagCompound.getCompoundTag(TAG_CURRENT_ASPECTS));
        totalAspects.readFromNBT(par1NBTTagCompound.getCompoundTag(TAG_TOTAL_ASPECTS));

        enchantments.clear();
        for (int i : par1NBTTagCompound.getIntArray(TAG_ENCHANTS))
            enchantments.add(i);
        levels.clear();
        for (int i : par1NBTTagCompound.getIntArray(TAG_LEVELS))
            levels.add(i);

        NBTTagList var2 = par1NBTTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        inventorySlots = new ItemStack[getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < inventorySlots.length)
                inventorySlots[var5] = ItemStack.loadItemStackFromNBT(var4);
        }
    }

    public void writeCustomNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setIntArray(TAG_LEVELS, ArrayUtils.toPrimitive(levels.toArray(new Integer[levels.size()])));

        par1NBTTagCompound.setIntArray(TAG_ENCHANTS, ArrayUtils.toPrimitive(enchantments.toArray(new Integer[enchantments.size()])));

        NBTTagCompound totalAspectsCmp = new NBTTagCompound();
        totalAspects.writeToNBT(totalAspectsCmp);

        NBTTagCompound currentAspectsCmp = new NBTTagCompound();
        currentAspects.writeToNBT(currentAspectsCmp);

        par1NBTTagCompound.setBoolean(TAG_WORKING, working);
        par1NBTTagCompound.setTag(TAG_TOTAL_ASPECTS, totalAspectsCmp);
        par1NBTTagCompound.setTag(TAG_CURRENT_ASPECTS, currentAspectsCmp);
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
    }

*/
