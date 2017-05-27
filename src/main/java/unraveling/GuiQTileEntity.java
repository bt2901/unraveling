package unraveling;

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

import unraveling.ContainerQ;
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
            this.drawTexturedModalRect(x + 90, y + 45, 206, 0, i1, 10);
            // this.drawTexturedModalRect(k + 93, l + 15 + 46 - i1, 176, 46 - i1, 9, i1);
            this.drawTexturedModalRect(x + 71, y + 40, 242, 31, 15, 15);
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
        /*
        if (aspectHovered != null)
            ClientHelper.renderTooltip(mx - x, my - y, Arrays.asList(EnumChatFormatting.AQUA + aspectHovered.getName(), EnumChatFormatting.GRAY + aspectHovered.getLocalizedDescription()));
        */
        super.drawGuiContainerForegroundLayer(mx, my);
    }

}
/*
package thaumcraft.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.tiles.TileSpa;

@SideOnly(value=Side.CLIENT)
public class GuiSpa
extends GuiContainer {
    private TileSpa spa;
    private float xSize_lo;
    private float ySize_lo;

    public GuiSpa(InventoryPlayer par1InventoryPlayer, TileSpa teSpa) {
        super((Container)new ContainerSpa(par1InventoryPlayer, teSpa));
        this.spa = teSpa;
    }

    public void func_73863_a(int par1, int par2, float par3) {
        ArrayList<String> list;
        super.func_73863_a(par1, par2, par3);
        this.xSize_lo = par1;
        this.ySize_lo = par2;
        int baseX = this.field_147003_i;
        int baseY = this.field_147009_r;
        int mposx = par1 - (baseX + 104);
        int mposy = par2 - (baseY + 10);
        if (mposx >= 0 && mposy >= 0 && mposx < 10 && mposy < 55) {
            list = new ArrayList<String>();
            FluidStack fluid = this.spa.tank.getFluid();
            if (fluid != null) {
                list.add(fluid.getFluid().getLocalizedName(fluid));
                list.add("" + fluid.amount + " mb");
                this.drawHoveringText(list, par1, par2, this.field_146289_q);
            }
        }
        mposx = par1 - (baseX + 88);
        mposy = par2 - (baseY + 34);
        if (mposx >= 0 && mposy >= 0 && mposx < 10 && mposy < 10) {
            list = new ArrayList();
            if (this.spa.getMix()) {
                list.add(StatCollector.func_74838_a((String)"text.spa.mix.true"));
            } else {
                list.add(StatCollector.func_74838_a((String)"text.spa.mix.false"));
            }
            this.drawHoveringText(list, par1, par2, this.field_146289_q);
        }
    }


    public static void renderFluid(IIcon icon) {
        Minecraft.func_71410_x().field_71446_o.func_110577_a(TextureMap.field_110575_b);
        Tessellator tessellator = Tessellator.field_78398_a;
        float f1 = icon.func_94212_f();
        float f2 = icon.func_94206_g();
        float f3 = icon.func_94209_e();
        float f4 = icon.func_94210_h();
        GL11.glScalef((float)8.0f, (float)8.0f, (float)8.0f);
        for (int a = 0; a < 6; ++a) {
            tessellator.func_78382_b();
            tessellator.func_78369_a(1.0f, 1.0f, 1.0f, 1.0f);
            tessellator.func_78375_b(0.0f, 0.0f, 1.0f);
            tessellator.func_78374_a(0.0, (double)(1 + a), 0.0, (double)f1, (double)f4);
            tessellator.func_78374_a(1.0, (double)(1 + a), 0.0, (double)f3, (double)f4);
            tessellator.func_78374_a(1.0, (double)(0 + a), 0.0, (double)f3, (double)f2);
            tessellator.func_78374_a(0.0, (double)(0 + a), 0.0, (double)f1, (double)f2);
            tessellator.func_78381_a();
        }
    }
mouseClicked
    protected void func_73864_a(int mx, int my, int par3) {
        super.func_73864_a(mx, my, par3);
        int gx = (this.field_146294_l - this.field_146999_f) / 2;
        int gy = (this.field_146295_m - this.field_147000_g) / 2;
        int var7 = mx - (gx + 89);
        int var8 = my - (gy + 35);
        if (var7 >= 0 && var8 >= 0 && var7 < 8 && var8 < 8) {
            this.field_146297_k.field_71442_b.func_78756_a(this.field_147002_h.field_75152_c, 1);
            this.playButtonClick();
            return;
        }
    }

    private void playButtonClick() {
        this.field_146297_k.field_71451_h.field_70170_p.func_72980_b(this.field_146297_k.field_71451_h.field_70165_t, this.field_146297_k.field_71451_h.field_70163_u, this.field_146297_k.field_71451_h.field_70161_v, "thaumcraft:cameraclack", 0.4f, 1.0f, false);
    }
}


*/
