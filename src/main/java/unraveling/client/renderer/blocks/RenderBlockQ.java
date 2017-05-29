package unraveling.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
//import net.minecraft.client.renderer.renderManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import thaumcraft.client.renderers.models.ModelResearchTable;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.ItemRenderer;

import unraveling.block.BlockQuaesitum;
import unraveling.block.TFBlocks;
import unraveling.tileentity.TileQuaesitum;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.config.ConfigBlocks;

public class RenderBlockQ extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer  {
//public class RenderBlockQ implements ISimpleBlockRenderingHandler {
	
	final int renderID;

    //dummy Quaesitum for inventory rendering
    private TileQuaesitum dummy;
	public RenderBlockQ(int i) {
		this.renderID = i;
	}

    @Override
    public final void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        renderBlock(Tessellator.instance, tileEntity.getWorldObj(), x, y, z, tileEntity.getBlockType(), tileEntity, f, 0, RenderBlocks.getInstance(), true);
    }
    @Override
    public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return renderBlock(Tessellator.instance, world, x, y, z, block, world.getTileEntity(x, y, z), 0, modelId, renderer, false);
    }

    @Override
    public final boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }
    @Override
    public final boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }
    
    @Override
    public final void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        renderInventoryBlock(TFBlocks.quaesitum, 0, 0, RenderBlocks.getInstance());
        //renderBlock(Tessellator.instance, Minecraft.getMinecraft().theWorld, 0, 0, 0, TFBlocks.quaesitum, dummy, 0, 0, RenderBlocks.getInstance(), true);
    }    
    /*
    @Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        renderBlock(Tessellator.instance, Minecraft.getMinecraft().theWorld, 0, 0, 0, block, dummy, 0, 0, renderer, true);
    } */       
    
    protected boolean renderBlock(Tessellator tessellator, IBlockAccess world, double x, double y, double z, Block block, TileEntity tile, float f, int modelId, RenderBlocks renderer, boolean callFromInventory) {
        
        if (callFromInventory) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);        
        }
        renderer.clearOverrideBlockTexture();
        renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, (10.0F/16.0F), 1.0F);

        // render slab: top and bottom
        renderer.renderFaceYNeg(block, x, y + 6.0F/16.0F, z, BlockQuaesitum.slabBottom);
        renderer.renderFaceYPos(block, x, y, z, BlockQuaesitum.slabTop);

        // render slab: sides
        renderer.renderFaceXPos(block, x, y, z, BlockQuaesitum.slabSide);
        renderer.renderFaceXNeg(block, x, y, z, BlockQuaesitum.slabSide);
        renderer.renderFaceZNeg(block, x, y, z, BlockQuaesitum.slabSide);
        renderer.renderFaceZPos(block, x, y, z, BlockQuaesitum.slabSide);

        // render column
        renderer.renderFaceXPos(block, x - 6.0F/16.0F, y, z, BlockQuaesitum.pillar);
        renderer.renderFaceXNeg(block, x + 6.0F/16.0F, y, z, BlockQuaesitum.pillar);
        renderer.renderFaceZNeg(block, x, y, z + 6.0F/16.0F, BlockQuaesitum.pillar);
        renderer.renderFaceZPos(block, x, y, z - 6.0F/16.0F, BlockQuaesitum.pillar);

        // render bottom slab: top and bottom
        renderer.renderFaceYNeg(block, x, y, z, BlockQuaesitum.foundation);
        renderer.renderFaceYPos(block, x, y - 7.0F/16.0F, z, BlockQuaesitum.foundation);

        // render bottom slab: sides
        renderer.renderFaceXPos(block, x - 2.0F/16.0F, y, z, BlockQuaesitum.foundationSide);
        renderer.renderFaceXNeg(block, x + 2.0F/16.0F, y, z, BlockQuaesitum.foundationSide);
        renderer.renderFaceZNeg(block, x, y, z + 2.0F/16.0F, BlockQuaesitum.foundationSide);
        renderer.renderFaceZPos(block, x, y, z - 2.0F/16.0F, BlockQuaesitum.foundationSide);
        
        block.setBlockBoundsForItemRender();
        if (callFromInventory) {
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
        //GL11.glPopMatrix();
        //renderInkwell(tessellator);
        
        return true;
	}
    
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}
    public void renderInkwell(Tessellator tessellator) {
        /*
        GL11.glPushMatrix();
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        
        ModelRenderer Inkwell = new ModelRenderer((ModelBase)new ModelResearchTable(), 0, 44);
        Inkwell.func_78789_a(0.0f, 0.0f, 0.0f, 3, 2, 3);
        Inkwell.func_78793_a(-6.0f, -2.0f, 3.0f);
        Inkwell.func_78787_b(128, 64);
        Inkwell.field_78809_i = true;

        
        Inkwell.func_78785_a(0.0625f);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
        */
        GL11.glPushMatrix();
        GL11.glRotatef((float)-90.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        GL11.glRotatef((float)180.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTranslatef((float)-0.17f, (float)0.1f, (float)-0.15f);
        GL11.glRotatef((float)15.0f, (float)0.0f, (float)1.0f, (float)0.0f);
        IIcon icon = ((BlockTable)ConfigBlocks.blockTable).iconQuill;
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        // this.field_147501_a.field_147553_e.func_110577_a(TextureMap.locationItemsTexture);
        ItemRenderer.renderItemIn2D((Tessellator)tessellator, 
            icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 
            icon.getIconWidth(), icon.getIconHeight(), (float)0.025f);
        GL11.glPopMatrix();

    }
    
    @Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tessellator =  Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);        
        renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, (10.0F/16.0F), 1.0F);

        // render slab: top and bottom
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0F, 6.0F/16.0F, 0.0F, BlockQuaesitum.slabBottom);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0F, 0.0F, 0.0F, BlockQuaesitum.slabTop);
        tessellator.draw();



        // render slab: sides
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceXPos(block, 0.0F, 0.0F, 0.0F, BlockQuaesitum.slabSide);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceXNeg(block, 0.0F, 0.0F, 0.0F, BlockQuaesitum.slabSide);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceZNeg(block, 0.0F, 0.0F, 0.0F, BlockQuaesitum.slabSide);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceZPos(block, 0.0F, 0.0F, 0.0F, BlockQuaesitum.slabSide);
        tessellator.draw();

        // render column
        if (false) {
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceXPos(block, -6.0F/16.0F, 0.0F, 0.0F, BlockQuaesitum.pillar);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceXNeg(block, 6.0F/16.0F, 0.0F, 0.0F, BlockQuaesitum.pillar);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceZNeg(block, 0.0F, 0.0F, 6.0F/16.0F, BlockQuaesitum.pillar);
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceZPos(block, 0.0F, 0.0F, -6.0F/16.0F, BlockQuaesitum.pillar);
            tessellator.draw();
        }
        // render bottom slab: top and bottom
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0F, 0.0F, 0.0F, BlockQuaesitum.foundation);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0F, -7.0F/16.0F, 0.0F, BlockQuaesitum.foundation);
        tessellator.draw();

        // render bottom slab: sides
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceXPos(block, -2.0F/16.0F, 0.0F, 0.0F, BlockQuaesitum.foundationSide);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceXNeg(block, 2.0F/16.0F, 0.0F, 0.0F, BlockQuaesitum.foundationSide);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceZNeg(block, 0.0F, 0.0F, 2.0F/16.0F, BlockQuaesitum.foundationSide);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceZPos(block, 0.0F, 0.0F, -2.0F/16.0F, BlockQuaesitum.foundationSide);
        tessellator.draw();
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);

        block.setBlockBoundsForItemRender();
	}

}

