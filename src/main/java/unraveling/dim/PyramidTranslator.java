package unraveling.dim;

// save to nbt: move to storage
// nbt: size, room coords
// storage now is "level storage"
// move to storage: addRoomsToMaze

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import unraveling.block.TFBlocks;
import unraveling.dim.PyramidStorage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;



/**
 * This is a maze of cells and walls.
 * 
 * The cells are at odd numbered x and y values, and the walls are at even numbered ones.  This does make the storage slightly inefficient, but oh wells.
 * 
 * @author Ben
 *
 */
public class PyramidTranslator extends StructureComponent {
    
    private static final int FLOOR_LEVEL = 1;
	int rcoords[];
	private int level;

	public int width; // cells wide (x)
	public int depth; // cells deep (z)
	
	public int oddBias; // corridor thickness, default 3
	public int evenBias; // wall thickness here.  NYI 
	
	public int tall; // wall blocks tall
	public int head;// blocks placed above the maze
	public int roots;// blocks placed under the maze (used for hedge mazes)
	
	public int worldX; // set when we first copy the maze into the world
	public int worldY;
	public int worldZ;
    
	public Block wallBlockID;
	public int wallBlockMeta;
	
	public Block wallVar0ID;
	public int wallVar0Meta;
	public float wallVarRarity;

	public Block headBlockID;
	public int headBlockMeta;
	
	public Block rootBlockID;
	public int rootBlockMeta;
	
	public Block pillarBlockID;
	public int pillarBlockMeta;
	
	public Block doorBlockID;
	public int doorBlockMeta;
		
	protected int rawWidth;
	protected int rawDepth;
    public PyramidStorage pstorage;
	
	public static final int OUT_OF_BOUNDS = Integer.MIN_VALUE;
	public static final int OOB = OUT_OF_BOUNDS;
	public static final int ROOM = 5;
	public static final int DOOR = 6;
    public    int cellsWidth = 32;
    public    int cellsDepth = 32;
	
	public Random rand;
    
    public PyramidTranslator() {
        super();
    }
	public PyramidTranslator(World world, Random rand, int index, int x, int y, int z) {
		super(index);
        int level = 1;
        int entranceX = 11;
        int entranceZ = 11;
        
        // default values
		oddBias = 3;
		evenBias = 1;
		tall = 3;
		head = 0;
		roots = 0;
        pstorage = new PyramidStorage(cellsWidth, cellsDepth);
		wallBlockID = Blocks.stone;
		wallBlockMeta = 0;
		rootBlockID = Blocks.stone;
		rootBlockMeta = 0;

		headBlockID = Blocks.stone;
		headBlockMeta = 0;
		head = 1;
		roots = 1;
		
		
		this.width = cellsWidth;
		this.depth = cellsDepth;
		
		this.rawWidth = width * 2 + 1;
		this.rawDepth = depth * 2 + 1;
		
		rand = new Random();

        this.coordBaseMode = 0;
		this.level = level;
		this.boundingBox = new StructureBoundingBox(x-getRadius(), y, z-getRadius(), x + getRadius(), y + 5, z + getRadius());


		// set the seed to a fixed value based on this maze's x and z
		setFixedMazeSeed();

		// rooms
		int nrooms = 7;
		rcoords = new int[nrooms * 2];

		addRoomsToMaze(entranceX, entranceZ, nrooms);

		// make actual maze
		pstorage.generateRecursiveBacktracker(0, 0);
	}
    	
    protected int getRaw(int rawx, int rawz) {
        return pstorage.getRaw(rawx, rawz);
	}

	
	/**
	 * Copy the maze into a StructureTFComponent
	 */
	public void copyToStructure(World world, int dx, int dy, int dz, StructureComponent component, StructureBoundingBox sbb) {
		for(int x = 0; x < rawWidth; x++)
		{
			for(int z = 0; z < rawDepth; z++)
			{
				// only draw walls.  if the data is 0 the there's a wall
				if (getRaw(x, z) == 0)
				{
					int mdx = dx + (x / 2 * (evenBias + oddBias));
					int mdz = dz + (z / 2 * (evenBias + oddBias));
					
					if (evenBias > 1)
					{
						mdx--;
						mdz--;
					}
					
					if(isEven(x) && isEven(z))
					{
							for(int even = 0; even < evenBias; even++)
							{
								for(int even2 = 0; even2 < evenBias; even2++)
								{
									for(int y = 0; y < head; y++)
									{
										putHeadBlock(world, mdx + even, dy + tall + y, mdz + even2, component, sbb);
									}
									for(int y = 0; y < tall; y++)
									{
                                        putWallBlock(world, mdx + even, dy + y, mdz + even2, component, sbb);
									}
									for(int y = 1; y <= roots; y++)
									{
										putRootBlock(world, mdx + even, dy - y, mdz + even2, component, sbb);
									}
								}
							}
					}
					if(isEven(x) && !isEven(z))
					{
						// make a | vertical | wall!
						for(int even = 0; even < evenBias; even++)
						{
							for(int odd = 1; odd <= oddBias; odd++)
							{
								makeWallThing(world, dy, component, sbb, mdx, mdz, even, odd);
							}
						}
					}
					if(!isEven(x) && isEven(z))
					{
						// make a - horizontal - wall!
						for(int even = 0; even < evenBias; even++)
						{
							for(int odd = 1; odd <= oddBias; odd++)
							{
								makeWallThing(world, dy, component, sbb, mdx, mdz, odd, even);
							}
						}
					}
				}
			}
		}
	}

