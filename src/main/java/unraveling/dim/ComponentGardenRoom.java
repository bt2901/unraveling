package unraveling.dim;

import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSlab;
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


public class ComponentGardenRoom extends ComponentPyramidRoom {

    public ComponentGardenRoom() {
        super();
    }

    public ComponentGardenRoom(Random rand, int x, int y, int z) {
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
    /**
     * Makes a planter.  Depending on the situation, it can be filled with trees, flowers, or crops
     */
    protected void decoratePlanter(World world, Random rand, StructureBoundingBox sbb, int cx, int cz, boolean isBig) {
        
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, cx + 0, 1, cz + 1, sbb);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, cx + 0, 1, cz - 1, sbb);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, cx + 1, 1, cz + 0, sbb);
        placeBlockAtCurrentPosition(world, Blocks.stone_slab, 0, cx - 1, 1, cz + 0, sbb);

        if (isBig) {
            placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, cx - 1, 1, cz - 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, cx + 1, 1, cz - 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, cx + 1, 1, cz + 1, sbb);
            placeBlockAtCurrentPosition(world, Blocks.double_stone_slab, 0, cx - 1, 1, cz + 1, sbb);
        }

        // place a cute planted thing
        placeBlockAtCurrentPosition(world, Blocks.grass, 0, cx + 0, 1, cz + 0, sbb);

        Block planterBlock;
        int planterMeta;
        switch (rand.nextInt(6)) {
        case 0:
                planterBlock = Blocks.sapling;
                planterMeta = 0;
                break;
        case 1:
                planterBlock = Blocks.sapling;
                planterMeta = 1;
                break;
        case 2:
                planterBlock = Blocks.sapling;
                planterMeta = 2;
                break;
        case 3:
                planterBlock = Blocks.sapling;
                planterMeta = 3;
                break;
        case 4:
                planterBlock = Blocks.brown_mushroom;
                planterMeta = 0;
                break;
        case 5:
        default:
                planterBlock = Blocks.red_mushroom;
                planterMeta = 0;
                break;
        }
        placeBlockAtCurrentPosition(world, planterBlock, planterMeta, cx + 0, 2, cz + 0, sbb);
            
        // try to grow a tree
        if (planterBlock == Blocks.sapling) {
        int wx = getXWithOffset(cx, cz);
        int wy = getYWithOffset(2);
        int wz = getZWithOffset(cx, cz);
        ((BlockSapling)Blocks.sapling).func_149878_d(world, wx, wy, wz, world.rand);
        }
        // or a mushroom
        if (planterBlock == Blocks.brown_mushroom || planterBlock == Blocks.red_mushroom) {
        int wx = getXWithOffset(cx, cz);
        int wy = getYWithOffset(2);
        int wz = getZWithOffset(cx, cz);
        ((BlockMushroom)planterBlock).updateTick(world, wx, wy, wz, world.rand);
        }
        
        // otherwise, place the block into a flowerpot
        /*
        Block whatHappened = this.getBlockAtCurrentPosition(world, cx + 0, 2, cz + 0, sbb);
        if (whatHappened == planterBlock || whatHappened == Blocks.air)
        {
                int potMeta = 0;//BlockFlowerPot.getMetaForPlant(new ItemStack(planterBlock, 1, planterMeta));
                placeBlockAtCurrentPosition(world, Blocks.flower_pot, potMeta, cx + 0, 2, cz + 0, sbb);
        }*/
    }
    public void fillPillars(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block what, int meta) {

        fillWithMetadataBlocks(world, sbb, minX, minY, minZ, minX, maxY, minZ, what, meta, what, meta, false);
        fillWithMetadataBlocks(world, sbb, maxX, minY, minZ, maxX, maxY, minZ, what, meta, what, meta, false);
        fillWithMetadataBlocks(world, sbb, minX, minY, maxZ, minX, maxY, maxZ, what, meta, what, meta, false);
        fillWithMetadataBlocks(world, sbb, maxX, minY, maxZ, maxX, maxY, maxZ, what, meta, what, meta, false);
    }
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        makeFancyEntrance(world, sbb);
        Block brick = ConfigBlocks.blockCosmeticSolid;
        int brickmeta = 11;
        int pace = PyramidMain.oddBias + PyramidMain.evenBias;
        int platf = 2;
        fillWithMetadataBlocks(world, sbb, pace, 1, pace, 2*pace, platf-1, 2*pace, brick, brickmeta, brick, brickmeta, false);
        float r = rand.nextFloat();
        // float r = 0.33f;
        if (r > 0.75) { // tree
            int cx = roomWidth/2;
            int cz = roomDepth/2;
            decoratePlanter(world, rand, sbb, cx, cz, true); 
            return true;
        } 
        if (r > 0.50) {
            fillWithMetadataBlocks(world, sbb, pace+1, 1, pace+1, 2*pace-1, platf-1, 2*pace-1, Blocks.sand, 0, Blocks.sand, 0, false);
            fillWithMetadataBlocks(world, sbb, pace+1, platf, pace+1, 2*pace-1, platf, 2*pace-1, ConfigBlocks.blockCustomPlant, 3, ConfigBlocks.blockCustomPlant, 3, false);
            return true;
        } 
        if (r > 0.25) {
            fillWithMetadataBlocks(world, sbb, pace+1, 1, pace+1, 2*pace-1, platf-1, 2*pace-1, Blocks.soul_sand, 0, Blocks.soul_sand, 0, false);
            fillWithMetadataBlocks(world, sbb, pace+1, platf, pace+1, 2*pace-1, platf, 2*pace-1, Blocks.nether_wart, 0, Blocks.nether_wart, 0, false);
            return true;
        } 
        platf = 3;
        int glassmeta = 2;
        Block opaque = ConfigBlocks.blockCosmeticOpaque;
        fillWithMetadataBlocks(world, sbb, pace, 1, pace, 2*pace, platf-1, 2*pace, opaque, glassmeta, opaque, glassmeta, false);
        fillPillars(world, sbb, pace, 1, pace, 2*pace, platf-1, 2*pace, brick, PyramidMain.wallBlockMeta);
        fillWithMetadataBlocks(world, sbb, pace+1, 1, pace+1, 2*pace-1, platf-1, 2*pace-1, Blocks.water, 0, Blocks.water, 0, false);
        
        return true;
    }
}
