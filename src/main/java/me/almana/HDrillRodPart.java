package me.almana;

import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.code.ArrayListNoNulls;
import gregapi.render.BlockTextureDefault;
import gregapi.render.BlockTextureMulti;
import gregapi.render.ITexture;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public class HDrillRodPart extends MultiTileEntityMultiBlockPart implements IMultiTileEntity.IMTE_IsNormalCube, IMultiTileEntity.IMTE_SetBlockBoundsBasedOnState, IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool, IMultiTileEntity.IMTE_GetSelectedBoundingBoxFromPool, IMultiTileEntity.IMTE_ShouldSideBeRendered {
   private static final double MIN = (double)0.25F;
   private static final double MAX = (double)0.75F;
   private static final byte[] TBS_X = new byte[]{2, 2, 2, 2, 0, 1};
   private static final byte[] TBS_Z = new byte[]{2, 2, 0, 1, 2, 2};

   private boolean alongX() {
      return this.getMultiTileEntityID() == 7;
   }

   public ITexture getTexture2(Block aBlock, int aRenderPass, byte aSide, boolean[] aShouldSideBeRendered) {
      if (!aShouldSideBeRendered[aSide]) {
         return null;
      } else {
         boolean alongX = this.alongX();
         byte tbs = (alongX ? TBS_X : TBS_Z)[aSide];
         ITexture tTexture = BlockTextureMulti.get(new ITexture[]{BlockTextureDefault.get(this.mTextures[this.mDesign][tbs], this.mRGBa), BlockTextureDefault.get(this.mTextures[this.mDesign][tbs + 3])});
         return (ITexture)(tbs != 2 || !alongX && aSide <= 3 ? tTexture : new RotatedTexture(tTexture));
      }
   }

   public boolean isNormalCube() {
      return false;
   }

   public boolean isSurfaceOpaque2(byte aSide) {
      return false;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool() {
      return this.alongX() ? this.box((double)0.0F, (double)0.25F, (double)0.25F, (double)1.0F, (double)0.75F, (double)0.75F) : this.box((double)0.25F, (double)0.25F, (double)0.0F, (double)0.75F, (double)0.75F, (double)1.0F);
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool() {
      return this.alongX() ? this.box((double)0.0F, (double)0.25F, (double)0.25F, (double)1.0F, (double)0.75F, (double)0.75F) : this.box((double)0.25F, (double)0.25F, (double)0.0F, (double)0.75F, (double)0.75F, (double)1.0F);
   }

   public void setBlockBoundsBasedOnState(Block aBlock) {
      if (this.alongX()) {
         this.box(aBlock, (double)0.0F, (double)0.25F, (double)0.25F, (double)1.0F, (double)0.75F, (double)0.75F);
      } else {
         this.box(aBlock, (double)0.25F, (double)0.25F, (double)0.0F, (double)0.75F, (double)0.75F, (double)1.0F);
      }

   }

   public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
      if (this.alongX()) {
         this.box(aBlock, (double)0.0F, (double)0.25F, (double)0.25F, (double)1.0F, (double)0.75F, (double)0.75F);
      } else {
         this.box(aBlock, (double)0.25F, (double)0.25F, (double)0.0F, (double)0.75F, (double)0.75F, (double)1.0F);
      }

      return true;
   }

   public ArrayListNoNulls<ItemStack> getDrops(int aFortune, boolean aSilkTouch) {
      return ST.arraylist(new ItemStack[0]);
   }

   public boolean shouldSideBeRendered(byte aSide) {
      boolean full = this.alongX() ? aSide == 4 || aSide == 5 : aSide == 2 || aSide == 3;
      return !full || super.shouldSideBeRendered(aSide);
   }

   public String getTileEntityName() {
      return "craftiffia.multiblock.rod.horizontal";
   }
}