	protected void makeWallThing(World world, int dy, StructureComponent component, StructureBoundingBox sbb, int mdx, int mdz, int even, int odd) {
		for(int y = 0; y < head; y++)
		{
			putHeadBlock(world, mdx + even, dy + tall + y, mdz + odd, component, sbb);
		}
		for(int y = 0; y < tall; y++)
		{
			putWallBlock(world, mdx + even, dy + y, mdz + odd, component, sbb);
		}
		for(int y = 1; y <= roots; y++)
		{
			putRootBlock(world, mdx + even, dy - y, mdz + odd, component, sbb);
		}
	}
	
	/**
	 * Puts a wall block in the world, at the specified world coordinates.
	 */
	protected void putWallBlock(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, wallBlockID, wallBlockMeta, 2);
	}
	
	/**
	 * Puts a wall block in the structure, at the specified structure coordinates.
	 */
	protected void putWallBlock(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb)
	{
		if (wallVarRarity > 0 && rand.nextFloat() < this.wallVarRarity)
		{
			super.placeBlockAtCurrentPosition(world, wallVar0ID, wallVar0Meta, x, y, z, sbb);
		}
		else
		{
			super.placeBlockAtCurrentPosition(world, wallBlockID, wallBlockMeta, x, y, z, sbb);
		}
	}
	
	
	/**
	 * Carves a block into the world.
	 * TODO: check what's there?  maybe only certain blocks?
	 */
	protected void carveBlock(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, Blocks.air, 0, 2);
	}
	
	protected void putHeadBlock(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, headBlockID, headBlockMeta, 2);
	}
	
		
	/**
	 * If the worldX is set properly, this returns where in that world the maze coordinate x lies
	 * 
	 * @param x
	 * @return
	 */
	int getWorldX(int x)
	{
		return worldX + (x * (evenBias + oddBias)) + 1;
	}
	
	/**
	 * If the worldZ is set properly, this returns where in that world the maze coordinate z lies
	 * 
	 * @param z
	 * @return
	 */
	int getWorldZ(int z)
	{
		return worldZ + (z * (evenBias + oddBias)) + 1;
	}
    /**
	 * I'm not sure why I made a function of this simple thing.  Maybe I need like... a macro?
	 */
	public final boolean isEven(int n) {
		return n % 2 == 0; 
	}

	/**
	 * Puts a root block in the world, at the specified world coordinates.
	 */
	protected void putRootBlock(World world, int x, int y, int z)
	{
		world.setBlock(x, y, z, rootBlockID, rootBlockMeta, 2);
	}


    /**
	 * Puts a root block in the structure, at the specified structure coordinates.
	 */
	protected void putRootBlock(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb)
	{
		super.placeBlockAtCurrentPosition(world, rootBlockID, rootBlockMeta, x, y, z, sbb);
	}	
	protected void putHeadBlock(World world, int x, int y, int z, StructureComponent component, StructureBoundingBox sbb)
	{
		super.placeBlockAtCurrentPosition(world, headBlockID, headBlockMeta, x, y, z, sbb);
	}


	private void addRoomsToMaze(int entranceX, int entranceZ, int nrooms) {
		// make one entrance room always
		rcoords[0] = entranceX;
		rcoords[1] = entranceZ;
		pstorage.carveRoom1(entranceX, entranceZ);
		
		// add room coordinates, trying to keep them separate from existing rooms
		for (int i = 1; i < nrooms; i++)
		{
			int rx, rz;
			do {
				rx = pstorage.rand.nextInt(getMazeSize() - 2) + 1;
				rz = pstorage.rand.nextInt(getMazeSize() - 2) + 1;
			} while(isNearRoom(rx, rz, rcoords, i == 1 ? 7 : 4));

			pstorage.carveRoom1(rx, rz);
			
			//System.out.println("Initially carving room " + rx + ", " + rz);

			rcoords[i * 2] = rx;
			rcoords[i * 2 + 1] = rz;
		}
	}

	private void setFixedMazeSeed() {
		pstorage.setSeed(this.boundingBox.minX * 90342903 + this.boundingBox.minY * 90342903 ^ this.boundingBox.minZ);
	}
	

	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		// super.func_143012_a(par1NBTTagCompound); spawn list index and decorations
		
        par1NBTTagCompound.setInteger("mazeLevel", this.level);
        par1NBTTagCompound.setIntArray("roomCoords", this.rcoords);
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		// super.func_143011_b(par1NBTTagCompound);
        this.level = par1NBTTagCompound.getInteger("mazeLevel");
        this.rcoords = par1NBTTagCompound.getIntArray("roomCoords");
 
        // recreate maze object
		pstorage =  new PyramidStorage(getMazeSize(), getMazeSize());
		setFixedMazeSeed();
		
		// blank out rcoords above 1 so that the room generation works properly
		//TODO: re-do this. :)
		for (int i = 2; i < rcoords.length; i++)
		{
			this.rcoords[i] = 0;
		}
		
		// recreate rooms
		this.addRoomsToMaze(this.rcoords[0], this.rcoords[1], (this.rcoords.length + 1) / 2);
		
		// regenerate maze
		pstorage.generateRecursiveBacktracker(0, 0);
	}
	
    //protected ComponentTFMazeRoom makeRoom(Random random, int i, int dx, int dz) {}
	//protected void decorateDeadEndsCorridors(Random random, List list) {}
	//protected ComponentTFMazeDeadEnd makeDeadEnd(Random random, int dx, int dz, int rotation) {}
	//protected ComponentTFMazeCorridor makeCorridor(Random random, int dx, int dz, int rotation) {}

	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		super.buildComponent(structurecomponent, list, random);
		
		// add a second story
		if (this.level == 1)
		{
            /*
			int centerX = boundingBox.minX + ((boundingBox.maxX - boundingBox.minX) / 2);
			int centerZ = boundingBox.minZ + ((boundingBox.maxZ - boundingBox.minZ) / 2);
			
			// PyramidTranslator maze = new PyramidTranslator(1, centerX, boundingBox.minY - 10, centerZ, rcoords[2], rcoords[3], 2);
            
			PyramidTranslator maze = new PyramidTranslator(world, random, 2, centerX, boundingBox.minY - 10, centerZ);
            // (1, , rcoords[2], rcoords[3], 2);
			list.add(maze);
			maze.buildComponent(this, list, random);
            */
		}
	
		
		// add rooms where we have our coordinates
		for (int i = 0; i < rcoords.length / 2; i++)
		{
			int dx = rcoords[i * 2];
			int dz = rcoords[i * 2 + 1];
	
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
		// level 2 maze surrounded by bedrock
		if (level == 2)
		{
			fillWithBlocks(world, sbb, 0, -1, 0, getDiameter() + 2, 6, getDiameter() + 2, Blocks.bedrock, Blocks.air, false);
		}
		
		// clear the area
		fillWithAir(world, sbb, 1, 1, 1, getDiameter(), 4, getDiameter());
//		fillWithBlocks(world, sbb, 0, 0, 0, getDiameter(), 0, getDiameter(), TFBlocks.mazestone, Blocks.stone, false);
//		fillWithBlocks(world, sbb, 0, 5, 0, getDiameter(), 5, getDiameter(), TFBlocks.mazestone, Blocks.stone, true);
		fillWithMetadataBlocks(world, sbb, 1, 5, 1, getDiameter(), 5, getDiameter(), Blocks.stone, 0, Blocks.stone, 0, this.level == 1);
		fillWithMetadataBlocks(world, sbb, 1, 0, 1, getDiameter(), 0, getDiameter(), Blocks.stone, 0, Blocks.stone, 0, false);
		
		
		copyToStructure(world, 1, 2, 1, this, sbb);

		return true;

	}

	public int getMazeSize() {
		return this.cellsWidth;
	}
	
	public int getRadius() {
		return (int) (getMazeSize() * 2.5);
	}
	
	public int getDiameter() {
		return getMazeSize() * 5;
	}
	
	/**
	 * @return true if the specified dx and dz are within 3 of a room specified in rcoords
	 */
	protected boolean isNearRoom(int dx, int dz, int[] rcoords, int range) {
		// if proposed coordinates are covering the origin, return true to stop the room from causing the maze to fail
		if (dx == 1 && dz == 1) {
			return true;
		}
		
		for (int i = 0; i < rcoords.length / 2; i++)
		{
			int rx = rcoords[i * 2];
			int rz = rcoords[i * 2 + 1];
			
			if (rx == 0 && rz == 0) {
				continue;
			}
			
			if (Math.abs(dx - rx) < range && Math.abs(dz - rz) < range) {
				return true;
			}
		}
		return false;
	}

}







