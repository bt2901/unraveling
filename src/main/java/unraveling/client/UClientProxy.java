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
import unraveling.client.model.ModelTFDeathTome;
import unraveling.client.model.ModelTFLich;
import unraveling.client.model.ModelTFLichMinion;
import unraveling.client.model.ModelTFLoyalZombie;
import unraveling.client.renderer.entity.RenderTFGenericLiving;
import unraveling.client.renderer.blocks.RenderBlockDarkGen;
import unraveling.client.renderer.blocks.RenderBlockQ;

import unraveling.client.renderer.entity.RenderTFLich;
import unraveling.client.renderer.entity.RenderTFBiped;
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
        
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLich.class, new RenderTFLich(new ModelTFLich(), 1.0F));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLichMinion.class, new RenderTFBiped(new ModelTFLichMinion(), 1.0F, "textures/entity/zombie/zombie.png"));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFLoyalZombie.class, new RenderTFBiped(new ModelTFLoyalZombie(), 1.0F, "textures/entity/zombie/zombie.png"));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFDeathTome.class, new RenderTFGenericLiving(new ModelTFDeathTome(), 0.625F, "textures/entity/enchanting_table_book.png"));
        
		// projectiles
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLichBolt.class, new RenderSnowball(Items.ender_pearl));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFTwilightWandBolt.class, new RenderSnowball(Items.ender_pearl));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFTomeBolt.class, new RenderSnowball(Items.paper));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLichBomb.class, new RenderSnowball(Items.magma_cream));
				
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
