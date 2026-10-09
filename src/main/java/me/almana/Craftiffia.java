package me.almana;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.MD;
import me.almana.p2p.GTEnergyTunnelItem;
import me.almana.scan.ProspectorNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "craftiffia",
   name = "Craftiffia-Core",
   version = "0.1.0",
   dependencies = "required-after:gregtech;required-after:modularui2;after:appliedenergistics2"
)
public class Craftiffia {
   public static final String MODID = "craftiffia";
   public static final String NAME = "Craftiffia-Core";
   public static final String VERSION = "0.1.0";
   public static final Logger LOG = LogManager.getLogger("craftiffia");
   public static Craftiffia INSTANCE;
   public static MultiTileEntityRegistry REGISTRY;
   public static ProspectorItem PROSPECTOR;
   public static Item GT_TUNNEL;
   public static boolean DEBUG = false;
   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      INSTANCE = this;
      DrillConfig.load(event.getModConfigurationDirectory());
      REGISTRY = new MultiTileEntityRegistry("craftiffia.multitileentity");
      ProspectorNetwork.init();
      DrillChunkLoader.init();
      (new DrillBlocks()).run();
      PROSPECTOR = new ProspectorItem();
      PROSPECTOR.func_77637_a((CreativeTabs)REGISTRY.mCreativeTabs.get((short)0));
      if (MD.AE.mLoaded) {
         GT_TUNNEL = GTEnergyTunnelItem.register((CreativeTabs)REGISTRY.mCreativeTabs.get((short)0));
      }
      LOG.info("Craftiffia-Core preInit: blocks + prospector item registered");
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      DrillRecipes.register();
      LOG.info("Craftiffia-Core init: recipes registered");
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new BuildDrillCommand());
      event.registerServerCommand(new BuildDrillCommand(true));
      event.registerServerCommand(new DebugModeCommand());
   }

}
