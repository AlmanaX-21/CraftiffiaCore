package me.almana;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.ArrayListNoNulls;
import gregapi.data.CS;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

public class HorizontalDrillWallPart extends MultiTileEntityMultiBlockPart {
   public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
      if (!aShouldSideBeRendered[aSide]) {
         return null;
      } else {
         byte tbs = CS.FACES_TBS[2];
         return BlockTextureMulti.get(new ITexture[]{BlockTextureDefault.get(this.mTextures[this.mDesign][tbs], this.mRGBa), BlockTextureDefault.get(this.mTextures[this.mDesign][tbs + 3])});
      }
   }

   private static ItemStack originalWall() {
      return MultiTileEntityRegistry.getRegistry("gt.multitileentity").getItem(18026);
   }

   public ArrayListNoNulls<ItemStack> getDrops(int aFortune, boolean aSilkTouch) {
      return ST.arraylist(new ItemStack[]{originalWall()});
   }

   public ItemStack getPickBlock(MovingObjectPosition aTarget) {
      return originalWall();
   }

   public String getTileEntityName() {
      return "craftiffia.multiblock.wall.horizontal";
   }
}
