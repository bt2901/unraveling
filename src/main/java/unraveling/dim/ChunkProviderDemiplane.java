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
import net.minecraft.world.gen.FlatLayerInfo;

import thaumcraft.common.config.ConfigBlocks;

import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.event.terraingen.TerrainGen;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE;

import java.util.List;
import java.util.Random;
import unraveling.dim.PyramidFeature;

public class ChunkProviderDemiplane implements IChunkProvider {

	private MapGenBase ravineGenerator = TerrainGen.getModdedMapGen(new MapGenRavine(), RAVINE);
    private final byte[] cachedBlockIDs = new byte[256];
    private final byte[] cachedBlockMetadata = new byte[256];
    //Basic on ChunkProviderFlat code
    private World worldObj;
    private Random random;
    //private OreClusterGenerator generator;
    private PyramidFeature pyramidGenerator;

    public ChunkProviderDemiplane(World par1World, long par2, boolean par4) {
        this.worldObj = par1World;
        this.random = new Random(par2);
        //generator = new OreClusterGenerator();
        pyramidGenerator = new PyramidFeature();


        FlatLayerInfo flatlayerinfo = new FlatLayerInfo(256, Blocks.bedrock);

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
		generateTerrain3(cx, cz, blockStorage, metaStorage);
        for (int i = 0; i < 5; ++i) {
            ravineGenerator.func_151539_a(this, this.worldObj, cx + i*100, cz + i*100, blockStorage);
        }
				
		// fake byte array
		Block[] fake = new Block[0];
		pyramidGenerator.func_151539_a(this, worldObj, cx, cz, fake);

		Chunk chunk = new Chunk(worldObj, blockStorage, metaStorage, cx, cz);
		
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, cx * 16, cz * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int k1 = 0; k1 < abyte.length; ++k1) {
            abyte[k1] = (byte) abiomegenbase[k1].biomeID;
        }
		chunk.generateSkylightMap();
	
