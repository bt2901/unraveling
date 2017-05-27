package unraveling;

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


    public ContainerQ(TileQuaesitum analyzer, InventoryPlayer playerInv) {
        this.playerInv = playerInv;
        
        this.analyzer = analyzer;

        addSlotToContainer(slotInput = new SlotLimitedHasAspects(analyzer, 0, 22, 30));
        //addSlotToContainer(slotPaper = new SlotLimitedByClass(Items.paper.class, analyzer, 1, 124, 21));
        //addSlotToContainer(slotPaper = new Slot(analyzer, 1, 124, 21));
        addSlotToContainer(slotPaper = new SlotLimitedByItemstack(new ItemStack(Items.paper), analyzer, 1, 124, 21));
        addSlotToContainer(slotScribe = new SlotLimitedByClass(IScribeTools.class, analyzer, 2, 91, 21));
        addSlotToContainer(slotNote = new SlotOutput(analyzer, 3, 156, 31));

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


}
