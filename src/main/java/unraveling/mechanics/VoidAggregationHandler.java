package unraveling.mechanics;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Iterator;

import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import unraveling.UnravelingMod;
import unraveling.block.UBlocks;
import unraveling.tileentity.TileDarkGenMain;
import unraveling.tileentity.TileDarkGen;
import unraveling.mechanics.VoidPacketHandler;
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

/*
How it works?

if all void aggregators are working (consuming vis):
they try to consume essentia (T & V) in some balanced way
if they cannot: 
    so bad.
    something bad (but occasionaly useful) happens
if they can:
    convert one block

if there's nothing to convert:
all is OK
if there's things to convert but unablle to:
    something bad
    
*/

public class VoidAggregationHandler {
    
    public static final int GEN_NUM = 4;

	private static HashMap<WorldCoordinates, Integer> generatorsGroupId = new HashMap<WorldCoordinates, Integer>();
	private static HashMap<Integer, ShapeData> generatorsShape = new HashMap<Integer, ShapeData>();
	private static HashMap<Integer, HashSet<WorldCoordinates>> generatorsUpdated = new HashMap<Integer, HashSet<WorldCoordinates>>();
	private static HashSet<WorldCoordinates> orphanGenerators = new HashSet<WorldCoordinates>();
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
        HashSet<WorldCoordinates> validGens = generatorsUpdated.get(group_id);
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
            group_id = assignId(dg, worldObj);
        }
        
        if (validGens.contains(wc)) {
            validGens.clear();
            System.out.println("VAH: bad things incoming");
            FMLProxyPacket m = VoidPacketHandler.makeVentPacket(dg.xCoord, dg.yCoord, dg.zCoord, Aspect.ELDRITCH.getColor());
            sendMessage(m, dg.xCoord, dg.yCoord, dg.zCoord, worldObj);
            // we already checked this group on previous tick 
            // break
            return;
        }
        validGens.add(wc);
        if (validGens.size() == GEN_NUM) {
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
        if (rand.nextInt(5) == 0) {
            int x = shape.minx + rand.nextInt(1) * rand.nextInt(squareSide);
            int x2 = shape.maxx - rand.nextInt(1) * rand.nextInt(squareSide);

            int z = shape.minz + rand.nextInt(1) * rand.nextInt(squareSide);
            int z2 = shape.maxz - rand.nextInt(1) * rand.nextInt(squareSide);

            int y = shape.cury + rand.nextInt(squareSide);
            int y2 = shape.cury + rand.nextInt(squareSide);
            int col = (rand.nextBoolean())? Aspect.DARKNESS.getColor() : Aspect.VOID.getColor();
            if (rand.nextInt(5) == 0) {
                col = Aspect.ELDRITCH.getColor();
            }
            FMLProxyPacket message = VoidPacketHandler.makeBlockParticlePacket(x, y, z, x2, y2, z2, col);
            sendMessage(message, x, y, z, worldObj);
        }
        for (int x = shape.minx; x <= shape.maxx; x++) {
            for (int z = shape.minz; z <= shape.maxz; z++) {
                for (int y = shape.cury; y <= shape.cury + squareSide; y++) {
                    Block id = worldObj.getBlock(x, y, z);
                    int meta = worldObj.getBlockMetadata(x, y, z);
                    if (id == Blocks.iron_ore && meta == 0) {
                        if (rand.nextInt(40) == 0) {
                            worldObj.setBlock(x, y, z, UBlocks.voidOre);
                            worldObj.markBlockForUpdate(x, y, z);
                            
                            FMLProxyPacket message = VoidPacketHandler.makeTransformBlockPacket(x, y, z);
                            worldObj.playSoundEffect((double)x, (double)y, (double)z, UnravelingMod.ID + ":random.blockconvert", 1.0F, 1.0F);

                            sendMessage(message, x, y, z, worldObj);
                            // return;
                        }
                    }
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
                if (id == UBlocks.darkGen) {
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
        ShapeData shape = generatorsShape.get(group_id);
        int squareSide = shape.maxx - shape.minx;
        int result = 0;
        
        ArrayList<Vec3> myGenerators = shape.cornersList();
		for(int i = 0; i < GEN_NUM; i++) {
            Vec3 genPos = myGenerators.get(i);
            int x = (int)genPos.xCoord;
            int y = (int)genPos.yCoord;
            int z = (int)genPos.zCoord;
            TileEntity gen = worldObj.getTileEntity(x, y, z);
            // result += catalyst
            // result += consumed vis

        }
        
        for (int x = shape.minx; x <= shape.maxx; x++) {
            for (int z = shape.minz; z <= shape.maxz; z++) {
                for (int y = shape.cury; y <= shape.cury + squareSide; y++) {
                    Block id = worldObj.getBlock(x, y, z);
                    if (id == UBlocks.voidOre) {
                        result += 1;
                    }
                    if (id.getUnlocalizedName().equals("voidmetalBlock") || id.getUnlocalizedName().equals("voidblock")) {
                        result += 4;
                    }
                }
            }
        }
        return result;
    }
    public int calcCostCap(int group_id, World worldObj) {
        int base = 5;
        int weakestCatalyst = 5;
        int brightestPlace = 0;

        ShapeData shape = generatorsShape.get(group_id);
        
        ArrayList<Vec3> myGenerators = shape.cornersList();
		for(int i = 0; i < GEN_NUM; i++) {
            Vec3 genPos = myGenerators.get(i);
            int x = (int)genPos.xCoord;
            int y = (int)genPos.yCoord;
            int z = (int)genPos.zCoord;
            TileEntity gen = worldObj.getTileEntity(x, y, z);
            Block id = worldObj.getBlock(x, y, z);
            int meta = worldObj.getBlockMetadata(x, y, z);
            if (id != UBlocks.darkGen || meta != 0) {
                return -1; // EVERYBODY PANIC
            }
            // TODO: check Tile Entity: light, catalyst
            // if gen is not valid: retutn -1
        }
        return base - weakestCatalyst + brightestPlace / 2;
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
            
            // TODO: check Tile Entity: essentia
            // result += consumed essentia
            
        }
        
        // Block id = worldObj.getBlock(x, y, z);
        // int meta = worldObj.getBlockMetadata(x, y, z);
        // TODO: check if this is ore

        return 1;
    }
    
    public static void sendMessage(FMLProxyPacket message, int x, int y, int z, World worldObj) {
        NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, x, y, z, 64);
        UnravelingMod.genericChannel.sendToAllAround(message, targetPoint);
    }
    
    public void maybeSpawnBadThing(int fragility, int potency) {
        /*
        if (potency >= 500) {
            maybe spawn Hungry Node
        }
        if (potency >= 300) {
            import thaumcraft.common.lib.utils.EntityUtils
            maybe spawn wisp (tenebrae, vacuous, alienis)
            if (potency >= 400)
                EntityUtils.makeChampion(EntityMob entity, boolean persist)
        }
        if (potency >= 100) {
            spawn shadow gas from TT
            consume catalyst
            flux 
            blindness
            attack player
        }

        */
    }
}
