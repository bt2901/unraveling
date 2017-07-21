package unraveling.dim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import net.minecraft.nbt.NBTTagCompound;
import unraveling.UnravelingConfig;



/**
 * Based on a Twilight Forest maze by Benimatic.
 */
public class PyramidMap {
	
	public int cellsWidth; // cells wide (x)
	public int cellsDepth; // cells deep (z)
		
	public int worldX; // set when we first copy the maze into the world
	public int worldY;
	public int worldZ;
	
	protected int rawWidth;
	protected int rawDepth;
	protected int[] storage;
    private long mySeed;

	public static final int ROOM_INFO_LEN = 3;
	
	public static final int OUT_OF_BOUNDS = Integer.MIN_VALUE;
	public static final int OOB = OUT_OF_BOUNDS;
	public static final int ROOM = 5;
	public static final int ROOMCENTRAL = 6;
	public static final int ROOM2LOW = 7;
	public static final int ROOM2HIGH = 8;
	public static final int ROOM2SUDDEN_LOW = 9;
	public static final int ROOM_VIRTUAL = 10;

	public static final int ENTRANCE = 99;
	public static final int ROOM_VPR = 100;
	public static final int ROOM_GARDEN = 101;
	public static final int ROOM_TRAP = 102;

	public static final int CORIDOR_TRAP_WEST = 200;
	public static final int CORIDOR_TRAP_SOUTH = 201;
	public static final int CORIDOR_TRAP_EAST = 202;
	public static final int CORIDOR_TRAP_NORTH = 203;
	
	public Random rand;
    public int[] rcoords = new int[0];
	
	public PyramidMap(int cellsWidth, int cellsDepth) {
		
		this.cellsWidth = cellsWidth;
		this.cellsDepth = cellsDepth;
		
		this.rawWidth = cellsWidth * 2 + 1;
		this.rawDepth = cellsDepth * 2 + 1;
		storage = new int [rawWidth * rawDepth];
		
		rand = new Random();
	}
	
	/**
	 * Gets the value from a cell in the maze
	 */
	public int getCell(int x, int z) {
		return getRaw(x * 2  + 1, z * 2 + 1);
	}

	/**
	 * Puts a value into a cell in the maze
	 */
	public void putCell(int x, int z, int value) {
		putRaw(x * 2 + 1, z * 2 + 1, value);
	}
	
	/**
	 * Returns true if the specified cell equals the specified value
	 */
	public boolean cellIsFree(int x, int z) {
		return getCell(x, z) == 0 || (getCell(x, z) > 200 && getCell(x, z) < 300) ;
	}
	
	/**
	 * Gets the wall value, or OUT_OF_BOUNDS if the area is out of bounds or the coordinates are not orthogonally adjacent.
	 */
	public int getWall(int sx, int sz, int dx, int dz)
	{
		if (dx == sx + 1 && dz == sz)
		{
			return getRaw(sx * 2 + 2, sz * 2 + 1);
		}
		if (dx == sx - 1 && dz == sz)
		{
			return getRaw(sx * 2 + 0, sz * 2 + 1);
		}
		if (dx == sx && dz == sz + 1)
		{
			return getRaw(sx * 2 + 1, sz * 2 + 2);
		}
		if (dx == sx && dz == sz - 1)
		{
			return getRaw(sx * 2 + 1, sz * 2 + 0);
		}
		
		System.out.println("Wall check out of bounds; s = " + sx + ", " + sz + "; d = " + dx + ", " + dz);
		
		return OUT_OF_BOUNDS;
	}
	
	public void putWall(int sx, int sz, int dx, int dz, int value)
	{
		if (dx == sx + 1 && dz == sz)
		{
			putRaw(sx * 2 + 2, sz * 2 + 1, value);
		}
		if (dx == sx - 1 && dz == sz)
		{
			putRaw(sx * 2  + 0, sz * 2 + 1, value);
		}
		if (dx == sx && dz == sz + 1)
		{
			putRaw(sx * 2 + 1, sz * 2 + 2, value);
		}
		if (dx == sx && dz == sz - 1)
		{
			putRaw(sx * 2 + 1, sz * 2 + 0, value);
		}
	}
	
