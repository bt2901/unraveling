package unraveling.mechanics;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import unraveling.UnravelingConfig;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class SlotLimitedResearchable
extends Slot {
    public SlotLimitedResearchable(IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
    }

    public boolean isItemValid(ItemStack stack) {
        AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
        if ((al = ThaumcraftCraftingManager.getBonusTags(stack, al)) != null && al.size() > 0) {
            return true;
        }
        return (UnravelingConfig.RelatedResearch(stack) != null);
    }
}
