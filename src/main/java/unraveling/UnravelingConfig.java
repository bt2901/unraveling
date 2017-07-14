package unraveling;


import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import unraveling.mechanics.voidgen.TileDarkGen;
import unraveling.tileentity.TileQuaesitum;
import unraveling.mechanics.ContainerQ;
import unraveling.mechanics.GuiQTileEntity;
import unraveling.mechanics.voidgen.GuiDarkGen;
import unraveling.mechanics.voidgen.ContainerDarkGen;
import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigItems;
import unraveling.mechanics.ExaminationData.Discovery;
import cpw.mods.fml.common.registry.GameRegistry;
import unraveling.item.ItemArtifact;


import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import cpw.mods.fml.common.registry.GameData;

import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;


public class UnravelingConfig {
    
    public static float catalystDestroyChance = 0.25F;
    public static boolean debug = true;

    public static int maxVisReserve = 8;
    public static int maxEssentiaReserve = 16;
    public static int baseVoidProductionCost = 11;

    public static Item getItem(String mod, String item) {
        Item target = GameRegistry.findItem(mod, item);
        // if(target == null)
        //    throw new ItemNotFoundException(mod, item);
        return target;
    }
    public static Discovery RelatedResearch(ItemStack is) {
        
        System.out.println(GameRegistry.findUniqueIdentifierFor(is.getItem()));
        // TODO: HashMap or something. Not sure what to do if there are several researches to a single item
        for (Discovery disc : ConfigHandler.research) {
            if (is.isItemEqual(disc.item)) {
                return disc;
            }
        }
        
        return null;
    }
    
    
    
    public static int getCatalystPower(ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        Item item = stack.getItem();
        if (stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 0))) { // alumentum
            return 1;
        }
        if (item == Items.ender_pearl || item == Items.ender_eye) {
            return 2;
        }
        if (stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 16)) || stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 17))) { // void seed / void ingot
            return 3;
        }
        try {
            if (Loader.isModLoaded("gadomancy")) {
                Item elementOfDarkness = getItem("gadomancy", "itemElement");
                if (stack.isItemEqual(new ItemStack(elementOfDarkness, 1, 0))) { // element of darkness
                    return 5;
                }
            }
            if (Loader.isModLoaded("ExtraUtilities")) {
                Item ender_lily = getItem("ExtraUtilities", "plant/ender_lilly");
                if (stack.isItemEqual(new ItemStack(ender_lily, 1, 0))) { // ender lily
                    return 4;
                }
            }
        }  catch(IllegalArgumentException e) {
            return 0;
        }
                    
        return 0;
    }

}
