package unraveling;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Iterator;


import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import unraveling.block.TFBlocks;
import unraveling.tileentity.TileDarkGenMain;
import unraveling.tileentity.TileDarkGen;
import unraveling.VoidPacketHandler;
import thaumcraft.common.Thaumcraft;

import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import net.minecraft.tileentity.TileEntity;

public class VoidAggregationHandler {

	//private static HashMap<Integer, ArrayList<WorldCoordinates>> generatorsGroup = new HashMap<Integer, ArrayList<WorldCoordinates>>();
    public static final int GEN_NUM = 4;

	//private static HashMap<Integer, WorldCoordinates> generatorsMain = new HashMap<Integer, WorldCoordinates>();
	private static HashMap<WorldCoordinates, Integer> generatorsGroupId = new HashMap<WorldCoordinates, Integer>();
	private static HashMap<Integer, ShapeData> generatorsShape = new HashMap<Integer, ShapeData>();
	private static HashMap<Integer, HashSet<WorldCoordinates>> generatorsUpdated = new HashMap<Integer, HashSet<WorldCoordinates>>();
	private static HashSet<WorldCoordinates> orphanGenerators = new HashSet<WorldCoordinates>();
	// private static HashSet<WorldCoordinates> orphanGeneratorsMain = new HashMap<Integer, HashSet<WorldCoordinates>>();
    public static int ticks = 0;
	private static Random rand = new Random();
    
    public static void notifyOfDestruction(TileEntity tileentity) {
        System.out.println("Destruction");
    }
    
