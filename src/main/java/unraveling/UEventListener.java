package unraveling;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import unraveling.item.ItemArtifact;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.client.lib.PlayerNotifications;
import thaumcraft.common.lib.research.PlayerKnowledge;
import thaumcraft.common.Thaumcraft;


import unraveling.block.UBlocks;
import unraveling.EldritchLore;
import unraveling.dim.WorldProviderDemiplane;


import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;


import cpw.mods.fml.relauncher.Side;



import cpw.mods.fml.common.event.FMLInterModComms;

import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.internal.FMLMessage.EntitySpawnMessage;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;

import thaumcraft.common.config.ConfigItems;


public class UEventListener {
    
    /**
     * When player changes dimensions, send the rule status if they're moving to the Twilight Forest
	 */
	@SubscribeEvent
	public void playerPortals(PlayerChangedDimensionEvent event) {
        int warp = Thaumcraft.proxy.getPlayerKnowledge().getWarpTotal(event.player.getCommandSenderName());
        System.out.println(event.player + " has warp " + warp);
        
        // PlayerNotifications.addNotification(msg);
        // return stack;
    

		/*if (!event.player.worldObj.isRemote && event.player instanceof EntityPlayerMP && event.toDim == TwilightForestMod.dimensionID) {
			this.sendEnforcedProgressionStatus((EntityPlayerMP)event.player, event.player.worldObj.getGameRules().getGameRuleBooleanValue(TwilightForestMod.ENFORCED_PROGRESSION_RULE));
		}*/
    }
}
