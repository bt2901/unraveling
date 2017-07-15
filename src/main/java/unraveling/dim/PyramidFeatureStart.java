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

import unraveling.UnravelingConfig;

public class PyramidFeatureStart extends StructureStart {
			
    static
    {
    	MapGenStructureIO.registerStructure(PyramidFeatureStart.class, "Ziggurath");
        MapGenStructureIO.func_143031_a(ComponentPyramidRoom.class, "ZRoom");
        MapGenStructureIO.func_143031_a(ComponentPyramidCentralRoom.class, "ZRoomCentral");
        MapGenStructureIO.func_143031_a(PyramidMain.class, "ZMain");
        MapGenStructureIO.func_143031_a(ComponentPyramidEntrance.class, "ZEnter");
        MapGenStructureIO.func_143031_a(PyramidLevel.class, "ZLevel");
        MapGenStructureIO.func_143031_a(ComponentPyramidStairs.class, "ZStairs");
        MapGenStructureIO.func_143031_a(ComponentVoidProductionRoom.class, "ZVPR");
        MapGenStructureIO.func_143031_a(ComponentGardenRoom.class, "ZGarden");
        MapGenStructureIO.func_143031_a(ComponentPyramidTrap.class, "ZRoomTrap");
        MapGenStructureIO.func_143031_a(ComponentCoridorTrap.class, "ZCoridorTrap");
    }
    
    public PyramidFeatureStart() {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PyramidFeatureStart(World world, Random rand, int chunkX, int chunkZ)  {
		int x = (chunkX * 16) + 8;
		int z = (chunkZ * 16) + 8;
		int y = UnravelingConfig.maxDemiplaneHeight - 3*PyramidMain.height/2; //TODO: maybe a biome-specific altitude for some of them?

		StructureComponent firstComponent = new PyramidMain(world, rand, x, y, z);
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