	/**
	 * Returns true if there is a wall there
	 */
	public boolean isWall(int sx, int sz, int dx, int dz)
	{
		return getWall(sx, sz, dx, dz) == 0;
	}

	/**
	 * Puts a value into the raw storage.
	 */
	public void putRaw(int rawx, int rawz, int value)
	{
		if (rawx >= 0 && rawx < rawWidth && rawz >= 0 && rawz < rawDepth)
		{
			storage[rawz * rawWidth + rawx] = value;
		}
	}
	
	/**
	 * Gets a value from raw storage
	 */
	protected int getRaw(int rawx, int rawz)
	{
		if (rawx < 0 || rawx >= rawWidth || rawz < 0 || rawz >= rawDepth)
		{
			return OUT_OF_BOUNDS;
		} else {
			return storage[rawz * rawWidth + rawx];
		}
	}
	
	/**
	 * Sets the random seed to a specific value
	 */
	public void setSeed(long newSeed) {
        mySeed = newSeed;
		rand.setSeed(newSeed);
	}
	/**
	 * Carves a room into the maze.  The coordinates given are cell coordinates.
	 */
	public void carveRoom0(int cx, int cz)
	{

		putCell(cx, cz, 5);
		
		putCell(cx + 1, cz, 5);
		putWall(cx, cz, cx + 1, cz, 5);
		putCell(cx - 1, cz, 5);
		putWall(cx, cz, cx - 1, cz, 5);
		putCell(cx, cz + 1, 5);
		putWall(cx, cz, cx, cz + 1, 5);
		putCell(cx, cz - 1, 5);
		putWall(cx, cz, cx, cz - 1, 5);
	}
	
	/**
	 * This room is a 3x3 cell room with exits in every direction.
	 */
	public void carveCustomRoom(int cx, int cz, int type)
	{
        if (type == ROOM_VIRTUAL) {
            return;
        }
		int rx = cx * 2  + 1;
		int rz = cz * 2  + 1;
		
		// remove walls and cells
		for(int i = -2; i <= 2; i++)
		{
			for(int j = -2; j <= 2; j++)
			{
				putRaw(rx + i, rz + j, type);
			}
		}
		
		// mark the exit areas as unmazed
		putCell(rx, rz + 1, 0);
		putCell(rx, rz - 1, 0);
		putCell(rx + 1, rz, 0);
		putCell(rx - 1, rz, 0);
		
		// make 4 exits (if not at the edge of the maze)
		if (getRaw(rx, rz + 4) != OUT_OF_BOUNDS) {
			putRaw(rx, rz + 3, ROOM);
		}
		if (getRaw(rx, rz - 4) != OUT_OF_BOUNDS) {
			putRaw(rx, rz - 3, ROOM);
		}
		if (getRaw(rx + 4, rz) != OUT_OF_BOUNDS) {
			putRaw(rx + 3, rz, ROOM);
		}
		if (getRaw(rx- 4, rz) != OUT_OF_BOUNDS) {
			putRaw(rx - 3, rz, ROOM);
		}
	}	
	
	
	/**
	 * Adds four exits into the maze.
	 */
	public void add4Exits()
	{
		int hx = rawWidth / 2 + 1;
		int hz = rawDepth / 2 + 1;
		
		putRaw(hx, 0, ROOM);
		putRaw(hx, rawDepth - 1, ROOM);
		putRaw(0, hz, ROOM);
		putRaw(rawWidth - 1, hz, ROOM);
	}
	
