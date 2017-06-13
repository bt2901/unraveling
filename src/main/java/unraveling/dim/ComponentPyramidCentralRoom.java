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


public class ComponentPyramidCentralRoom extends ComponentPyramidRoom {
    int level;

	public ComponentPyramidCentralRoom() {
		super();
	}

	public ComponentPyramidCentralRoom(Random rand, int x, int y, int z, int i) {
		super(rand, x, y, z, PyramidMap.ROOMCENTRAL);
        this.level = i;
	}
    
    public void createDoorway(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block what, int meta) {
        fillWithMetadataBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, what, meta, what, meta, false);
        int cX = (minX + maxX)/2;
        int cZ = (minZ + maxZ)/2;
        placeBlockAtCurrentPosition(world, Blocks.air, 0, cX, minY, cZ, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, cX, minY + 1, cZ, sbb);
    }
    public void makeStairsXPos(World world, StructureBoundingBox sbb, int minZ, int maxZ, int startX, int startY, int howLong) {
        Block brick = PyramidMain.wallBlockID;
        int meta = PyramidMain.wallBlockMeta;
        int x = startX;
        int y = startY;
        for (int z = minZ; z <= maxZ; ++z) {
            for (int i = 0; i <= howLong; ++i) {
                x = startX + i;
                y = (i == 0)? startY : startY + i - 1;
                placeBlockAtCurrentPosition(world, brick, meta, x, y, z, sbb);
            }
        }
    }
    
    public void createPlatforms(World world, StructureBoundingBox sbb) {
        int myX; 
        int myZ; 
        
        myX = 1;
        myZ = roomDepth / 2;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
        myX = roomWidth - 1;
        myZ = roomDepth / 2;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
        myX = roomWidth / 2;
        myZ = 1;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
        myX = roomWidth / 2;
        myZ = roomDepth - 1;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
    }
    public void createFourDoorways(World world, StructureBoundingBox sbb) {
            int minX; 
            int minZ; 
            int maxX; 
            int maxZ; 
            minX = 0; maxX = 0;
            minZ = roomDepth / 2 - 1;
            maxZ = roomDepth / 2 + 1;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
            minX = roomWidth; maxX = roomWidth;
            minZ = roomDepth / 2 - 1;
            maxZ = roomDepth / 2 + 1;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
            minX = roomWidth/2 - 1; 
            maxX = roomWidth/2 + 1;
            minZ = roomDepth; maxZ = roomDepth;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
            minX = roomWidth/2 - 1; 
            maxX = roomWidth/2 + 1;
            minZ = 0; maxZ = 0;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta);
    }


	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		;
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, roomHeight - 1, roomDepth - 1);
        createPlatforms(world, sbb);
        createFourDoorways(world, sbb);

        int minZ = 1;
        int maxZ = 2;
        int startX = roomWidth/2 + 1;
        int startY = 0;
        makeStairsXPos(world, sbb, minZ, maxZ, startX, startY, 3);
		return true;
	}
	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		super.func_143012_a(par1NBTTagCompound);
        par1NBTTagCompound.setInteger("level", level);
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
        this.level = par1NBTTagCompound.getInteger("level");
 	}
    
}
