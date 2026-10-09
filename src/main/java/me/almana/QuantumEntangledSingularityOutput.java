package me.almana;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class QuantumEntangledSingularityOutput {
   private final AtomicLong mNextFrequency = new AtomicLong(System.currentTimeMillis());

   public ItemStack[] create(ItemStack aTemplate, int aProcessCount) {
      ItemStack[] tOutputs = new ItemStack[aProcessCount * 2];

      for (int i = 0; i < aProcessCount; i++) {
         long tFrequency = this.mNextFrequency.incrementAndGet();
         tOutputs[i * 2] = this.createOutput(aTemplate, tFrequency);
         tOutputs[i * 2 + 1] = this.createOutput(aTemplate, tFrequency);
      }

      return tOutputs;
   }

   private ItemStack createOutput(ItemStack aTemplate, long aFrequency) {
      ItemStack tOutput = aTemplate.func_77946_l();
      NBTTagCompound tTag = new NBTTagCompound();
      tTag.func_74772_a("freq", aFrequency);
      tOutput.func_77982_d(tTag);
      tOutput.stackSize = 1;
      return tOutput;
   }
}
