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


import unraveling.block.TFBlocks;
import unraveling.entity.TFCreatures;
import unraveling.EldritchLore;
import unraveling.mechanics.PacketQResearch;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;



import cpw.mods.fml.relauncher.Side;
//import unraveling.item.BehaviorTFMobEggDispense;
//import unraveling.item.ItemTFMagicMap;
//import unraveling.item.ItemTFMazeMap;
import unraveling.item.TFItems;
import unraveling.item.TFRecipes;
//import unraveling.structures.StructureTFMajorFeatureStart;
//import unraveling.tileentity.TileEntityTFFirefly;
//import unraveling.tileentity.TileEntityTFFlameJet;
import unraveling.tileentity.TileEntityTFLichSpawner;
import unraveling.tileentity.TileDarkGenMain;
import unraveling.tileentity.TileDarkGen;
import unraveling.tileentity.TileQuaesitum;

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
	public static final String VERSION = "0.0alpha";
	
	public static final String MODEL_DIR = "unraveling:textures/model/";
	public static final String GUI_DIR = "unraveling:textures/gui/";
	public static final String ENVRIO_DIR = "unraveling:textures/environment/";
	public static final String ARMOR_DIR = "unraveling:textures/armor/";
	
	//public static final int GUI_ID_UNCRAFTING = 1;
	//public static final int GUI_ID_FURNACE = 2;


	public static int dimensionID;
	public static int backupdimensionID = -777;
	public static int dimensionProviderID;
    
	// misc options
    public static boolean creatureCompatibility;
    public static boolean allowPortalsInOtherDimensions;
    public static String unravelingSeed;
    public static String portalCreationItemString;

	public static int idMobLich;
	public static int idMobLichMinion;
	public static int idMobLoyalZombie;
	public static int idMobDeathTome;
	//public static int idMobFirefly;

    
	public static int idVehicleSpawnLichBolt = 1;
	public static int idVehicleSpawnTwilightWandBolt = 2;
	public static int idVehicleSpawnLichBomb = 3;
	public static int idVehicleSpawnTomeBolt = 4;
	
	// public static final TFEventListener eventListener = new TFEventListener();
	public static FMLEventChannel genericChannel;
	public static SimpleNetworkWrapper netHandler;
    //public static PacketHandler voidPacketHandler;
	
	@Instance(ID)
	public static UnravelingMod instance;

	@SidedProxy(clientSide = "unraveling.client.TFClientProxy", serverSide = "unraveling.TFCommonProxy")
	public static TFCommonProxy proxy;

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
		TFBlocks.registerBlocks();

		// items
		TFItems.registerItems();
				
	}

    @EventHandler
	public void load(FMLInitializationEvent evt) {

		registerCreatures();
		TFRecipes.registerRecipes();
		registerTileEntities();
        
		// GUI
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		
		// event listener, for those events that seem worth listening to
		//MinecraftForge.EVENT_BUS.register(eventListener);
		//FMLCommonHandler.instance().bus().register(eventListener); // we're getting events off this bus too
		// tick listener
		// FMLCommonHandler.instance().bus().register(tickHandler);
		
		// set up portal item
		Item portalItem = Items.diamond;
		// tickHandler.portalItem = portalItem;
		
		// make some channels for our maps
		// TFMapPacketHandler mapPacketHandler = new TFMapPacketHandler();
		// NetworkRegistry.INSTANCE.newEventDrivenChannel(ItemTFMagicMap.STR_ID).register(mapPacketHandler);
		// NetworkRegistry.INSTANCE.newEventDrivenChannel(ItemTFMazeMap.STR_ID).register(mapPacketHandler);

		// generic channel that handles biome change packets, but could handle some other stuff in the future
		UnravelingMod.genericChannel = NetworkRegistry.INSTANCE.newEventDrivenChannel(UnravelingMod.ID);
		UnravelingMod.netHandler = NetworkRegistry.INSTANCE.newSimpleChannel(UnravelingMod.ID + "2");
		UnravelingMod.netHandler.registerMessage(PacketQResearch.class, PacketQResearch.class, 2901, Side.SERVER);
		// render and other client stuff
		proxy.doOnLoadRegistration();
		
		// dimension provider
		// DimensionManager.registerProviderType(UnravelingMod.dimensionProviderID, WorldProviderTwilightForest.class, false);
		// enter biomes into dictionary
		// TFBiomeBase.registerWithBiomeDictionary();

        FMLInterModComms.sendMessage("Thaumcraft", "championWhiteList", "Unraveling.Twilight Lich:95");
        EldritchLore.addResearch();
	}
	
	/**
	 * Post init
	 */
    @EventHandler
	public void postInit(FMLPostInitializationEvent evt) 
	{
		// register dimension with Forge
        /*
		if (!DimensionManager.isDimensionRegistered(UnravelingMod.dimensionID))
		{
			DimensionManager.registerDimension(UnravelingMod.dimensionID, UnravelingMod.dimensionProviderID);
		}
		else
		{
			FMLLog.warning("[unraveling] Twilight Forest detected that the configured dimension id '%d' is being used.  Using backup ID.  It is recommended that you configure this mod to use a unique dimension ID.", dimensionID);
			DimensionManager.registerDimension(UnravelingMod.backupdimensionID, UnravelingMod.dimensionProviderID);
			UnravelingMod.dimensionID = UnravelingMod.backupdimensionID;
		}*/
		
		// thaumcraft integration
		if (Loader.isModLoaded("Thaumcraft"))
		{
			registerThaumcraftIntegration();
		}
		else
		{
			FMLLog.info("[unraveling] Did not find Thaumcraft, did not load ThaumcraftApi integration.");
		}
	}
	
    @EventHandler
	public void startServer(FMLServerStartingEvent event)
	{
		// dispenser behaviors
		// registerDispenseBehaviors(event.getServer());
	}

	private void registerCreatures() {
		TFCreatures.registerTFCreature(unraveling.entity.boss.EntityTFLich.class, "Twilight Lich", idMobLich, 0xaca489, 0x360472);
		TFCreatures.registerTFCreature(unraveling.entity.EntityTFDeathTome.class, "Death Tome", idMobDeathTome, 0x774e22, 0xdbcdbe);
		TFCreatures.registerTFCreature(unraveling.entity.boss.EntityTFLichMinion.class, "Lich Minion", idMobLichMinion);
		TFCreatures.registerTFCreature(unraveling.entity.EntityTFLoyalZombie.class, "Loyal Zombie", idMobLoyalZombie);
		
		EntityRegistry.registerModEntity(unraveling.entity.boss.EntityTFLichBolt.class, "tflichbolt",  idVehicleSpawnLichBolt, this, 150, 2, true);
		EntityRegistry.registerModEntity(unraveling.entity.EntityTFTwilightWandBolt.class, "tftwilightwandbolt", idVehicleSpawnTwilightWandBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(unraveling.entity.EntityTFTomeBolt.class, "tftomebolt", idVehicleSpawnTomeBolt, this, 150, 5, true);
		EntityRegistry.registerModEntity(unraveling.entity.boss.EntityTFLichBomb.class, "tflichbomb", idVehicleSpawnLichBomb, this, 150, 3, true);
	}
	
	
	private void registerTileEntities() {
		//GameRegistry.registerTileEntity(TileEntityTFFirefly.class, "Firefly");
		GameRegistry.registerTileEntity(TileEntityTFLichSpawner.class, "Lich Spawner");
		GameRegistry.registerTileEntity(TileDarkGenMain.class, "Void Aggregator");
		GameRegistry.registerTileEntity(TileDarkGen.class, "Darkness Generator");
        GameRegistry.registerTileEntity(TileQuaesitum.class, "Quaesitum");
		//GameRegistry.registerTileEntity(TileEntityTFSmoker.class, "Swamp Smoker");
		//GameRegistry.registerTileEntity(TileEntityTFPoppingJet.class, "Popping Flame Jet");
		//GameRegistry.registerTileEntity(TileEntityTFFlameJet.class, "Lit Flame Jet");
	}

	/**
	 * Use the thaumcraft API to register our things with aspects and biomes with values
	 */
	private void registerThaumcraftIntegration() 
	{
		try {
	
			// items
			registerTCObjectTag(TFItems.scepterTwilight, -1, (new AspectList()).add(Aspect.MAGIC, 8).add(Aspect.ELDRITCH, 8).add(Aspect.WEAPON, 8));
			registerTCObjectTag(TFItems.scepterLifeDrain, -1, (new AspectList()).add(Aspect.MAGIC, 8).add(Aspect.LIFE, 8).add(Aspect.HUNGER, 8));
			registerTCObjectTag(TFItems.scepterZombie, -1, (new AspectList()).add(Aspect.MAGIC, 8).add(Aspect.UNDEAD, 8).add(Aspect.ENTROPY, 8));
			//registerTCObjectTag(TFItems.magicMapFocus, -1, (new AspectList()).add(Aspect.MAGIC, 4).add(Aspect.SENSES, 8));
			//registerTCObjectTag(TFItems.mazeMapFocus, -1, (new AspectList()).add(Aspect.MAGIC, 4).add(Aspect.SENSES, 8).add(Aspect.ORDER, 4));

            
			// blocks
			// registerTCObjectTag(TFBlocks.firefly, -1, (new AspectList()).add(Aspect.FLIGHT, 1).add(Aspect.LIGHT, 2));
			//registerTCObjectTag(TFBlocks.uncraftingTable, -1, (new AspectList()).add(Aspect.TREE, 4).add(Aspect.ENTROPY, 8).add(Aspect.EXCHANGE, 12).add(Aspect.CRAFT, 16));
			//registerTCObjectTag(TFBlocks.fireJet, -1, (new AspectList()).add(Aspect.FIRE, 4).add(Aspect.AIR, 2).add(Aspect.MOTION, 2));
			//registerTCObjectTag(TFBlocks.forceField, -1, (new AspectList()).add(Aspect.MAGIC, 3).add(Aspect.ARMOR, 4));
			
            ThaumcraftApi.addSmeltingBonus(new ItemStack(TFBlocks.voidOre), new ItemStack(ConfigItems.itemNugget, 1, 7));

			
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
	 * Register all dispenser behaviors.
	private void registerDispenseBehaviors(MinecraftServer minecraftServer)
	{
		BlockDispenser.dispenseBehaviorRegistry.putObject(TFItems.spawnEgg, new BehaviorTFMobEggDispense(minecraftServer));
	}
	 */
	
	/**
	 * Load our config file and set default values
	 */
	private void loadConfiguration(Configuration configFile) {
		configFile.load();
		
    	// fixed values, don't even read the config
    	idMobLich = 190;
    	idMobLichMinion = 192;
    	idMobLoyalZombie = 193;
    	idMobDeathTome = 203;
	    
	    if (configFile.hasChanged()) {
	    	configFile.save();
	    }
	}

	
}
