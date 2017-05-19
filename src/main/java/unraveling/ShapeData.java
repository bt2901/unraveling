package unraveling;

import java.util.List;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import thaumcraft.api.WorldCoordinates;


public class ShapeData {
    public int minx;
    public int minz;
    public int maxx;
    public int maxz;
    public int cury;

    public ShapeData() {}

    public ShapeData(int par1, int par2, int par3, int side) {
        this.minx = par1;
        this.cury = par2;
        this.minz = par3;
        this.maxx = par1 + side;
        this.maxz = par3 + side;
    }
    public static boolean isValid(WorldCoordinates g1, WorldCoordinates g2, WorldCoordinates g3, WorldCoordinates g4) {
        ArrayList<WorldCoordinates> tmp = new ArrayList<WorldCoordinates>();
        tmp.add(g1);
        tmp.add(g2);
        tmp.add(g3);
        tmp.add(g4);
        return isValid(tmp, tmp.size());
    }

    public static boolean isValid_te(List<TileEntity> myGenerators, int GEN_NUM) {
        ArrayList<WorldCoordinates> tmp = new ArrayList<WorldCoordinates>();
		for(int i = 0; i < GEN_NUM; i++) {
            tmp.add(new WorldCoordinates(myGenerators.get(i)));
        }
        return isValid(tmp, GEN_NUM);
    }
    public static boolean isValid(List<WorldCoordinates> myGenerators, int GEN_NUM) {
        if (myGenerators.size() != GEN_NUM) {
            return false;
        }
        int minx, maxx, minz, maxz, cury;
        minx = maxx = (int)myGenerators.get(0).x;
        minz = maxz = (int)myGenerators.get(0).z;
        cury = (int)myGenerators.get(0).y;
		for(int i = 0; i < GEN_NUM; i++) {
            WorldCoordinates tile = myGenerators.get(i);
            int x = (int)tile.x;
            int y = (int)tile.y;
            int z = (int)tile.z;
            if (cury != y) {
                return false;
            }
            minx = Math.min(minx, x);
            minz = Math.min(minz, z);
            maxx = Math.max(maxx, x);
            maxz = Math.max(maxz, z);
        }
        // we aren't inside of square
        if ((maxz - minz) != (maxx - minx)) {
            return false;
        }
		for(int i = 0; i < GEN_NUM; i++) {
            WorldCoordinates tile = myGenerators.get(i);
            int x = (int)tile.x;
            int z = (int)tile.z;
            boolean is_corner = ((x == minx)||(x == maxx)) && ((z == minz)||(z == maxz));
            if (!is_corner) {
                return false;
            }
        }
        return true;
    }
    
    public ShapeData(List<WorldCoordinates> myGenerators, int GEN_NUM) throws IllegalArgumentException {
        if (!isValid(myGenerators, GEN_NUM)) {
            throw new IllegalArgumentException();
        }
        
        minx = maxx = (int)myGenerators.get(0).x;
        minz = maxz = (int)myGenerators.get(0).z;
        cury = (int)myGenerators.get(0).y;
		for(int i = 0; i < GEN_NUM; i++) {
            WorldCoordinates tile = myGenerators.get(i);
            int x = (int)tile.x;
            int y = (int)tile.y;
            int z = (int)tile.z;
            minx = Math.min(minx, x);
            minz = Math.min(minz, z);
            maxx = Math.max(maxx, x);
            maxz = Math.max(maxz, z);
            
        }
    }
    
    public boolean equals(Object par1Obj)
    {
        if (!(par1Obj instanceof ShapeData))
        {
            return false;
        }
        else
        {
        	ShapeData that = (ShapeData)par1Obj;
            return this.cury == that.cury 
                && this.minx == that.minx && this.minz == that.minz 
                && this.maxx == that.maxx && this.maxz == that.maxz;
        }
    }

    public int hashCode()
    {
        return this.minx + this.cury << 8 + this.maxz << 16;
    }

    public void readNBT(NBTTagCompound nbt) {
    	this.minx = nbt.getInteger("min_x");
    	this.maxx = nbt.getInteger("max_x");
    	this.cury = nbt.getInteger("cur_y");
    	this.minz = nbt.getInteger("min_z");
    	this.maxz = nbt.getInteger("max_z");
    }
    
    public void writeNBT(NBTTagCompound nbt) {
    	nbt.setInteger("min_x", minx);
    	nbt.setInteger("max_x", maxx);
    	nbt.setInteger("cur_y", cury);
    	nbt.setInteger("min_z", minz);
    	nbt.setInteger("max_z", maxz);
    }
    public ArrayList<Vec3> cornersList() {
        ArrayList<Vec3> myCorners = new ArrayList<Vec3>();
        myCorners.add(Vec3.createVectorHelper(minx, cury, minz));
        myCorners.add(Vec3.createVectorHelper(minx, cury, maxz));
        myCorners.add(Vec3.createVectorHelper(maxx, cury, minz));
        myCorners.add(Vec3.createVectorHelper(maxx, cury, maxz));

        return myCorners;        
    }

    
}
