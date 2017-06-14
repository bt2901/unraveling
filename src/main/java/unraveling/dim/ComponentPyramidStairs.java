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


public class ComponentPyramidStairs extends StructureComponent {
    int clock;
    int roomHeight;
    int roomWidth;
    int roomDepth;

    Block brick = PyramidMain.wallBlockID;
    // int meta = PyramidMain.wallBlockMeta;
    int meta = 1;
    
	public ComponentPyramidStairs() {
		super();
	}

	public ComponentPyramidStairs(Random rand, int x, int y, int z, int mode, int clock) {
		super(clock);
        if (mode == 1) {
            clock = 1-clock;
            mode = 3;
        } else if (mode == 0) {
            clock = 1-clock;
            mode = 1;
        }
        this.clock = clock;
        this.coordBaseMode = mode;
        
        roomWidth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        roomDepth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        roomHeight = PyramidMain.height;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + roomWidth, y + roomHeight, z + roomDepth);
 	}
    
    public void makeStairsXPos(World world, StructureBoundingBox sbb, int minZ, int maxZ, int startX, int startY, int howLong) {
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
    
    public void makeStairsZPos(World world, StructureBoundingBox sbb, int minX, int maxX, int startZ, int startY, int howLong) {
        int z = startZ;
        int y = startY;
        for (int x = minX; x <= maxX; ++x) {
            for (int i = 0; i <= howLong; ++i) {
                z = startZ + i;
                y = (i == 0)? startY : startY + i - 1;
                placeBlockAtCurrentPosition(world, brick, meta, x, y, z, sbb);
            }
        }
    }

	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		;
	}
    
    public void makeStairs(World world, StructureBoundingBox sbb) {
        int minVal = 1;
        int startY = 0;
        int howLong = 3;
        if (clock == 0) {
            makeStairsXPos(world, sbb, minVal, minVal + 1, roomWidth/2 + 1, startY, howLong);
        } else {
            makeStairsZPos(world, sbb, minVal, minVal + 1, roomDepth/2 + 1, startY, howLong);
        }
        startY = howLong - 1;
        int startPos = 2;
        if (clock == 0) {
            int maxVal = roomWidth - 1;
            makeStairsZPos(world, sbb, maxVal - 1, maxVal, startPos, startY, howLong);
        } else {
            int maxVal = roomDepth - 1;
            makeStairsXPos(world, sbb, maxVal - 1, maxVal, startPos, startY, howLong);
        }
        
    }

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        makeStairs(world, sbb);
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
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		this.roomHeight = par1NBTTagCompound.getInteger("roomHeight");
        this.roomWidth = par1NBTTagCompound.getInteger("roomWidth");
        this.roomDepth = par1NBTTagCompound.getInteger("roomDepth");
 	}
    
}
