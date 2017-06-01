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
import unraveling.block.TFBlocks;
import unraveling.client.model.ModelTFDeathTome;
import unraveling.client.model.ModelTFLich;
import unraveling.client.model.ModelTFLichMinion;
import unraveling.client.model.ModelTFLoyalZombie;
import unraveling.client.renderer.entity.RenderTFGenericLiving;
import unraveling.client.renderer.entity.RenderTFProtectionBox;
import unraveling.client.renderer.blocks.RenderBlockTFFireflyJar;
import unraveling.client.renderer.blocks.RenderBlockDarkGen;
import unraveling.client.renderer.blocks.RenderBlockQ;
import unraveling.client.particle.EntityTFProtectionFX;

//import unraveling.client.renderer.TFMagicMapRenderer;
//import unraveling.client.renderer.TFMazeMapRenderer;
import unraveling.client.renderer.entity.RenderTFTinyFirefly;
import unraveling.client.renderer.entity.RenderTFLich;
import unraveling.client.renderer.entity.RenderTFBiped;
import unraveling.mechanics.VoidPacketHandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import thaumcraft.common.Thaumcraft;

public class UClientProxy extends UCommonProxy {

	int blockComplexRenderID;
	int blockDarkGenRenderID;
	int blockQRenderID;
	/**
	 * Called during mod loading.  Registers renderers and stuff
	 */
	@Override
	public void doOnLoadRegistration() {
		Minecraft mc = FMLClientHandler.instance().getClient();
		
		// client tick listener
		//clientTicker = new TFClientTicker();
		//FMLCommonHandler.instance().bus().register(clientTicker);
		
		// client events
		//clientEvents = new TFClientEvents();
		//MinecraftForge.EVENT_BUS.register(clientEvents);
		
		// packet listener
		VoidPacketHandler voidPacketHandler = new VoidPacketHandler();
		UnravelingMod.genericChannel.register(voidPacketHandler);
        
		//RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.passive.EntityTFTinyFirefly.class, new RenderTFTinyFirefly());
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLich.class, new RenderTFLich(new ModelTFLich(), 1.0F));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLichMinion.class, new RenderTFBiped(new ModelTFLichMinion(), 1.0F, "textures/entity/zombie/zombie.png"));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFLoyalZombie.class, new RenderTFBiped(new ModelTFLoyalZombie(), 1.0F, "textures/entity/zombie/zombie.png"));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFDeathTome.class, new RenderTFGenericLiving(new ModelTFDeathTome(), 0.625F, "textures/entity/enchanting_table_book.png"));
		//RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.passive.EntityTFMobileFirefly.class, new RenderTFTinyFirefly());
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFProtectionBox.class, new RenderTFProtectionBox());
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFTinyFirefly.class, new RenderTFTinyFirefly());
        //registerTileEntitySpecialRenderer(TileDarkRelay.class, new TileDarkRelayRenderer());

        
		// projectiles
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLichBolt.class, new RenderSnowball(Items.ender_pearl));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFTwilightWandBolt.class, new RenderSnowball(Items.ender_pearl));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.EntityTFTomeBolt.class, new RenderSnowball(Items.paper));
		RenderingRegistry.registerEntityRenderingHandler(unraveling.entity.boss.EntityTFLichBomb.class, new RenderSnowball(Items.magma_cream));
		
		// tile entities
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFFirefly.class, new TileEntityTFFireflyRenderer());
		
		// map item renderer
		//MinecraftForgeClient.registerItemRenderer(TFItems.magicMap, new TFMagicMapRenderer(mc.gameSettings, mc.getTextureManager()));
		//TFMazeMapRenderer mazeRenderer = new TFMazeMapRenderer(mc.gameSettings, mc.getTextureManager());
		//MinecraftForgeClient.registerItemRenderer(TFItems.mazeMap, mazeRenderer);
		//MinecraftForgeClient.registerItemRenderer(TFItems.oreMap, mazeRenderer);
		
		
		// block render ids
		blockComplexRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderBlockTFFireflyJar(blockComplexRenderID));
		blockDarkGenRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderBlockDarkGen(blockDarkGenRenderID));
		blockQRenderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderBlockQ(blockQRenderID));
		
	}

	public int getComplexBlockRenderID() {
		return blockComplexRenderID;
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

	/**
	 * Spawns a particle.  This is my copy of RenderGlobal.spawnParticle where I implement custom particles.
	 */
	public void spawnParticle(World world, String particleType, double x, double y, double z, double velX, double velY, double velZ)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null && mc.theWorld == world)
		{
			// TODO: check render settings?
			double distX = mc.renderViewEntity.posX - x;
			double distY = mc.renderViewEntity.posY - y;
			double distZ = mc.renderViewEntity.posZ - z;

			EntityFX particle = null;

			double maxDist = 64.0D; // normally 16.0D

			// check for particle max distance
			if (distX * distX + distY * distY + distZ * distZ < maxDist * maxDist)
			{
				if (particleType.equals("protection"))
				{
					particle = new EntityTFProtectionFX(world, x, y, z, velX, velY, velZ);
				}
				// if we made a partcle, go ahead and add it
				if (particle != null)
				{
					particle.prevPosX = particle.posX;
					particle.prevPosY = particle.posY;
					particle.prevPosZ = particle.posZ;
					
					// we keep having a non-threadsafe crash adding particles directly here, so let's pass them to a buffer
					//clientTicker.addParticle(particle); 
					mc.effectRenderer.addEffect(particle); // maybe it's fixed?
				}
			}
		}
	}
    @Override
    public void doBlockTransformEffect(World worldObj, int x, int y, int z) {
        System.out.println("doBlockTransformEffect");
        Thaumcraft.proxy.burst(worldObj, (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, 1.0F);
        Thaumcraft.proxy.reservoirBubble(worldObj, x, y, z, 0x4D00FF);
        //worldObj.playSoundEffect((double)x, (double)y, (double)z, UnravelingMod.ID + ":random.necromancy", 1.0F, 1.0F);
    };
}
