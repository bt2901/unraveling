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
    public static Item voidCluster;

    public static Item voidBook;
    // public static Item thaumBook;

    public static Item scrutinyNote;
    public static Item spawnEgg;
    
    public static Item scepterTwilight;
    public static Item scepterLifeDrain;
    public static Item scepterZombie;
    public static Item wandPacification;
    
    public static void registerItems()
    {
    
        ender_compass = new ItemCompassStone().setUnlocalizedName("enderCompass");
        GameRegistry.registerItem(ender_compass, "ender_compass");

        voidCluster = new ItemVoidCluster();
        GameRegistry.registerItem(voidCluster, "voidCluster");

        // thaumBook = new ItemThaumiumBook().setUnlocalizedName("itemThaumiumBook");
        // GameRegistry.registerItem(thaumBook, "thaumium_book");

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
                
    }

}
