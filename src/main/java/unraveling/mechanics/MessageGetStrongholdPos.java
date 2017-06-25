package unraveling.mechanics;

// based on endercompass by siribby

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.ChunkPosition;

public class MessageGetStrongholdPos implements IMessage, IMessageHandler<MessageGetStrongholdPos, IMessage> {
    public MessageGetStrongholdPos() {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public IMessage onMessage(MessageGetStrongholdPos message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        ChunkPosition position = player.worldObj.findClosestStructure("Stronghold", (int) player.posX, (int) player.posY, (int) player.posZ);
        return position != null ? new MessageSetStrongholdPos(position) : null;
    }
}
