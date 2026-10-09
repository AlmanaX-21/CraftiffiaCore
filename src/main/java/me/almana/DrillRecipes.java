package me.almana;

import gregapi.data.ANY;
import gregapi.data.CS;
import gregapi.data.FL;
import gregapi.data.IL;
import gregapi.data.MD;
import gregapi.data.MT;
import gregapi.data.OD;
import gregapi.data.OP;
import gregapi.data.RM;
import gregapi.util.CR;
import gregapi.util.ST;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class DrillRecipes {
   public static final String CIRCUIT_MASTER_PLUS = "craftiffia:circuit_master";

   public static void register() {
      for(int t = 5; t <= 9; ++t) {
         for(ItemStack s : OreDictionary.getOres(CS.OD_CIRCUITS[t])) {
            OreDictionary.registerOre("craftiffia:circuit_master", s);
         }
      }

      ItemStack tHead = Craftiffia.REGISTRY.getItem(2);
      CR.shaped(tHead, CR.DEF_REV_NCC, new Object[]{"   ", "DMD", "GIG", 'D', OP.gem.dat(ANY.Diamond), 'M', OP.casingMachine.dat(MT.TungstenSteel), 'G', OP.gearGt.dat(MT.TungstenSteel), 'I', OP.toolHeadDrill.dat(MT.TungstenSteel)});
      ItemStack tController = Craftiffia.REGISTRY.getItem(3);
      CR.shaped(tController, CR.DEF_REV_NCC, new Object[]{"PYP", "CMC", "GIG", 'P', CS.OD_CIRCUITS[6], 'Y', IL.CONVEYERS[3], 'C', CS.OD_CIRCUITS[6], 'M', OP.casingMachine.dat(MT.Ti), 'G', OP.gearGtSmall.dat(MT.TungstenSteel), 'I', OP.toolHeadDrill.dat(MT.TungstenSteel)});
      ItemStack tHHead = Craftiffia.REGISTRY.getItem(5);
      CR.shaped(tHHead, CR.DEF_REV_NCC, new Object[]{"GDG", "IMI", "GDG", 'D', OP.gem.dat(ANY.Diamond), 'G', OP.gearGt.dat(MT.TungstenSteel), 'I', OP.toolHeadDrill.dat(MT.TungstenSteel), 'M', OP.casingMachine.dat(MT.TungstenSteel)});
      ItemStack tHController = Craftiffia.REGISTRY.getItem(6);
      CR.shaped(tHController, CR.DEF_REV_NCC, new Object[]{"PCG", "YMI", "PCG", 'P', IL.Processor_Crystal_Emerald, 'C', "craftiffia:circuit_master", 'G', OP.gearGtSmall.dat(MT.TungstenSteel), 'Y', IL.CONVEYERS[4], 'M', OP.casingMachine.dat(MT.Ti), 'I', OP.toolHeadDrill.dat(MT.TungstenSteel)});
      CR.shaped(ST.make(Craftiffia.PROSPECTOR, 1L, 0L), CR.DEF_REV_NCC, new Object[]{"ELS", "CMC", "RBd", 'E', IL.EMITTERS[2], 'L', OD.paneGlass, 'S', IL.SENSORS[4], 'C', CS.OD_CIRCUITS[4], 'M', OP.casingSmall.dat(MT.Al), 'R', OP.screw.dat(MT.Al), 'B', "gt:re-battery2"});
      CR.shaped(ST.make(Craftiffia.PROSPECTOR, 1L, 1L), CR.DEF_REV_NCC, new Object[]{"ELS", "CMC", "RBd", 'E', IL.EMITTERS[3], 'L', OD.paneGlass, 'S', IL.SENSORS[5], 'C', CS.OD_CIRCUITS[6], 'M', OP.casingSmall.dat(MT.StainlessSteel), 'R', OP.screw.dat(MT.StainlessSteel), 'B', "gt:re-battery3"});
      CR.shaped(ST.make(Craftiffia.PROSPECTOR, 1L, 2L), CR.DEF_REV_NCC, new Object[]{"ELS", "CMC", "RBd", 'E', IL.EMITTERS[4], 'L', OD.paneGlass, 'S', IL.SENSORS[6], 'C', IL.Processor_Crystal_Ruby, 'M', OP.casingSmall.dat(MT.Cr), 'R', OP.screw.dat(MT.Cr), 'B', "gt:re-battery4"});

      if (MD.AE.mLoaded) {
         RM.Lightning.addRecipe(
            new QuantumEntangledSingularityRecipe(
               ST.make(MD.AE, "item.ItemMultiMaterial", 1L, 47L),
               new FluidStack[] {FL.Ender.make(144)},
               ST.make(MD.AE, "item.ItemMultiMaterial", 2L, 48L),
               CS.V[4],
               1000
            )
         );
         ItemStack tMeTunnel = ST.make(MD.AE, "item.ItemMultiPart", 1L, 460L);
         ItemStack tGtTunnel = ST.make(Craftiffia.GT_TUNNEL, 1L, 0L);
         CR.shapeless(tGtTunnel, CR.DEF_NCC, new Object[]{tMeTunnel});
         CR.shapeless(tMeTunnel, CR.DEF_NCC, new Object[]{tGtTunnel});
      }
   }
}
