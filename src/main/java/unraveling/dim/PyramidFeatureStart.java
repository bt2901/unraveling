package unraveling.dim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;

//import twilightforest.structures.lichtower.TFLichTowerPieces;
//import twilightforest.structures.minotaurmaze.ComponentTFMazeRuins;
//import twilightforest.structures.minotaurmaze.TFMinotaurMazePieces;
import unraveling.dim.PyramidTranslator;
//import unraveling.dim.TFMinotaurMazePieces;

//import twilightforest.world.TFWorldChunkManager;


public class PyramidFeatureStart extends StructureStart {
			
    static
    {
    	MapGenStructureIO.registerStructure(PyramidFeatureStart.class, "Ziggurath");
        MapGenStructureIO.func_143031_a(PyramidTranslator.class, "ZMain");
    	//TFLichTowerPieces.registerPieces();
    }
    
    public PyramidFeatureStart() {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PyramidFeatureStart(World world, Random rand, int chunkX, int chunkZ)  {

		int x = (chunkX << 4) + 8;
		int z = (chunkZ << 4) + 8;
		int y = 50; //TODO: maybe a biome-specific altitude for some of them?
        
		
		StructureComponent firstComponent = new PyramidTranslator(world, rand, 0, x, y, z);
        components.add(firstComponent);
        firstComponent.buildComponent(firstComponent, components, rand);

		updateBoundingBox();
        
        //moveToAvgGroundLevel(world, x, z);
	}
	    
    /**
     * Check if the component is within the chunk bounding box, but check as if it was one larger
     */
	@SuppressWarnings("unused")
	private boolean isIntersectingLarger(StructureBoundingBox chunkBB, StructureComponent component) {
		StructureBoundingBox compBB = component.getBoundingBox();
		
		// don't bother checking Y
        return (compBB.maxX + 1) >= chunkBB.minX && (compBB.minX - 1) <= chunkBB.maxX && (compBB.maxZ + 1) >= chunkBB.minZ && (compBB.minZ - 1) <= chunkBB.maxZ;

	}

    public void func_143022_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143022_a(par1NBTTagCompound);
        //par1NBTTagCompound.setBoolean("Conquered", this.isConquered);
    }

    public void func_143017_b(NBTTagCompound nbttagcompound) {
        super.func_143017_b(nbttagcompound);
        // this.isConquered = nbttagcompound.getBoolean("Conquered");
    }

 
}
