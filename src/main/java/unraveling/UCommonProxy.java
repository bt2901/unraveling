package unraveling;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import unraveling.mechanics.voidgen.TileDarkGen;
import unraveling.tileentity.TileQuaesitum;
import unraveling.mechanics.ContainerQ;
import unraveling.mechanics.GuiQTileEntity;
import unraveling.mechanics.voidgen.GuiDarkGen;
import unraveling.mechanics.voidgen.ContainerDarkGen;

import cpw.mods.fml.common.network.IGuiHandler;

public class UCommonProxy implements IGuiHandler {
	
	/**
	 * Called during the pre-load step.  Register stuff here.  
	 * Obviously most stuff in the common category will be just registered in the mod file
	 */
	public void doPreLoadRegistration() {
		;
	}
	
	/**
	 * Called during the load step.  Register stuff here.  
	 * Obviously most stuff in the common category will be just registered in the mod file
	 */
	public void doOnLoadRegistration() {
		;
	}

	public int getDarkGenRenderID() {
		return 0;
	}
	public int getQRenderID() {
		return 0;
	}

	public World getClientWorld() {
		return null;
	}
	
	/**
	 * Spawns a particle.  This is my copy of RenderGlobal.spawnParticle where I implement custom particles.
	 * Null op except on the client.
	 */
	public void spawnParticle(World world, String particleType, double x, double y, double z, double velX, double velY, double velZ) {
		;
	}

    public static final int GUI_ID_Q = 0;
    public static final int GUI_ID_DG = 1;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ID_Q)
            return new ContainerQ((TileQuaesitum) world.getTileEntity(x, y, z), player.inventory);
        if (ID == GUI_ID_DG)
            return new ContainerDarkGen((TileDarkGen) world.getTileEntity(x, y, z), player.inventory);

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == GUI_ID_Q)
            return new GuiQTileEntity((TileQuaesitum) world.getTileEntity(x, y, z), player.inventory);
        if (ID == GUI_ID_DG)
            return new GuiDarkGen((TileDarkGen) world.getTileEntity(x, y, z), player.inventory);

        return null;
    }

    public void doBlockTransformEffect(World worldObj, int blockX, int blockY, int blockZ) {
    };

}
