package unraveling.client.renderer.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import unraveling.block.BlockDarkGen;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockDarkGen implements ISimpleBlockRenderingHandler {
	
	final int renderID;

	public RenderBlockDarkGen(int i) {
		this.renderID = i;
	}
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        // TODO
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderblocks) {
        // render slab: top and bottom
        // renderblocks.overrideBlockTexture = BlockDarkGen.slab;
        renderblocks.clearOverrideBlockTexture();
        //renderblocks.setRenderBounds(0.1875F, 0.0F, 0.1875F, 0.8125F, 0.875F, 0.8125F);
        renderblocks.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 7.0F/16.0F, 1.0F);
        renderblocks.renderStandardBlock(block, x, y, z);

        // render gem
        renderblocks.overrideBlockTexture = BlockDarkGen.gemDark;
        renderblocks.setRenderBounds(0.25F, 0.5F, 0.25F, 0.75F, 1.0F, 0.75F);
        renderblocks.renderStandardBlock(block, x, y, z);
        renderblocks.clearOverrideBlockTexture();
        // render pillar (?)
        // screw the pillar
                
        block.setBlockBoundsForItemRender();
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
		return false;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}
}
/*



	@Override
	public boolean renderWorldBlock(IBlockAccess world, int par2, int par3, int par4,
			Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, par2, par3, par4));
	        
	        float f = 1.0F;
	        int l = block.colorMultiplier(world, par2, par3, par4);
	        float f1 = (float)(l >> 16 & 255) / 255.0F;
	        float f2 = (float)(l >> 8 & 255) / 255.0F;
	        float f3 = (float)(l & 255) / 255.0F;
	        float f4;
	        float xMove = (2.0F/16.0F);
	        xMove = 0;
	        float y = par3;
	        float x = par2 + xMove;
	        float z = par4 - xMove;
	
	        if (EntityRenderer.anaglyphEnable)
	        {
	            float f5 = (f1 * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
	            f4 = (f1 * 30.0F + f2 * 70.0F) / 100.0F;
	            float f6 = (f1 * 30.0F + f3 * 70.0F) / 100.0F;
	            f1 = f5;
	            f2 = f4;
	            f3 = f6;
	        }
	
	        tessellator.setColorOpaque_F(f * f1, f * f2, f * f3);
		
        IIcon icon = block.getBlockTextureFromSide(2);
       f4 = (2.0F/16.0F);
       f4 =0;
		block.setBlockBounds(0.0F,0.0F,0.0F,1.0F,1.0F,1.0F);
        renderer.renderFaceXPos(block, (double)((float)x  - f4), (double)y, (double)z, icon);
        renderer.renderFaceXNeg(block, (double)((float)x  + f4), (double)y, (double)z, icon);
        renderer.renderFaceZNeg(block, (double)x, (double)y, (double)((float)z  + f4), icon);
        renderer.renderFaceZPos(block, (double)x, (double)y, (double)((float)z - f4), icon);
        f4 = (3.0F/16.0F);
        IIcon icon1 = BlockEverfullUrn.middleSide;
        renderer.renderFaceXPos(block, (double)((float)x  - f4), (double)y, (double)z, icon1);
        renderer.renderFaceXNeg(block, (double)((float)x  + f4), (double)y, (double)z, icon1);
        renderer.renderFaceZNeg(block, (double)x, (double)y, (double)((float)z  + f4), icon1);
        renderer.renderFaceZPos(block, (double)x, (double)y, (double)((float)z - f4), icon1);
        f4 = (2.0F/16.0F);
        IIcon icon2 = BlockEverfullUrn.topSide;
        renderer.renderFaceXPos(block, (double)((float)x  - f4), (double)y, (double)z, icon2);
        renderer.renderFaceXNeg(block, (double)((float)x  + f4), (double)y, (double)z, icon2);
        renderer.renderFaceZNeg(block, (double)x, (double)y, (double)((float)z  + f4), icon2);
        renderer.renderFaceZPos(block, (double)x, (double)y, (double)((float)z - f4), icon2);
        IIcon icon3 = BlockEverfullUrn.topTop;
       IIcon icon4 = BlockEverfullUrn.bottomTop;
        f4 = (7.0F/16.0F);
        renderer.renderFaceYPos(block, (double)x, (double)((float)y-f4), (double)z, icon4);
        renderer.renderFaceYPos(block, (double)x, (double)((float)y ), (double)z, icon3);
        f4 = (13.0F/16.0F);
        IIcon icon5 = BlockEverfullUrn.bottomBottom;
        IIcon icon6 = BlockEverfullUrn.topBottom;
        renderer.renderFaceYNeg(block, (double)x, (double)((float)y), (double)z, icon5);
        renderer.renderFaceYNeg(block, (double)x, (double)((float)y+f4), (double)z, icon6);
        

        return true;
	}



public static void DrawFaces(vl renderblocks, pb block, int i1, int i2, int i3, int i4, int i5, int i6, boolean solidtop) {
        adz tessellator = adz.a;
        GL11.glTranslatef((float)-0.5f, (float)-0.5f, (float)-0.5f);
        tessellator.b();
        tessellator.b(0.0f, -1.0f, 0.0f);
        renderblocks.a(block, 0.0, 0.0, 0.0, i1);
        tessellator.a();
        if (solidtop) {
            GL11.glDisable((int)3008);
        }
        tessellator.b();
        tessellator.b(0.0f, 1.0f, 0.0f);
        renderblocks.b(block, 0.0, 0.0, 0.0, i2);
        tessellator.a();
        if (solidtop) {
            GL11.glEnable((int)3008);
        }
        tessellator.b();
        tessellator.b(0.0f, 0.0f, 1.0f);
        renderblocks.e(block, 0.0, 0.0, 0.0, i3);
        tessellator.a();
        tessellator.b();
        tessellator.b(0.0f, 0.0f, -1.0f);
        renderblocks.f(block, 0.0, 0.0, 0.0, i4);
        tessellator.a();
        tessellator.b();
        tessellator.b(1.0f, 0.0f, 0.0f);
        renderblocks.c(block, 0.0, 0.0, 0.0, i5);
        tessellator.a();
        tessellator.b();
        tessellator.b(-1.0f, 0.0f, 0.0f);
        renderblocks.d(block, 0.0, 0.0, 0.0, i6);
        tessellator.a();
        GL11.glTranslatef((float)0.5f, (float)0.5f, (float)0.5f);
    }

    
    
public static boolean renderBlockDarkGen(xd w, vl rb, int i, int j, int k, pb block, int md2, boolean inv) {
        float t1 = 0.0625f;
        float t4 = 0.25f;
        float t2 = 0.125f;
        if (block.c() == 0 || inv) {
            block.a(0.0f, 0.0f, 0.0f, 1.0f, 0.5f - t1, 1.0f);
            if (inv) {
                ThaumCraftRenderer.DrawFaces(rb, block, 105, 104, 108, 108, 108, 108, true);
            } else {
                rb.o(block, i, j, k);
            }
            block.a(0.5f - t2, 0.5f - t2, 0.5f - t2, 0.5f + t2, 0.5f, 0.5f + t2);
            if (inv) {
                ThaumCraftRenderer.DrawFaces(rb, block, 108, true);
            } else {
                rb.o(block, i, j, k);
            }
            block.a(t4, 0.5f, t4, 1.0f - t4, 1.0f, 1.0f - t4);
            if (inv) {
                ThaumCraftRenderer.DrawFaces(rb, block, 109, 109, 108, 108, 108, 108, true);
            } else {
                rb.f(block, (double)i, (double)j, (double)k, 108);
                rb.e(block, (double)i, (double)j, (double)k, 108);
                rb.d(block, (double)i, (double)j, (double)k, 108);
                rb.c(block, (double)i, (double)j, (double)k, 108);
                rb.b(block, (double)i, (double)j, (double)k, 109);
                rb.a(block, (double)i, (double)j, (double)k, 109);
            }
        }
        rb.d = -1;
        block.a(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        return true;
    }



*/