package me.almana;

import gregapi.block.IBlockPlacable;
import gregapi.block.prefixblock.PrefixBlock;
import gregapi.code.ItemStackContainer;
import gregapi.data.CS.BlocksGT;
import gregapi.oredict.OreDictPrefix;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.oredict.OreDictionary;

public class AreaMineLogic {
   public static final int UNSET = Integer.MIN_VALUE;
   public int mY = Integer.MIN_VALUE;
   public int mChunk = 0;
   public int mOffset = 0;
   public boolean mDone = false;

   public boolean ready() {
      return this.mY != Integer.MIN_VALUE;
   }

   public void reset(int aStartY) {
      this.mY = aStartY;
      this.mChunk = 0;
      this.mOffset = 0;
      this.mDone = false;
   }

   public void readFromNBT(NBTTagCompound aNBT) {
      if (aNBT.func_74764_b("craftiffia.mine.y")) {
         this.mY = aNBT.func_74762_e("craftiffia.mine.y");
      }

      this.mChunk = aNBT.func_74762_e("craftiffia.mine.chunk");
      this.mOffset = aNBT.func_74762_e("craftiffia.mine.offset");
      this.mDone = aNBT.func_74767_n("craftiffia.mine.done");
   }

   public void writeToNBT(NBTTagCompound aNBT) {
      aNBT.func_74768_a("craftiffia.mine.y", this.mY);
      aNBT.func_74768_a("craftiffia.mine.chunk", this.mChunk);
      aNBT.func_74768_a("craftiffia.mine.offset", this.mOffset);
      aNBT.func_74757_a("craftiffia.mine.done", this.mDone);
   }

   public List<ItemStack> mineStep(World aWorld, int aCtrlX, int aCtrlZ, int aOresPerOperation) {
      List<ItemStack> rDrops = new ArrayList();
      if (!this.mDone && this.ready()) {
         int tBaseCX = (aCtrlX >> 4) - 1;
         int tBaseCZ = (aCtrlZ >> 4) - 1;
         int tX0 = tBaseCX + this.mChunk % 3 << 4;
         int tZ0 = tBaseCZ + this.mChunk / 3 << 4;
         int tMined = 0;
         Block tFiller = fillerBlock(aWorld);

         while(this.mOffset < 256 && tMined < aOresPerOperation) {
            int x = tX0 + this.mOffset % 16;
            int z = tZ0 + this.mOffset / 16;
            ++this.mOffset;
            Block tBlock = aWorld.func_147439_a(x, this.mY, z);
            if (tBlock != Blocks.field_150350_a && !tBlock.isAir(aWorld, x, this.mY, z)) {
               int tMeta = aWorld.func_72805_g(x, this.mY, z);
               if (this.isOre(tBlock, tMeta) && (tBlock != BlocksGT.RockOres || aWorld.field_73012_v.nextInt(100) >= DrillConfig.active.rockOreLayerSkipChance)) {
                  if (aWorld.field_73012_v.nextInt(100) < DrillConfig.active.miningStoneChance) {
                     rDrops.add(baseStone(aWorld, tBlock));
                     --this.mOffset;
                     break;
                  }

                  rDrops.addAll(tBlock.getDrops(aWorld, x, this.mY, z, tMeta, 0));
                  aWorld.func_147465_d(x, this.mY, z, tFiller, 0, 2);
                  ++tMined;
               }
            }
         }

         if (this.mOffset >= 256) {
            this.nextSlice();
         }

         return rDrops;
      } else {
         return rDrops;
      }
   }

   private void nextSlice() {
      this.mOffset = 0;
      if (++this.mChunk > 8) {
         this.mChunk = 0;
         if (--this.mY < 1) {
            this.mDone = true;
         }
      }

   }

   static Block fillerBlock(World aWorld) {
      if (aWorld.field_73011_w instanceof WorldProviderHell) {
         return Blocks.field_150424_aL;
      } else {
         return aWorld.field_73011_w instanceof WorldProviderEnd ? Blocks.field_150377_bs : Blocks.field_150348_b;
      }
   }

   static ItemStack baseStone(World aWorld, Block aOre) {
      Block tFiller = fillerBlock(aWorld);
      return new ItemStack(tFiller == Blocks.field_150348_b ? Blocks.field_150347_e : tFiller);
   }

   private static ItemStack stoneFrom(Block aOre, Map<ItemStackContainer, IBlockPlacable> aMap) {
      for(Map.Entry<ItemStackContainer, IBlockPlacable> tEntry : aMap.entrySet()) {
         if (tEntry.getValue() == aOre) {
            return ((ItemStackContainer)tEntry.getKey()).toStack();
         }
      }

      return null;
   }

   private boolean isOre(Block aBlock, int aMeta) {
      if (aBlock instanceof PrefixBlock) {
         OreDictPrefix tPrefix = ((PrefixBlock)aBlock).mPrefix;
         if (tPrefix != null && tPrefix.mNameInternal.startsWith("ore")) {
            return true;
         }
      }

      for(int tID : OreDictionary.getOreIDs(new ItemStack(aBlock, 1, aMeta))) {
         if (OreDictionary.getOreName(tID).startsWith("ore")) {
            return true;
         }
      }

      return false;
   }
}
