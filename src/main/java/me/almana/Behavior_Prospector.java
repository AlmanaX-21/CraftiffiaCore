package me.almana;

import gregapi.data.LH.Chat;
import gregapi.data.TD.Energy;
import gregapi.item.multiitem.MultiItem;
import gregapi.item.multiitem.behaviors.IBehavior;
import gregapi.util.UT.Entities;
import java.util.ArrayList;
import java.util.List;
import me.almana.scan.OreData;
import me.almana.scan.OreScan;
import me.almana.scan.ProspectorNetwork;
import me.almana.scan.ScanMode;
import me.almana.scan.ScanResponse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class Behavior_Prospector extends IBehavior.AbstractBehaviorDefault {
   public final int mRadius;
   public final boolean mBedrock;
   public final boolean mFluidSources;
   public final int mTier;

   public Behavior_Prospector(int aRadius, boolean aBedrock, boolean aFluidSources, int aTier) {
      this.mRadius = aRadius;
      this.mBedrock = aBedrock;
      this.mFluidSources = aFluidSources;
      this.mTier = aTier;
   }

   private long scanCost() {
      switch (this.mTier) {
         case 1:
            return DrillConfig.active.prospectorHV;
         case 2:
            return DrillConfig.active.prospectorEV;
         default:
            return DrillConfig.active.prospectorMV;
      }
   }

   public ItemStack onItemRightClick(MultiItem aItem, ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
      if (aWorld.field_72995_K) {
         return aStack;
      } else {
         int x = MathHelper.func_76128_c(aPlayer.field_70165_t);
         int y = MathHelper.func_76128_c(aPlayer.field_70163_u);
         int z = MathHelper.func_76128_c(aPlayer.field_70161_v);
         if (!Craftiffia.DEBUG) {
            long tCost = this.scanCost();
            if (!aItem.useEnergy(Energy.EU, aStack, tCost, aPlayer, aPlayer.field_71071_by, aWorld, x, y, z, false)) {
               Entities.sendchat(aPlayer, new String[]{Chat.RED + "Not enough energy to scan."});
               return aStack;
            }

            aItem.useEnergy(Energy.EU, aStack, tCost, aPlayer, aPlayer.field_71071_by, aWorld, x, y, z, true);
         }

         int tChunks = this.mRadius * 2 + 1;
         OreScan tScan = new OreScan();
         tScan.process(ScanMode.LARGE, aWorld, x, z, tChunks);
         List<OreData> tSurface = new ArrayList(tScan.scannedOres);
         List<OreData> tBedrock = new ArrayList();
         List<OreData> tFluids = new ArrayList();
         if (this.mBedrock) {
            OreScan tBedScan = new OreScan();
            tBedScan.process(ScanMode.BEDROCK, aWorld, x, z, tChunks);
            tBedrock.addAll(tBedScan.scannedOres);
         }

         if (this.mFluidSources) {
            OreScan tFluidScan = new OreScan();
            tFluidScan.process(ScanMode.FLUID_BEDROCK, aWorld, x, z, tChunks);
            tFluids.addAll(tFluidScan.scannedOres);
         }

         ProspectorNetwork.WRAPPER.sendTo(new ScanResponse(tSurface, tBedrock, tFluids, tScan.x_origin, tScan.z_origin, ScanMode.LARGE.ordinal(), tChunks, this.mBedrock, this.mFluidSources), (EntityPlayerMP)aPlayer);
         return aStack;
      }
   }

   public List<String> getAdditionalToolTips(MultiItem aItem, List<String> aList, ItemStack aStack) {
      int n = this.mRadius * 2 + 1;
      aList.add(Chat.WHITE + "Scans ore veins per chunk in a " + n + "x" + n + " chunk area");
      aList.add(Chat.WHITE + "Right-click to scan and open the map");
      if (this.mBedrock) {
         aList.add(Chat.WHITE + "Toggle Surface/Bedrock view in the map");
      }

      if (this.mFluidSources) {
         aList.add(Chat.WHITE + "Includes Fluid Sources tab in the map");
      }

      aList.add(Chat.WHITE + "Requires ModularUI2 on the client");
      aList.add(Chat.GREEN + "Energy per scan: " + Chat.WHITE + this.scanCost() + " EU");
      return aList;
   }
}