	/**
	 * Generates a maze using the recursive backtracking algorithm. 
	 * 
	 * @param sx The starting x coordinate
	 * @param sz The starting y coordinate
	 */
	public void generateRecursiveBacktracker(int sx, int sz) {
        boolean roomHere = !cellIsFree(sx, sz);
        int attempts = 0;
        if (cellsWidth < 2 || cellsDepth < 2) {
            return;
        }
        while(roomHere && attempts < 10) {
            sx = rand.nextInt(cellsWidth - 2) + 1;
            sz = rand.nextInt(cellsDepth - 2) + 1;
            attempts += 1;
            roomHere = !cellIsFree(sx, sz);
        }
        rbGen(sx, sz);
	}
    public void addTrappedCoridors(){

        // CORIDOR_TRAP_WEST = minX
        if (!UnravelingConfig.debug) {

            putCell(0, 7, CORIDOR_TRAP_WEST);
            putCell(0, 8, CORIDOR_TRAP_WEST);
            putCell(0, 9, CORIDOR_TRAP_WEST);
            putCell(0, 10, CORIDOR_TRAP_WEST);
        
            // CORIDOR_TRAP_SOUTH = minZ
            putCell(7, 0, CORIDOR_TRAP_SOUTH);
            putCell(8, 0, CORIDOR_TRAP_SOUTH);
            putCell(9, 0, CORIDOR_TRAP_SOUTH);
            putCell(10, 0, CORIDOR_TRAP_SOUTH);
        }

        putCell(cellsWidth-1, 7, CORIDOR_TRAP_EAST);
        putCell(cellsWidth-1, 8, CORIDOR_TRAP_EAST);
        putCell(cellsWidth-1, 9, CORIDOR_TRAP_EAST);
        putCell(cellsWidth-1, 10, CORIDOR_TRAP_EAST);

        // CORIDOR_TRAP_NORTH = maxZ?
        putCell(7, cellsDepth-1, CORIDOR_TRAP_NORTH);
        putCell(8, cellsDepth-1, CORIDOR_TRAP_NORTH);
        putCell(9, cellsDepth-1, CORIDOR_TRAP_NORTH);
        putCell(10, cellsDepth-1, CORIDOR_TRAP_NORTH);
        
    }
	
	/**
	 * Mark the cell as visited.  If we have any unvisited neighbors, pick one randomly, carve the wall between them, then call this function on that neighbor.
	 * 
	 * @param sx
	 * @param sz
	 */
	public void rbGen(int sx, int sz) {
		// mark cell as visited
		putCell(sx, sz, 1);
		
		// count the unvisted neighbors
		int unvisited = 0;
		if (cellIsFree(sx + 1, sz)) {
			unvisited++;
		}
		if (cellIsFree(sx - 1, sz)) {
			unvisited++;
		}
		if (cellIsFree(sx, sz + 1)) {
			unvisited++;
		}
		if (cellIsFree(sx, sz - 1)) {
			unvisited++;
		}
		
		// if there are no unvisited neighbors, return
		if (unvisited == 0)
		{
			return;
		}
		
		// otherwise, pick a random neighbor to visit
		int rn = rand.nextInt(unvisited);
		int dx, dz;
		dx = dz = 0;
		
		if (cellIsFree(sx + 1, sz)) {
			if (rn == 0) {
				dx = sx + 1;
				dz = sz;
			}
			rn--;
		}
		if (cellIsFree(sx - 1, sz)) {
			if (rn == 0) {
				dx = sx - 1;
				dz = sz;
			}
			rn--;
		}
		if (cellIsFree(sx, sz + 1)) {
			if (rn == 0) {
				dx = sx;
				dz = sz + 1;
			}
			rn--;
		}
		if (cellIsFree(sx, sz - 1)) {
			if (rn == 0) {
				dx = sx;
				dz = sz - 1;
			}
		}
        putWall(sx, sz, dx, dz, 2);
		
		// call function recursively at the destination
		rbGen(dx, dz);
		// the destination has run out of free spaces, let's try this square again, up to 2 more times
		rbGen(sx, sz);
		rbGen(sx, sz);
		return;
	}    
    	/**
	 * @return true if the specified dx and dz are within 3 of a room specified in rcoords
	 */

