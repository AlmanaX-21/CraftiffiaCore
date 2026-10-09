package me.almana;

import gregapi.block.MaterialMachines;
import gregapi.block.multitileentity.MultiTileEntityBlock;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.MT;
import gregapi.data.TD.Energy;
import gregapi.util.UT.NBT;
import net.minecraft.block.Block;

public class DrillBlocks implements Runnable {
   public static final int MINING_HEAD_ID = 2;
   public static final int MINING_CONTROLLER_ID = 3;
   public static final int DRILL_ROD_ID = 4;
   public static final int HORIZ_HEAD_ID = 5;
   public static final int HORIZ_CONTROLLER_ID = 6;
   public static final int HORIZ_ROD_X_ID = 7;
   public static final int HORIZ_ROD_Z_ID = 8;
   public static final int HORIZ_WALL_ENERGY_ID = 9;

   public void run() {
      MultiTileEntityRegistry aRegistry = Craftiffia.REGISTRY;
      MultiTileEntityBlock aMachine = MultiTileEntityBlock.getOrCreate("craftiffia", "machine", MaterialMachines.instance, Block.field_149777_j, "wrench", 0, 0, 15, true, true);
      MultiTileEntityBlock aRod = MultiTileEntityBlock.getOrCreate("craftiffia", "rod", MaterialMachines.instance, Block.field_149777_j, "wrench", 0, 0, 15, false, false);
      aRegistry.add("Craftiffia Test Casing", "Craftiffia", 1, 0, CraftiffiaPart.class, MT.Ti.mToolQuality, 64, aMachine, NBT.make("gt.material", MT.Ti, new Object[]{"gt.hardness", 9.0F, "gt.resistance", 9.0F, "gt.texture", "metalwall", "gt.designs", 7}), new Object[0]);
      aRegistry.add("Drill Rod Segment", "Craftiffia", 4, 0, DrillRodPart.class, MT.TungstenSteel.mToolQuality, 64, aRod, NBT.make("gt.material", MT.TungstenSteel, new Object[]{"gt.hardness", 12.5F, "gt.resistance", 12.5F, "gt.texture", "minerpipe", "gt.designs", 0}), new Object[0]);
      aRegistry.add("Mining Drill Head", "Craftiffia", 2, 0, CraftiffiaPart.class, MT.TungstenSteel.mToolQuality, 64, aMachine, NBT.make("gt.material", MT.TungstenSteel, new Object[]{"gt.hardness", 12.5F, "gt.resistance", 12.5F, "gt.texture", "bedrockdrill", "gt.designs", 0}), new Object[0]);
      aRegistry.add("Mining Drill Controller", "Craftiffia", 3, 0, MiningDrillController.class, MT.Ti.mToolQuality, 16, aMachine, NBT.make("gt.material", MT.Ti, new Object[]{"gt.hardness", 9.0F, "gt.resistance", 9.0F, "gt.texture", "bedrockdrill", "gt.energy.accepted", Energy.RU}), new Object[0]);
      aRegistry.add("Horizontal Mining Drill Head", "Craftiffia", 5, 0, HorizontalDrillHeadPart.class, MT.TungstenSteel.mToolQuality, 64, aMachine, NBT.make("gt.material", MT.TungstenSteel, new Object[]{"gt.hardness", 12.5F, "gt.resistance", 12.5F, "gt.texture", "bedrockdrill", "gt.designs", 0}), new Object[0]);
      aRegistry.add("Horizontal Mining Drill Controller", "Craftiffia", 6, 0, HorizontalDrillController.class, MT.Ti.mToolQuality, 16, aMachine, NBT.make("gt.material", MT.Ti, new Object[]{"gt.hardness", 9.0F, "gt.resistance", 9.0F, "gt.texture", "bedrockdrill", "gt.energy.accepted", Energy.RU}), new Object[0]);
      aRegistry.add("Drill Rod Segment X", "Craftiffia", 7, 0, HDrillRodPart.class, MT.TungstenSteel.mToolQuality, 64, aRod, NBT.make("gt.material", MT.TungstenSteel, new Object[]{"gt.hardness", 12.5F, "gt.resistance", 12.5F, "gt.texture", "minerpipe", "gt.designs", 0}), new Object[0]);
      aRegistry.add("Drill Rod Segment Z", "Craftiffia", 8, 0, HDrillRodPart.class, MT.TungstenSteel.mToolQuality, 64, aRod, NBT.make("gt.material", MT.TungstenSteel, new Object[]{"gt.hardness", 12.5F, "gt.resistance", 12.5F, "gt.texture", "minerpipe", "gt.designs", 0}), new Object[0]);
      aRegistry.add("Horizontal Drill Energy Wall", "Craftiffia", 9, 0, HorizontalDrillWallPart.class, MT.Ti.mToolQuality, 64, aMachine, NBT.make("gt.material", MT.Ti, new Object[]{"gt.hardness", 9.0F, "gt.resistance", 9.0F, "gt.texture", "metalwalldense", "gt.designs", 7}), new Object[0]);
   }
}
