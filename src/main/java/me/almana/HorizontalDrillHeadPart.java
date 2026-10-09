package me.almana;

import gregapi.data.CS;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import net.minecraft.block.Block;

public class HorizontalDrillHeadPart extends MultiTileEntityMultiBlockPart {
   private byte frontSide() {
      if (this.mTargetPos == null) {
         return 6;
      } else {
         int sx = Integer.signum(this.field_145851_c - this.mTargetPos.field_71574_a);
         int sz = Integer.signum(this.field_145849_e - this.mTargetPos.field_71573_c);

         for(byte side : CS.ALL_SIDES_HORIZONTAL) {
            if (CS.OFFX[side] == sx && CS.OFFZ[side] == sz) {
               return side;
            }
         }

         return 6;
      }
   }

   public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
      if (!aShouldSideBeRendered[aSide]) {
         return null;
      } else {
         byte front = this.frontSide();
         byte tbs = aSide == front ? CS.FACES_TBS[0] : CS.FACES_TBS[2];
         return BlockTextureMulti.get(new ITexture[]{BlockTextureDefault.get(this.mTextures[this.mDesign][tbs], this.mRGBa), BlockTextureDefault.get(this.mTextures[this.mDesign][tbs + 3])});
      }
   }

   public String getTileEntityName() {
      return "craftiffia.multiblock.head.horizontal";
   }
}
