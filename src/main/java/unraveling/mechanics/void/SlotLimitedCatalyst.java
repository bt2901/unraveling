package unraveling.mechanics.voidgen;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLimitedCatalyst
extends Slot {
    public SlotLimitedCatalyst(IInventory par2IInventory, int par3, int par4, int par5) {
        super(par2IInventory, par3, par4, par5);
    }

    public boolean isItemValid(ItemStack stack) {
        return (VoidAggregationHandler.getCatalystPower(stack) > 0);
    }
}
