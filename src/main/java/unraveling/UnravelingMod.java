package unraveling;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.lib.research.PlayerKnowledge;
import thaumcraft.common.Thaumcraft;


import unraveling.block.UBlocks;
import unraveling.entity.TFCreatures;
import unraveling.EldritchLore;
import unraveling.mechanics.PacketQResearch;
import unraveling.mechanics.voidgen.PacketDrainSwitch;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import unraveling.dim.WorldProviderDemiplane;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;


import cpw.mods.fml.relauncher.Side;
import unraveling.item.UItems;
import unraveling.item.URecipes;

import unraveling.tileentity.TileEntityTFLichSpawner;

import unraveling.mechanics.voidgen.TileDarkGenMain;
import unraveling.mechanics.voidgen.TileDarkGen;
import unraveling.mechanics.MessageGetStrongholdPos;
import unraveling.mechanics.MessageSetStrongholdPos;
import unraveling.tileentity.TileQuaesitum;
import unraveling.dim.TileVoidPortal;

import unraveling.tileentity.TileEntityTrapRoom;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import thaumcraft.api.*;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ItemApi;
import thaumcraft.common.config.ConfigItems;


@Mod(modid = UnravelingMod.ID, name = "Unraveling Of the Eldritch", version = UnravelingMod.VERSION)
public class UnravelingMod {
	
	public static final String ID = "unraveling";
	public static final String VERSION = "0.2beta";
	public static final UEventListener eventListener = new UEventListener();
	
	public static int dimensionID;
	public static int dimensionProviderID;
        
	public static int idVehicleSpawnLichBolt = 1;
	public static int idVehicleSpawnTwilightWandBolt = 2;
	public static int idVehicleSpawnLichBomb = 3;
	public static int idVehicleSpawnTomeBolt = 4;

    public static final String MODEL_DIR = "unraveling:textures/model/";
    public static final String GUI_DIR = "unraveling:textures/gui/";

	
	public static FMLEventChannel genericChannel;
	public static SimpleNetworkWrapper netHandler;
    //public static PacketHandler voidPacketHandler;
	
	@Instance(ID)
	public static UnravelingMod instance;

	@SidedProxy(clientSide = "unraveling.client.UClientProxy", serverSide = "unraveling.UCommonProxy")
	public static UCommonProxy proxy;

	public UnravelingMod() {
		UnravelingMod.instance = this;
	}
	
    @EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// load config
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		loadConfiguration(config);
		// sounds on client, and whatever else needs to be registered pre-load
		proxy.doPreLoadRegistration();

		// initialize & register blocks
		UBlocks.registerBlocks();

