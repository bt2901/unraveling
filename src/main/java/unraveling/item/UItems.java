package unraveling.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import unraveling.UnravelingMod;
import cpw.mods.fml.common.registry.GameRegistry;

public class UItems {

	
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
    
    public static void registerItems()
    {
    
        spawnEgg = new ItemSpawnEgg().setUnlocalizedName("tfspawnegg");
        ender_compass = new ItemCompassStone().setUnlocalizedName("enderCompass");
        GameRegistry.registerItem(ender_compass, "ender_compass");
        
        scepterTwilight = new ItemTFTwilightWand().setUnlocalizedName("scepterTwilight").setMaxStackSize(1).setFull3D();
    	scepterLifeDrain = new ItemTFScepterLifeDrain().setUnlocalizedName("scepterLifeDrain").setMaxStackSize(1).setFull3D();
    	scepterZombie = new ItemTFZombieWand().setUnlocalizedName("scepterZombie").setMaxStackSize(1).setFull3D();

    	//lichPowder = new Item().setUnlocalizedName("lichPowder");
        //lichPowder.isRare = true;
        //lichPowder.itemIcon = par1IconRegister.registerIcon(TwilightForestMod.ID + ":" + this.getUnlocalizedName().substring(5));
        //registerTFItem(lichPowder, "Lich Skull Powder");
        
        necroFocus = new necroFocus().setUnlocalizedName("necroFocus");
        GameRegistry.registerItem(necroFocus, "Necromancy Focus");
        
    	artifact = new ItemArtifact().setUnlocalizedName("artifact");
        GameRegistry.registerItem(artifact, "Artifact");
        
    	scrutinyNote = new ItemScrutinyNote().setUnlocalizedName("scrutinyNote");
        GameRegistry.registerItem(scrutinyNote, "scrutinyNote");
        
        registerTFItem(scepterTwilight, "Scepter of Twilight");
        registerTFItem(scepterLifeDrain, "Scepter of Life Draining");
        registerTFItem(scepterZombie, "Zombie Scepter");
        
        registerTFItem(spawnEgg, "Spawn");
    }

	private static void registerTFItem(Item item, String englishName) {
		GameRegistry.registerItem(item, item.getUnlocalizedName(), UnravelingMod.ID);
	}

}
