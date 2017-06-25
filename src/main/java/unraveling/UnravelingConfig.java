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
        boolean alternate = true;
        Item item = is.getItem();
        int meta = is.getItemDamage();
        if (alternate) {
            if (item == new ItemStack(Blocks.ender_chest, 1, 0).getItem()) {
                return  "FOCUS_ENDER_CHEST";
            }
        }
        // TODO: different recipes depending on. Using virtual research
        if (item == Items.ender_eye || item == ConfigItems.itemCompassStone) {
            return  "ENDERCOMPASS";
        }
        if (item == ConfigItems.itemSanityChecker) {
            return  "ASTRALSNARE";
        }
        if (item instanceof ItemArtifact) {
            if (meta == 2 || meta == 3) {
                return  "VOIDORE";
            }
            // this.clazz.isAssignableFrom(is.func_77973_b().getClass()
            return  "lost";
        }
        
        return null;
    }

}
