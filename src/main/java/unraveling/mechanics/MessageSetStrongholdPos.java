package unraveling.mechanics;

// based on endercompass by siribby

import unraveling.item.ItemCompassStone;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.ChunkPosition;

public class MessageSetStrongholdPos implements IMessage, IMessageHandler<MessageSetStrongholdPos, IMessage> {
    private int x;
    private int y;
    private int z;

    public MessageSetStrongholdPos() {}

    public MessageSetStrongholdPos(ChunkPosition position) {
        x = position.chunkPosX;
        y = position.chunkPosY;
        z = position.chunkPosZ;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public IMessage onMessage(MessageSetStrongholdPos message, MessageContext ctx) {
        ItemCompassStone.strongholdPos = new ChunkPosition(message.x, message.y, message.z);
        return null;
    }
}
