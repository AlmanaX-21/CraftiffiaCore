package me.almana.scan;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class HandlerDummy implements IMessageHandler<ScanResponse, IMessage> {
   public IMessage onMessage(ScanResponse message, MessageContext ctx) {
      System.err.println("ScanResponse received on wrong side: " + ctx.side);
      return null;
   }
}
