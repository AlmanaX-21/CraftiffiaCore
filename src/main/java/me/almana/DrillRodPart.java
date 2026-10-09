package me.almana;

import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.code.ArrayListNoNulls;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.util.ST;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public class DrillRodPart extends MultiTileEntityMultiBlockPart implements IMultiTileEntity.IMTE_IsNormalCube, IMultiTileEntity.IMTE_SetBlockBoundsBasedOnState, IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool, IMultiTileEntity.IMTE_GetSelectedBoundingBoxFromPool, IMultiTileEntity.IMTE_ShouldSideBeRendered {
   private static final double MIN = (double)0.25F;
   private static final double MAX = (double)0.75F;

   public boolean isNormalCube() {
      return false;
   }

   public boolean isSurfaceOpaque2(byte aSide) {
      return false;
   }

   public AxisAlignedBB getCollisionBoundingBoxFromPool() {
      return this.box((double)0.25F, (double)0.0F, (double)0.25F, (double)0.75F, (double)1.0F, (double)0.75F);
   }

   public AxisAlignedBB getSelectedBoundingBoxFromPool() {
      return this.box((double)0.25F, (double)0.0F, (double)0.25F, (double)0.75F, (double)1.0F, (double)0.75F);
   }

   public void setBlockBoundsBasedOnState(Block aBlock) {
      this.box(aBlock, (double)0.25F, (double)0.0F, (double)0.25F, (double)0.75F, (double)1.0F, (double)0.75F);
   }

   public boolean setBlockBounds2(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {
      this.box(aBlock, (double)0.25F, (double)0.0F, (double)0.25F, (double)0.75F, (double)1.0F, (double)0.75F);
      return true;
   }

   public ArrayListNoNulls<ItemStack> getDrops(int aFortune, boolean aSilkTouch) {
      return ST.arraylist(new ItemStack[0]);
   }

   public boolean shouldSideBeRendered(byte aSide) {
      return aSide > 1 || super.shouldSideBeRendered(aSide);
   }

   public String getTileEntityName() {
      return "craftiffia.multiblock.rod";
   }
}
