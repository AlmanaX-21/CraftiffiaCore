package me.almana.scan;

import gregapi.block.prefixblock.PrefixBlock;
import gregapi.block.prefixblock.PrefixBlockTileEntity;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT.Code;
import me.almana.Craftiffia;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ScanHelpers {
   public static PrefixBlock prefixBlock(TileEntity aTile) {
      PrefixBlock tBlock = null;

      try {
         PrefixBlockTileEntity tTile = (PrefixBlockTileEntity)aTile;
         World tWorld = aTile.func_145831_w();
         tBlock = (PrefixBlock)tWorld.func_147439_a(tTile.field_145851_c, tTile.field_145848_d, tTile.field_145849_e);
      } catch (Exception var4) {
         Craftiffia.LOG.error("Not a PrefixBlock");
      }

      return tBlock;
   }

   public static int col(OreDictMaterial aMat) {
      return Code.getRGBaInt(aMat.mRGBaSolid);
   }
}
