package unraveling.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderBiped;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import unraveling.UCommonProxy;
import unraveling.UnravelingMod;
import unraveling.client.renderer.blocks.RenderBlockDarkGen;
import unraveling.client.renderer.blocks.RenderBlockQ;

import unraveling.mechanics.voidgen.VoidPacketHandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import thaumcraft.common.Thaumcraft;

public class UClientProxy extends UCommonProxy {

	int blockDarkGenRenderID;
	int blockQRenderID;
	/**
	 * Called during mod loading.  Registers renderers and stuff
	 */
	@Override
	public void doOnLoadRegistration() {
		Minecraft mc = FMLClientHandler.instance().getClient();
				
		// packet listener
		VoidPacketHandler voidPacketHandler = new VoidPacketHandler();
		UnravelingMod.genericChannel.register(voidPacketHandler);

		// block render ids
		blockDarkGenRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderBlockDarkGen(blockDarkGenRenderID));
		blockQRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderBlockQ(blockQRenderID));
		
	}
	public int getDarkGenRenderID() {
		return blockDarkGenRenderID;
	}
	public int getQRenderID() {
		return blockQRenderID;
	}
	
	@Override
	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}
    public static EntityPlayer getPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }	

    @Override
    public void doBlockTransformEffect(World worldObj, int x, int y, int z) {
        Thaumcraft.proxy.burst(worldObj, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, 1.0F);
        Thaumcraft.proxy.reservoirBubble(worldObj, x, y, z, 0x4D00FF);
    };
}
