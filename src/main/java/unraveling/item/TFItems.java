package unraveling.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import unraveling.UnravelingMod;
import cpw.mods.fml.common.registry.GameRegistry;

public class TFItems {

	
    public static Item lichPowder;
    public static Item necroFocus;
    public static Item ender_compass;
    public static Item artifact;

    public static Item scrutinyNote;
    public static Item aspectNote;
    public static Item spawnEgg;
    
    public static Item scepterTwilight;
    public static Item scepterLifeDrain;
    public static Item scepterZombie;
    public static Item wandPacification;

    //public static Item magicMap;
    //public static Item mazeMap;
    //public static Item oreMap;
    //public static Item magicMapFocus;
    //public static Item mazeMapFocus;

    //public static Item mazebreakerPick;
    //public static Item emptyMagicMap;
    //public static Item emptyMazeMap;
    //public static Item emptyOreMap;
    
    public static void registerItems()
    {
    
        spawnEgg = new ItemSpawnEgg().setUnlocalizedName("tfspawnegg");
        ender_compass = new ItemCompassStone().setUnlocalizedName("enderCompass");
        GameRegistry.registerItem(ender_compass, "ender_compass");
        
        
        scepterTwilight = new ItemTFTwilightWand().setUnlocalizedName("scepterTwilight").setMaxStackSize(1).setFull3D();
    	scepterLifeDrain = new ItemTFScepterLifeDrain().setUnlocalizedName("scepterLifeDrain").setMaxStackSize(1).setFull3D();
    	scepterZombie = new ItemTFZombieWand().setUnlocalizedName("scepterZombie").setMaxStackSize(1).setFull3D();

        
    	//wandPacification = new ItemTF().setIconIndex(6).setUnlocalizedName("wandPacification").setMaxStackSize(1).setFull3D();
        /*
    	magicMap = new ItemTFMagicMap().setUnlocalizedName("magicMap").setMaxStackSize(1);
    	mazeMap = new ItemTFMazeMap(false).setUnlocalizedName("mazeMap").setMaxStackSize(1);
    	oreMap = new ItemTFMazeMap(true).setUnlocalizedName("oreMap").setMaxStackSize(1);
    	feather = new ItemTF().setUnlocalizedName("tfFeather");
    	magicMapFocus = new ItemTF().setUnlocalizedName("magicMapFocus");
    	emptyMagicMap = (new ItemTFEmptyMagicMap()).setUnlocalizedName("emptyMagicMap");
    	emptyMazeMap = (new ItemTFEmptyMazeMap(false)).setUnlocalizedName("emptyMazeMap");
    	emptyOreMap = (new ItemTFEmptyMazeMap(true)).setUnlocalizedName("emptyOreMap");
    	mazeMapFocus = new ItemTF().setUnlocalizedName("mazeMapFocus");
                registerTFItem(magicMap, "Magic Map");
        registerTFItem(mazeMap, "Maze Map");
        registerTFItem(oreMap, "Maze/Ore Map");
        registerTFItem(feather, "Raven's Feather");
        registerTFItem(magicMapFocus, "Magic Map Focus");
        registerTFItem(mazeMapFocus, "Maze Map Focus");
        registerTFItem(emptyMagicMap, "Blank Magic Map");
        registerTFItem(emptyMazeMap, "Blank Maze Map");
        registerTFItem(emptyOreMap, "Blank Maze/Ore Map");

        */

    	//lichPowder = new Item().setUnlocalizedName("lichPowder");
        //lichPowder.isRare = true;
        //lichPowder.itemIcon = par1IconRegister.registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
        //registerTFItem(lichPowder, "Lich Skull Powder");
        
        necroFocus = new necroFocus().setUnlocalizedName("necroFocus");
        GameRegistry.registerItem(necroFocus, "Necromancy Focus");
        
    	aspectNote = new ItemAspectNote().setUnlocalizedName("aspectNote");
        GameRegistry.registerItem(aspectNote, "Research Aspect Note");

    	artifact = new ItemArtifact().setUnlocalizedName("artifact");
        GameRegistry.registerItem(artifact, "Artifact");
        
    	scrutinyNote = new ItemScrutinyNote().setUnlocalizedName("scrutinyNote");
        GameRegistry.registerItem(scrutinyNote, "scrutinyNote");
        
        registerTFItem(scepterTwilight, "Scepter of Twilight");
        registerTFItem(scepterLifeDrain, "Scepter of Life Draining");
        registerTFItem(scepterZombie, "Zombie Scepter");
        //registerTFItem(wandPacification, "Wand of Pacification [NYI]");
        
        registerTFItem(spawnEgg, "Spawn");
    }

	private static void registerTFItem(Item item, String englishName) {
		//LanguageRegistry.instance().addNameForObject(item, "en_US", englishName);
		GameRegistry.registerItem(item, item.getUnlocalizedName(), UnravelingMod.ID);
	}

}
