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
import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.init.Blocks;
import thaumcraft.common.config.ConfigItems;
import unraveling.mechanics.ExaminationData.Discovery;
import cpw.mods.fml.common.registry.GameRegistry;
import unraveling.item.ItemArtifact;

public class UnravelingConfig {
    
    public static float catalystDestroyChance = 0.25F;
    public static boolean debug = true;

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
                Item ender_lily = getItem("ExtraUtilities", "ItemBlockEnderLily");
                if (stack.isItemEqual(new ItemStack(ender_lily, 1, 0))) { // ender lily
                    return 4;
                }
            }
        } catch(ItemNotFoundException e) {
            return 0;
        }
                    
        return 0;
    }
    public static Discovery RelatedResearch(ItemStack is) {
        // TODO: config
        boolean alterRecipes = true;
        Item item = is.getItem();
        int meta = is.getItemDamage();
        if (alterRecipes) {
            if (Loader.isModLoaded("ThaumicTinkerer")) {
                if (item == new ItemStack(Blocks.ender_chest, 1, 0).getItem()) {
                    return new Discovery(is, "FOCUS_ENDER_CHEST", true);
                }
            }
        }
        // TODO: different recipes depending on. Using virtual research
        if (item == Items.ender_eye || item == ConfigItems.itemCompassStone) {
            return new Discovery(is, "ASTRALSNARE");
        }
        if (item == ConfigItems.itemSanityChecker) {
            return new Discovery(is, "ASTRALSNARE");
        }
        if (item instanceof ItemArtifact) {
            if (meta == 2 || meta == 3) {
                return new Discovery(is, "VOIDORE");
            }
            // this.clazz.isAssignableFrom(is.func_77973_b().getClass()
            return new Discovery(is, "lost");
        }
        
        return null;
    }
    public static Item getItem(String mod, String item) throws ItemNotFoundException {
        Item target = GameRegistry.findItem(mod, item);
        if(target == null)
            throw new ItemNotFoundException(mod, item);
        return target;
    }

    public static class ItemNotFoundException extends Exception {
        public ItemNotFoundException(String mod, String item){
            super("Unable to find item " + item + " in mod " + mod + "! Are you using the correct version of the mod?");
        }
    }
}
