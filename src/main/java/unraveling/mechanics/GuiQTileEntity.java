package unraveling.mechanics;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ScanManager;

import unraveling.mechanics.ContainerQ;
import unraveling.UnravelingMod;
import unraveling.tileentity.TileQuaesitum;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuiQTileEntity extends GuiContainer {

    private static final ResourceLocation gui = new ResourceLocation(UnravelingMod.ID + ":textures/gui/gui_q.png");
    private static Random rand = new Random();

    int x, y;
    TileQuaesitum quaesitum;
    Aspect aspectHovered = null;

    public GuiQTileEntity(TileQuaesitum quaesitum, InventoryPlayer inv) {
        super(new ContainerQ(quaesitum, inv));
        this.quaesitum = quaesitum;
    }

    @Override
    public void initGui() {
        super.initGui();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
    }



    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mx, int my) {
        aspectHovered = null;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(gui);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        //GL11.glEnable(3042);

        ItemStack stack = quaesitum.getStackInSlot(0);
        if (this.quaesitum.researchtime > 0) {
            //drawTexturedModalRect(x + 90, y + 44, 206, 0, 50, 10);
            int i1 = this.quaesitum.getResearchTimeScaled(50);
            this.drawTexturedModalRect(x + 90, y + 43, 206, 0, i1, 10);
            // this.drawTexturedModalRect(k + 93, l + 15 + 46 - i1, 176, 46 - i1, 9, i1);
            this.drawTexturedModalRect(x + 72, y + 40, 242, 31, 15, 15);
        }
        //GL11.glDisable(3042);
    }

    protected void mouseClicked(int mx, int my, int par3) {
        super.mouseClicked(mx, my, par3);
        int buttonPosX = mx - (x + 70); // 95 - (? + 89) = 6
        int buttonPosY = my - (y + 39);
        if (buttonPosX >= 0 && buttonPosY >= 0 && buttonPosX < 16 && buttonPosY < 16) {
            UnravelingMod.netHandler.sendToServer(new PacketQResearch(quaesitum));
            // this.playButtonClick();
            return;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        super.drawGuiContainerForegroundLayer(mx, my);
    }
}