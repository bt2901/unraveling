package unraveling.mechanics;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import thaumcraft.common.Thaumcraft;


import java.util.Random;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import unraveling.UnravelingMod;

public class VoidPacketHandler {

	public static final byte TRANSFORM_BLOCK = 1;
	public static final byte PARTICLE_BLOCK = 2;
	public static final byte VENT = 3;
    
    public static Random rand = new Random();

	/**
	 * Packet!
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void incomingPacket(ClientCustomPacketEvent event)
	{
		ByteBuf buf = event.packet.payload();
		
		// read first byte to see what kind of packet
		int discriminatorByte = buf.readByte();
		
		if (discriminatorByte == TRANSFORM_BLOCK) {
			processTransformBlock(buf);
		}
		if (discriminatorByte == PARTICLE_BLOCK) {
			processParticleBlock(buf);
		}
		if (discriminatorByte == VENT) {
			processBadVent(buf);
		}
	}


	
	@SideOnly(Side.CLIENT)
	private void processTransformBlock(ByteBuf buf) {
		int blockX = buf.readInt();
		int blockY = buf.readInt();
		int blockZ = buf.readInt();
		
		World worldObj = Minecraft.getMinecraft().theWorld;
        System.out.println("processTransformBlock");

		UnravelingMod.proxy.doBlockTransformEffect(worldObj, blockX, blockY, blockZ);
	}
    
	@SideOnly(Side.CLIENT)
	private void processParticleBlock(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
		int x2 = buf.readInt();
		int y2 = buf.readInt();
		int z2 = buf.readInt();
        int color = buf.readInt();
        World worldObj = Minecraft.getMinecraft().theWorld;
        Thaumcraft.proxy.essentiaTrailFx(worldObj, x, y, z, x2, y2, z2, 1, color, 1);
	}
	@SideOnly(Side.CLIENT)
	private void processBadVent(ByteBuf buf) {
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
        int color = buf.readInt();
        float dx = x + ((rand.nextFloat() - rand.nextFloat()) + 0.5F);
    	float dy = y + 1.1F;
    	float dz = z + ((rand.nextFloat() - rand.nextFloat()) + 0.5F);
        
        World worldObj = Minecraft.getMinecraft().theWorld;
        Thaumcraft.proxy.drawVentParticles(worldObj, x + 0.5, y + 1, z + 0.5, dx, dy, dz, color, 3.0F);
	}
	                    
	
    
	/**
	 * Make a FMLProxyPacket that contains a block to display transformation effects for
	 */
	public static FMLProxyPacket makeTransformBlockPacket(int blockX, int blockY, int blockZ) {
        System.out.println("makeTransformBlockPacket");

        PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
        
        payload.writeByte(TRANSFORM_BLOCK); // discriminator byte

        payload.writeInt(blockX);
        payload.writeInt(blockY);
        payload.writeInt(blockZ);

		FMLProxyPacket pkt = new FMLProxyPacket(payload, UnravelingMod.ID);

		return pkt;
	}
	/**
	 * Make a FMLProxyPacket that contains data about FX
	 */
	public static FMLProxyPacket makeBlockParticlePacket(int blockX, int blockY, int blockZ, int x2, int y2, int z2, int col) {

        PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
        
        payload.writeByte(PARTICLE_BLOCK); // discriminator byte

        payload.writeInt(blockX);
        payload.writeInt(blockY);
        payload.writeInt(blockZ);
        payload.writeInt(x2);
        payload.writeInt(y2);
        payload.writeInt(z2);
        payload.writeInt(col);

		FMLProxyPacket pkt = new FMLProxyPacket(payload, UnravelingMod.ID);
		return pkt;
	}
    public static FMLProxyPacket makeVentPacket(int blockX, int blockY, int blockZ, int col) {

        PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
        
        payload.writeByte(VENT); // discriminator byte

        payload.writeInt(blockX);
        payload.writeInt(blockY);
        payload.writeInt(blockZ);
        payload.writeInt(col);

		FMLProxyPacket pkt = new FMLProxyPacket(payload, UnravelingMod.ID);
		return pkt;
	}
}
