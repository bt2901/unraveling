package unraveling.dim;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import thaumcraft.common.config.ConfigBlocks;

import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.event.terraingen.TerrainGen;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE;

import java.util.List;
import java.util.Random;
import unraveling.dim.PyramidFeature;
import unraveling.block.UBlocks;
import unraveling.UnravelingConfig;

import unraveling.perlin.Gradient;

public class ChunkProviderDemiplane implements IChunkProvider {

	private MapGenBase ravineGenerator = TerrainGen.getModdedMapGen(new MapGenRavine(), RAVINE);

    private World worldObj;
    private Random random;
    private PyramidFeature pyramidGenerator;
	private Gradient noiseGen1;
	private Gradient noiseGen2;
	private Gradient noiseGen3;
    
    public ChunkProviderDemiplane(World par1World, long par2, boolean par4) {
        this.worldObj = par1World;
        this.random = new Random(par2);
        //noiseGen1 = new NoiseGeneratorPerlin(random, 4);
        //noiseGen2 = new NoiseGeneratorPerlin(random, 4);
        //noiseGen3 = new NoiseGeneratorPerlin(random, 1);
        this.noiseGen1 = new Gradient(this.random.nextLong(), 4, 0.25F);
        this.noiseGen2 = new Gradient(this.random.nextLong(), 4, 0.25F);
        this.noiseGen3 = new Gradient(this.random.nextLong(), 1, 0.25F);        
        pyramidGenerator = new PyramidFeature();
    }
    @Override
	public void recreateStructures(int var1, int var2) {
		pyramidGenerator.func_151539_a(this, worldObj, var1, var2, (Block[]) null);
	}

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int par1, int par2) {
        return this.provideChunk(par1, par2);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
     
    @Override
	public Chunk provideChunk(int cx, int cz) {
        // rand.setSeed(cx * 0x4f9939f508L + cz * 0x1ef1565bd5L);
		Block blockStorage[] = new Block[16 * 16 * 256];
		byte metaStorage[] = new byte[16 * 16 * 256];
		// generateTerrain3(cx, cz, blockStorage, metaStorage);
		generateTerrain(cx, cz, blockStorage, metaStorage);
        for (int i = 0; i < UnravelingConfig.ravineFrequency; ++i) {
            ravineGenerator.func_151539_a(this, this.worldObj, cx + i*100, cz + i*100, blockStorage);
        }
				
		// fake byte array
		Block[] fake = new Block[0];
		pyramidGenerator.func_151539_a(this, worldObj, cx, cz, fake);

        this.replaceBlocksForBiome(cx, cz, blockStorage, metaStorage, null);
		Chunk chunk = new Chunk(worldObj, blockStorage, metaStorage, cx, cz);
		
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, cx * 16, cz * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int k1 = 0; k1 < abyte.length; ++k1) {
            abyte[k1] = (byte) abiomegenbase[k1].biomeID;
        }
        // this.createCraters(par1, par2, ids, meta);
        
		chunk.generateSkylightMap();
	
		return chunk;
	}

    public void replaceBlocksForBiome(int par1, int par2, Block[] idArray, byte[] arrayOfMeta, BiomeGenBase[] par4ArrayOfBiomeGenBase) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    if (idArray[this.getIndex(x, y, z)] == Blocks.stone) {
                        idArray[this.getIndex(x, y, z)] = UnravelingConfig.demiplaneStone;
                    }
                }        
            }
        }
    }    
    public void generateTerrain(int chunkX, int chunkZ, Block[] idArray, byte[] metaArray) {
        this.noiseGen1.setFrequency(0.0125F);
        this.noiseGen2.setFrequency(0.015F);
        this.noiseGen3.setFrequency(0.01F);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                final double d = this.noiseGen1.getNoise(x + chunkX * 16, z + chunkZ * 16) * 8;
                final double d2 = this.noiseGen2.getNoise(x + chunkX * 16, z + chunkZ * 16) * 24;
                double d3 = this.noiseGen3.getNoise(x + chunkX * 16, z + chunkZ * 16) - 0.1;
                d3 *= 4;

                double yDev = 0;
                double yBottomDev = 0;

                if (d3 < 0.0D) {
                    yDev = d;
                    yBottomDev = d2 + d;
                }
                else if (d3 > 1.0D) {
                    yDev = d2;
                    yBottomDev = d - d2;
                }
                else {
                    yBottomDev = d + (d2 - d) * d3;
                    yDev = d + (d2 - d) * d3;
                }

                for (int y = 0; y < 128; y++) {
                    if (y < UnravelingConfig.maxDemiplaneHeight + yDev) {
                        // idArray[this.getIndex(x, y, z)] = UBlocks.saprolite;
                        idArray[this.getIndex(x, y, z)] = Blocks.stone;
                        metaArray[this.getIndex(x, y, z)] = 0;
                    }
                    if (y < UnravelingConfig.minDemiplaneHeight + yBottomDev) {
                        idArray[this.getIndex(x, y, z)] = Blocks.air;
                        metaArray[this.getIndex(x, y, z)] = 0;
                    }
                }
            }
        }
    }
    public int getIndex(int x, int y, int z) {
        return (x * 16 + z) * 256 + y;
    }

	public void generateTerrain3(int chunkX, int chunkZ, Block[] storage, byte[] metaStorage)
    {
        byte seaLevel = 50;
        //if (Math.abs(chunkX) > 20 || Math.abs(chunkZ) > 20 ){
        //    return;
        //}
        int miny = 20;
        int maxy = 50 - ((chunkX + chunkZ)%10)/4; // TODO: use noise here or something
        for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
                miny = 20 + (x*z)%2;
                // int maxy = 50 - ((chunkX * 16 + x + chunkZ * 16 + z)%10)/4;
                // int maxy = 50 - ((chunkX + chunkZ + z)%10)/4;
                for (int y = 0; y <= 127; y++) {
                    int index = (x * 16 + z) * 256 + y;
                    if (y > miny && y < maxy) {
                        storage[index] = UBlocks.saprolite;
                    } else {
                        if (y > 120) {
                            storage[index] = Blocks.air;
                            // storage[index] = ConfigBlocks.blockEldritchNothing;
                            // metaStorage[index] = 1;
                        } else {
                            storage[index] = Blocks.air;
                        }
                    }
                }
            }
        }
    }
    

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
        int k = par2 * 16;
        int l = par3 * 16;
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
        boolean flag = false;
        this.random.setSeed(this.worldObj.getSeed());
        long i1 = this.random.nextLong() / 2L * 2L + 1L;
        long j1 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long) par2 * i1 + (long) par3 * j1 ^ this.worldObj.getSeed());
        pyramidGenerator.generateStructuresInChunk(worldObj, random, par2, par3);

        int k1;
        int l1;
        int i2;
        /*
        if (this.generator != null) {
            l1 = k + this.random.nextInt(16) + 8;
            k1 = this.random.nextInt(128);
            i2 = l + this.random.nextInt(16) + 8;
            this.generator.generate(this.random, par2, par3, this.worldObj, this, this);
        }*/

    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "Hollow Lands";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
        return null;
    }

    @Override
    public ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5) {
        return null;
    }

    public int getLoadedChunkCount() {
        return 0;
    }
}
/*


    public void createCraters(int chunkX, int chunkZ, Block[] chunkArray, byte[] metaArray)
    {
        for (int cx = chunkX - 2; cx <= chunkX + 2; cx++)
        {
            for (int cz = chunkZ - 2; cz <= chunkZ + 2; cz++)
            {
                for (int x = 0; x < ChunkProviderMoon.CHUNK_SIZE_X; x++)
                {
                    for (int z = 0; z < ChunkProviderMoon.CHUNK_SIZE_Z; z++)
                    {
                        if (Math.abs(this.randFromPoint(cx * 16 + x, (cz * 16 + z) * 1000)) < this.noiseGen4.getNoise(x * ChunkProviderMoon.CHUNK_SIZE_X + x, cz * ChunkProviderMoon.CHUNK_SIZE_Z + z) / ChunkProviderMoon.CRATER_PROB)
                        {
                            final Random random = new Random(cx * 16 + x + (cz * 16 + z) * 5000);
                            final EnumCraterSize cSize = EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.length)];
                            final int size = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE;
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, chunkArray, metaArray);
                        }
                    }
                }
            }
        }
    }

    public void makeCrater(int craterX, int craterZ, int chunkX, int chunkZ, int size, Block[] chunkArray, byte[] metaArray)
    {
        for (int x = 0; x < ChunkProviderMoon.CHUNK_SIZE_X; x++)
        {
            for (int z = 0; z < ChunkProviderMoon.CHUNK_SIZE_Z; z++)
            {
                double xDev = craterX - (chunkX + x);
                double zDev = craterZ - (chunkZ + z);
                if (xDev * xDev + zDev * zDev < size * size)
                {
                    xDev /= size;
                    zDev /= size;
                    final double sqrtY = xDev * xDev + zDev * zDev;
                    double yDev = sqrtY * sqrtY * 6;
                    yDev = 5 - yDev;
                    int helper = 0;
                    for (int y = 127; y > 0; y--)
                    {
                        if (Blocks.air != chunkArray[this.getIndex(x, y, z)] && helper <= yDev)
                        {
                            chunkArray[this.getIndex(x, y, z)] = Blocks.air;
                            metaArray[this.getIndex(x, y, z)] = 0;
                            helper++;
                        }
                        if (helper > yDev)
                        {
                            break;
                        }
                    }
                }
            }
        }
    }*/