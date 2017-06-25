package unraveling.mechanics.voidgen;

import thaumcraft.api.aspects.Aspect;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import unraveling.mechanics.voidgen.TileDarkGen;
import unraveling.mechanics.PacketTile;
import unraveling.UnravelingMod;

public class PacketDrainSwitch extends PacketTile<TileDarkGen> implements IMessageHandler<PacketDrainSwitch, IMessage> {

    boolean isDarkness;

    public PacketDrainSwitch() {
        super();
    }

    public PacketDrainSwitch(TileDarkGen tile) {
        super(tile);
        isDarkness = tile.drainSelected == Aspect.DARKNESS;
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        super.toBytes(byteBuf);
        byteBuf.writeBoolean(isDarkness);
    }

    @Override
    public void fromBytes(ByteBuf byteBuf) {
        super.fromBytes(byteBuf);
        isDarkness = byteBuf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketDrainSwitch message, MessageContext ctx) {
        super.onMessage(message, ctx);
        if (!ctx.side.isServer())
            throw new IllegalStateException("received PacketDrainSwitch " + message + "on client side!");
        message.tile.drainSelected = (isDarkness)? Aspect.DARKNESS : Aspect.VOID;
        return null;
    }
}