		// items
		UItems.registerItems();
				
	}

    @EventHandler
	public void load(FMLInitializationEvent evt) {

		registerCreatures();
		registerTileEntities();
        
		// GUI
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        MinecraftForge.EVENT_BUS.register(eventListener);
		FMLCommonHandler.instance().bus().register(eventListener); 
        
		UnravelingMod.genericChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(UnravelingMod.ID);
		UnravelingMod.netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(UnravelingMod.ID + "2");

        int idx = 0;
		UnravelingMod.netHandler.registerMessage(PacketQResearch.class, PacketQResearch.class, idx++, Side.SERVER);
		UnravelingMod.netHandler.registerMessage(PacketDrainSwitch.class, PacketDrainSwitch.class, idx++, Side.SERVER);
		UnravelingMod.netHandler.registerMessage(MessageGetStrongholdPos.class, MessageGetStrongholdPos.class, idx++, Side.SERVER);
		UnravelingMod.netHandler.registerMessage(MessageSetStrongholdPos.class, MessageSetStrongholdPos.class, idx++, Side.CLIENT);

        
		// render and other client stuff
		proxy.doOnLoadRegistration();
		
		// dimension provider
		// DimensionManager.registerProviderType(UnravelingMod.dimensionProviderID, WorldProviderTwilightForest.class, false);
		// enter biomes into dictionary
		// TFBiomeBase.registerWithBiomeDictionary();
        DimensionManager.registerProviderType(2901, WorldProviderDemiplane.class, false);
        DimensionManager.registerDimension(2901, 2901);
        
        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Unraveling.Twilight Lich:95");
	}
	
	/**
	 * Post init
	 */
    @EventHandler
	public void postInit(FMLPostInitializationEvent evt) 
	{
		URecipes.registerRecipes();
        EldritchLore.addResearch();
		// register dimension with Forge
		//if (!DimensionManager.isDimensionRegistered(UnravelingMod.dimensionID))
        //DimensionManager.registerDimension(UnravelingMod.dimensionID, UnravelingMod.dimensionProviderID);
		
		// thaumcraft integration
		if (Loader.isModLoaded("Thaumcraft")) {
			registerThaumcraftIntegration();
		} else {
			FMLLog.info("[unraveling] Did not find Thaumcraft, did not load ThaumcraftApi integration.");
		}
	}
	
    @EventHandler
	public void startServer(FMLServerStartingEvent event) {}

	private void registerCreatures() {
		TFCreatures.registerTFCreature(unraveling.entity.boss.EntityTFLich.class, "Twilight Lich", 190, 0xaca489, 0x360472);
		TFCreatures.registerTFCreature(unraveling.entity.EntityTFDeathTome.class, "Death Tome", 191, 0x774e22, 0xdbcdbe);
		TFCreatures.registerTFCreature(unraveling.entity.boss.EntityTFLichMinion.class, "Lich Minion", 192);
		TFCreatures.registerTFCreature(unraveling.entity.EntityTFLoyalZombie.class, "Loyal Zombie", 81);
		
		EntityRegistry.registerModEntity(unraveling.entity.boss.EntityTFLichBolt.class, "tflichbolt",  idVehicleSpawnLichBolt, this, 150, 2, true);
		EntityRegistry.registerModEntity(unraveling.entity.EntityTFTwilightWandBolt.class, "tftwilightwandbolt", idVehicleSpawnTwilightWandBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(unraveling.entity.EntityTFTomeBolt.class, "tftomebolt", idVehicleSpawnTomeBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(unraveling.entity.boss.EntityTFLichBomb.class, "tflichbomb", idVehicleSpawnLichBomb, this, 150, 3, true);
	}
	
	
	private void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityTFLichSpawner.class, "Lich Spawner");
		GameRegistry.registerTileEntity(TileDarkGenMain.class, "Void Aggregator");
		GameRegistry.registerTileEntity(TileDarkGen.class, "Darkness Generator");
        GameRegistry.registerTileEntity(TileQuaesitum.class, "Quaesitum");
        GameRegistry.registerTileEntity(TileVoidPortal.class, "Void Portal");
        GameRegistry.registerTileEntity(TileEntityTrapRoom.class, "GolemSpawner");
	}

	/**
	 * Use the thaumcraft API to register our things with aspects and biomes with values
	 */
	private void registerThaumcraftIntegration() 
	{
		try {
	
			// items
			registerTCObjectTag(UItems.scepterTwilight, -1, (new AspectList()).add(Aspect.MAGIC, 8).add(Aspect.ELDRITCH, 8).add(Aspect.WEAPON, 8));
			registerTCObjectTag(UItems.scepterLifeDrain, -1, (new AspectList()).add(Aspect.MAGIC, 8).add(Aspect.LIFE, 8).add(Aspect.HUNGER, 8));
			registerTCObjectTag(UItems.scepterZombie, -1, (new AspectList()).add(Aspect.MAGIC, 8).add(Aspect.UNDEAD, 8).add(Aspect.ENTROPY, 8));

		} 
		catch (Exception e) 
		{
			FMLLog.warning("[Unraveling] Had an %s error while trying to register TC Aspects.", e.getLocalizedMessage());
			// whatever.
		}

	}
	
	/**
	 * Register a block with Thaumcraft aspects
	 */
	private void registerTCObjectTag(Block block, int meta, AspectList list) {
		if (meta == -1) {
			meta = OreDictionary.WILDCARD_VALUE;
		}		
		ThaumcraftApi.registerObjectTag(new ItemStack(block, 1, meta), list);
	}
	
	/**
	 * Register an item with Thaumcraft aspects
	 */
	private void registerTCObjectTag(Item item, int meta, AspectList list) {
		if (meta == -1) {
			meta = OreDictionary.WILDCARD_VALUE;
		}	
		ThaumcraftApi.registerObjectTag(new ItemStack(item, 1, meta), list);
	}

	
	/**
	 * Load our config file and set default values
	 */
	private void loadConfiguration(Configuration configFile) {
		configFile.load();
			    
	    if (configFile.hasChanged()) {
	    	configFile.save();
	    }
	}


	
}
