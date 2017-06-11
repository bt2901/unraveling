package unraveling.dim;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import unraveling.dim.PyramidFeatureStart;

public class PyramidFeature extends MapGenStructure {
	
    public PyramidFeature() {}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		return (chunkZ % 32 == 0 && chunkX % 32 == 0);
	}

	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ) {
		// fix rand
        this.rand.setSeed(worldObj.getSeed());
        long rand1 = this.rand.nextLong();
        long rand2 = this.rand.nextLong();
        long chunkXr1 = (long)(chunkX) * rand1;
        long chunkZr2 = (long)(chunkZ) * rand2;
        this.rand.setSeed(chunkXr1 ^ chunkZr2 ^ worldObj.getSeed());
        this.rand.nextInt();
		
		return new PyramidFeatureStart(worldObj, rand, chunkX, chunkZ);
	}
	
	
    public String func_143025_a()
    {
        return "Ziggurath";
    }
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int getSpawnListIndexAt(int par1, int par2, int par3)
    {
    	int highestFoundIndex = 0;
        /*
        Iterator startIterator = this.structureMap.values().iterator();
        while (startIterator.hasNext()) {
            StructureStart start = (StructureStart)startIterator.next();
            if (start.isSizeableStructure() && start.getBoundingBox().intersectsWith(par1, par3, par1, par3))
            {
                Iterator<StructureComponent> componentIterator = start.getComponents().iterator();

                while (componentIterator.hasNext())
                {
                    StructureComponent component = (StructureComponent)componentIterator.next();

                    if (component != null && component.getBoundingBox() != null && component.getBoundingBox().isVecInside(par1, par2, par3))
                    {
                    	if (component instanceof StructureTFComponent)
                    	{
                    		StructureTFComponent tfComponent = (StructureTFComponent)component;
                    		
                    		//System.out.println("found a tfComponent at the specified coordinates.  It's a " + tfComponent + ", index = " + tfComponent.spawnListIndex);
                    		
                    		if (tfComponent.spawnListIndex > highestFoundIndex)
                    		{
                    			highestFoundIndex = tfComponent.spawnListIndex;
                    		}
                    	}
                    	else
                    	{
                    		return 0;
                    	}
                    }
                }
            }
        }
        */
        return highestFoundIndex;
    }

}