/*
	 //* Copies the maze into the world by placing walls.
	public void copyToWorld(World world, int dx, int dy, int dz)
	{
		worldX = dx;
		worldY = dy;
		worldZ = dz;
		
		for(int x = 0; x < rawWidth; x++)
		{
			for(int z = 0; z < rawDepth; z++)
			{
				if (pstorage.getRaw(x, z) == 0)
				{
					int mdx = dx + (x / 2 * (evenBias + oddBias));
					int mdz = dz + (z / 2 * (evenBias + oddBias));
					
					if(isEven(x) && isEven(z))
					{
                        // make a block!
                        for(int y = 0; y < head; y++) {
                            putHeadBlock(world, mdx, dy + tall + y, mdz);
                        }
                        for(int y = 0; y < tall; y++) {
                            putWallBlock(world, mdx, dy + y, mdz);
                        }
                        for(int y = 1; y <= roots; y++) {
                            putRootBlock(world, mdx, dy - y, mdz);
                        }
					}
					if(isEven(x) && !isEven(z))
					{
						// make a | vertical | wall!
						for(int even = 0; even < evenBias; even++)
						{
							for(int odd = 1; odd <= oddBias; odd++)
							{
								for(int y = 0; y < head; y++)
								{
									putHeadBlock(world, mdx + even, dy + tall + y, mdz + odd);
								}
								for(int y = 0; y < tall; y++)
								{
									putWallBlock(world, mdx + even, dy + y, mdz + odd);
								}
								for(int y = 1; y <= roots; y++)
								{
									putRootBlock(world, mdx + even, dy - y, mdz + odd);
								}
							}
						}
					}
					if(!isEven(x) && isEven(z))
					{
						// make a - horizontal - wall!
						for(int even = 0; even < evenBias; even++)
						{
							for(int odd = 1; odd <= oddBias; odd++)
							{
								for(int y = 0; y < head; y++)
								{
									putHeadBlock(world, mdx + odd, dy + tall + y, mdz + even);
								}
								for(int y = 0; y < tall; y++)
								{
									putWallBlock(world, mdx + odd, dy + y, mdz + even);
								}
								for(int y = 1; y <= roots; y++)
								{
									putRootBlock(world, mdx + odd, dy - y, mdz + even);
								}
							}
						}
					}
				}
			}
		}
		
		placeTorches(world);
	}
	
	 //* Copies the maze into the world by carving out empty spaces.
	public void carveToWorld(World world, int dx, int dy, int dz)
	{
		worldX = dx;
		worldY = dy;
		worldZ = dz;
		
		for(int x = 0; x < rawWidth; x++)
		{
			for(int z = 0; z < rawDepth; z++)
			{
				if (getRaw(x, z) != 0)
				{
					int mdx = dx + (x / 2 * (evenBias + oddBias));
					int mdz = dz + (z / 2 * (evenBias + oddBias));

					if (isEven(x) && isEven(z))
					{
						// carve a one-block wide pillar
						for(int y = 0; y < tall; y++)
						{
							carveBlock(world, mdx, dy + y, mdz);
						}		
					}
					else if (isEven(x) && !isEven(z))
					{
						// carve a | vertical | wall
						for(int i = 1; i <= oddBias; i++)
						{
							for(int y = 0; y < tall; y++)
							{
								carveBlock(world, mdx, dy + y, mdz + i);
							}
						}
					}
					else if (!isEven(x) && isEven(z))
					{
						// carve a - horizontal - wall!
						for(int i = 1; i <= oddBias; i++)
						{
							for(int y = 0; y < tall; y++)
							{
								carveBlock(world, mdx + i, dy + y, mdz);
							}
						}
					}
					else if (!isEven(x) && !isEven(z)) // this should always be true at this point
					{
						// carve an open space
						for(int mx = 1; mx <= oddBias; mx++)
						{
							for(int mz = 1; mz <= oddBias; mz++)
							{
								for(int y = 0; y < tall; y++)
								{
									carveBlock(world, mdx + mx, dy + y, mdz + mz);
								}
							}
						}
					}
				}
			}
		}

		placeTorches(world);
	}
	
*/



