package unraveling.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
//import net.minecraft.client.renderer.renderManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import thaumcraft.client.renderers.models.ModelResearchTable;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.ItemRenderer;

import unraveling.block.BlockQuaesitum;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import thaumcraft.common.blocks.BlockTable;
import thaumcraft.common.config.ConfigBlocks;

public class RenderBlockQ implements ISimpleBlockRenderingHandler {
	
	final int renderID;

	public RenderBlockQ(int i) {
		this.renderID = i;
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
        /*
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
        */
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
        // TODO
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator =  Tessellator.instance;

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
        //renderInkwell();
        return true;
	}
    /*
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int par2, int par3, int par4,
			Block block, int modelId, RenderBlocks renderer) {
	        
        float f = 1.0F;
        int l = block.colorMultiplier(world, par2, par3, par4);
        float f1 = (float)(l >> 16 & 255) / 255.0F;
        float f2 = (float)(l >> 8 & 255) / 255.0F;
        float f3 = (float)(l & 255) / 255.0F;
        float f4;

        // WTF???
        if (EntityRenderer.anaglyphEnable)
        {
            float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
            f4 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
            f1 = f5;
            f2 = f4;
            f3 = f6;
        }
        double x = par2;
        double y = par3;
        double z = par4;
			
        double empty_width;
        
        // render pillar on sides
        empty_width = (6.0F/16.0F);
        IIcon icon1 = BlockDarkGen.pillarSide;
        renderer.renderFaceXPos(block, (double)(x - empty_width), y, z, icon1);
        renderer.renderFaceXNeg(block, (double)(x + empty_width), y, z, icon1);
        renderer.renderFaceZPos(block, x, y, (double)(z - empty_width), icon1);
        renderer.renderFaceZNeg(block, x, y, (double)(z + empty_width), icon1);

        // render gem: top and bottom
        double gem_width = (8.0F/16.0F);
        double gem_dist = (4.0F/16.0F);
        IIcon gemUp = BlockDarkGen.gemUp;
        renderer.renderFaceYPos(block, x, y, z, gemUp);
        renderer.renderFaceYNeg(block, x, (double)(y - gem_width + 1), z, gemUp);
        // render gem on sides
        IIcon gemSide = BlockDarkGen.gemSide;
        empty_width = (4.0F/16.0F);
        renderer.renderFaceXPos(block, (double)(x - empty_width), y, z, gemSide);
        renderer.renderFaceXNeg(block, (double)(x + empty_width), y, z, gemSide);
        renderer.renderFaceZNeg(block, x, y, (double)(z + empty_width), gemSide);
        renderer.renderFaceZPos(block, x, y, (double)(z - empty_width), gemSide);
        
        // render slab: top and bottom
        empty_width = (7.0F/16.0F);
        renderer.renderFaceYPos(block, x, (double)(y - empty_width), z, BlockDarkGen.slabTop);
        renderer.renderFaceYNeg(block, x, y, z, BlockDarkGen.slabBottom);
        // render slab: sides
        renderer.renderFaceXPos(block, x, y, z, BlockDarkGen.slabSide);
        renderer.renderFaceXNeg(block, x, y, z, BlockDarkGen.slabSide);
        renderer.renderFaceZPos(block, x, y, z, BlockDarkGen.slabSide);
        renderer.renderFaceZNeg(block, x, y, z, BlockDarkGen.slabSide);

        return true;
	}*/

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
        // TODO
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}
    public void renderInkwell() {
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
        Tessellator tessellator = Tessellator.instance;
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        // this.field_147501_a.field_147553_e.func_110577_a(TextureMap.locationItemsTexture);
        ItemRenderer.renderItemIn2D((Tessellator)tessellator, 
            icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV(), 
            icon.getIconWidth(), icon.getIconHeight(), (float)0.025f);
        GL11.glPopMatrix();

    }

}