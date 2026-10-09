package me.almana;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class DrillConfig {
   public static DrillConfig active = defaults();
   public static final String FILE_NAME = "drills.yml";
   public static final int ORES_PER_OPERATION = 1;
   public final long miningPowerPerTick;
   public final long horizontalPowerPerTick;
   public final boolean chunkload;
   public final int chunkRadius;
   public final long prospectorMV;
   public final long prospectorHV;
   public final long prospectorEV;
   public final int miningStoneChance;
   public final int horizontalStoneChance;
   public final int rockOreLayerSkipChance;
   public final double miningTicksPerOperation;
   public final double horizontalTicksPerOperation;
   public final long miningLubricantPerOperation;
   public final long horizontalLubricantPerOperation;

   public DrillConfig(long aMiningPowerPerTick, long aHorizontalPowerPerTick, boolean aChunkload, int aChunkRadius, long aProspectorMV, long aProspectorHV, long aProspectorEV, int aMiningStoneChance, int aHorizontalStoneChance, int aRockOreLayerSkipChance, double aMiningTicksPerOperation, double aHorizontalTicksPerOperation, long aMiningLubricantPerOperation, long aHorizontalLubricantPerOperation) {
      this.miningPowerPerTick = aMiningPowerPerTick;
      this.horizontalPowerPerTick = aHorizontalPowerPerTick;
      this.chunkload = aChunkload;
      this.chunkRadius = aChunkRadius;
      this.prospectorMV = aProspectorMV;
      this.prospectorHV = aProspectorHV;
      this.prospectorEV = aProspectorEV;
      this.miningStoneChance = aMiningStoneChance;
      this.horizontalStoneChance = aHorizontalStoneChance;
      this.rockOreLayerSkipChance = aRockOreLayerSkipChance;
      this.miningTicksPerOperation = aMiningTicksPerOperation;
      this.horizontalTicksPerOperation = aHorizontalTicksPerOperation;
      this.miningLubricantPerOperation = aMiningLubricantPerOperation;
      this.horizontalLubricantPerOperation = aHorizontalLubricantPerOperation;
   }

   public static DrillConfig defaults() {
      return new DrillConfig(20480L, 4096L, true, 1, 1024L, 6144L, 36864L, 90, 90, 0, (double)1.5F, (double)6.0F, 50L, 100L);
   }

   public static DrillConfig parse(String aYaml) {
      DrillConfig tBase = defaults();
      int tMineStone = tBase.miningStoneChance;
      int tHorizStone = tBase.horizontalStoneChance;
      long tProspMV = tBase.prospectorMV;
      long tProspHV = tBase.prospectorHV;
      long tProspEV = tBase.prospectorEV;
      int tRockOreSkip = tBase.rockOreLayerSkipChance;
      double tMiningTicks = tBase.miningTicksPerOperation;
      double tHorizTicks = tBase.horizontalTicksPerOperation;
      long tMiningLube = tBase.miningLubricantPerOperation;
      long tHorizLube = tBase.horizontalLubricantPerOperation;
      long tMiningPower = tBase.miningPowerPerTick;
      long tHorizPower = tBase.horizontalPowerPerTick;

      for(String tLine : aYaml.split("\\r?\\n")) {
         String tTrimmed = trimComment(tLine).trim();
         if (!tTrimmed.isEmpty()) {
            String[] tParts = tTrimmed.split(":", 2);
            if (tParts.length == 2) {
               String tKey = tParts[0].trim();
               String tValue = tParts[1].trim();
               if ("mining_drill_stone_chance".equals(tKey)) {
                  tMineStone = readInt(tValue, tMineStone);
               }

               if ("horizontal_drill_stone_chance".equals(tKey)) {
                  tHorizStone = readInt(tValue, tHorizStone);
               }

               if ("prospector_mv_energy".equals(tKey)) {
                  tProspMV = readLong(tValue, tProspMV);
               }

               if ("prospector_hv_energy".equals(tKey)) {
                  tProspHV = readLong(tValue, tProspHV);
               }

               if ("prospector_ev_energy".equals(tKey)) {
                  tProspEV = readLong(tValue, tProspEV);
               }

               if ("rock_ore_layer_skip_chance".equals(tKey)) {
                  tRockOreSkip = readInt(tValue, tRockOreSkip);
               }

               if ("mining_drill_ticks_per_operation".equals(tKey)) {
                  tMiningTicks = readDouble(tValue, tMiningTicks);
               }

               if ("horizontal_drill_ticks_per_operation".equals(tKey)) {
                  tHorizTicks = readDouble(tValue, tHorizTicks);
               }

               if ("mining_drill_lubricant_per_operation".equals(tKey)) {
                  tMiningLube = readLong(tValue, tMiningLube);
               }

               if ("horizontal_drill_lubricant_per_operation".equals(tKey)) {
                  tHorizLube = readLong(tValue, tHorizLube);
               }

               if ("mining_drill_power_per_tick".equals(tKey)) {
                  tMiningPower = readLong(tValue, tMiningPower);
               }

               if ("horizontal_drill_power_per_tick".equals(tKey)) {
                  tHorizPower = readLong(tValue, tHorizPower);
               }

            }
         }
      }

      if (tMineStone < 0 || tMineStone > 100) {
         reject("mining_drill_stone_chance");
         tMineStone = tBase.miningStoneChance;
      }

      if (tHorizStone < 0 || tHorizStone > 100) {
         reject("horizontal_drill_stone_chance");
         tHorizStone = tBase.horizontalStoneChance;
      }

      if (tRockOreSkip < 0 || tRockOreSkip > 100) {
         reject("rock_ore_layer_skip_chance");
         tRockOreSkip = tBase.rockOreLayerSkipChance;
      }

      if (tProspMV < 0L) {
         reject("prospector_mv_energy");
         tProspMV = tBase.prospectorMV;
      }

      if (tProspHV < 0L) {
         reject("prospector_hv_energy");
         tProspHV = tBase.prospectorHV;
      }

      if (tProspEV < 0L) {
         reject("prospector_ev_energy");
         tProspEV = tBase.prospectorEV;
      }

      if (tMiningTicks <= (double)0.0F) {
         reject("mining_drill_ticks_per_operation");
         tMiningTicks = tBase.miningTicksPerOperation;
      }

      if (tHorizTicks <= (double)0.0F) {
         reject("horizontal_drill_ticks_per_operation");
         tHorizTicks = tBase.horizontalTicksPerOperation;
      }

      if (tMiningLube < 0L) {
         reject("mining_drill_lubricant_per_operation");
         tMiningLube = tBase.miningLubricantPerOperation;
      }

      if (tHorizLube < 0L) {
         reject("horizontal_drill_lubricant_per_operation");
         tHorizLube = tBase.horizontalLubricantPerOperation;
      }

      if (tMiningPower < 1L) {
         reject("mining_drill_power_per_tick");
         tMiningPower = tBase.miningPowerPerTick;
      }

      if (tHorizPower < 1L) {
         reject("horizontal_drill_power_per_tick");
         tHorizPower = tBase.horizontalPowerPerTick;
      }

      return new DrillConfig(tMiningPower, tHorizPower, tBase.chunkload, tBase.chunkRadius, tProspMV, tProspHV, tProspEV, tMineStone, tHorizStone, tRockOreSkip, tMiningTicks, tHorizTicks, tMiningLube, tHorizLube);
   }

   public static DrillConfig load(File aConfigDir) {
      File tDir = new File(aConfigDir, "craftiffia");
      File tFile = new File(tDir, "drills.yml");

      try {
         active = tFile.exists() ? parse(read(tFile)) : defaults();
         write(tDir, tFile, toYaml(active));
      } catch (IOException e) {
         Craftiffia.LOG.warn("Failed to load drill config, using defaults", e);
         active = defaults();
      }

      return active;
   }

   public static String toYaml(DrillConfig aConfig) {
      return "# Percent chance a mined ore yields plain stone instead of the ore drop\nmining_drill_stone_chance: " + aConfig.miningStoneChance + "\nhorizontal_drill_stone_chance: " + aConfig.horizontalStoneChance + "\n# EU consumed per scan by each ore prospector tier\nprospector_mv_energy: " + aConfig.prospectorMV + "\nprospector_hv_energy: " + aConfig.prospectorHV + "\nprospector_ev_energy: " + aConfig.prospectorEV + "\n# Percent chance a Rock Ore layer block (gregtech:gt.block.rockores) is skipped entirely\nrock_ore_layer_skip_chance: " + aConfig.rockOreLayerSkipChance + "\n# Ticks between each mining/boring operation (delay per operation, higher = slower)\nmining_drill_ticks_per_operation: " + num(aConfig.miningTicksPerOperation) + "\nhorizontal_drill_ticks_per_operation: " + num(aConfig.horizontalTicksPerOperation) + "\n# Lubricant (mB) consumed each time a mining/boring operation completes\nmining_drill_lubricant_per_operation: " + aConfig.miningLubricantPerOperation + "\nhorizontal_drill_lubricant_per_operation: " + aConfig.horizontalLubricantPerOperation + "\n# RU consumed every tick each drill is running; also sets the max input voltage per wall\nmining_drill_power_per_tick: " + aConfig.miningPowerPerTick + "\nhorizontal_drill_power_per_tick: " + aConfig.horizontalPowerPerTick + "\n";
   }

   private static String num(double aValue) {
      return aValue == Math.rint(aValue) ? String.valueOf((long)aValue) : String.valueOf(aValue);
   }

   private static void reject(String aKey) {
      Craftiffia.LOG.warn("Drill config value out of range for " + aKey + ", using the default");
   }

   private static String trimComment(String aLine) {
      int tComment = aLine.indexOf(35);
      return tComment >= 0 ? aLine.substring(0, tComment) : aLine;
   }

   private static int readInt(String aValue, int aFallback) {
      try {
         return Integer.parseInt(aValue);
      } catch (NumberFormatException var3) {
         return aFallback;
      }
   }

   private static long readLong(String aValue, long aFallback) {
      try {
         return Long.parseLong(aValue);
      } catch (NumberFormatException var4) {
         return aFallback;
      }
   }

   private static double readDouble(String aValue, double aFallback) {
      try {
         return Double.parseDouble(aValue);
      } catch (NumberFormatException var4) {
         return aFallback;
      }
   }

   private static String read(File aFile) throws IOException {
      StringBuilder rYaml = new StringBuilder();
      BufferedReader tReader = new BufferedReader(new InputStreamReader(new FileInputStream(aFile), StandardCharsets.UTF_8));

      try {
         for(String tLine = tReader.readLine(); tLine != null; tLine = tReader.readLine()) {
            rYaml.append(tLine).append('\n');
         }
      } finally {
         tReader.close();
      }

      return rYaml.toString();
   }

   private static void write(File aDir, File aFile, String aYaml) throws IOException {
      if (!aDir.exists()) {
         aDir.mkdirs();
      }

      BufferedWriter tWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile), StandardCharsets.UTF_8));

      try {
         tWriter.write(aYaml);
      } finally {
         tWriter.close();
      }

   }
}
