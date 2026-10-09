package me.almana;

import gregapi.block.multitileentity.IMultiTileEntity;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.code.TagData;
import gregapi.data.CS;
import gregapi.data.LH;
import gregapi.data.CS.FluidsGT;
import gregapi.data.CS.GarbageGT;
import gregapi.data.LH.Chat;
import gregapi.data.TD.Energy;
import gregapi.fluid.FluidTankGT;
import gregapi.tileentity.energy.ITileEntityEnergy;
import gregapi.tileentity.energy.ITileEntityEnergyDataCapacitor;
import gregapi.tileentity.machines.ITileEntityRunningPossible;
import gregapi.tileentity.multiblocks.IMultiBlockEnergy;
import gregapi.tileentity.multiblocks.IMultiBlockFluidHandler;
import gregapi.tileentity.multiblocks.ITileEntityMultiBlockController;
import gregapi.tileentity.multiblocks.MultiTileEntityMultiBlockPart;
import gregapi.tileentity.multiblocks.TileEntityBase10MultiBlockBase;
import gregapi.util.ST;
import gregapi.util.WD;
import gregapi.util.UT.NBT;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class MiningDrillController extends TileEntityBase10MultiBlockBase implements ITileEntityEnergy, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockFluidHandler, IFluidHandler, ITileEntityRunningPossible {
   public static final int OUT_SLOTS = 16;
   public static final int POWER_BUFFER_TICKS = 20;
   public long mEnergy = 0L;
   public TagData mEnergyTypeAccepted;
   public FluidTankGT mTank;
   public final AreaMineLogic mLogic;
   private boolean mWasFormed;
   private double mProgress;
   private static final int[] ACCESSIBLE_SLOTS;

   public MiningDrillController() {
      this.mEnergyTypeAccepted = Energy.RU;
      this.mTank = new FluidTankGT(16000L);
      this.mLogic = new AreaMineLogic();
      this.mWasFormed = false;
      this.mProgress = (double)0.0F;
   }

   public void readFromNBT2(NBTTagCompound aNBT) {
      super.readFromNBT2(aNBT);
      this.mEnergy = aNBT.func_74763_f("gt.energy");
      if (aNBT.func_74764_b("gt.energy.accepted")) {
         this.mEnergyTypeAccepted = TagData.createTagData(aNBT.func_74779_i("gt.energy.accepted"));
      }

      this.mTank.readFromNBT(aNBT, "gt.tank.0");
      this.mLogic.readFromNBT(aNBT);
   }

   public void writeToNBT2(NBTTagCompound aNBT) {
      super.writeToNBT2(aNBT);
      NBT.setNumber(aNBT, "gt.energy", this.mEnergy);
      this.mTank.writeToNBT(aNBT, "gt.tank.0");
      this.mLogic.writeToNBT(aNBT);
   }

   public boolean checkStructure2(ChunkCoordinates aCoordinates, Entity aPlayer, IInventory aInventory) {
      if (this.field_145848_d < 4) {
         return false;
      } else {
         boolean tSuccess = true;
         int tWalls = MultiTileEntityRegistry.getRegistry("gt.multitileentity").currentID();
         int tSelf = this.getMultiTileEntityRegistryID();

         for(int dy = -1; dy > -4; --dy) {
            for(int dx = -1; dx < 2; ++dx) {
               for(int dz = -1; dz < 2; ++dz) {
                  if (dx == 0 && dz == 0) {
                     if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, dy, dz, 2, tSelf, 0, -1, aCoordinates, aPlayer, aInventory)) {
                        tSuccess = false;
                     }
                  } else if (dy == -2 && (dx == 0 || dz == 0)) {
                     if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, dy, dz, 18006, tWalls, 3, -3, aCoordinates, aPlayer, aInventory)) {
                        tSuccess = false;
                     }
                  } else if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, dy, dz, 18006, tWalls, 0, -1, aCoordinates, aPlayer, aInventory)) {
                     tSuccess = false;
                  }
               }
            }
         }

         return tSuccess;
      }
   }

   public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
      aList.add(Chat.CYAN + LH.get("gt.lang.structure") + ":");
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.mining.1"));
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.mining.2"));
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.mining.3"));
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.mining.4"));
      long tPower = this.powerPerTick();
      String tUnit = this.mEnergyTypeAccepted.getLocalisedChatNameShort();
      aList.add(Chat.GREEN + LH.get("gt.lang.energy.input") + ": " + Chat.WHITE + Math.min(512L, tPower) + " to " + tPower + " " + tUnit + Chat.WHITE + "/t (up to " + tPower * 4L + " " + tUnit + Chat.WHITE + "/t total)");
      aList.add(Chat.GREEN + "Consumption: " + Chat.WHITE + tPower + " " + tUnit + Chat.WHITE + "/t + " + DrillConfig.active.miningLubricantPerOperation + " mB Lubricant/operation");
      super.addToolTips(aList, aStack, aF3_H);
   }

   public boolean isInsideStructure(int aX, int aY, int aZ) {
      return aX >= this.field_145851_c - 1 && aY >= this.field_145848_d - 3 && aZ >= this.field_145849_e - 1 && aX <= this.field_145851_c + 1 && aY <= this.field_145848_d && aZ <= this.field_145849_e + 1;
   }

   public byte getDefaultSide() {
      return 1;
   }

   public boolean[] getValidSides() {
      return CS.SIDES_TOP;
   }

   public long onToolClick2(String aTool, long aRemainingDurability, long aQuality, Entity aPlayer, List<String> aChatReturn, IInventory aPlayerInventory, boolean aSneaking, ItemStack aStack, byte aSide, float aHitX, float aHitY, float aHitZ) {
      long rReturn = super.onToolClick2(aTool, aRemainingDurability, aQuality, aPlayer, aChatReturn, aPlayerInventory, aSneaking, aStack, aSide, aHitX, aHitY, aHitZ);
      if (rReturn > 0L) {
         return rReturn;
      } else if (this.isClientSide()) {
         return 0L;
      } else {
         return aTool.equals("plunger") ? GarbageGT.trash(this.mTank) : 0L;
      }
   }

   public void onMagnifyingGlass2(List<String> aChatReturn) {
      aChatReturn.add(this.mTank.content("WARNING: NO LUBRICANT!!!"));
   }

   public long doInject(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {
      if (this.mEnergy > this.energyCapacity()) {
         return 0L;
      } else {
         aSize = Math.abs(aSize);
         if (!aDoInject) {
            return aAmount;
         } else if (aSize > this.getEnergySizeInputMax(aEnergyType, aSide)) {
            this.explode((double)6.0F);
            return aAmount;
         } else {
            this.mEnergy += aAmount * aSize;
            return aAmount;
         }
      }
   }

   public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {
      return !aEmitting && aEnergyType == this.mEnergyTypeAccepted;
   }

   public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {
      return this.isEnergyType(aEnergyType, aSide, false);
   }

   public boolean isEnergyCapacitorType(TagData aEnergyType, byte aSide) {
      return aEnergyType == this.mEnergyTypeAccepted;
   }

   public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {
      return this.powerPerTick();
   }

   public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {
      return this.powerPerTick();
   }

   public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {
      return Math.min(512L, this.powerPerTick());
   }

   public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {
      return this.powerPerTick();
   }

   public long getEnergyStored(TagData aEnergyType, byte aSide) {
      return aEnergyType == this.mEnergyTypeAccepted ? this.mEnergy : 0L;
   }

   public long getEnergyCapacity(TagData aEnergyType, byte aSide) {
      return aEnergyType == this.mEnergyTypeAccepted ? this.energyCapacity() : 0L;
   }

   public Collection<TagData> getEnergyTypes(byte aSide) {
      return this.mEnergyTypeAccepted.AS_LIST;
   }

   public Collection<TagData> getEnergyCapacitorTypes(byte aSide) {
      return this.mEnergyTypeAccepted.AS_LIST;
   }

   private long powerPerTick() {
      return DrillConfig.active.miningPowerPerTick;
   }

   private long energyCapacity() {
      return this.powerPerTick() * 20L;
   }

   protected IFluidTank getFluidTankFillable(MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToFill) {
      return null;
   }

   protected IFluidTank getFluidTankDrainable(MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToDrain) {
      return this.mTank;
   }

   protected IFluidTank[] getFluidTanks(MultiTileEntityMultiBlockPart aPart, byte aSide) {
      return this.mTank.AS_ARRAY;
   }

   protected IFluidTank getFluidTankFillable2(byte aSide, FluidStack aFluidToFill) {
      return FluidsGT.LUBRICANT.contains(aFluidToFill.getFluid().getName()) ? this.mTank : null;
   }

   protected IFluidTank getFluidTankDrainable2(byte aSide, FluidStack aFluidToDrain) {
      return this.mTank;
   }

   protected IFluidTank[] getFluidTanks2(byte aSide) {
      return this.mTank.AS_ARRAY;
   }

   public void onTick2(long aTimer, boolean aIsServerSide) {
      super.onTick2(aTimer, aIsServerSide);
      if (aIsServerSide) {
         for(int i = 0; i < 16 && !this.inventoryEmpty(); ++i) {
            ST.move(this.delegator((byte)1), this.getAdjacentInventory((byte)1));
         }

         boolean tFormed = this.checkStructure(false);
         if (!tFormed && this.mWasFormed) {
            this.clearRod();
         }

         this.mWasFormed = tFormed;
         if (!this.mLogic.ready() && tFormed) {
            this.mLogic.reset(this.field_145848_d - 4);
         }

         if (this.mLogic.mDone) {
            this.clearRod();
         }

         boolean tDebug = Craftiffia.DEBUG;
         long tPower = this.powerPerTick();
         long tLube = DrillConfig.active.miningLubricantPerOperation;
         double tInterval = DrillConfig.active.miningTicksPerOperation;
         if (tFormed && !this.mLogic.mDone && (tDebug || this.mEnergy >= tPower && this.mTank.has(tLube) && this.inventoryEmpty())) {
            if (!tDebug) {
               this.mEnergy -= tPower;
            }

            if (++this.mProgress >= tInterval) {
               this.mProgress -= tInterval;
               if (!tDebug) {
                  this.mTank.drainAll(tLube);
               }

               int tPrevY = this.mLogic.mY;

               for(ItemStack tStack : this.mLogic.mineStep(this.field_145850_b, this.field_145851_c, this.field_145849_e, 1)) {
                  this.addOutput(tStack);
               }

               if (this.mLogic.mY < tPrevY) {
                  this.placeRod();
               }
            }
         }

      }
   }

   public void func_145843_s() {
      if (this.isServerSide()) {
         TileEntity tPending = (TileEntity)CS.LAST_BROKEN_TILEENTITY.get();
         this.clearRod();
         CS.LAST_BROKEN_TILEENTITY.set(tPending);
      }

      super.func_145843_s();
   }

   private boolean isRodBlock(int y) {
      TileEntity te = this.field_145850_b.func_147438_o(this.field_145851_c, y, this.field_145849_e);
      if (!(te instanceof IMultiTileEntity)) {
         return false;
      } else {
         IMultiTileEntity mte = (IMultiTileEntity)te;
         return mte.getMultiTileEntityID() == 4 && mte.getMultiTileEntityRegistryID() == (short)Craftiffia.REGISTRY.currentID();
      }
   }

   private void placeRod() {
      if (this.mLogic.ready()) {
         ItemStack tRod = Craftiffia.REGISTRY.getItem(4);

         for(int y = this.field_145848_d - 4; y >= this.mLogic.mY; --y) {
            if (!this.isRodBlock(y)) {
               WD.set(this.field_145850_b, this.field_145851_c, y, this.field_145849_e, tRod);
            }
         }

      }
   }

   private void clearRod() {
      for(int y = this.field_145848_d - 4; y >= 1 && this.isRodBlock(y); --y) {
         this.field_145850_b.func_147468_f(this.field_145851_c, y, this.field_145849_e);
      }

   }

   private boolean inventoryEmpty() {
      for(int i = 0; i < this.func_70302_i_(); ++i) {
         if (this.slotHas(i)) {
            return false;
         }
      }

      return true;
   }

   private void addOutput(ItemStack aStack) {
      for(int i = 0; i < this.func_70302_i_() && aStack.field_77994_a > 0; ++i) {
         ItemStack tSlot = this.slot(i);
         if (tSlot == null) {
            this.slot(i, aStack.func_77946_l());
            return;
         }

         if (ST.equal(tSlot, aStack, true)) {
            int tMove = Math.min(tSlot.func_77976_d() - tSlot.field_77994_a, aStack.field_77994_a);
            tSlot.field_77994_a += tMove;
            aStack.field_77994_a -= tMove;
         }
      }

      if (aStack.field_77994_a > 0) {
         this.field_145850_b.func_72838_d(new EntityItem(this.field_145850_b, (double)this.field_145851_c + (double)0.5F, (double)this.field_145848_d + (double)1.5F, (double)this.field_145849_e + (double)0.5F, aStack));
      }

   }

   public ItemStack[] getDefaultInventory(NBTTagCompound aNBT) {
      return new ItemStack[16];
   }

   public boolean canDrop(int aInventorySlot) {
      return true;
   }

   public int[] getAccessibleSlotsFromSide2(byte aSide) {
      return ACCESSIBLE_SLOTS;
   }

   public boolean canInsertItem2(int aSlot, ItemStack aStack, byte aSide) {
      return false;
   }

   public boolean canExtractItem2(int aSlot, ItemStack aStack, byte aSide) {
      return true;
   }

   public String getTileEntityName() {
      return "craftiffia.multiblock.drill.mining";
   }

   public boolean getStateRunningPossible() {
      return this.mStructureOkay && (Craftiffia.DEBUG || this.mTank.has(DrillConfig.active.miningLubricantPerOperation));
   }

   static {
      LH.add("gt.tooltip.multiblock.craftiffia.mining.1", "3x3x3 of Titanium Walls with a vertical core of 3 Mining Drill Heads");
      LH.add("gt.tooltip.multiblock.craftiffia.mining.2", "Controller on top centre facing up; Energy on the 4 mid-height side walls; Lubricant fed directly into the Controller");
      LH.add("gt.tooltip.multiblock.craftiffia.mining.3", "Requires Lubricant and Power to mine a 3x3 chunk area downwards");
      LH.add("gt.tooltip.multiblock.craftiffia.mining.4", "Mines a 48x48 region straight down, turning Ores to Stone");
      ACCESSIBLE_SLOTS = new int[16];

      for(int i = 0; i < 16; ACCESSIBLE_SLOTS[i] = i++) {
      }

   }
}
