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


public class ComponentCoridorTrap extends StructureComponent {

	public ComponentCoridorTrap() {
		super();
	}

	public ComponentCoridorTrap(Random rand, int x, int y, int z, int mode) {
		super(mode);
        this.coordBaseMode = mode;
        int segWidth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        int segDepth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        int roomHeight = PyramidMain.height;

        this.boundingBox = new StructureBoundingBox(x, y, z, x + segWidth, y + roomHeight, z + segDepth);
	}
    public void fillWithOutsideWalls(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
       fillWithMetadataBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, PyramidMain.headBlockID, PyramidMain.headBlockMeta, PyramidMain.headBlockID, PyramidMain.headBlockMeta, false);
    }

    public void createTripwire(World world, StructureBoundingBox sbb, int x, int y, int z) {
    }
    public void createTrapLevel(World world, StructureBoundingBox sbb) {

        int y;
        int x;
        int z;
        y = 1;
        x = 1;
        fillWithOutsideWalls(world, sbb, 0, y, 1, 3, y, 3);
        placeBlockAtCurrentPosition(world, UBlocks.golemSpawner, 0, x, y, 1, sbb);
        placeBlockAtCurrentPosition(world, UBlocks.golemSpawner, 0, x, y, 3, sbb);

        
        y = 0;

        
        
        
        y = -1;
        x = 0;
        fillWithOutsideWalls(world, sbb, x, y, 1, x+3, y, 3);
        z = 1;
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 1, x, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, x+1, y-2, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, x+2, y-2, z, sbb);

        z = 3;
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 1, x, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, x+1, y-2, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, x+2, y-2, z, sbb);

        
        placeBlockAtCurrentPosition(world, Blocks.air, 0, x, y-2, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, 0, y-2, z, sbb);
        placeBlockAtCurrentPosition(world, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, x, y-1, z, sbb);

        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 0, 0, y+1, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 0, x, y+1, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, 0, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, x, y, 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, 0, y-1, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, x, y-1, 0, sbb);
    } 

	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
	}
    
	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int pace = PyramidMain.evenBias + PyramidMain.oddBias;
        createTrapLevel(world, sbb);
        createTripwire(world, sbb, pace, 0, pace );
		return true;
	}
	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) { }

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) { }
    
}
/*
Schematic

Level 1
oooRGRR
TTTRRRR
oooRGRR

Level 0
oooRRoW
oooPoBW
oooRRoW

Level -1
oooPWWR
oooRRRR
oooPWWR

vertical at z = 1 or 3:
oooRGRR
oooRRoW
oooPWWR

vertical at z = 2:
TTTRRRR
oooPoBW
oooRRRR

*/