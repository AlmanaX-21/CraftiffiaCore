package me.almana.scan;

public class OreData {
   public final int x;
   public final int y;
   public final int z;
   public final short matID;
   public final String name;

   public OreData(int x, int y, int z, short matID) {
      this(x, y, z, matID, (String)null);
   }

   public OreData(int x, int y, int z, short matID, String name) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.matID = matID;
      this.name = name;
   }
}
