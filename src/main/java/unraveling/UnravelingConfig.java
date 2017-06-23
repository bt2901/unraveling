package unraveling;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import unraveling.mechanics.voidgen.TileDarkGen;
import unraveling.tileentity.TileQuaesitum;
import unraveling.mechanics.ContainerQ;
import unraveling.mechanics.GuiQTileEntity;
import unraveling.mechanics.voidgen.GuiDarkGen;
import unraveling.mechanics.voidgen.ContainerDarkGen;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigItems;

import unraveling.item.ItemArtifact;

public class UnravelingConfig {
    
    public static float catalystDestroyChance = 0.25F;

    public static int maxVisReserve = 8;
    public static int maxEssentiaReserve = 16;
    public static int baseVoidProductionCost = 11;
    
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
        if (stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 16)) || stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 17)))        { // void seed / void ingot
            return 3;
        }
        // ender lily: 4
        // element of darkness: 5

        return 0;
    }
    public static String RelatedResearch(ItemStack is) {
        // TODO: config
        boolean alternate = false;
        Item item = is.getItem();
        if (alternate) {
            // if (item == )
        }
        if (item == Items.ender_eye) {
            return "ENDERCOMPASS";
        }
        if (item == ConfigItems.itemSanityChecker) {
            return "ASTRALSNARE";
        }
        if (item instanceof ItemArtifact) {
            // this.clazz.isAssignableFrom(is.func_77973_b().getClass()
            return "lost";
        }
        // http://takahikokawasaki.github.io/minecraft-resources/javadoc/forge/1.7.10-10.13.2.1291/net/minecraft/init/Items.html
        
        return null;
    }

}
