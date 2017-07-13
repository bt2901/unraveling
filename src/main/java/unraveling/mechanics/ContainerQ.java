package unraveling.mechanics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.common.container.SlotLimitedHasAspects;
import thaumcraft.common.container.SlotLimitedByClass;
import thaumcraft.common.container.SlotOutput;
import thaumcraft.common.container.SlotLimitedByItemstack;
import unraveling.tileentity.TileQuaesitum;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.init.Items;
import net.minecraft.inventory.ICrafting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


import thaumcraft.api.IScribeTools;

public class ContainerQ extends Container {

    public TileQuaesitum analyzer;
    Slot slotInput;
    Slot slotPaper;
    Slot slotScribe;
    Slot slotNote;
    InventoryPlayer playerInv;
    public int lastResearchTime = 0;
    public int lastProgress = 0;

    public static int INPUT = 0;
    public static int PAPER_HERE = 1;
    public static int SCRIBE_HERE = 2;
    public static int OUTPUT = 3;

    public ContainerQ(TileQuaesitum analyzer, InventoryPlayer playerInv) {
        this.playerInv = playerInv;
        
        this.analyzer = analyzer;

        //addSlotToContainer(slotInput = new SlotLimitedHasAspects(analyzer, 0, 22, 30));
        addSlotToContainer(slotInput = new Slot(analyzer, INPUT, 22, 30));

        addSlotToContainer(slotPaper = new SlotLimitedByItemstack(new ItemStack(Items.paper), analyzer, PAPER_HERE, 124, 21));
        addSlotToContainer(slotScribe = new SlotLimitedByClass(IScribeTools.class, analyzer, SCRIBE_HERE, 91, 21));
        addSlotToContainer(slotNote = new SlotOutput(analyzer, OUTPUT, 156, 31));

        initPlayerInv();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return analyzer.isUseableByPlayer(entityplayer);
    }
    /*
    @Override
    public void onCraftGuiOpened(ICrafting par1ICrafting) {
        super.onCraftGuiOpened(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate((Container)this, 0, this.analyzer.researchtime);
    }*/
    
    @Override
    public void addCraftingToCrafters(ICrafting par1ICrafting) {
        super.addCraftingToCrafters(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 0, this.analyzer.researchtime);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.analyzer.progress);
    }

    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (int i = 0; i < this.crafters.size(); ++i) {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);
            if (this.lastResearchTime != this.analyzer.researchtime) {
                icrafting.sendProgressBarUpdate((Container)this, 0, this.analyzer.researchtime);
            }
            if (this.lastProgress != this.analyzer.progress) {
                icrafting.sendProgressBarUpdate((Container)this, 1, this.analyzer.progress);
            }
        }
        this.lastResearchTime = this.analyzer.researchtime;
        this.lastProgress = this.analyzer.progress;
    }
    @SideOnly(value=Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            this.analyzer.researchtime = par2;
        }
        if (par1 == 1) {
            this.analyzer.progress = par2;
        }
    }

    /**
    * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
    */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (par2 <= OUTPUT && par2 >= INPUT) {
                // try to place in player inventory
                if (!this.mergeItemStack(itemstack1, OUTPUT+1, OUTPUT+36+1, true)) {
                    return null;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            // itemstack is in player inventory, try to place in appropriate slot
            else if (par2 > OUTPUT) {
                for (int i = 2; i >= 0; --i) {
                    Slot shiftedInSlot = (Slot)inventorySlots.get(i);
                    if(shiftedInSlot.isItemValid(itemstack1)) {
                        // scribing tools has max stack size of 1
                        if (i == SCRIBE_HERE && shiftedInSlot.getHasStack()) {
                            continue;
                        }
                        if (!mergeItemStack(itemstack1, i, i + 1, false)) {
                            return null;
                        }
                        
                    }
                }
            }
            
            if(itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
            
            if(itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }
        return itemstack;
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


}
