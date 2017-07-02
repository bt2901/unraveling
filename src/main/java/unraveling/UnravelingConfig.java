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
        if (stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 16)) || stack.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 17))) { // void seed / void ingot
            return 3;
        }
        // ender lily: 4
        // element of darkness: 5

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
/*
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
                        Item kamiResource = getItem("ThaumicTinkerer", "kamiResource");
                    list = new AspectList().add(DarkAspects.NETHER, 2).add(Aspect.MAGIC, 1).add(Aspect.CRYSTAL, 1);
                    ThaumcraftApi.registerObjectTag(new ItemStack(kamiResource, 1, 6), list);
                    list = new AspectList().add(Aspect.ELDRITCH, 2).add(Aspect.MAGIC, 1).add(Aspect.CRYSTAL, 1);
                    ThaumcraftApi.registerObjectTag(new ItemStack(kamiResource, 1, 7), list);

*/
}
