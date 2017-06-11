package unraveling.dim;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import java.util.List;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.common.config.ConfigBlocks;


/**
 * Based on a Twilight Forest maze by Benimatic.
 */
public class PyramidMain extends StructureComponent {
    
    private static final int FLOOR_LEVEL = 1;

	public int width; // cells wide (x)
	public int depth; // cells deep (z)
	
    // default values
	public static int oddBias  = 3; // corridor thickness, default 3
	public static int evenBias = 1; // wall thickness here.  NYI 
	
	public static int height = 4; // wall blocks tall
	public static int head = 0;   // blocks placed above the maze
	public static int roots = 0;  // blocks placed under the maze (used for hedge mazes)

	public static int levelsTall = 4;
	
	public int worldX; // set when we first copy the maze into the world
	public int worldY;
	public int worldZ;
    
	public static Block wallBlockID  = ConfigBlocks.blockCosmeticSolid;
	public static int wallBlockMeta = 11;
	
	public static Block wallVar0ID;
	public static int wallVar0Meta;
	public static float wallVarRarity;

	public static Block headBlockID = ConfigBlocks.blockCosmeticSolid;
	public static int headBlockMeta = 12;
	
	public static Block rootBlockID = ConfigBlocks.blockCosmeticSolid;
	public static int rootBlockMeta = 14;
	
	public static Block pillarBlockID;
	public static int pillarBlockMeta;
			
	protected int rawWidth;
	protected int rawDepth;
	
	public static final int OUT_OF_BOUNDS = Integer.MIN_VALUE;
    public    int cellsWidth = 12;
    public    int cellsDepth = 12;
	
    private ArrayList<PyramidMap> mazes = new ArrayList<PyramidMap>();
    
    public PyramidMain() {
        super();
    }
	public PyramidMain(World world, Random rand, int x, int y, int z) {
		super(0);

        levelsTall = 3;
        int entranceX = cellsDepth/2;
        int entranceZ = cellsWidth/2;
		this.width = cellsWidth;
		this.depth = cellsDepth;
		
		this.rawWidth = width * 2 + 1;
		this.rawDepth = depth * 2 + 1;
		
        this.coordBaseMode = 0;

        int radius = (int) ((cellsWidth + 2) * (evenBias + oddBias) * 0.5);
		this.boundingBox = new StructureBoundingBox(x-radius, y, z-radius, x + radius, y + height*(levelsTall+1), z + radius);
        
        for (int i=0; i < levelsTall; ++i) {
            PyramidMap newMaze = new PyramidMap(cellsWidth - height*i/2, cellsDepth - height*i/2);
            
            // set the seed to a fixed value based on this maze's x and z
            setFixedMazeSeed(newMaze, i);

            newMaze.addRoomsToMaze(2);
            newMaze.addBonusRoom(entranceX-i, entranceZ-i, PyramidMap.ROOMCENTRAL);
            if (i > 0) {
                newMaze.addBonusRoom(mazes.get(i-1).rcoords[0] - 1, mazes.get(i-1).rcoords[1] - 1, PyramidMap.ROOM2HIGH);
            }
            // set seed again
            setFixedMazeSeed(newMaze, i);
            // make actual maze
            newMaze.generateRecursiveBacktracker(0, 0);
            mazes.add(newMaze);
        }
        
        
		
		
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

	private void setFixedMazeSeed(PyramidMap pmap, int level) {
		pmap.setSeed(this.boundingBox.minX * 90342903 + this.boundingBox.minY * 90342903 ^ this.boundingBox.minZ + level);
	}
	

	/**
	 * Save to NBT
	 */
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		// super.func_143012_a(par1NBTTagCompound); spawn list index and decorations
        for (int i=0; i < levelsTall; ++i) {
            String key = ("level" + i);
            if (!par1NBTTagCompound.hasKey(key)) {
                par1NBTTagCompound.setTag(key, new NBTTagCompound());
            }
            mazes.get(i).writeToNBT(par1NBTTagCompound.getCompoundTag(key));
        }
		
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
		// super.func_143011_b(par1NBTTagCompound);
        mazes.clear();
        for (int i=0; i < levelsTall; ++i) {
            PyramidMap newMaze = PyramidMap.readFromNBT(par1NBTTagCompound.getCompoundTag("level" + i));
            mazes.add(newMaze);
        }
 	}
	
	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		super.buildComponent(structurecomponent, list, random);
					
        int centerX = boundingBox.minX + ((boundingBox.maxX - boundingBox.minX) / 2);
        int centerZ = boundingBox.minZ + ((boundingBox.maxZ - boundingBox.minZ) / 2);
        for (int i=0; i < levelsTall; ++i) {
			PyramidLevel levelBuilder = new PyramidLevel(random, 
                centerX, boundingBox.minY + (height)*i, centerZ, i, mazes.get(i));
			list.add(levelBuilder);
			levelBuilder.buildComponent(this, list, random);
        }
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int ringMin;
        int ringMax;
        int l = (this.boundingBox.maxX - this.boundingBox.minX) + 2; // TODO: why +2??
        //int startH = boundingBox.minY;
        //int endH = boundingBox.minY + (height)*levelsTall;
        int startH = 1;
        int endH = (height)*(levelsTall + 3) + startH;
        for (int i=startH; i < endH; ++i) {
            fillWithMetadataBlocks(world, sbb, i, i, i, l - i, i, l - i, 
                headBlockID, headBlockMeta, headBlockID, headBlockMeta, false);
        }

		return true;
	}
	

}

