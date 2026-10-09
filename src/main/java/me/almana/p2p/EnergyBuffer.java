package me.almana.p2p;

import net.minecraft.nbt.NBTTagCompound;

public class EnergyBuffer {
   public static final long MAX_AMPERES = 64L;
   private long mVoltage;
   private long mStored;

   public long offer(long aVoltage, long aAmperage, boolean aDoInject) {
      if (aVoltage <= 0L || aAmperage <= 0L) return 0L;
      long tAccepted = Math.min(aAmperage, MAX_AMPERES - this.mStored / aVoltage);
      if (tAccepted <= 0L) return 0L;
      if (aDoInject) {
         this.mVoltage = aVoltage;
         this.mStored += tAccepted * aVoltage;
      }
      return tAccepted;
   }

   public long amperes() {
      return this.mVoltage <= 0L ? 0L : this.mStored / this.mVoltage;
   }

   public long voltage() {
      return this.mVoltage;
   }

   public void take(long aAmperes) {
      this.mStored -= aAmperes * this.mVoltage;
   }

   public void load(NBTTagCompound aNBT) {
      this.mVoltage = aNBT.getLong("gt.eu.voltage");
      this.mStored = aNBT.getLong("gt.eu.stored");
   }

   public void save(NBTTagCompound aNBT) {
      aNBT.setLong("gt.eu.voltage", this.mVoltage);
      aNBT.setLong("gt.eu.stored", this.mStored);
   }
}
