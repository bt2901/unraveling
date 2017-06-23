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
        if (type == PyramidMap.ROOM2SUDDEN_LOW) {
            roomHeight = PyramidMain.height * 2;
        }
        this.boundingBox = new StructureBoundingBox(x, y, z, x + roomWidth, y + roomHeight, z + roomDepth);
        if (type == PyramidMap.ENTRANCE) {
            this.boundingBox = new StructureBoundingBox(x, y, z - 3 * roomDepth, x + roomWidth, y + roomHeight, z + roomDepth);
        }
	}
    public void fillWithWalls(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
       fillWithMetadataBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, false);
    }
    public void fillAroundHorizontally(World world, StructureBoundingBox sbb, int myX, int myY, int myZ, int radius, Block what, int meta) {
       fillWithMetadataBlocks(world, sbb, myX - radius, myY, myZ - radius, myX + radius, myY, myZ + radius, what, meta, what, meta, false);
    }


	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2SUDDEN_LOW) {
        }
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        
        // if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOMCENTRAL || type == PyramidMap.ROOM2SUDDEN_LOW) {
        if (type == PyramidMap.ROOM2LOW) {
        }
        if (type == PyramidMap.ROOM2SUDDEN_LOW) {
        }
        if (type == PyramidMap.ROOM_VIRTUAL) {
            fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, roomHeight - 1, roomDepth - 1);
        }
        if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM_VIRTUAL) {
            fillWithWalls(world, sbb, 0, 0, 0, roomWidth, 0, roomDepth);
            fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, 0, roomDepth - 1);
        }
        if (type == PyramidMap.ROOM_VIRTUAL) {
            // if (rand.nextFloat() > 0.33) {
                fillWithAir(world, sbb, roomWidth, 1, 1, roomWidth + PyramidMain.oddBias, roomHeight - 1, roomDepth - 1);
            //}
        }
        if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2SUDDEN_LOW) {
            // fillWithMetadataBlocks(world, sbb, 1, roomHeight, 1, roomWidth - 1, roomHeight, roomDepth - 1, Blocks.air, 0, Blocks.air, 0, false);

        }
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
