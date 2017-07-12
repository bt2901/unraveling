package unraveling.mechanics.voidgen;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.init.Items;
import net.minecraft.inventory.ICrafting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


import thaumcraft.api.IScribeTools;

public class ContainerDarkGen extends Container {

    public TileDarkGen gen;

    Slot slotInput;
    InventoryPlayer playerInv;


    public ContainerDarkGen(TileDarkGen gen, InventoryPlayer playerInv) {
        this.playerInv = playerInv;
        
        this.gen = gen;

        addSlotToContainer(slotInput = new SlotLimitedCatalyst(gen, 0, 86, 16));

        initPlayerInv();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return gen.isUseableByPlayer(entityplayer);
    }
    

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack var3 = null;
        Slot var4 = (Slot) inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack()) {
            ItemStack var5 = var4.getStack();

            if (par2 == 0 || var5 != null && var4.isItemValid(var5)) {
                var3 = var5.copy();

                if (par2 < 1) {
                    if (!mergeItemStack(var5, 1, 37, false))
                        return null;
                } else if (!mergeItemStack(var5, 0, 1, false))
                    return null;

                if (var5.stackSize == 0)
                    var4.putStack(null);
                else
                    var4.onSlotChanged();

                if (var5.stackSize == var3.stackSize)
                    return null;

                var4.onPickupFromSlot(par1EntityPlayer, var5);
            }
        }

        return var3;
    }
    public void initPlayerInv() {
        int ys = getInvYStart();
        int xs = getInvXStart();

        for (int x = 0; x < 3; ++x)
            for (int y = 0; y < 9; ++y)
                addSlotToContainer(new Slot(playerInv, y + x * 9 + 9, xs + y * 18, ys + x * 18));

        for (int x = 0; x < 9; ++x)
            addSlotToContainer(new Slot(playerInv, x, xs + x * 18, ys + 58));
    }

    public int getInvYStart() {
        return 84;
    }

    public int getInvXStart() {
        return 8;
    }
    



        /*
    @Override
    public void addCraftingToCrafters(ICrafting par1ICrafting) {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, X);
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            if (this.lastPower != X) {
                icrafting.sendProgressBarUpdate((Container)this, 0, X);
            }
        }
        this.lastPower = X;
    }
    @SideOnly(value=Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            this.gen.power = par2;
        }
    }
    */
}
