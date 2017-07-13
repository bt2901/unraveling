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

import unraveling.item.ItemStackSerializer;
import unraveling.item.DiscoverySerializer;
import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigItems;
import unraveling.mechanics.ExaminationData.Discovery;
import cpw.mods.fml.common.registry.GameRegistry;
import unraveling.item.UItems;


import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import cpw.mods.fml.common.registry.GameData;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import net.minecraftforge.common.config.Configuration;



public class ConfigHandler {
    
    public static List<Discovery> research;
    private static Gson gson;
    private static File directory;
    private static Configuration cfg;
    public static void init(File cfgDirectory, Configuration config) {
        directory = cfgDirectory;
        cfg = config;
    }
    public static void doYourThing() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
        gsonBuilder.registerTypeAdapter(Discovery.class, new DiscoverySerializer());
        gson = gsonBuilder.setPrettyPrinting().create();
        research = readConfiguration(directory);
        System.out.println(research);
        // List<Discovery> research = readConfiguration(directory);
    }

    private static List<Discovery> readConfiguration(File configurationDirectory) {

        File cfgDirectory = new File(configurationDirectory, UnravelingMod.ID.toLowerCase());
        if (!cfgDirectory.exists()) {
            if (!cfgDirectory.mkdirs()) {
                //Reference.logger.error("Could not create directory!");
            }
        }
        File scrutinyData = new File(cfgDirectory, "scrutiny.json");
        List<Discovery> data = tryToReadFile(scrutinyData);
        return data; 
    }
    

    public static Item getItem(String mod, String item) {
        Item target = GameRegistry.findItem(mod, item);
        // if(target == null)
        //    throw new ItemNotFoundException(mod, item);
        return target;
    }

    private static List<Discovery> tryToReadFile(File file) {
        BufferedReader buffer = null;
        try {
            return readFile(file, buffer);
        } catch (IOException e) {
            // Reference.logger.error("IO failure!", e);
        } catch (JsonSyntaxException e) {
            // Reference.logger.error(String.format("Malformed JSON in %s!", file.getName()), e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    // Reference.logger.error("IO failure!", e);
                }
            }
        }
        return null;
    }
    private static void writeDefaults(File file) throws IOException {
        if (!file.createNewFile()) {
            throw new IOException();
        }
        List defaults = Arrays.asList(
            new Discovery(new ItemStack(Blocks.ender_chest, 1, 0), "FOCUS_ENDER_CHEST", true), 
            new Discovery(new ItemStack(Items.ender_eye, 1, 0), "ASTRALSNARE"),
            new Discovery(new ItemStack(ConfigItems.itemCompassStone, 1, 0), "ASTRALSNARE"),
            new Discovery(new ItemStack(ConfigItems.itemSanityChecker, 1, 0), "ASTRALSNARE"),
            new Discovery(new ItemStack(UItems.artifact, 1, 2), "VOIDGEN"),
            new Discovery(new ItemStack(UItems.artifact, 1, 3), "VOIDGEN"),
            new Discovery(new ItemStack(UItems.artifact, 1, 1), "lost"),
            new Discovery(new ItemStack(UItems.voidCluster), "VOIDPURE", true)
        );
        String json = gson.toJson(defaults);
        FileUtils.writeStringToFile(file, json, "UTF8");
    }
    
    private static List<Discovery> readFile(File file, BufferedReader buffer) throws IOException, JsonSyntaxException {
            if (file.getParentFile() != null) {
                if (!file.getParentFile().mkdirs()) {
                    // Reference.logger.debug("Could not create directory!");
                }
            }

            if (!file.exists()) {
                writeDefaults(file);
            }

            if (file.canRead()) {
                FileReader fileInputStream = new FileReader(file);
                buffer = new BufferedReader(fileInputStream);

                String str = "";

                String line;
                while ((line = buffer.readLine()) != null) {
                    str += line + "\n";
                }

                return gson.fromJson(str, new TypeToken<ArrayList<Discovery>>() {}.getType());
            }
            return null;

    }
}
