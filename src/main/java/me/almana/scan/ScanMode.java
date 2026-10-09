package me.almana.scan;

import gregapi.data.OP;
import gregapi.oredict.OreDictPrefix;

public enum ScanMode {
   NONE((OreDictPrefix)null),
   LARGE(OP.ore),
   SMALL(OP.oreSmall),
   DENSE_AND_NORMAL(OP.oreDense),
   BEDROCK(OP.oreBedrock),
   FLUID_BEDROCK(OP.bucket),
   ROCK(OP.rockGt),
   FLUID(OP.bucket);

   public final OreDictPrefix PREFIX;

   private ScanMode(OreDictPrefix op) {
      this.PREFIX = op;
   }

   public String local() {
      return this.PREFIX.mNameLocal;
   }

   public boolean isTE() {
      switch (this) {
         case LARGE:
         case ROCK:
         case BEDROCK:
         case SMALL:
         case FLUID_BEDROCK:
            return true;
         default:
            return false;
      }
   }
}
