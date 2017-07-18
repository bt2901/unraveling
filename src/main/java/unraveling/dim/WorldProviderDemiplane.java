package unraveling.dim;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.entity.Entity;

public class WorldProviderDemiplane extends WorldProvider {

    private float[] colorsSunriseSunset = new float[4];

    public void registerWorldChunkManager() {
        this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.beach, this.dimensionId);
        this.dimensionId = 2901;
        this.hasNoSky = false;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
    {
        return 0.0F;
    }

    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderDemiplane(this.worldObj, this.worldObj.getSeed(), false);
    }

    public int getAverageGroundLevel() {
        return 50;
    }

    @Override
    public boolean doesXZShowFog(int par1, int par2) {
        return false;
    }

    public String getDimensionName() {
        return "Howling Isles";
    }

    public boolean renderStars() {
        return true;
    }

    public float getStarBrightness(World world, float f) {
        return 1.0F;
    }
    public float getSunBrightness(World world, float f) {
        return 0.0F;
    }


    public boolean renderClouds() {
        return false;
    }

    public boolean renderVoidFog() {
        return true;
    }

    public float setSunSize() {
        return 0.0F;
    }

    public float setMoonSize() {
        return 0.0F;
    }

    public boolean canRespawnHere() {
        return false;
    }

    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public float getCloudHeight() {
        return 0F;
    }

    public boolean canCoordinateBeSpawn(int par1, int par2) {
        return false;
    }

    protected void generateLightBrightnessTable() {
        float f = 0.1F;

        for (int i = 0; i <= 15; ++i)
        {
            float f1 = 1.0F - (float)i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }
    }

    @SideOnly(Side.CLIENT)
    public String getWelcomeMessage() {
        return null;
    }



	@Override
    public Vec3 getFogColor(float var1, float var2) {
        float f = 1.0F - this.getStarBrightness(1.0F);
        f = 0.0F;
        return Vec3.createVectorHelper(210F / 255F * f, 120F / 255F * f, 59F / 255F * f);
    }
    
    public boolean canRainOrSnow() {
        return false;
    }

    @Override
    public void updateWeather() {
        if (this.canRainOrSnow())
        {
            super.updateWeather();
        }
        else
        {
            this.worldObj.getWorldInfo().setRainTime(0);
            this.worldObj.getWorldInfo().setRaining(false);
            this.worldObj.getWorldInfo().setThunderTime(0);
            this.worldObj.getWorldInfo().setThundering(false);
            this.worldObj.rainingStrength = 0.0F;
            this.worldObj.thunderingStrength = 0.0F;
        }
    }

    @Override
    public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
        return this.canRainOrSnow();
    }

    @Override
    public boolean canDoLightning(Chunk chunk) {
        return this.canRainOrSnow();
    }

    @Override
    public boolean canDoRainSnowIce(Chunk chunk) {
        return this.canRainOrSnow();
    }

    @Override
    public float[] calcSunriseSunsetColors(float var1, float var2) {
        return null;
    }
    
    @Override
    public double getHorizon() {
        return 44.0D;
    }    

    public boolean isDaytime() {
        return false;
    }
    
    @Override
    public Vec3 getSkyColor(Entity cameraEntity, float partialTicks) {
        float f = 1.0F - this.getStarBrightness(1.0F)/2;
        f = 0.0F;
        return Vec3.createVectorHelper(154 / 255.0F * f, 114 / 255.0F * f, 66 / 255.0F * f);
    }

    @Override
    public boolean isSkyColored()
    {
        return true;
    }
    // TODO
    // getBiomeGenForCoords(BlockPos pos)  
}
