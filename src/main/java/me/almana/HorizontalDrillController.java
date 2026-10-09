package me.almana;

import com.google.common.collect.UnmodifiableIterator;
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
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class HorizontalDrillController extends TileEntityBase10MultiBlockBase implements ITileEntityEnergy, ITileEntityEnergyDataCapacitor, IMultiBlockEnergy, IMultiBlockFluidHandler, IFluidHandler, ITileEntityRunningPossible, IMultiTileEntity.IMTE_BreakBlock {
   public static final int OUT_SLOTS = 16;
   public static final int DENSE_WALL = 18026;
   public static final int ENERGY_DEPTH = 3;
   public static final int POWER_BUFFER_TICKS = 20;
   public long mEnergy = 0L;
   public TagData mEnergyTypeAccepted;
   public FluidTankGT mTank;
   public final HorizontalMineLogic mLogic;
   private boolean mWasFormed;
   private ForgeChunkManager.Ticket mTicket;
   private double mProgress;
   private static final int[] ACCESSIBLE_SLOTS;

   public HorizontalDrillController() {
      this.mEnergyTypeAccepted = Energy.RU;
      this.mTank = new FluidTankGT(16000L);
      this.mLogic = new HorizontalMineLogic();
      this.mWasFormed = false;
      this.mTicket = null;
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
      if (!CS.SIDES_HORIZONTAL[this.mFacing]) {
         return false;
      } else if (this.field_145848_d < 4) {
         return false;
      } else {
         boolean tSuccess = true;
         int tWalls = MultiTileEntityRegistry.getRegistry("gt.multitileentity").currentID();
         int tSelf = this.getMultiTileEntityRegistryID();
         boolean tAxisX = CS.SIDES_AXIS_X[this.mFacing];
         int fx = CS.OFFX[this.fwd()];
         int fz = CS.OFFZ[this.fwd()];
         int rx = tAxisX ? 0 : 1;
         int rz = tAxisX ? 1 : 0;

         for(int u = -1; u <= 1; ++u) {
            for(int r = -1; r <= 1; ++r) {
               for(int depth = 0; depth <= 5; ++depth) {
                  int dx = fx * depth + rx * r;
                  int dz = fz * depth + rz * r;
                  if (depth != 0) {
                     if (r == 0) {
                        if (u == 0) {
                           if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, u, dz, 5, tSelf, 0, -1, aCoordinates, aPlayer, aInventory)) {
                              tSuccess = false;
                           }
                        } else if (depth == 3) {
                           if (!this.checkEnergyWall(dx, u, dz, tSelf, tWalls, aCoordinates, aPlayer, aInventory)) {
                              tSuccess = false;
                           }
                        } else if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, u, dz, 18026, tWalls, 0, -1, aCoordinates, aPlayer, aInventory)) {
                           tSuccess = false;
                        }
                     } else if (u == 0) {
                        if (depth == 3) {
                           if (!this.checkEnergyWall(dx, u, dz, tSelf, tWalls, aCoordinates, aPlayer, aInventory)) {
                              tSuccess = false;
                           }
                        } else if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, u, dz, 18026, tWalls, 0, -1, aCoordinates, aPlayer, aInventory)) {
                           tSuccess = false;
                        }
                     } else if (!ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, u, dz, 18026, tWalls, 0, -1, aCoordinates, aPlayer, aInventory)) {
                        tSuccess = false;
                     }
                  }
               }
            }
         }

         if (this.isServerSide()) {
            this.syncEnergyPorts(tSuccess);
         }

         return tSuccess;
      }
   }

   public boolean breakBlock() {
      if (this.isServerSide()) {
         this.syncEnergyPorts(false);
      }

      return false;
   }

   private boolean checkEnergyWall(int dx, int dy, int dz, int tSelf, int tWalls, ChunkCoordinates aCoordinates, Entity aPlayer, IInventory aInventory) {
      return ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, dy, dz, 9, tSelf, 3, -3, aCoordinates, aPlayer, aInventory) ? true : ITileEntityMultiBlockController.Util.checkAndSetTargetOffset(this, dx, dy, dz, 18026, tWalls, 0, -1, aCoordinates, aPlayer, aInventory);
   }

   private void syncEnergyPorts(boolean aFormed) {
      TileEntity tPending = (TileEntity)CS.LAST_BROKEN_TILEENTITY.get();
      MultiTileEntityRegistry tGT = MultiTileEntityRegistry.getRegistry("gt.multitileentity");
      int tOld = aFormed ? 18026 : 9;
      short tOldReg = (short)(aFormed ? tGT.currentID() : Craftiffia.REGISTRY.currentID());
      ItemStack tNew = aFormed ? Craftiffia.REGISTRY.getItem(9) : tGT.getItem(18026);
      int fx = CS.OFFX[this.fwd()];
      int fz = CS.OFFZ[this.fwd()];
      int rx = CS.SIDES_AXIS_X[this.mFacing] ? 0 : 1;
      int rz = CS.SIDES_AXIS_X[this.mFacing] ? 1 : 0;

      for(int u = -1; u <= 1; ++u) {
         for(int r = -1; r <= 1; ++r) {
            if (u == 0 != (r == 0)) {
               int x = this.field_145851_c + fx * 3 + rx * r;
               int y = this.field_145848_d + u;
               int z = this.field_145849_e + fz * 3 + rz * r;
               TileEntity tTE = this.field_145850_b.func_147438_o(x, y, z);
               if (tTE instanceof IMultiTileEntity) {
                  IMultiTileEntity tMTE = (IMultiTileEntity)tTE;
                  if (tMTE.getMultiTileEntityID() == tOld && tMTE.getMultiTileEntityRegistryID() == tOldReg && (aFormed || ((MultiTileEntityMultiBlockPart)tTE).getTarget(false) == this)) {
                     WD.set(this.field_145850_b, x, y, z, tNew);
                     if (aFormed) {
                        TileEntity tPort = this.field_145850_b.func_147438_o(x, y, z);
                        if (tPort instanceof MultiTileEntityMultiBlockPart) {
                           ((MultiTileEntityMultiBlockPart)tPort).setTarget(this, 3, -3);
                        }
                     }
                  }
               }
            }
         }
      }

      CS.LAST_BROKEN_TILEENTITY.set(tPending);
   }

   public void addToolTips(List<String> aList, ItemStack aStack, boolean aF3_H) {
      aList.add(Chat.CYAN + LH.get("gt.lang.structure") + ":");
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.horizontal.1"));
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.horizontal.2"));
      aList.add(Chat.WHITE + LH.get("gt.tooltip.multiblock.craftiffia.horizontal.3"));
      long tPower = this.powerPerTick();
      String tUnit = this.mEnergyTypeAccepted.getLocalisedChatNameShort();
      aList.add(Chat.GREEN + LH.get("gt.lang.energy.input") + ": " + Chat.WHITE + Math.min(512L, tPower) + " to " + tPower + " " + tUnit + Chat.WHITE + "/t (up to " + tPower * 4L + " " + tUnit + Chat.WHITE + "/t total)");
      aList.add(Chat.GREEN + "Consumption: " + Chat.WHITE + tPower + " " + tUnit + Chat.WHITE + "/t + " + DrillConfig.active.horizontalLubricantPerOperation + " mB Lubricant/operation");
      super.addToolTips(aList, aStack, aF3_H);
   }

   public boolean isInsideStructure(int aX, int aY, int aZ) {
      int xa = this.field_145851_c;
      int xb = this.field_145851_c + CS.OFFX[this.fwd()] * 5;
      int za = this.field_145849_e;
      int zb = this.field_145849_e + CS.OFFZ[this.fwd()] * 5;
      int minX;
      int maxX;
      int minZ;
      int maxZ;
      if (CS.SIDES_AXIS_X[this.mFacing]) {
         minX = Math.min(xa, xb);
         maxX = Math.max(xa, xb);
         minZ = this.field_145849_e - 1;
         maxZ = this.field_145849_e + 1;
      } else {
         minZ = Math.min(za, zb);
         maxZ = Math.max(za, zb);
         minX = this.field_145851_c - 1;
         maxX = this.field_145851_c + 1;
      }

      return aX >= minX && aX <= maxX && aY >= this.field_145848_d - 1 && aY <= this.field_145848_d + 1 && aZ >= minZ && aZ <= maxZ;
   }

   public byte getDefaultSide() {
      return 3;
   }

   public boolean[] getValidSides() {
      return CS.SIDES_HORIZONTAL;
   }

   private byte fwd() {
      return CS.OPOS[this.mFacing];
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
      return DrillConfig.active.horizontalPowerPerTick;
   }

   private long energyCapacity() {
      return this.powerPerTick() * 20L;
   }

   protected IFluidTank getFluidTankFillable(MultiTileEntityMultiBlockPart aPart, byte aSide, FluidStack aFluidToFill) {
      return FluidsGT.LUBRICANT.contains(aFluidToFill.getFluid().getName()) ? this.mTank : null;
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
         byte tOut = this.mFacing;

         for(int i = 0; i < 16 && !this.inventoryEmpty(); ++i) {
            ST.move(this.delegator(tOut), this.getAdjacentInventory(tOut));
         }

         boolean tFormed = this.checkStructure(false);
         if (!tFormed && this.mWasFormed) {
            this.clearRod();
            this.releaseTicket();
         }

         this.mWasFormed = tFormed;
         if (tFormed) {
            this.ensureChunkload();
            boolean tDebug = Craftiffia.DEBUG;
            long tPower = this.powerPerTick();
            long tLube = DrillConfig.active.horizontalLubricantPerOperation;
            double tInterval = DrillConfig.active.horizontalTicksPerOperation;
            if (tDebug || this.mEnergy >= tPower && this.mTank.has(tLube) && this.inventoryEmpty()) {
               if (!tDebug) {
                  this.mEnergy -= tPower;
               }

               if (++this.mProgress >= tInterval) {
                  this.mProgress -= tInterval;
                  if (!tDebug) {
                     this.mTank.drainAll(tLube);
                  }

                  int tMinedDepth = this.mLogic.frontierDist();
                  this.loadFrontierChunks();

                  for(ItemStack tStack : this.mLogic.mineStep(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e, this.fwd(), 1)) {
                     this.addOutput(tStack);
                  }

                  this.placeRod(tMinedDepth);
                  if (this.mLogic.mAdvanced) {
                     this.updateFrontierChunks();
                  }
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
         this.releaseTicket();
      }

      super.func_145843_s();
   }

   public void adoptTicket(ForgeChunkManager.Ticket aTicket) {
      this.mTicket = aTicket;
   }

   private void ensureChunkload() {
      if (DrillConfig.active.chunkload) {
         if (this.mTicket == null) {
            ForgeChunkManager.Ticket tTicket = DrillChunkLoader.claim(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
            if (tTicket == null) {
               tTicket = DrillChunkLoader.request(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e);
            }

            this.mTicket = tTicket;
            if (this.mTicket != null) {
               ForgeChunkManager.forceChunk(this.mTicket, new ChunkCoordIntPair(this.field_145851_c >> 4, this.field_145849_e >> 4));
            }
         }

         this.updateFrontierChunks();
      }
   }

   private List<ChunkCoordIntPair> loadFrontierChunks() {
      List<ChunkCoordIntPair> rChunks = this.frontierChunks();

      for(ChunkCoordIntPair tChunk : HorizontalMineLogic.generationChunks(rChunks, DrillConfig.active.chunkRadius)) {
         this.field_145850_b.func_72964_e(tChunk.field_77276_a, tChunk.field_77275_b);
      }

      return rChunks;
   }

   private List<ChunkCoordIntPair> frontierChunks() {
      int tDist = this.mLogic.frontierDist();
      return HorizontalMineLogic.frontierChunks(this.field_145851_c + CS.OFFX[this.fwd()] * tDist, this.field_145849_e + CS.OFFZ[this.fwd()] * tDist, CS.SIDES_AXIS_X[this.mFacing]);
   }

   private void updateFrontierChunks() {
      if (this.mTicket != null) {
         List<ChunkCoordIntPair> tNew = this.loadFrontierChunks();
         ChunkCoordIntPair tController = new ChunkCoordIntPair(this.field_145851_c >> 4, this.field_145849_e >> 4);
         UnmodifiableIterator var3 = this.mTicket.getChunkList().iterator();

         while(var3.hasNext()) {
            ChunkCoordIntPair tChunk = (ChunkCoordIntPair)var3.next();
            if (!tNew.contains(tChunk) && !tController.equals(tChunk)) {
               ForgeChunkManager.unforceChunk(this.mTicket, tChunk);
            }
         }

         for(ChunkCoordIntPair tChunk : tNew) {
            if (!this.mTicket.getChunkList().contains(tChunk)) {
               ForgeChunkManager.forceChunk(this.mTicket, tChunk);
            }
         }

         if (!this.mTicket.getChunkList().contains(tController)) {
            ForgeChunkManager.forceChunk(this.mTicket, tController);
         }

      }
   }

   private void releaseTicket() {
      if (this.mTicket != null) {
         ForgeChunkManager.releaseTicket(this.mTicket);
         this.mTicket = null;
      }

   }

   private boolean isRodBlock(int aX, int aY, int aZ) {
      TileEntity te = this.field_145850_b.func_147438_o(aX, aY, aZ);
      if (!(te instanceof IMultiTileEntity)) {
         return false;
      } else {
         IMultiTileEntity mte = (IMultiTileEntity)te;
         int tID = mte.getMultiTileEntityID();
         return (tID == 7 || tID == 8) && mte.getMultiTileEntityRegistryID() == (short)Craftiffia.REGISTRY.currentID();
      }
   }

   private void placeRod(int aDist) {
      ItemStack tRod = Craftiffia.REGISTRY.getItem(CS.SIDES_AXIS_X[this.mFacing] ? 7 : 8);
      int x = this.field_145851_c + CS.OFFX[this.fwd()] * aDist;
      int z = this.field_145849_e + CS.OFFZ[this.fwd()] * aDist;
      if (this.field_145850_b.func_72899_e(x, this.field_145848_d, z)) {
         if (!this.isRodBlock(x, this.field_145848_d, z)) {
            WD.set(this.field_145850_b, x, this.field_145848_d, z, tRod);
         }

      }
   }

   private void clearRod() {
      int tLastDepth = 6 + this.mLogic.mDepth;
      int tLastCX = Integer.MIN_VALUE;
      int tLastCZ = Integer.MIN_VALUE;

      for(int d = 6; d <= tLastDepth; ++d) {
         int x = this.field_145851_c + CS.OFFX[this.fwd()] * d;
         int z = this.field_145849_e + CS.OFFZ[this.fwd()] * d;
         int cx = x >> 4;
         int cz = z >> 4;
         if (cx != tLastCX || cz != tLastCZ) {
            this.field_145850_b.func_72964_e(cx, cz);
            tLastCX = cx;
            tLastCZ = cz;
         }

         if (!this.isRodBlock(x, this.field_145848_d, z)) {
            break;
         }

         this.field_145850_b.func_147468_f(x, this.field_145848_d, z);
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
      return "craftiffia.multiblock.drill.horizontal";
   }

   public boolean getStateRunningPossible() {
      return this.mStructureOkay && (Craftiffia.DEBUG || this.mTank.has(DrillConfig.active.horizontalLubricantPerOperation));
   }

   static {
      LH.add("gt.tooltip.multiblock.craftiffia.horizontal.1", "5 Horizontal Mining Drill Heads in a line along the bore, Controller behind them, wrapped in a 3-wide shell of Dense Titanium Walls");
      LH.add("gt.tooltip.multiblock.craftiffia.horizontal.2", "Energy on all 4 side walls at the middle head layer only; Lubricant fed directly into the Controller");
      LH.add("gt.tooltip.multiblock.craftiffia.horizontal.3", "Bores a 15x15 area forwards forever, turning Ores to Stone, staying chunkloaded");
      ACCESSIBLE_SLOTS = new int[16];

      for(int i = 0; i < 16; ACCESSIBLE_SLOTS[i] = i++) {
      }

   }
}