		return chunk;
	}
	public void generateTerrain3(int chunkX, int chunkZ, Block[] storage, byte[] metaStorage)
    {
        byte seaLevel = 63;
        //if (Math.abs(chunkX) > 20 || Math.abs(chunkZ) > 20 ){
        //    return;
        //}
        //this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
        //this.makeLandPerBiome2(chunkX * 4, 0, chunkZ * 4);
        int miny = 20;
        int maxy = 50 - ((chunkX + chunkZ)%10)/4;
        //System.out.println("gen chunk " + miny + " " + maxy);
        for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
                miny = 20 + (x*z)%2;
                // int maxy = 50 - ((chunkX * 16 + x + chunkZ * 16 + z)%10)/4;
                // int maxy = 50 - ((chunkX + chunkZ + z)%10)/4;
                for (int y = 0; y <= 127; y++) {
                    int index = (x * 16 + z) * 256 + y;
                    if (y > miny && y < maxy) {
                        storage[index] = Blocks.stone;
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
    

     
     /*
    public Chunk provideChunk(int par1, int par2) {
        Chunk chunk = new Chunk(this.worldObj, par1, par2);

        for (int k = 0; k < this.cachedBlockIDs.length; ++k) {
            int l = k >> 4;
            ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

            if (extendedblockstorage == null) {
                extendedblockstorage = new ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
                chunk.getBlockStorageArray()[l] = extendedblockstorage;
            }

            for (int i1 = 0; i1 < 16; ++i1) {
                for (int j1 = 0; j1 < 16; ++j1) {
                    extendedblockstorage.func_150818_a(i1, k & 15, j1, Block.getBlockById(this.cachedBlockIDs[k] & 255));
                    extendedblockstorage.setExtBlockMetadata(i1, k & 15, j1, this.cachedBlockMetadata[k]);
                }
            }
        }

        chunk.generateSkylightMap();
        BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, par1 * 16, par2 * 16, 16, 16);
        byte[] abyte = chunk.getBiomeArray();

        for (int k1 = 0; k1 < abyte.length; ++k1) {
            abyte[k1] = (byte) abiomegenbase[k1].biomeID;
        }
        chunk.generateSkylightMap();
        return chunk;
    }*/
    /*
    	@Override
	public Chunk provideChunk(int cx, int cz) {
		rand.setSeed(cx * 0x4f9939f508L + cz * 0x1ef1565bd5L);
		Block blockStorage[] = new Block[16 * 16 * TFWorld.CHUNKHEIGHT];
		byte metaStorage[] = new byte[16 * 16 * TFWorld.CHUNKHEIGHT];
		generateTerrain2(cx, cz, blockStorage);
				
		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, cx * 16, cz * 16, 16, 16);
		deformTerrainForFeature(cx, cz, blockStorage, metaStorage);
		caveGenerator.func_151539_a(this, worldObj, cx, cz, blockStorage);
		ravineGenerator.func_151539_a(this, worldObj, cx, cz, blockStorage);
		// fake byte array
		Block[] fake = new Block[0];

		majorFeatureGenerator.func_151539_a(this, worldObj, cx, cz, fake);
		
		Chunk chunkchunk = new Chunk(worldObj, blockStorage, metaStorage, cx, cz);
	
		// load in biomes, to prevent striping?!
		byte[] chunkBiomes = chunk.getBiomeArray();
		for (int i = 0; i < chunkBiomes.length; ++i) {
			chunkBiomes[i] = (byte) this.biomesForGeneration[i].biomeID;
		}
	
		chunk.generateSkylightMap();
	
		return chunk;
	}

    */

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
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package twilightforest.world;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import twilightforest.TFFeature;
import twilightforest.biomes.TFBiomeBase;
import twilightforest.block.TFBlocks;
import cpw.mods.fml.common.eventhandler.Event.Result;

// Referenced classes of package net.minecraft.src:
//            IChunkProvider, MapGenCaves, MapGenStronghold, MapGenVillage, 
//            MapGenMineshaft, MapGenRavine, NoiseGeneratorOctaves, World, 
//            WorldChunkManager, Block, BiomeGenBase, Chunk, 
//            MapGenBase, MathHelper, BlockSand, WorldGenLakes, 
//            WorldGenDungeons, SpawnerAnimals, IProgressUpdate

public class ChunkProviderTwilightForest implements IChunkProvider {
	private Random rand;
	//private NoiseGeneratorOctaves noiseGen1;
	//private NoiseGeneratorOctaves noiseGen2;
	//private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorOctaves noiseGen4;
	public NoiseGeneratorOctaves noiseGen5;
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves mobSpawnerNoise;
	private World worldObj;
	private double stoneNoise[];
	private TFGenCaves caveGenerator;
	private TFGenRavine ravineGenerator;
	private BiomeGenBase biomesForGeneration[];
	double noise3[];
	double noise1[];
	double noise2[];
	double noise5[];
	double noise6[];
	float squareTable[];
	int unusedIntArray32x32[][];
	
	
    private WorldType field_147435_p;

    private NoiseGeneratorOctaves field_147431_j;
    private NoiseGeneratorOctaves field_147432_k;
    private NoiseGeneratorOctaves field_147429_l;
    private NoiseGeneratorPerlin field_147430_m;
	
    private final double[] terrainCalcs;
    private final float[] parabolicField;
	
    double[] field_147427_d;
    double[] field_147428_e;
    double[] field_147425_f;
    double[] field_147426_g;
    int[][] field_73219_j = new int[32][32];

	private MapGenTFMajorFeature majorFeatureGenerator;
	private MapGenTFHollowTree hollowTreeGenerator;

	public ChunkProviderTwilightForest(World world, long l, boolean flag) {
		stoneNoise = new double[256];
		caveGenerator = new TFGenCaves();
	
		majorFeatureGenerator = new MapGenTFMajorFeature();
		hollowTreeGenerator = new MapGenTFHollowTree();
	
		ravineGenerator = new TFGenRavine();
		unusedIntArray32x32 = new int[32][32];
		worldObj = world;
		rand = new Random(l);
		//noiseGen1 = new NoiseGeneratorOctaves(rand, 16);
		//noiseGen2 = new NoiseGeneratorOctaves(rand, 16);
		//noiseGen3 = new NoiseGeneratorOctaves(rand, 8);
		noiseGen4 = new NoiseGeneratorOctaves(rand, 4);
		noiseGen5 = new NoiseGeneratorOctaves(rand, 10);
		noiseGen6 = new NoiseGeneratorOctaves(rand, 16);
		mobSpawnerNoise = new NoiseGeneratorOctaves(rand, 8);
		
		
        this.field_147435_p = world.getWorldInfo().getTerrainType();
        this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
        this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
		
        this.terrainCalcs = new double[825];
        this.parabolicField = new float[25];

        for (int j = -2; j <= 2; ++j)
        {
            for (int k = -2; k <= 2; ++k)
            {
                float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }
	}

	@Override
	public Chunk provideChunk(int cx, int cz) {
		rand.setSeed(cx * 0x4f9939f508L + cz * 0x1ef1565bd5L);
		Block blockStorage[] = new Block[16 * 16 * TFWorld.CHUNKHEIGHT];
		byte metaStorage[] = new byte[16 * 16 * TFWorld.CHUNKHEIGHT];
		generateTerrain2(cx, cz, blockStorage);
				
		biomesForGeneration = worldObj.getWorldChunkManager().loadBlockGeneratorData(biomesForGeneration, cx * 16, cz * 16, 16, 16);
		deformTerrainForFeature(cx, cz, blockStorage, metaStorage);
		caveGenerator.func_151539_a(this, worldObj, cx, cz, blockStorage);
		ravineGenerator.func_151539_a(this, worldObj, cx, cz, blockStorage);
		// fake byte array
		Block[] fake = new Block[0];

		majorFeatureGenerator.func_151539_a(this, worldObj, cx, cz, fake);
		
		Chunk chunk = new Chunk(worldObj, blockStorage, metaStorage, cx, cz);
	
		// load in biomes, to prevent striping?!
		byte[] chunkBiomes = chunk.getBiomeArray();
		for (int i = 0; i < chunkBiomes.length; ++i) {
			chunkBiomes[i] = (byte) this.biomesForGeneration[i].biomeID;
		}
	
		chunk.generateSkylightMap();
	
		return chunk;
	}

	public void generateTerrain2(int chunkX, int chunkZ, Block[] blockStorage)
    {
        byte seaLevel = 63;
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);
        this.makeLandPerBiome2(chunkX * 4, 0, chunkZ * 4);

        for (int k = 0; k < 4; ++k)
        {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1)
            {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2)
                {
                    double d0 = 0.125D;
                    double d1 = this.terrainCalcs[k1 + k2];
                    double d2 = this.terrainCalcs[l1 + k2];
                    double d3 = this.terrainCalcs[i2 + k2];
                    double d4 = this.terrainCalcs[j2 + k2];
                    double d5 = (this.terrainCalcs[k1 + k2 + 1] - d1) * d0;
                    double d6 = (this.terrainCalcs[l1 + k2 + 1] - d2) * d0;
                    double d7 = (this.terrainCalcs[i2 + k2 + 1] - d3) * d0;
                    double d8 = (this.terrainCalcs[j2 + k2 + 1] - d4) * d0;

                    for (int l2 = 0; l2 < 8; ++l2)
                    {
                        double d9 = 0.25D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i3 = 0; i3 < 4; ++i3)
                        {
                            int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
                            short short1 = 256;
                            j3 -= short1;
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * d14;
                            double d15 = d10 - d16;

                            for (int k3 = 0; k3 < 4; ++k3)
                            {
                                if ((d15 += d16) > 0.0D)
                                {
                                    blockStorage[j3 += short1] = Blocks.stone;
                                }
                                else if (k2 * 8 + l2 < seaLevel)
                                {
                                    blockStorage[j3 += short1] = Blocks.water;
                                }
                                else
                                {
                                    blockStorage[j3 += short1] = null;
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
    }

    private void makeLandPerBiome2(int x, int zero, int z)
    {

        this.field_147426_g = this.noiseGen6.generateNoiseOctaves(this.field_147426_g, x, z, 5, 5, 200.0D, 200.0D, 0.5D);
        this.field_147427_d = this.field_147429_l.generateNoiseOctaves(this.field_147427_d, x, zero, z, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.field_147428_e = this.field_147431_j.generateNoiseOctaves(this.field_147428_e, x, zero, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.field_147425_f = this.field_147432_k.generateNoiseOctaves(this.field_147425_f, x, zero, z, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        int terrainIndex = 0;
        int noiseIndex = 0;
 
        for (int ax = 0; ax < 5; ++ax)
        {
            for (int az = 0; az < 5; ++az)
            {
                float totalVariation = 0.0F;
                float totalHeight = 0.0F;
                float totalFactor = 0.0F;
                byte two = 2;
                BiomeGenBase biomegenbase = this.biomesForGeneration[ax + 2 + (az + 2) * 10];

                for (int ox = -two; ox <= two; ++ox)
                {
                    for (int oz = -two; oz <= two; ++oz)
                    {
                        BiomeGenBase biomegenbase1 = this.biomesForGeneration[ax + ox + 2 + (az + oz + 2) * 10];
                        float rootHeight = biomegenbase1.rootHeight;
                        float heightVariation = biomegenbase1.heightVariation;

                        if (this.field_147435_p == WorldType.AMPLIFIED && rootHeight > 0.0F)
                        {
                            rootHeight = 1.0F + rootHeight * 2.0F;
                            heightVariation = 1.0F + heightVariation * 4.0F;
                        }

                        float heightFactor = this.parabolicField[ox + 2 + (oz + 2) * 5] / (rootHeight + 2.0F);

                        if (biomegenbase1.rootHeight > biomegenbase.rootHeight)
                        {
                            heightFactor /= 2.0F;
                        }

                        totalVariation += heightVariation * heightFactor;
                        totalHeight += rootHeight * heightFactor;
                        totalFactor += heightFactor;
                    }
                }

                totalVariation /= totalFactor;
                totalHeight /= totalFactor;
                totalVariation = totalVariation * 0.9F + 0.1F;
                totalHeight = (totalHeight * 4.0F - 1.0F) / 8.0F;
                double terrainNoise = this.field_147426_g[noiseIndex] / 8000.0D;

                if (terrainNoise < 0.0D)
                {
                    terrainNoise = -terrainNoise * 0.3D;
                }

                terrainNoise = terrainNoise * 3.0D - 2.0D;

                if (terrainNoise < 0.0D)
                {
                    terrainNoise /= 2.0D;

                    if (terrainNoise < -1.0D)
                    {
                        terrainNoise = -1.0D;
                    }

                    terrainNoise /= 1.4D;
                    terrainNoise /= 2.0D;
                }
                else
                {
                    if (terrainNoise > 1.0D)
                    {
                        terrainNoise = 1.0D;
                    }

                    terrainNoise /= 8.0D;
                }

                ++noiseIndex;
                double heightCalc = (double)totalHeight;
                double variationCalc = (double)totalVariation;
                heightCalc += terrainNoise * 0.2D;
                heightCalc = heightCalc * 8.5D / 8.0D;
                double d5 = 8.5D + heightCalc * 4.0D;

                for (int ay = 0; ay < 33; ++ay)
                {
                    double d6 = ((double)ay - d5) * 12.0D * 128.0D / 256.0D / variationCalc;

                    if (d6 < 0.0D)
                    {
                        d6 *= 4.0D;
                    }

                    double d7 = this.field_147428_e[terrainIndex] / 512.0D;
                    double d8 = this.field_147425_f[terrainIndex] / 512.0D;
                    double d9 = (this.field_147427_d[terrainIndex] / 10.0D + 1.0D) / 2.0D;
                    double terrainCalc = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

                    if (ay > 29)
                    {
                        double d11 = (double)((float)(ay - 29) / 3.0F);
                        terrainCalc = terrainCalc * (1.0D - d11) + -10.0D * d11;
                    }

                    this.terrainCalcs[terrainIndex] = terrainCalc;
                    ++terrainIndex;
                }
            }
        }
    }

    


	
	// * Replaces the stone that was placed in with blocks that match the biome
    public void replaceBlocksForBiome(int chunkX, int chunkZ, Block[] blockStorage, byte[] metaStorage, BiomeGenBase[] biomes)
    {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, chunkX, chunkZ, blockStorage, biomes);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return;

        double d0 = 0.03125D;
        this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

        for (int z = 0; z < 16; ++z)
        {
            for (int x = 0; x < 16; ++x)
            {
                BiomeGenBase biomegenbase = biomes[x + z * 16];
                biomegenbase.genTerrainBlocks(this.worldObj, this.rand, blockStorage, metaStorage, chunkX * 16 + z, chunkZ * 16 + x, this.stoneNoise[x + z * 16]);
            }
        }
    }

	@Override
	public Chunk loadChunk(int i, int j) {
		return provideChunk(i, j);
	}

	 // * Raises up and hollows out the hollow hills.
	 
	public void deformTerrainForFeature(int cx, int cz, Block[] blockStorage, byte[] metaStorage) {
		// what feature are we near?
		TFFeature nearFeature = TFFeature.getNearestFeature(cx, cz, worldObj);
		if (!nearFeature.isTerrainAltered) {
			// well that was easy.
			return;
		}

		int[] nearCenter = TFFeature.getNearestCenter(cx, cz, worldObj);

		int hx = nearCenter[0];
		int hz = nearCenter[1];
		
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int dx = x - hx;
				int dz = z - hz;

				if (nearFeature == TFFeature.hedgeMaze || nearFeature == TFFeature.nagaCourtyard || nearFeature == TFFeature.questGrove) {
					// hedge mazes, naga arena
					flattenTerrainForFeature(blockStorage, nearFeature, x, z, dx, dz);
				}
			}
		}

		// done!
	}

	private void flattenTerrainForFeature(Block[] storage, TFFeature nearFeature, int x, int z, int dx, int dz) {
		int oldGround;
		int newGround;
		float squishfactor = 0;
		int mazeheight = TFWorld.SEALEVEL + 1;
		int FEATUREBOUNDRY = (nearFeature.size * 2 + 1) * 8 - 8;

		if (dx <= -FEATUREBOUNDRY) {
			squishfactor = (-dx - FEATUREBOUNDRY) / 8.0f;
		}

		if (dx >= FEATUREBOUNDRY) {
			squishfactor = (dx - FEATUREBOUNDRY) / 8.0f;
		}
		if (dz <= -FEATUREBOUNDRY) {
			squishfactor = Math.max(squishfactor, (-dz - FEATUREBOUNDRY) / 8.0f);
		}

		if (dz >= FEATUREBOUNDRY) {
			squishfactor = Math.max(squishfactor, (dz - FEATUREBOUNDRY) / 8.0f);
		}

		if (squishfactor > 0) {
			// blend the old terrain height to arena height
			newGround = -1;

			for (int y = 0; y <= 127; y++) {
				int index = (x * 16 + z) * TFWorld.CHUNKHEIGHT + y;
				Block currentTerrain = storage[index];
				if (currentTerrain == Blocks.stone) {
					// we're still in ground
					continue;
				} else {
					if (newGround == -1) {
						// we found the lowest chunk of earth
						oldGround = y;
						mazeheight += ((oldGround - mazeheight) * squishfactor);

						newGround = oldGround;
					}
				}
			}
		}

		// sets the groundlevel to the mazeheight
		for (int y = 0; y <= 127; y++) {
			int index = (x * 16 + z) * TFWorld.CHUNKHEIGHT + y;
			if (y < mazeheight && (storage[index] == Blocks.air || storage[index] == Blocks.water)) {
				storage[index] = Blocks.stone;
			}
			if (y >= mazeheight && storage[index] != Blocks.water) {
				storage[index] = Blocks.air;
			}
		}
	}

	private float pseudoRand(int bx, int bz) {
		Random rand = new Random(this.worldObj.getSeed() + (bx * 321534781) ^ (bz * 756839));
		rand.setSeed(rand.nextLong());
		return rand.nextFloat();
	}

	@Override
	public boolean chunkExists(int i, int j) {
		return true;
	}

	// int chunksGenerated = 0;
	// long totalTime = 0;

	@Override
	public void populate(IChunkProvider ichunkprovider, int chunkX, int chunkZ) {
		//long startTime = System.nanoTime();

		BlockFalling.fallInstantly = true;
		int mapX = chunkX * 16;
		int mapY = chunkZ * 16;

		BiomeGenBase biomeGen = worldObj.getBiomeGenForCoords(mapX + 16, mapY + 16);

		rand.setSeed(worldObj.getSeed());
		long l1 = (rand.nextLong() / 2L) * 2L + 1L;
		long l2 = (rand.nextLong() / 2L) * 2L + 1L;
		rand.setSeed(chunkX * l1 + chunkZ * l2 ^ worldObj.getSeed());

		boolean disableFeatures = false;

		disableFeatures |= this.majorFeatureGenerator.generateStructuresInChunk(worldObj, rand, chunkX, chunkZ);
		disableFeatures |= !TFFeature.getNearestFeature(chunkX, chunkZ, worldObj).areChunkDecorationsEnabled;
		
		hollowTreeGenerator.generateStructuresInChunk(worldObj, rand, chunkX, chunkZ);

		if (!disableFeatures && rand.nextInt(4) == 0 && biomeGen.theBiomeDecorator.generateLakes) {
			int i1 = mapX + rand.nextInt(16) + 8;
			int i2 = rand.nextInt(TFWorld.CHUNKHEIGHT);
			int i3 = mapY + rand.nextInt(16) + 8;
			(new WorldGenLakes(Blocks.water)).generate(worldObj, rand, i1, i2, i3);
		}
		
		if (!disableFeatures && rand.nextInt(32) == 0) // reduced from 8
		{
			int j1 = mapX + rand.nextInt(16) + 8;
			int j2 = rand.nextInt(rand.nextInt(TFWorld.CHUNKHEIGHT - 8) + 8);
			int j3 = mapY + rand.nextInt(16) + 8;
			if (j2 < TFWorld.SEALEVEL || rand.nextInt(10) == 0) {
				(new WorldGenLakes(Blocks.lava)).generate(worldObj, rand, j1, j2, j3);
			}
		}
		for (int k1 = 0; k1 < 8; k1++) {
			int k2 = mapX + rand.nextInt(16) + 8;
			int k3 = rand.nextInt(TFWorld.CHUNKHEIGHT);
			int l3 = mapY + rand.nextInt(16) + 8;
			(new WorldGenDungeons()).generate(worldObj, rand, k2, k3, l3);
		}

		biomeGen.decorate(worldObj, rand, mapX, mapY);
		SpawnerAnimals.performWorldGenSpawning(worldObj, biomeGen, mapX + 8, mapY + 8, 16, 16, rand);
		mapX += 8;
		mapY += 8;
		for (int i2 = 0; i2 < 16; i2++) {
			for (int j3 = 0; j3 < 16; j3++) {
				int j4 = worldObj.getPrecipitationHeight(mapX + i2, mapY + j3);
				if (worldObj.isBlockFreezable(i2 + mapX, j4 - 1, j3 + mapY)) {
					worldObj.setBlock(i2 + mapX, j4 - 1, j3 + mapY, Blocks.ice, 0, 2);
				}
				if (worldObj.func_147478_e(i2 + mapX, j4, j3 + mapY, true)) {
					worldObj.setBlock(i2 + mapX, j4, j3 + mapY, Blocks.snow_layer, 0, 2);
				}
			}
		}

		BlockFalling.fallInstantly = false;

		// long endTime = System.nanoTime();
		// long chunkTime = (endTime - startTime);
		//
		// System.out.println("Generated a chunk in " + chunkTime +
		// " nanoseconds.");
		//
		// this.totalTime += chunkTime;
		// this.chunksGenerated++;
		//
		// System.out.println("Generated " + chunksGenerated +
		// " chunks in avg. " + (totalTime / chunksGenerated) + " nanos or " +
		// ((int)( (totalTime / chunksGenerated / 1000000))) + " millis.");
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate iprogressupdate) {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public String makeString() {
		return "TwilightLevelSource";
	}

	
	
	// * Is the block specified part of a structure bounding box?
	
	public boolean isBlockInStructureBB(int mapX, int mapY, int mapZ) {
		return this.majorFeatureGenerator.hasStructureAt(mapX, mapY, mapZ);
	}

	public StructureBoundingBox getSBBAt(int mapX, int mapY, int mapZ) {
		return this.majorFeatureGenerator.getSBBAt(mapX, mapY, mapZ);
	}

	public boolean isBlockProtected(int x, int y, int z) {
		return this.majorFeatureGenerator.isBlockProtectedAt(x, y, z);
	}

	public boolean isBlockInFullStructure(int x, int z) {
		return this.majorFeatureGenerator.isBlockInFullStructure(x, z);
	}
	
	public boolean isBlockNearFullStructure(int x, int z, int range) {
		return this.majorFeatureGenerator.isBlockNearFullStructure(x, z, range);
	}
	
	public StructureBoundingBox getFullSBBAt(int mapX, int mapZ) {
		return this.majorFeatureGenerator.getFullSBBAt(mapX, mapZ);
	}


	public StructureBoundingBox getFullSBBNear(int mapX, int mapZ, int range) {
		return this.majorFeatureGenerator.getFullSBBNear(mapX, mapZ, range);

	}

	@Override
	public int getLoadedChunkCount() {
		return 0;
	}

	@Override
	public void recreateStructures(int var1, int var2) {
		majorFeatureGenerator.func_151539_a(this, worldObj, var1, var2, (Block[]) null);
	}

	@Override
	public boolean unloadQueuedChunks() {
		return false;
	}

	@Override
	public void saveExtraData() {
	}

	@Override
	public ChunkPosition func_147416_a(World var1, String var2, int var3,
			int var4, int var5) {
		return null;
	}
}
*/