package unraveling.dim;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileVoidPortal extends TileEntity {

    @Override
    public void updateEntity() {
        for (Object e : worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1))) {
            if (e instanceof Entity) {
                ((Entity) e).travelToDimension(2901);
            }
        }
    }
}
