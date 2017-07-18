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

    protected int getMetaAtCurrentPosition(World p_151548_1_, int p_151548_2_, int p_151548_3_, int p_151548_4_, StructureBoundingBox p_151548_5_) {
        int l = this.getXWithOffset(p_151548_2_, p_151548_4_);
        int i1 = this.getYWithOffset(p_151548_3_);
        int j1 = this.getZWithOffset(p_151548_2_, p_151548_4_);
        return p_151548_1_.getBlockMetadata(l, i1, j1);
    }
    
    @Override
    protected int getXWithOffset(int p_74865_1_, int p_74865_2_)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return this.boundingBox.minX + p_74865_1_;
            case 2:
                return this.boundingBox.minX + p_74865_1_;
            case 1:
                return this.boundingBox.maxX - p_74865_2_;
            case 3:
                return this.boundingBox.maxX - p_74865_1_;
            default:
                return p_74865_1_;
        }
    }

    @Override
    protected int getZWithOffset(int p_74873_1_, int p_74873_2_)
    {
        switch (this.coordBaseMode)
        {
            case 0:
                return this.boundingBox.minZ + p_74873_2_;
            case 1:
                return this.boundingBox.minZ + p_74873_1_;
            case 2:
                return this.boundingBox.maxZ - p_74873_2_;
            case 3:
                return this.boundingBox.maxZ - p_74873_2_;
            default:
                return p_74873_2_;
        }
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
    public void makeFancyEntrance(World world, StructureBoundingBox sbb) {
        int pace = PyramidMain.oddBias + PyramidMain.evenBias;
        int h = 2;

        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, 0, h, pace, sbb);
        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, 0, h, 2*pace, sbb);

        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, pace, h, 0, sbb);
        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, 2*pace, h, 0, sbb);

        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, roomWidth, h, pace, sbb);
        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, roomWidth, h, 2*pace, sbb);

        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, pace, h, roomDepth, sbb);
        placeBlockAtCurrentPosition(world, ConfigBlocks.blockEldritch, 4, 2*pace, h, roomDepth, sbb);
    }


	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2SUDDEN_LOW) {
        }
        
        if (type == PyramidMap.ROOM_TRAP) {
            int randInt = random.nextInt(2);
            for (int i=0; i < 4; ++i) {
                ComponentPyramidTrap trapBuilder = new ComponentPyramidTrap(random, 
                    boundingBox.minX, boundingBox.minY, boundingBox.minZ, i, randInt);
                list.add(trapBuilder);
                trapBuilder.buildComponent(this, list, random);
            }
        }
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        
        // if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOMCENTRAL || type == PyramidMap.ROOM2SUDDEN_LOW) {
        if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM) {
            makeFancyEntrance(world, sbb);
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
