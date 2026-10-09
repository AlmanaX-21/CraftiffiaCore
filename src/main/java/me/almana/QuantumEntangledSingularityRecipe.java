package me.almana;

import gregapi.recipes.Recipe;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class QuantumEntangledSingularityRecipe extends Recipe {
   private final QuantumEntangledSingularityOutput mOutput = new QuantumEntangledSingularityOutput();

   public QuantumEntangledSingularityRecipe(ItemStack aInput, FluidStack[] aFluidInputs, ItemStack aOutput, long aEnergy, long aDuration) {
      super(true, true, true, new ItemStack[] {aInput}, new ItemStack[] {aOutput}, null, new long[] {10000}, aFluidInputs, new FluidStack[0], aDuration, aEnergy, 0);
   }

   @Override
   public ItemStack[] getOutputs(Random aRandom, int aProcessCount) {
      return this.mOutput.create(this.mOutputs[0], aProcessCount);
   }
}
