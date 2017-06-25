
package unraveling.mechanics;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import unraveling.tileentity.TileQuaesitum;
import unraveling.mechanics.PacketTile;
import unraveling.UnravelingMod;

public class PacketQResearch extends PacketTile<TileQuaesitum> implements IMessageHandler<PacketQResearch, IMessage> {

    private static final long serialVersionUID = 1497188581985763661L; // TODO: why? I copied this from TT, don't know what this number does

    public PacketQResearch() {
        super();
    }

    public PacketQResearch(TileQuaesitum tile) {
        super(tile);
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        super.toBytes(byteBuf);
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        super.fromBytes(byteBuf);
    }

    @Override
    public IMessage onMessage(PacketQResearch message, MessageContext ctx) {
        super.onMessage(message, ctx);
        if (!ctx.side.isServer())
            throw new IllegalStateException("received PacketQResearch " + message + "on client side!");
        message.tile.startResearch(message.player);
        return null;
    }
}
