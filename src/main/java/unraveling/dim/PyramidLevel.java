package unraveling.dim;

// save to nbt: move to storage
// nbt: size, room coords
// storage now is "level storage"
// move to storage: addRoomsToMaze

import java.util.Random;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import unraveling.block.TFBlocks;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.common.config.ConfigBlocks;


/**
 * This is a maze of cells and walls.
 * 
 * The cells are at odd numbered x and y values, and the walls are at even numbered ones.  This does make the storage slightly inefficient, but oh wells.
 * 
 * @author Ben
 *
 */
public class PyramidLevel extends StructureComponent {
    

	protected int rawWidth;
	protected int rawDepth;
    public PyramidMap pstorage;
	private int level;
    
    public    int cellsWidth;
    public    int cellsDepth;
	
	public Random rand;
    
    public PyramidLevel() {
        super();
    }
	public PyramidLevel(Random rand, int x, int y, int z, int myLevel, PyramidMap myMaze) {
		super(0);
        int entranceX = 11;
        int entranceZ = 11;
        
        cellsWidth = myMaze.cellsWidth;
        cellsDepth = myMaze.cellsDepth;
        
        this.rawWidth = cellsWidth * 2 + 1;
		this.rawDepth = cellsDepth * 2 + 1;
        // default values
        pstorage = myMaze;
		
		
        this.rand = rand;

        this.coordBaseMode = 0;
		this.level = myLevel;
		this.boundingBox = new StructureBoundingBox(x-getRadius(), y, z-getRadius(), x + getRadius(), y + PyramidMain.height, z + getRadius());

	}
    	
    protected int getRaw(int rawx, int rawz) {
        return pstorage.getRaw(rawx, rawz);
	}

    /**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		super.buildComponent(structurecomponent, list, random);
			
		// add rooms where we have our coordinates
		for (int i = 0; i < pstorage.rcoords.length / 2; i++)
		{
			int dx = pstorage.rcoords[i * 2];
			int dz = pstorage.rcoords[i * 2 + 1];
	
			// add the room as a component
			// ComponentTFMazeRoom room = makeRoom(random, i, dx, dz);
			// list.add(room);
			// room.buildComponent(this, list, random);
		}

		// find dead ends and corridors and make components for them
		// decorateDeadEndsCorridors(random, list);
	}
	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
		// clear the area
		fillWithAir(world, sbb, 1, 2, 1, getDiameter(), PyramidMain.height, getDiameter());
        
        // fillWithBlocks(world, sbb, 0, 0, 0, getDiameter(), 0, getDiameter(), TFBlocks.mazestone, Blocks.stone, false);
        // fillWithBlocks(world, sbb, 0, 5, 0, getDiameter(), 5, getDiameter(), TFBlocks.mazestone, Blocks.stone, true);
		fillWithMetadataBlocks(world, sbb, 
            1, PyramidMain.height + 1, 1, 
            getDiameter(), PyramidMain.height + 1, getDiameter(), 
            PyramidMain.headBlockID, PyramidMain.headBlockMeta, PyramidMain.headBlockID, PyramidMain.headBlockMeta, false);
		//fillWithMetadataBlocks(world, sbb, 1, 1, 1, getDiameter() + 1, 1, getDiameter() + 1, 
        //    PyramidMain.rootBlockID, PyramidMain.rootBlockMeta, PyramidMain.rootBlockID, PyramidMain.rootBlockMeta, false);
		
		copyToStructure(world, 1, 2, 1, this, sbb);

		return true;

	}


    /**
	 * Copy the maze into a StructureComponent
	 */
	public void copyToStructure(World world, int dx, int dy, int dz, StructureComponent component, StructureBoundingBox sbb) {
        System.out.println("level: " + level + " w " + cellsWidth);
        System.out.println(Arrays.toString(pstorage.rcoords));
        if (level == 2) {
            System.out.println(Arrays.toString(pstorage.storage));
        }
		for(int x = 0; x < rawWidth; x++) {
			for(int z = 0; z < rawDepth; z++) {
                int mdx = dx + (x / 2 * (PyramidMain.evenBias + PyramidMain.oddBias));
                int mdz = dz + (z / 2 * (PyramidMain.evenBias + PyramidMain.oddBias));
                if (PyramidMain.evenBias > 1) {
                    mdx--;
                    mdz--;
                }
                // only draw walls.  if the data is 0 the there's a wall
				if (getRaw(x, z) == 0) {
					if(isEven(x) && isEven(z)) {
							for(int even = 0; even < PyramidMain.evenBias; even++) {
								for(int even2 = 0; even2 < PyramidMain.evenBias; even2++) {
									for(int y = 0; y < PyramidMain.head; y++) {
										putHeadBlock(world, mdx + even, dy + PyramidMain.height + y, mdz + even2, component, sbb);
									}
									for(int y = 0; y < PyramidMain.height; y++) {
                                        putWallBlock(world, mdx + even, dy + y, mdz + even2, component, sbb);
									}
									for(int y = 1; y <= PyramidMain.roots; y++) {
										putRootBlock(world, mdx + even, dy - y, mdz + even2, component, sbb);
									}
								}
							}
					}
					if(isEven(x) && !isEven(z)) {
						// make a | vertical | wall!
						for(int even = 0; even < PyramidMain.evenBias; even++) {
							for(int odd = 1; odd <= PyramidMain.oddBias; odd++) {
								makeWallThing(world, dy, component, sbb, mdx, mdz, even, odd);
							}
						}
					}
					if(!isEven(x) && isEven(z)) {
						// make a - horizontal - wall!
						for(int even = 0; even < PyramidMain.evenBias; even++) {
							for(int odd = 1; odd <= PyramidMain.oddBias; odd++) {
								makeWallThing(world, dy, component, sbb, mdx, mdz, odd, even);
							}
						}
					}
				}
                
				if (getRaw(x, z) == PyramidMap.ROOM2HIGH || getRaw(x, z) == PyramidMap.ROOMCENTRAL) {
                    Block what = Blocks.air;
                    int meta = 0;
                    if (getRaw(x, z) == PyramidMap.ROOMCENTRAL) {
                        what = PyramidMain.rootBlockID;
                        meta = PyramidMain.rootBlockMeta;
                    }
                    if (level == 2) {
                        //System.out.println("Making fancy floor! " + x + " " + z);
                        //System.out.println("set: " + mdx + " " + mdz + " " + dy);
                        //System.out.println("set: " + (mdx + PyramidMain.oddBias + PyramidMain.evenBias)
                        //    + " " + (mdz + PyramidMain.oddBias + PyramidMain.evenBias) + " " + dy);
                    }
                    for(int i = 0; i < PyramidMain.oddBias + PyramidMain.evenBias; ++i) {
                        for(int j = 0; j < PyramidMain.oddBias + PyramidMain.evenBias; ++j) {
                            // for(int y = 0; y < PyramidMain.height + 1; y++) {
                            super.placeBlockAtCurrentPosition(world, what, meta, mdx + i, dy - 1, mdz + j, sbb);

                        }
                    }
                }
			}
		}
	}

