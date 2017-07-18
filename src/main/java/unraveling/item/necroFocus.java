package unraveling.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import unraveling.UnravelingMod;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import cpw.mods.fml.common.FMLLog;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;

import java.util.List;

public class necroFocus extends ItemFocusBasic {

    private IIcon icon;
    private IIcon ornament;
    AspectList visCost = (new AspectList()).add(Aspect.ENTROPY, 300);
    AspectList fireCost = (new AspectList()).add(Aspect.ENTROPY, 300).add(Aspect.FIRE, 100);
    AspectList switchCost = (new AspectList()).add(Aspect.ENTROPY, 300).add(Aspect.ORDER, 100);
    //FocusUpgradeType hellfire = ForbiddenItems.getUpgrade(Config.hellfireUpgradeID, new ResourceLocation("forbidden", "textures/misc/hellfire.png"), "forbidden.upgrade.hellfire.name", "forbidden.upgrade.hellfire.text", (new AspectList()).add(DarkAspects.NETHER, 1));
    //FocusUpgradeType pandemonium = ForbiddenItems.getUpgrade(Config.pandemoniumUpgradeID, new ResourceLocation("forbidden", "textures/misc/pandemonium.png"), "forbidden.upgrade.pandemonium.name", "forbidden.upgrade.pandemonium.text", (new AspectList()).add(Aspect.DARKNESS, 1));

    public necroFocus(){
        super();
    }
	/**
	 * What block is the player pointing the wand at?
	 * 
	 * This very similar to player.rayTrace, but that method is not available on the server.
	 * 
	 * @return
	 */
	private MovingObjectPosition getPlayerPointVec(World worldObj, EntityPlayer player, float range) {
        Vec3 position = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 look = player.getLook(1.0F);
        Vec3 dest = position.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
        return worldObj.rayTraceBlocks(position, dest);
	}

    @Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer player, MovingObjectPosition wut) {
            // Get the wand
            ItemWandCasting wand = (ItemWandCasting)itemstack.getItem();
            // Ensure there is enough vis for the cast
            if( !wand.consumeAllVis( itemstack, player, getVisCost(itemstack), false, false ) ) {
                // Not enough vis
                return null;
            }
            // TODO: check for bones
            //if( player ) {
                // thumcraft print 
                // return null;
            // }
            // what block is the player pointing at?
            MovingObjectPosition mop = getPlayerPointVec(world, player, 20.0F);
            if (mop != null) {
                // make a zombie there
                EntityGolemBase zombie = new EntityGolemBase(world, EnumGolemType.FLESH, false);
                zombie.setPositionAndRotation(mop.hitVec.xCoord, mop.hitVec.yCoord, mop.hitVec.zCoord, 1.0F, 1.0F);  /// NPE here needs to be fixed
                // zombie.setTamed(true);
                try {
						//zombie.func_152115_b(player.getUniqueID().toString());
						zombie.setOwner(player.getCommandSenderName());
                } catch (NoSuchMethodError ex) {
						// ignore?
						FMLLog.warning("[unraveling] Could not determine player name for loyal zombie, ignoring error.");
                }
                // TODO: SFX
                // TODO: effects
                if (!world.isRemote) {
					zombie.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 1200, 1));
					world.spawnEntityInWorld(zombie);
                    world.playSoundAtEntity(zombie, UnravelingMod.ID + ":random.necromancy", 1.0F, 1.0F);
                    wand.consumeAllVis(itemstack, player, getVisCost(itemstack), true, false);
                    player.swingItem();
                } else {
                    for (int i = 0; i < 8; i++) {
                        double dx = (world.rand.nextDouble() - 0.5D) * (double)zombie.width;
                        double dy = world.rand.nextDouble() * (double)zombie.height - 0.25D;
                        double dz = (world.rand.nextDouble() - 0.5D) * (double)zombie.width;
                        //world.spawnParticle("hugesmoke", 
                        //    mop.hitVec.xCoord + 0.5, mop.hitVec.yCoord + 1, mop.hitVec.zCoord + 0.5, 0.0, 0.1, 0.5);
                        world.spawnParticle("mobspell", 
                            mop.hitVec.xCoord + dx, mop.hitVec.yCoord + dy, mop.hitVec.zCoord + dz, 0.5, 0.1, 0.0);
                    }
                    makeBlackMagicTrail(world, zombie.posX, zombie.posY + zombie.height / 2.0, zombie.posZ, 
                        player.posX, player.posY + player.height / 2.0, player.posZ);
                }
            }
        return itemstack;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon("unraveling:necroFocus");
        this.ornament = ir.registerIcon("unraveling:necromancyOrn");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int par1, int renderPass) {
        return renderPass == 1 ? this.icon : this.ornament;
    }

    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getFocusColor(ItemStack item){
        return 0x93C47D;
    }

    @Override
    public IIcon getOrnament(ItemStack focusstack) {
        return ornament;
    }

    @Override
    public AspectList getVisCost(ItemStack item){
        // return this.isUpgradedWith(item, hellfire) ? fireCost : this.isUpgradedWith(item, pandemonium) ? switchCost : visCost;
        return this.visCost;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack item, int rank){
        return null;

        /*
        switch(rank){
            case 1:
                return new FocusUpgradeType[] {FocusUpgradeType.frugal};
            case 2:
                return new FocusUpgradeType[] {hellfire, pandemonium, FocusUpgradeType.frugal};
            case 3:
            case 4:
            case 5:
                if(this.isUpgradedWith(item, hellfire))
                    return new FocusUpgradeType[] {FocusUpgradeType.potency, FocusUpgradeType.frugal, FocusUpgradeType.enlarge};
                else if(this.isUpgradedWith(item, pandemonium))
                    return new FocusUpgradeType[] {FocusUpgradeType.frugal, FocusUpgradeType.enlarge};
                else
                    return new FocusUpgradeType[] {FocusUpgradeType.frugal};
            default:
                return null;
        }*/
    }
     //Make a trail of particles from one point to another
    protected void makeBlackMagicTrail(World world, double srcX, double srcY, double srcZ, double destX, double destY, double destZ) {
		// make particle trail
		System.out.println("making trail ");
    	int particles = 32;
    	for (int i = 0; i < particles; i++)
    	{
    		double trailFactor = i / (particles - 1.0D);
    		float f = 0.2F;
    		float f1 = 0.2F;
    		float f2 = 0.2F;
    		double tx = srcX + (destX - srcX) * trailFactor + world.rand.nextGaussian() * 0.005;
    		double ty = srcY + (destY - srcY) * trailFactor + world.rand.nextGaussian() * 0.005;
    		double tz = srcZ + (destZ - srcZ) * trailFactor + world.rand.nextGaussian() * 0.005;
    		world.spawnParticle("mobSpell", tx, ty, tz, f, f1, f2);
    	}
	}

}