    private static boolean search(WorldCoordinates wc) {
        // very stupid bruteforce approach
        ArrayList<WorldCoordinates> tmp = new ArrayList<WorldCoordinates>(orphanGenerators);
        for (int i = 0; i < tmp.size(); ++i) {
            for (int j = i + 1; j < tmp.size(); ++j) {
                for (int k = j + 1; k < tmp.size(); ++k) {
                    if (ShapeData.isValid(tmp.get(i), tmp.get(j), tmp.get(k), wc)) {
                        //ShapeData sd = new ShapeData(tmp.get(i), tmp.get(j), tmp.get(k), wc);
                        int new_id = 0; // TODO
                        generatorsGroupId.put(wc, new_id); 
                        generatorsGroupId.put(tmp.get(i), new_id); 
                        generatorsGroupId.put(tmp.get(j), new_id); 
                        generatorsGroupId.put(tmp.get(k), new_id); 
                        
                        System.out.println("VAH: creating new shape for group " + new_id);

                        WorldCoordinates[] gens = {tmp.get(i), tmp.get(j), tmp.get(k), wc};                        generatorsShape.put(new_id, new ShapeData(Arrays.asList(gens), GEN_NUM));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static int assignId(TileEntity dg, World worldObj) {
        WorldCoordinates wc = new WorldCoordinates(dg);
        if (generatorsGroupId.containsKey(wc)) {
            int id = generatorsGroupId.get(wc);
            if (generatorsShape.containsKey(id) && generatorsShape.get(id) != null) {
                return id;
            }
        }
        System.out.println("VAH: in assignId(). ");
        if (orphanGenerators.size() < 3) {
            orphanGenerators.add(wc);
            return -1;
        }
        if (search(wc)) {
            for (Iterator<WorldCoordinates> iterator = orphanGenerators.iterator(); iterator.hasNext();) {
                WorldCoordinates owc = iterator.next();
                if (generatorsGroupId.containsKey(owc)) {
                    iterator.remove();
                }
            }
            return generatorsGroupId.get(wc);
        }
        orphanGenerators.add(wc);
        return -1;
    }
    
    public static void generatorUpdateCallback(int group_id, TileEntity dg, World worldObj) {
        WorldCoordinates wc = new WorldCoordinates(dg);
        //System.out.println("VAH: in callback(). " + group_id + " xCoord: " + dg.xCoord + " zCoord: " + dg.zCoord);
        HashSet<WorldCoordinates> validGens = generatorsUpdated.get(group_id);
        //System.out.println("VAH: validGens " + validGens);
        if (validGens == null) {
            if (group_id == -1) {
                group_id = assignId(dg, worldObj);
            }
            if (group_id == -1) {
                return;
            }
            validGens = new HashSet<WorldCoordinates>();
            generatorsUpdated.put(group_id, validGens);
        }
        ShapeData shape = generatorsShape.get(group_id);
        if (shape == null) {
            //System.out.println("VAH: null shape!");
            group_id = assignId(dg, worldObj);
        }
        
        //System.out.println("VAH: validGens.size() " + validGens.size() + " " + validGens);

        if (validGens.contains(wc)) {
            validGens.clear();
            System.out.println("VAH: bad things incoming");
            // we already checked this group on previous tick 
            // break
            return;
        }
        //int old_size = validGens.size();
        validGens.add(wc);
        if (validGens.size() == GEN_NUM) {
            // generatorsUpdated.put(group_id, 0);
            //System.out.println("VAH: in processTransformation(). ");
            processTransformation(worldObj, group_id);
            validGens.clear();
        }
    }
    // @SideOnly(Side.SERVER)
	public static void processTransformation(World worldObj, int group_id) {
        if (!generatorsShape.containsKey(group_id)) {
            System.out.println("VAH: unknown shape!");
            return;
        }
        ShapeData shape = generatorsShape.get(group_id);
        int squareSide = shape.maxx - shape.minx;
        if (rand.nextInt(10) == 0) {
            int x = shape.minx + rand.nextInt(1) * rand.nextInt(squareSide);
            int x2 = shape.maxx - rand.nextInt(1) * rand.nextInt(squareSide);

            int z = shape.minz + rand.nextInt(1) * rand.nextInt(squareSide);
            int z2 = shape.maxz - rand.nextInt(1) * rand.nextInt(squareSide);

            int y = shape.cury + rand.nextInt(squareSide);
            int y2 = shape.cury + rand.nextInt(squareSide);
            int col = Aspect.DARKNESS.getColor();

            FMLProxyPacket message = VoidPacketHandler.makeBlockParticlePacket(x, y, z, x2, y2, z2, col);
            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, x, y, z, 64);
            UnravelingMod.genericChannel.sendToAllAround(message, targetPoint);
        }
        for (int x = shape.minx; x <= shape.maxx; x++) {
            for (int z = shape.minz; z <= shape.maxz; z++) {
                for (int y = shape.cury; y <= shape.cury + squareSide; y++) {
                    Block id = worldObj.getBlock(x, y, z);
                    int meta = worldObj.getBlockMetadata(x, y, z);
                    if (id == Blocks.iron_ore && meta == 0) {
                        if (rand.nextInt(20) == 0) {
                            worldObj.setBlock(x, y, z, TFBlocks.voidOre);
                            worldObj.markBlockForUpdate(x, y, z);
                            
                            FMLProxyPacket message = VoidPacketHandler.makeTransformBlockPacket(x, y, z);

                            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, x, y, z, 64);
        
                            UnravelingMod.genericChannel.sendToAllAround(message, targetPoint);
                            return;
                        }
                    }
                    // check all gens
                    // if they aren't valid: knows_generators = false
                    // if they has no power
                }
            }
        }
    
		return;
	}

    public boolean searchForGenerators(World worldObj, TileDarkGenMain dgm) {
        WorldCoordinates wc = new WorldCoordinates(dgm);
        //if (!generatorsGroupId.containsKey(wc) {
		//	calculateNearbyNodes(world, x, y, z);
		//}
        ArrayList<WorldCoordinates> myGenerators = new ArrayList<WorldCoordinates>();
        System.out.println("searching for gens . . .");
        int y = dgm.yCoord;
        //for (int y = yCoord - 8; y <= yCoord + 8; y++) {
        for (int x = dgm.xCoord - 8; x <= dgm.xCoord + 8; x++) {
            for (int z = dgm.zCoord - 8; z <= dgm.zCoord + 8; z++) {
                Block id = worldObj.getBlock(x, y, z);
                if (id == TFBlocks.darkGen) {
                    System.out.println("found gens at " + x + " " + y + " " + z);
                    // TODO: 
                    myGenerators.add(new WorldCoordinates(x, y, z, worldObj.provider.dimensionId));
                    //Thaumcraft.proxy.drawInfusionParticles3(world, x, y, z, x + 1, y + 5, z + 3);
                }
            }
        }
        if (myGenerators.size() != GEN_NUM) {
            return false;
        }
        ShapeData sd = new ShapeData(myGenerators, GEN_NUM);
        int new_id = generatorsGroupId.size();
        generatorsGroupId.put(wc, new_id);
        //generatorsMain.put(new_id, wc);
        generatorsShape.put(new_id, sd);
        return true;
    }
    public int calcPotency(int group_id, World worldObj) {
        ArrayList<Vec3> myGenerators = generatorsShape.get(group_id).cornersList();
		for(int i = 0; i < GEN_NUM; i++) {
            Vec3 genPos = myGenerators.get(i);
            int x = (int)genPos.xCoord;
            int y = (int)genPos.yCoord;
            int z = (int)genPos.zCoord;
            //TileEntity gen = worldObj.getTileEntity(x, y, z);
            // TODO: check Tile Entity: light, catalyst
            // check void ore and void blocks in area
        }
        
        // Block id = worldObj.getBlock(x, y, z);
        // int meta = worldObj.getBlockMetadata(x, y, z);
        // TODO: check if this is ore

        return 1;
    }
    
    public int calcFragility(int group_id, World worldObj) {
        ArrayList<Vec3> myGenerators = generatorsShape.get(group_id).cornersList();
		for(int i = 0; i < GEN_NUM; i++) {
            Vec3 genPos = myGenerators.get(i);
            int x = (int)genPos.xCoord;
            int y = (int)genPos.yCoord;
            int z = (int)genPos.zCoord;
            //TileEntity gen = worldObj.getTileEntity(x, y, z);
            // TODO: check Tile Entity: light, catalyst
            // check void ore and void blocks in area
        }
        
        // Block id = worldObj.getBlock(x, y, z);
        // int meta = worldObj.getBlockMetadata(x, y, z);
        // TODO: check if this is ore

        return 1;
    }
}

    
    // -2: EVERYTHING IS BROKEN. cannot find GEN_NUM generators; We need to assign generators again
    // -1: some of gens aren't valid but some of them are. Very unstable configuration. 
    // 0: no essentia, no vis or something
    // 1: OK
    // TODO: I need just override ON BLOCK DROP in dark gen slaves
    /*
    public int checkGeneratorsAlive(TileDarkGenMain dgm) {
        if (myGenerators.size() != GEN_NUM) {
            return -2;
        }
        int numHavingVis = 0;
		for(int i = 0; i < GEN_NUM; i++) {
            Vec3 genPos = myGenerators.get(i);
            int x = (int)genPos.xCoord;
            int y = (int)genPos.yCoord;
            int z = (int)genPos.zCoord;
            Block id = worldObj.getBlock(x, y, z);
            int meta = worldObj.getBlockMetadata(x, y, z);
            if (id != TFBlocks.darkGen || meta != 0) {
                System.out.println("bad gen at " + genPos);
                return -2;
            } else {
                // TODO: check Tile Entity: essentia, catalyst
                // TODO: check Tile Entity: vis
                ++numHavingVis;
            }
        }
        if (numHavingVis > 0 && numHavingVis < GEN_NUM) {
            return -1;
        }
        return 1;
    }*/

	

/*
	static ArrayList<WorldCoordinates> cache = new ArrayList<WorldCoordinates>();
	public static ArrayList<Object[]> findClosestNodes(TileAbstractVisNode target,
			TileAbstractVisNode parent, ArrayList<Object[]> in) {
		
		if (cache.size() > 512 || cache.contains(new WorldCoordinates(parent))) return in;
		cache.add(new WorldCoordinates(parent));
		
		for (WeakReference<TileAbstractVisNode> childWR : parent.getChildren()) {
			TileAbstractVisNode child = childWR.get();

			if (child != null && !child.equals(target) && !child.equals(parent)) {
				float r2 = inRange(child.getWorldObj(), child.getLocation(),
						target.getLocation(), target.getRange());
				if (r2 > 0) {
					in.add(new Object[] { child, r2 });
				}
				
				in = findClosestNodes(target, child, in);
			}
		}
		return in;
	}
*/

    


	// public static HashMap<WorldCoordinates,WeakReference<TileAbstractVisNode>>
	// noderef = new HashMap<WorldCoordinates,WeakReference<TileAbstractVisNode>>();
	//
	// public static TileAbstractVisNode getClosestNodeWithinRadius(World world, int x,
	// int y, int z, int radius) {
	// TileAbstractVisNode out = null;
	// WorldCoordinates wc = null;
	// float cd = Float.MAX_VALUE;
	// for (int sx = x - radius; sx <= x + radius; sx++) {
	// for (int sy = y - radius; sy <= y + radius; sy++) {
	// for (int sz = z - radius; sz <= z + radius; sz++) {
	// wc = new WorldCoordinates(sx,sy,sz,world.provider.dimensionId);
	// if (noderef.containsKey(wc)) {
	// float d = wc.getDistanceSquared(x, y, z);
	// if (d<radius*radius && noderef.get(wc).get()!=null &&
	// !noderef.get(wc).get().isReceiver() &&
	// isNodeValid(noderef.get(wc).get().getParent())
	// ) {
	// out = noderef.get(wc).get();
	// cd = d;
	// }
	// }
	// }
	// }
	// }
	// return out;
	// }



    /*
    @Override
    public void updateEntity() {
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
        }
    }
*/

    
    
