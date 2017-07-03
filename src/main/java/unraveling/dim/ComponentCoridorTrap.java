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
import unraveling.UnravelingConfig;


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
        if (UnravelingConfig.debug) {
            System.out.println("Creating a room at " + x + " " + y + " " + z);
            System.out.println("Orientation is " + mode);
        }

	}
    public void fillWithOutsideWalls(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
       fillWithMetadataBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, PyramidMain.headBlockID, PyramidMain.headBlockMeta, PyramidMain.headBlockID, PyramidMain.headBlockMeta, false);
    }

    public void createTripwire(World world, StructureBoundingBox sbb, int z) {
        int m = getMetadataWithOffset(Blocks.tripwire_hook, 1) | 4;
        int m2 = getMetadataWithOffset(Blocks.tripwire_hook, 3) | 4;
        int y = 1;
        int minX = -3;
        int maxX = -1;
        placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, m2, minX, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.tripwire_hook, m, maxX, y, z, sbb);
        for (int x = minX+1; x < maxX; ++x) {
            placeBlockAtCurrentPosition(world, Blocks.tripwire, 0, x, y, z, sbb);
        }

    }
    public void createSliceWithControl(World world, StructureBoundingBox sbb, int z) {
        int y = 0;
        int minX = 0;
        int maxX = 3;
        // 0 1 : up/down
        // 4: almost, wrong dir
        // 5: good
        // 2, 3: perp dir
        int m = getMetadataWithOffset(Blocks.sticky_piston, 5);
        fillWithOutsideWalls(world, sbb, minX, -1, z, maxX, 1, z);
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, m, minX, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, minX+1, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_block, 0, minX+2, y, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, minX+3, y, z, sbb);
    }
    public void createSliceWithGolems(World world, StructureBoundingBox sbb, int z) {
        int minY = -1;
        int maxY = 1;
        int minX = 0;
        int maxX = 3;
        
        
        fillWithOutsideWalls(world, sbb, minX, minY, z, maxX, maxY, z);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, minX, maxY, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.air, 0, minX, maxY+1, z, sbb);
        placeBlockAtCurrentPosition(world, UBlocks.golemSpawner, 0, minX + 1, maxY, z, sbb);

        placeBlockAtCurrentPosition(world, Blocks.air, 0, minX + 2, maxY-1, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, minX + 3, maxY-1, z, sbb);
        
        placeBlockAtCurrentPosition(world, Blocks.sticky_piston, 1, minX, minY, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, minX + 1, minY, z, sbb);
        placeBlockAtCurrentPosition(world, Blocks.redstone_wire, 0, minX + 2, minY, z, sbb);

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
        createSliceWithGolems(world, sbb, 1);
        createSliceWithControl(world, sbb, 2);
        createSliceWithGolems(world, sbb, 3);
        createTripwire(world, sbb, 2);
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
