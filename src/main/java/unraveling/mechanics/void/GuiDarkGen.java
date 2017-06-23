package unraveling.mechanics.voidgen;

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

import unraveling.UnravelingConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GuiDarkGen extends GuiContainer {

    private static final ResourceLocation gui = new ResourceLocation(UnravelingMod.ID + ":textures/gui/gui_dg.png");

    int x, y;
    TileDarkGen gen;

    public GuiDarkGen(TileDarkGen gen, InventoryPlayer inv) {
        super(new ContainerDarkGen(gen, inv));
        this.gen = gen;
    }

    @Override
    public void initGui() {
        super.initGui();
        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
    }



    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mx, int my) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(gui);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        //GL11.glEnable(3042);

        ItemStack stack = gen.getStackInSlot(0);
        int power = UnravelingConfig.getCatalystPower(stack);
        this.drawTexturedModalRect(x + 86, y + 40, 176, power * 16 * 4, 16, 16);
        //GL11.glDisable(3042);
    }
    protected void mouseClicked(int mx, int my, int par3) {
        super.mouseClicked(mx, my, par3);
        int buttonPosX = mx - (x + 86);
        int buttonPosY = my - (y + 40);
        if (buttonPosX >= 0 && buttonPosY >= 0 && buttonPosX < 16 && buttonPosY < 16) {
            Aspect drain = Aspect.DARKNESS; // default
            if (gen.drainSelected == Aspect.DARKNESS) {
                drain = Aspect.VOID;
            }
            gen.setSuction(drain, 1);
            UnravelingMod.netHandler.sendToServer(new PacketDrainSwitch(gen));
            // this.playButtonClick();
            return;
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mx, int my) {
        super.drawGuiContainerForegroundLayer(mx, my);
    }
}
