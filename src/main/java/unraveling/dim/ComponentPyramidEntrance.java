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


public class ComponentPyramidEntrance extends ComponentPyramidRoom {
    int level;

	public ComponentPyramidEntrance() {
		super();
	}

	public ComponentPyramidEntrance(Random rand, int x, int y, int z) {
		super(rand, x, y, z, PyramidMap.ENTRANCE);
        this.level = 0;
        this.coordBaseMode = 0;
	}
    
    public void createDoorway(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block what, int meta) {
        fillWithMetadataBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, what, meta, what, meta, false);
        int cX = (minX + maxX)/2;
        int cZ = (minZ + maxZ)/2;
        placeBlockAtCurrentPosition(world, Blocks.air, 0, cX, minY, cZ, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, cX, minY + 1, cZ, sbb);
    }
    public void createTripwire(World world, StructureBoundingBox sbb, int x, int y, int z) {
        placeBlockAtCurrentPosition(world, Blocks.tripwire, 0, 0, y+1, z+1, sbb);
        placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, 0, 0, y+1, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.tripwire, 0, x+1, y+1, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, 0, x, y+1, 0, sbb);
    }
    public void createFloorTrap(World world, StructureBoundingBox sbb, int x, int y, int z) {
        fillWithAir(world, sbb, x, y+2, z, x, y-2, z);

        fillWithMetadataBlocks(world, sbb, 1, y-2, z, x, y-2, z, Blocks.redstone_wire, 0, Blocks.redstone_wire, 0, false);
        fillWithMetadataBlocks(world, sbb, x, y-2, 1, x, y-2, z, Blocks.redstone_wire, 0, Blocks.redstone_wire, 0, false);
        placeBlockAtCurrentPosition(world, Blocks.piston, 1, x, y-2, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, 0, y-1, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, x, y-1, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 0, 0, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 0, x, y, 0, sbb);
    } 

	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
	}
    
    public boolean replaceIfNotPartOfStructure(World world, StructureBoundingBox sbb, int x, int y, int z) {
        Block b = getBlockAtCurrentPosition(world, x, y, z, sbb);
        if (b == PyramidMain.wallBlockID || b == PyramidMain.headBlockID || b == PyramidMain.rootBlockID) {
            return false;
        }
        placeBlockAtCurrentPosition(world, Blocks.air, 0, x, y, z, sbb);
        return true;
    }

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int z0 = 3*roomDepth + 1;
        fillWithAir(world, sbb, 1, 1, z0, roomWidth - 1, roomHeight - 1, z0 + roomDepth - 2);
        int pace = PyramidMain.evenBias + PyramidMain.oddBias;
        int z = z0 - roomDepth;
        fillWithMetadataBlocks(world, sbb, pace + 1,     1, z, 2 * pace - 1, roomHeight - 1, z0,
            Blocks.air, 0, Blocks.air, 0, false);

        for (int z2 = z; z2 < z + roomDepth; ++z2) {
            for (int ylevel = 1; ylevel < PyramidMain.height; ++ylevel) {
                replaceIfNotPartOfStructure(world, sbb, pace, ylevel, z2);
                replaceIfNotPartOfStructure(world, sbb, pace - 1, ylevel, z2);
                replaceIfNotPartOfStructure(world, sbb, 2*pace, ylevel, z2);
                replaceIfNotPartOfStructure(world, sbb, 2*pace + 1, ylevel, z2);
            }
        }
        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, pace, 1, z + 5, sbb);
        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, 2*pace, 1, z + 5, sbb);
        fillWithMetadataBlocks(world, sbb, pace - 1, 0, z + 5, 2 * pace + 1, 0, z+4,
            ConfigBlocks.blockCosmeticSolid, 3, ConfigBlocks.blockCosmeticSolid, 3, false);
            
        for (int ylevel = 1; ylevel <= PyramidMain.height; ++ylevel) {
            z -= 4;
            fillWithWalls(world, sbb, pace - 1, ylevel, z, 2 * pace + 1, roomHeight, z + 3);
            fillWithMetadataBlocks(world, sbb, pace - 1, ylevel, z + 2, 2 * pace + 1, ylevel + 1, z + 3, 
                ConfigBlocks.blockSlabStone, 1, ConfigBlocks.blockSlabStone, 1, false);
            fillWithAir(world, sbb, pace - 1, ylevel + 1, z, 2 * pace + 1, roomHeight, z + 4);
        }
        fillWithAir(world, sbb, pace - 1, PyramidMain.height + 1, z, 2 * pace + 1, PyramidMain.height + 2, z + 8);
        // createFloorTrap(world, sbb, pace, 0, z0 + pace - 1);
        // createTripwire(world, sbb, pace, 0, z0 + pace - 0);
        // createFloorTrap(world, sbb, 2*pace-1, 0, z0 + pace - 1);
        // createDoorway(world, sbb);
		return true;
	}
	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		super.func_143012_a(par1NBTTagCompound);
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
 	}
    
}
