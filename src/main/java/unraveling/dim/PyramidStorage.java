package unraveling.dim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;



/**
 * This is a maze of cells and walls.
 * 
 * The cells are at odd numbered x and y values, and the walls are at even numbered ones.  This does make the storage slightly inefficient, but oh wells.
 * 
 * @author Ben
 *
 */
public class PyramidStorage {
	
	public int width; // cells wide (x)
	public int depth; // cells deep (z)
		
	public int worldX; // set when we first copy the maze into the world
	public int worldY;
	public int worldZ;
	
	protected int rawWidth;
	protected int rawDepth;
	protected int[] storage;
	
	public static final int OUT_OF_BOUNDS = Integer.MIN_VALUE;
	public static final int OOB = OUT_OF_BOUNDS;
	public static final int ROOM = 5;
	public static final int DOOR = 6;
	
	public Random rand;
	
	public PyramidStorage(int cellsWidth, int cellsDepth)
	{
		
		this.width = cellsWidth;
		this.depth = cellsDepth;
		
		this.rawWidth = width * 2 + 1;
		this.rawDepth = depth * 2 + 1;
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
	public boolean cellEquals(int x, int z, int value) {
		return getCell(x, z) == value;
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
	public void carveRoom1(int cx, int cz)
	{
		int rx = cx * 2  + 1;
		int rz = cz * 2  + 1;
		
		// remove walls and cells
		for(int i = -2; i <= 2; i++)
		{
			for(int j = -2; j <= 2; j++)
			{
				putRaw(rx + i, rz + j, ROOM);
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
	public void generateRecursiveBacktracker(int sx, int sz)
	{
		rbGen(sx, sz);
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
		if (cellEquals(sx + 1, sz, 0)) {
			unvisited++;
		}
		if (cellEquals(sx - 1, sz, 0)) {
			unvisited++;
		}
		if (cellEquals(sx, sz + 1, 0)) {
			unvisited++;
		}
		if (cellEquals(sx, sz - 1, 0)) {
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
		
		if (cellEquals(sx + 1, sz, 0)) {
			if (rn == 0) {
				dx = sx + 1;
				dz = sz;
			}
			rn--;
		}
		if (cellEquals(sx - 1, sz, 0)) {
			if (rn == 0) {
				dx = sx - 1;
				dz = sz;
			}
			rn--;
		}
		if (cellEquals(sx, sz + 1, 0)) {
			if (rn == 0) {
				dx = sx;
				dz = sz + 1;
			}
			rn--;
		}
		if (cellEquals(sx, sz - 1, 0)) {
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
	}    
}