    protected boolean isNearRoom(int dx, int dz, int[] rcoords, int range) {
		// if proposed coordinates are covering the origin, return true to stop the room from causing the maze to fail
		if (dx == 1 && dz == 1) {
			return true;
		}
		
		for (int i = 0; i < rcoords.length / ROOM_INFO_LEN; i++)
		{
			int rx = rcoords[i * ROOM_INFO_LEN];
			int rz = rcoords[i * ROOM_INFO_LEN + 1];
			int rtype = rcoords[i * ROOM_INFO_LEN + 2];
			
			if (rx == 0 && rz == 0) {
				continue;
			}
			
			if (Math.abs(dx - rx) < range && Math.abs(dz - rz) < range) {
				return true;
			}
		}
		return false;
	}
    
	public void addRandomRoom(int minDistWalls, int minDistRooms, int type) {
        int l = rcoords.length;
        int[] rcoords2 = new int[l + ROOM_INFO_LEN];
        System.arraycopy(rcoords, 0, rcoords2, 0, l);
        rcoords = rcoords2;
        
        minDistWalls += 1;
		// add room coordinates, trying to keep them separate from existing rooms

        int attempts = 0;
        int rx, rz;
        boolean badPlace;
        do {
				rx = rand.nextInt(cellsWidth - 2*minDistWalls) + minDistWalls;
				rz = rand.nextInt(cellsDepth - 2*minDistWalls) + minDistWalls;
                attempts += 1;
                badPlace = isNearRoom(rx, rz, rcoords, minDistRooms);
        } while(badPlace && attempts < 10);

        if (!badPlace) {
                carveCustomRoom(rx, rz, type);
                rcoords[l] = rx;
                rcoords[l + 1] = rz;
                rcoords[l + 2] = type;
        } else {
                System.out.println("failed to make room of type " + type + " at size " + cellsWidth);
        }
	}

	public void addBonusRoom(int entranceX, int entranceZ, int type) {
        int l = rcoords.length;
        int[] rcoords2 = new int[l + ROOM_INFO_LEN];
        System.arraycopy(rcoords, 0, rcoords2, 0, l);

		rcoords2[l] = entranceX;
		rcoords2[l + 1] = entranceZ;
		rcoords2[l + 2] = type;
		carveCustomRoom(entranceX, entranceZ, type);
        rcoords = rcoords2;
	}
    public int matchingRoom(int room) {
        switch (room) {
            case ROOM2LOW: return ROOM2HIGH;
            case ROOM2SUDDEN_LOW: return ROOM_VIRTUAL;
            case ROOMCENTRAL: return ROOMCENTRAL;
            default: return 0;
        }
    }
    public int randomRoomShape() {
        float rf = rand.nextFloat();
        if (rf < 0.4F) {
            return ROOM2LOW;
        }
        if (rf < 0.6F) {
            return ROOM2SUDDEN_LOW;
        }
        // return ROOM;
        return ROOM2SUDDEN_LOW;
    }
    
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setInteger("cellsWidth", this.cellsWidth);
        par1NBTTagCompound.setInteger("cellsDepth", this.cellsDepth);
        par1NBTTagCompound.setLong("mySeed", this.mySeed);
        par1NBTTagCompound.setIntArray("roomCoords", this.rcoords);
    }

	public static PyramidMap readFromNBT(NBTTagCompound nbttagcompound) {
        int cellsWidth = nbttagcompound.getInteger("cellsWidth");
        int cellsDepth = nbttagcompound.getInteger("cellsDepth");
        
        PyramidMap pstorage = new PyramidMap(cellsWidth, cellsDepth);
        pstorage.rcoords = nbttagcompound.getIntArray("roomCoords");
        pstorage.setSeed(nbttagcompound.getInteger("mySeed"));
		pstorage.generateRecursiveBacktracker(0, 0);
        
        return pstorage;
    }
    

}
