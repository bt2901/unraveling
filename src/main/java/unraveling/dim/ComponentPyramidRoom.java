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


public class ComponentPyramidRoom extends StructureComponent {
    int roomHeight;
    int roomWidth;
    int roomDepth;
    int type;

	public ComponentPyramidRoom() {
		super();
	}

	public ComponentPyramidRoom(Random rand, int x, int y, int z, int type) {
		super(type);
        this.coordBaseMode = rand.nextInt(4);
        this.type = type;
        roomWidth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        roomDepth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        roomHeight = PyramidMain.height;
        if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2SUDDEN_LOW) {
            roomHeight = PyramidMain.height * 2;
        }
        this.boundingBox = new StructureBoundingBox(x, y, z, x + roomWidth, y + roomHeight, z + roomDepth);
	}
    
    public void fillWithWalls(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
       fillWithMetadataBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, false);
    }
    public void fillAroundHorizontally(World world, StructureBoundingBox sbb, int myX, int myY, int myZ, int radius, Block what, int meta) {
       fillWithMetadataBlocks(world, sbb, myX - radius, myY, myZ - radius, myX + radius, myY, myZ + radius, what, meta, what, meta, false);
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
        
        Block what = Blocks.air;
        int meta = 0;
        int ypos = 0;
        // if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOMCENTRAL || type == PyramidMap.ROOM2SUDDEN_LOW) {
        if (type == PyramidMap.ROOMCENTRAL) {
                what = PyramidMain.rootBlockID;
                meta = PyramidMain.rootBlockMeta;
        }
        if (type == PyramidMap.ROOM2LOW) {
            what = PyramidMain.wallBlockID;
            meta = 1;
        }
        if (type == PyramidMap.ROOM2SUDDEN_LOW) {
            ypos = PyramidMain.height;
            what = ConfigBlocks.blockEldritch;
            meta = 4;
        }
        if (type == PyramidMap.ROOM_VIRTUAL) {
            what = ConfigBlocks.blockEldritch;
            meta = 4;
        }
        if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM_VIRTUAL) {
            fillWithWalls(world, sbb, 0, 0, 0, roomWidth, 0, roomDepth);
            fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, 0, roomDepth - 1);
        }
        if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2SUDDEN_LOW) {
            // fillWithMetadataBlocks(world, sbb, 1, roomHeight, 1, roomWidth - 1, roomHeight, roomDepth - 1, Blocks.air, 0, Blocks.air, 0, false);
        }
        
        if (type == PyramidMap.ROOMCENTRAL) {
            
            fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, roomHeight - 1, roomDepth - 1);
            createPlatforms(world, sbb);
            createFourDoorways(world, sbb);

            int minZ = 1;
            int maxZ = 2;
            int startX = roomWidth/2 + 1;
            int startY = 0;
            makeStairsXPos(world, sbb, minZ, maxZ, startX, startY, 3);
        }
		// fillWithMetadataBlocks(world, sbb, 1, ypos, 1, roomWidth, ypos, roomDepth, what, meta, what, meta, false);
        
        // DEBUG
		// fillWithMetadataBlocks(world, sbb, 0, 0, 0, roomWidth, roomHeight, roomDepth, Blocks.dirt, 0, Blocks.dirt, 0, false);

		return true;
	}
	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		
        par1NBTTagCompound.setInteger("roomHeight", roomHeight);
        par1NBTTagCompound.setInteger("roomDepth", roomDepth);
        par1NBTTagCompound.setInteger("roomWidth", roomWidth);
        par1NBTTagCompound.setInteger("type", type);
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		this.roomHeight = par1NBTTagCompound.getInteger("roomHeight");
        this.roomWidth = par1NBTTagCompound.getInteger("roomWidth");
        this.roomDepth = par1NBTTagCompound.getInteger("roomDepth");
        this.type = par1NBTTagCompound.getInteger("type");
 	}
    
}
