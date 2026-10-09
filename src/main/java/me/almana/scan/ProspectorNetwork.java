package me.almana.scan;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ProspectorNetwork {
   public static SimpleNetworkWrapper WRAPPER;

   public static void init() {
      WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel("CraftiffiaScan");
      WRAPPER.registerMessage(HandlerDummy.class, ScanResponse.class, 0, Side.SERVER);
      if (FMLCommonHandler.instance().getSide().isClient()) {
         WRAPPER.registerMessage(HandlerClient.class, ScanResponse.class, 0, Side.CLIENT);
      }

   }
}