	protected void makeWallThing(World world, int dy, StructureComponent component, StructureBoundingBox sbb, int mdx, int mdz, int even, int odd) {
		for(int y = 0; y < PyramidMain.head; y++) {
			putHeadBlock(world, mdx + even, dy + PyramidMain.height + y, mdz + odd, component, sbb);
		}
		for(int y = 0; y < PyramidMain.height - 3; y++) { // DEBUG
			putWallBlock(world, mdx + even, dy + y, mdz + odd, component, sbb);
		}
		for(int y = 1; y <= PyramidMain.roots; y++) {
			putRootBlock(world, mdx + even, dy - y, mdz + odd, component, sbb);
		}
	}
	
	/**
	 * Puts a wall block in the world, at the specified world coordinates.
	 */
	protected void putWallBlock(World world, int x, int y, int z) {
		world.setBlock(x, y, z, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, 2);
	}
	
	/**
	 * Puts a wall block in the structure, at the specified structure coordinates.
	 */
	protected void putWallBlock(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb) {
		if (PyramidMain.wallVarRarity > 0 && rand.nextFloat() < PyramidMain.wallVarRarity)
		{
			super.placeBlockAtCurrentPosition(world, PyramidMain.wallVar0ID, PyramidMain.wallVar0Meta, x, y, z, sbb);
		}
		else
		{
			super.placeBlockAtCurrentPosition(world, PyramidMain.wallBlockID, PyramidMain.wallBlockMeta, x, y, z, sbb);
		}
	}
	

    /**
	 * Puts a root block in the structure, at the specified structure coordinates.
	 */
	protected void putRootBlock(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb)
	{
		super.placeBlockAtCurrentPosition(world, PyramidMain.rootBlockID, PyramidMain.rootBlockMeta, x, y, z, sbb);
	}	
	protected void putHeadBlock(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb)
	{
		super.placeBlockAtCurrentPosition(world, PyramidMain.headBlockID, PyramidMain.headBlockMeta, x, y, z, sbb);
	}
    /**
	 * I'm not sure why I made a function of this simple thing.  Maybe I need like... a macro?
	 */
	public final boolean isEven(int n) {
		return n % 2 == 0; 
	}
	public int getMazeSize() {
		return this.cellsWidth;
	}
	
	public int getRadius() {
		return (int) (getMazeSize() * (PyramidMain.evenBias + PyramidMain.oddBias) * 0.5);
	}
	
	public int getDiameter() {
		return getMazeSize() * (PyramidMain.evenBias + PyramidMain.oddBias);
	}
    
    
	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
        pstorage.writeToNBT(par1NBTTagCompound);
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		// super.func_143011_b(par1NBTTagCompound);
        pstorage = PyramidMap.readFromNBT(par1NBTTagCompound);
 	}
	
}

