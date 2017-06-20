package unraveling.dim;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import unraveling.block.UBlocks;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.common.config.ConfigBlocks;


public class ComponentVoidProductionRoom extends ComponentPyramidRoom {

	public ComponentVoidProductionRoom() {
		super();
	}

	public ComponentVoidProductionRoom(Random rand, int x, int y, int z) {
	super(rand, x, y, z, PyramidMap.ROOM2SUDDEN_LOW);
        this.coordBaseMode = rand.nextInt(4);
	this.roomHeight = PyramidMain.height;
	}


	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		;
	}
	public void placeGen(World world, StructureBoundingBox sbb, int x, int minY, int z, int maxY) {
         
            placeBlockAtCurrentPosition(world, UBlocks.darkGen, 0, x, maxY, z, sbb);
            placeBlockAtCurrentPosition(world, ConfigBlocks.blockJar, 0, x, minY, z, sbb);
	    for (int y = minY + 1; y < maxY; ++y) {
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 3, x, y, z, sbb);
	    }
            placeBlockAtCurrentPosition(world, ConfigBlocks.blockMetalDevice, 14, x, maxY+1, z, sbb);
	}
	public void placeGenAny(World world, StructureBoundingBox sbb, int x, int minY, int z, int dx, int maxY, int dz, boolean fancy) {
		if(fancy){
                    // placeGenFancy(world, sbb, x, minY, z, dx, maxY, dz);
                    placeGenOutsideFancy(world, sbb, x, minY, z, dx, maxY, dz);
		} else {
                    placeGen(world, sbb, x, minY, z, maxY);
		}
	}
	public void placeGenOutsideFancy(World world, StructureBoundingBox sbb, int x, int minY, int z, int dx, int maxY, int dz) {
         
            placeBlockAtCurrentPosition(world, UBlocks.darkGen, 0, x, maxY, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockJar, 0, x-dx, minY, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockJar, 0, x, minY, z-dz, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 3, x-dx, maxY, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 3, x, maxY, z-dz, sbb);

	        for (int y = minY + 1; y < maxY; ++y) {
                    placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 0, x-dx, y, z, sbb);
                    placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 0, x, y, z-dz, sbb);
	        }
            placeBlockAtCurrentPosition(world, ConfigBlocks.blockMetalDevice, 14, x, maxY+1, z, sbb);
	}
	public void placeGenFancy(World world, StructureBoundingBox sbb, int x, int minY, int z, int dx, int maxY, int dz) {
         
            placeBlockAtCurrentPosition(world, UBlocks.darkGen, 0, x, maxY, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockJar, 0, x+dx, minY, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockJar, 0, x, minY, z+dz, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 4, x, maxY-1, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 3, x+dx, maxY-1, z, sbb);
                placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 3, x, maxY-1, z+dz, sbb);

	        for (int y = minY + 1; y < maxY-1; ++y) {
                    placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 0, x+dx, y, z, sbb);
                    placeBlockAtCurrentPosition(world, ConfigBlocks.blockTube, 0, x, y, z+dz, sbb);
	        }
            placeBlockAtCurrentPosition(world, ConfigBlocks.blockMetalDevice, 14, x, maxY+1, z, sbb);
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        
            // if (rand.nextFloat() > 0.33) {
	    Block otile = ConfigBlocks.blockCosmeticSolid;
	    int otilemeta = 1;
            int pace = PyramidMain.oddBias + PyramidMain.evenBias;
            int platf = 3;
            fillWithMetadataBlocks(world, sbb, pace, 1, pace, 2*pace, platf-1, 2*pace, otile, otilemeta, otile, otilemeta, false);

	    boolean fancy = true;
            placeGenAny(world, sbb, pace, 1, pace, +1, platf, +1, fancy);
            placeGenAny(world, sbb, 2*pace, 1, pace, -1, platf, +1, fancy);
            placeGenAny(world, sbb, pace, 1, 2*pace, +1, platf, -1, fancy);
            placeGenAny(world, sbb, 2*pace, 1, 2*pace, -1, platf, -1, fancy);

            // boolean orientation = (rand.nextFloat() > 0.5);
            boolean orientation = true;
            int myX = (orientation ? 1 : roomWidth / 2);
            int myZ = (orientation ? roomDepth / 2 : 1);

            int genX = (orientation ? 2 : roomWidth / 2);
            int genZ = (orientation ? roomDepth / 2 : 2);
            //int myX = 1;
            //int myZ = 1;
            fillAroundHorizontally(world, sbb, myX, roomHeight, myZ, 1, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
            placeBlockAtCurrentPosition(world, UBlocks.darkGenMain, 0, genX, roomHeight + 1, genZ, sbb);
            placeBlockAtCurrentPosition(world, ConfigBlocks.blockMetalDevice, 14, genX, roomHeight + 2, genZ, sbb);
            //}

        return true;
        }
}
