package unraveling.dim;

import java.util.Random;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import java.util.List;
import java.util.Random;
import net.minecraft.world.EnumSkyBlock;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.common.config.ConfigBlocks;
import unraveling.UnravelingConfig;


/**
 * Based on a Twilight Forest maze by Benimatic.
 */
public class PyramidLevel extends StructureComponent {
    

	protected int rawWidth;
	protected int rawDepth;
    public PyramidMap pstorage;
	private int level;
    
    public    int cellsWidth;
    public    int cellsDepth;
	
	public Random rand;
    
    // DEBUG
    public static int wallheight = (UnravelingConfig.debug)? PyramidMain.height - 3 : PyramidMain.height;    
    public PyramidLevel() {
        super();
    }
	public PyramidLevel(Random rand, int x, int y, int z, int myLevel, PyramidMap myMaze) {
		super(0);
        
        cellsWidth = myMaze.cellsWidth;
        cellsDepth = myMaze.cellsDepth;
        
        this.rawWidth = cellsWidth * 2 + 1;
		this.rawDepth = cellsDepth * 2 + 1;
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
			
		// add traps 
        for(int x = 0; x < rawWidth; x++) {
			for(int z = 0; z < rawDepth; z++) {
        		if (getRaw(x, z) >= 200 && getRaw(x, z) < 300) {
                    int worldX = getBoundingBox().minX + x/2 * (PyramidMain.evenBias + PyramidMain.oddBias) - 3;
                    int worldY = getBoundingBox().minY;
                    int worldZ = getBoundingBox().minZ + z/2 * (PyramidMain.evenBias + PyramidMain.oddBias) - 3;
                    int type = getRaw(x, z) % 200;

                    ComponentCoridorTrap trap = new ComponentCoridorTrap(random, worldX, worldY, worldZ, type);
                    list.add(trap);
                    trap.buildComponent(this, list, random);
                    // if (UnravelingConfig.debug) {
                        // System.out.println(list);
                    // }
                    
                }        
            }
        }


		// find dead ends and corridors and make components for them
		// decorateDeadEndsCorridors(random, list);
	}
    
    
    /**
	 * Nullify all the sky light at the specified positions, using world coordinates
	 */
	public void nullifySkyLight(World world, int sx, int sy, int sz, int dx, int dy, int dz) {
		for (int x = sx; x <= dx; x++) {
         	for (int z = sz; z <= dz; z++) {
             	for (int y = sy; y <= dy; y++) {
             		world.setLightValue(EnumSkyBlock.Sky, x, y, z, 0);
             	}
         	}
     	}
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        nullifySkyLight(world, boundingBox.minX - 1, boundingBox.minY - 1, boundingBox.minZ - 1, boundingBox.maxX + 1, boundingBox.maxY + 1, boundingBox.maxZ + 1);

		// clear the area
		fillWithAir(world, sbb, 1, 1, 1, getDiameter(), PyramidMain.height - 1, getDiameter());
        	
        
		fillWithMetadataBlocks(world, sbb, 
            1, PyramidMain.height, 1, getDiameter(), PyramidMain.height, getDiameter(), 
            PyramidMain.headBlockID, PyramidMain.headBlockMeta, PyramidMain.headBlockID, PyramidMain.headBlockMeta, false);

        copyToStructure(world, 1, 1, 1, this, sbb);
		fillWithMetadataBlocks(world, sbb, 
            1, 0, 1, getDiameter(), 0, getDiameter(), 
            PyramidMain.headBlockID, PyramidMain.headBlockMeta, PyramidMain.headBlockID, PyramidMain.headBlockMeta, false);
		return true;
	}
    public void describe() {
        System.out.println("level: " + level + " w " + cellsWidth);
        System.out.println(Arrays.toString(pstorage.rcoords));
        System.out.println(level + " " + pstorage.storage.length);
		for(int x = 0; x < rawWidth; x++) {
			for(int z = 0; z < rawDepth; z++) {
                int val = getRaw(x, z);
                String toPrint = " " + val;
                toPrint = (val == PyramidMap.ENTRANCE) ? " E" : toPrint;
                toPrint = (val == PyramidMap.ROOM_TRAP) ? " T" : toPrint;
                toPrint = (val >= 200) ? ("!" + (val % 200)) : toPrint;
                toPrint = (val == PyramidMap.ROOM_VPR) ? " V" : toPrint;
                toPrint = (val == PyramidMap.ROOM_GARDEN) ? " G" : toPrint;
                System.out.print("" + toPrint);
            }
            System.out.println(" ");

        }
    }

    /**
	 * Copy the maze into a StructureComponent
	 */
	public void copyToStructure(World world, int dx, int dy, int dz, StructureComponent component, StructureBoundingBox sbb) {
		for(int x = 0; x < rawWidth; x++) {
			for(int z = 0; z < rawDepth; z++) {
                int mdx = dx + (x / 2 * (PyramidMain.evenBias + PyramidMain.oddBias));
                int mdz = dz + (z / 2 * (PyramidMain.evenBias + PyramidMain.oddBias));
                if (PyramidMain.evenBias > 1) {
                    mdx--;
                    mdz--;
                }
                // draw walls.  if the data is 0 the there's a wall
				if (getRaw(x, z) == 0) {
					if(isEven(x) && isEven(z)) {
							for(int even = 0; even < PyramidMain.evenBias; even++) {
								for(int even2 = 0; even2 < PyramidMain.evenBias; even2++) {
									for(int y = 0; y < PyramidMain.head; y++) {
										putHeadBlock(world, mdx + even, dy + PyramidMain.height + y, mdz + even2, component, sbb);
									}
									for(int y = 0; y < wallheight; y++) { 
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
                

			}
		}
	}

	protected void makeWallThing(World world, int dy, StructureComponent component, StructureBoundingBox sbb, int mdx, int mdz, int even, int odd) {
		for(int y = 0; y < PyramidMain.head; y++) {
			putHeadBlock(world, mdx + even, dy + PyramidMain.height + y, mdz + odd, component, sbb);
		}
		for(int y = 0; y < wallheight; y++) { 
			putWallBlock2(world, mdx + even, dy + y, mdz + odd, component, sbb);
		}
		for(int y = 1; y <= PyramidMain.roots; y++) {
			putRootBlock(world, mdx + even, dy - y, mdz + odd, component, sbb);
		}
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
	protected void putWallBlock2(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb) {
        placeBlockAtCurrentPosition(world, PyramidMain.headBlockID, PyramidMain.headBlockMeta, x, y, z, sbb);
		
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
        pstorage = PyramidMap.readFromNBT(par1NBTTagCompound);
 	}
	
}

