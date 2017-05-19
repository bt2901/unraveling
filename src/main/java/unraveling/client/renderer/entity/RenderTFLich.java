package unraveling.client.renderer.entity;


import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import unraveling.UnravelingMod;
import unraveling.client.model.ModelTFLich;
import unraveling.entity.boss.EntityTFLich;



public class RenderTFLich extends RenderBiped {
	
	public static EntityTFLich entityLich = null;
	
    private static final ResourceLocation textureLocClothed = new ResourceLocation(UnravelingMod.MODEL_DIR + "twilightlich64.png");
    private static final ResourceLocation textureLocDamaged = new ResourceLocation(UnravelingMod.MODEL_DIR + "twilightlich64_orig.png");
    private static final ResourceLocation textureLocVeryDamaged = new ResourceLocation(UnravelingMod.MODEL_DIR + "twilightlich64_skull.png");


	public RenderTFLich(ModelBiped modelbiped, float f) {
		super(modelbiped, f);
		this.setRenderPassModel(new ModelTFLich(true));
	}
	
    protected int shouldRenderPass(EntityLivingBase entity, int i, float f)
    {
    	EntityTFLich lich = (EntityTFLich)entity;
    	if (i == 2) {
	        GL11.glEnable(3042 /*GL_BLEND*/);
//	        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        if (lich.isShadowClone()) {
	        	// clone alpha
	        	float shadow = 0.33f;
	            GL11.glColor4f(shadow, shadow, shadow, 0.8F);
	        	return 2;
	        }
	        else 
	        {
	        	if (lich.ticksExisted > 0)
	        	{
	        		BossStatus.setBossStatus(lich, false);
	        	}
	        	// shield alpha (shield texture already has alpha
	        	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0f);
	        	return 1;
	        }
	    	
	        
    	} else {
    		return 0;
    	}
    }

	/**
	 * Return our specific texture
	 */
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
    	EntityTFLich lich = (EntityTFLich)par1Entity;
        if (lich.getPhase() == 1){
            return textureLocClothed;
        }
        if (lich.getPhase() == 2){
            return textureLocDamaged;
        }
        return textureLocVeryDamaged;
    }
    
	@Override
	protected float getDeathMaxRotation(EntityLivingBase par1EntityLiving) {
		return 0F;
    }
	protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
		//if (p_77043_1_.isConverting()) {
		p_77043_3_ += (float) (Math.cos((double) p_77043_1_.ticksExisted * 3.25D) * Math.PI * 0.25D);
		//}

		super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
}

}
