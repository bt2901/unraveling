package unraveling;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
//import unraveling.inventory.ContainerTFUncrafting;
import cpw.mods.fml.common.network.IGuiHandler;

public class TFCommonProxy implements IGuiHandler {
	
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

	public int getComplexBlockRenderID() {
		return 0;
	}
	public int getDarkGenRenderID() {
		return 0;
	}
	//public int getCastleMagicBlockRenderID() {
	//	return 0;
	//}

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

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		/*
        if (id == UnravelingMod.GUI_ID_UNCRAFTING) {
			return new ContainerTFUncrafting(player.inventory, world, x, y, z);
		} */
        return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		/*
		if (id == unravelingMod.GUI_ID_UNCRAFTING) {
			return new unraveling.client.GuiTFGoblinCrafting(player.inventory, world, x, y, z);
		} */
        return null;
	}
    public void doBlockTransformEffect(World worldObj, int blockX, int blockY, int blockZ) {
    };

}
