package me.almana;

import gregapi.block.prefixblock.PrefixBlock;
import gregapi.data.CS;
import gregapi.data.CS.BlocksGT;
import gregapi.oredict.OreDictPrefix;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class HorizontalMineLogic {
   public static final int AREA = 15;
   public static final int HALF = 7;
   public static final int START_DEPTH = 6;
   public static final int CELLS = 225;
   public int mDepth = 0;
   public int mOffset = 0;
   public boolean mAdvanced = false;

   public void readFromNBT(NBTTagCompound aNBT) {
      this.mDepth = aNBT.func_74762_e("craftiffia.hmine.depth");
      this.mOffset = aNBT.func_74762_e("craftiffia.hmine.offset");
   }

   public void writeToNBT(NBTTagCompound aNBT) {
      aNBT.func_74768_a("craftiffia.hmine.depth", this.mDepth);
      aNBT.func_74768_a("craftiffia.hmine.offset", this.mOffset);
   }

   public int frontierDist() {
      return 6 + this.mDepth;
   }

   static List<ChunkCoordIntPair> frontierChunks(int aCX, int aCZ, boolean aAxisX) {
      List<ChunkCoordIntPair> rChunks = new ArrayList(2);
      if (aAxisX) {
         int cx = aCX >> 4;
         int czLo = aCZ - 7 >> 4;
         int czHi = aCZ + 7 >> 4;
         rChunks.add(new ChunkCoordIntPair(cx, czLo));
         if (czHi != czLo) {
            rChunks.add(new ChunkCoordIntPair(cx, czHi));
         }
      } else {
         int cz = aCZ >> 4;
         int cxLo = aCX - 7 >> 4;
         int cxHi = aCX + 7 >> 4;
         rChunks.add(new ChunkCoordIntPair(cxLo, cz));
         if (cxHi != cxLo) {
            rChunks.add(new ChunkCoordIntPair(cxHi, cz));
         }
      }

      return rChunks;
   }

   static List<ChunkCoordIntPair> generationChunks(List<ChunkCoordIntPair> aFrontier, int aRadius) {
      Set<ChunkCoordIntPair> tChunks = new LinkedHashSet();

      for(ChunkCoordIntPair tFrontier : aFrontier) {
         for(int x = -aRadius; x <= aRadius; ++x) {
            for(int z = -aRadius; z <= aRadius; ++z) {
               tChunks.add(new ChunkCoordIntPair(tFrontier.field_77276_a + x, tFrontier.field_77275_b + z));
            }
         }
      }

      return new ArrayList(tChunks);
   }

   public List<ItemStack> mineStep(World aWorld, int aCtrlX, int aCtrlY, int aCtrlZ, byte aFacing, int aOresPerOperation) {
      List<ItemStack> rDrops = new ArrayList();
      this.mAdvanced = false;
      int tDist = this.frontierDist();
      int tCX = aCtrlX + CS.OFFX[aFacing] * tDist;
      int tCZ = aCtrlZ + CS.OFFZ[aFacing] * tDist;
      boolean tAxisX = CS.SIDES_AXIS_X[aFacing];
      int tMined = 0;
      Block tFiller = AreaMineLogic.fillerBlock(aWorld);

      while(this.mOffset < 225 && tMined < aOresPerOperation) {
         int u = this.mOffset / 15 - 7;
         int r = this.mOffset % 15 - 7;
         ++this.mOffset;
         int x = tCX + (tAxisX ? 0 : r);
         int y = aCtrlY + u;
         int z = tCZ + (tAxisX ? r : 0);
         if (y >= 1 && aWorld.func_72899_e(x, y, z)) {
            Block tBlock = aWorld.func_147439_a(x, y, z);
            if (tBlock != Blocks.field_150350_a && !tBlock.isAir(aWorld, x, y, z)) {
               int tMeta = aWorld.func_72805_g(x, y, z);
               if (this.isOre(tBlock, tMeta) && (tBlock != BlocksGT.RockOres || aWorld.field_73012_v.nextInt(100) >= DrillConfig.active.rockOreLayerSkipChance)) {
                  if (aWorld.field_73012_v.nextInt(100) < DrillConfig.active.horizontalStoneChance) {
                     rDrops.add(AreaMineLogic.baseStone(aWorld, tBlock));
                     --this.mOffset;
                     break;
                  }

                  rDrops.addAll(tBlock.getDrops(aWorld, x, y, z, tMeta, 0));
                  aWorld.func_147465_d(x, y, z, tFiller, 0, 2);
                  ++tMined;
               }
            }
         }
      }

      if (this.mOffset >= 225) {
         this.mOffset = 0;
         ++this.mDepth;
         this.mAdvanced = true;
      }

      return rDrops;
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
