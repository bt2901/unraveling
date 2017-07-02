package unraveling.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.item.ItemStack;

import net.minecraft.entity.player.EntityPlayer;

import thaumcraft.common.entities.golems.ItemGolemPlacer;
import thaumcraft.common.lib.FakeThaumcraftPlayer;

import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.InventoryMob;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;
import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.entities.golems.Marker;


public class TileEntityTrapRoom extends TileEntity {
    protected int counter;
    String mobID = "Pig";
	public TileEntityTrapRoom() {
		// this.mobID = TFCreatures.getSpawnerNameFor("Twilight Lich");
	}
	
	public boolean anyPlayerInRange() {
    	EntityPlayer closestPlayer = worldObj.getClosestPlayer(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 9D);
    	
        return closestPlayer != null && closestPlayer.posY > yCoord - 4;
    }
	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public void updateEntity() {
		this.counter++;
		if(anyPlayerInRange()) {
			if (!worldObj.isRemote) {
				if (true || worldObj.difficultySetting != EnumDifficulty.PEACEFUL) {
					spawnCreature(worldObj, 1);

					// destroy block
					worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air, 0, 2);
				}
			}
		}

    }

    public boolean spawnCreature(World par0World, int side) {
        byte GUARD = 4;
        byte BUTCHER = 9;
        byte ORDER = 4;
        EntityGolemBase golem;
        boolean adv = false;
        if ((golem = new EntityGolemBase(par0World, EnumGolemType.STONE, adv)) != null) {

            double rx = xCoord + 0.5D;
            double ry = yCoord + 0.5D;
            double rz = zCoord + 0.5D;
            golem.setLocationAndAngles(rx, ry, rz, worldObj.rand.nextFloat() * 360F, 0.0F);
            // golem.playLivingSound();
            golem.setHomeArea(xCoord, yCoord+1, zCoord, 32);
            golem.setCore(GUARD);
            //golem.setUpgrade(0, ORDER); // ORDER upgrade: attack players
            golem.setToggle(3, false); // attack players
            golem.setToggle(1, false); // attack hostiles
            // skip golem.upgrades
            String deco = "";
            // skip golem.decoration

            golem.setup(side);
            par0World.spawnEntityInWorld((Entity)golem);
            golem.setGolemDecoration(deco);
            golem.setOwner("NOT_YOU");
            // golem.setMarkers(ItemGolemBell.getMarkers(stack));
            // golem.setCustomNameTag("golem");
            // golem.enablePersistence();
            
            // skip golem.inventory.readFromNBT(nbttaglist2);
            
        }
        return golem != null;
    }
    
	/**
	 * Range?
	 */
	protected int getRange() {
		return 50;
	}

	/**
	 * Create a copy of what we spawn
	 */
	protected EntityLiving makeMyCreature() {
		return (EntityLiving)EntityList.createEntityByName(mobID, worldObj);
	}
}
