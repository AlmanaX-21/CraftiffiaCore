package me.almana;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class DebugModeCommand extends CommandBase {
   public String func_71517_b() {
      return "debugmode";
   }

   public String func_71518_a(ICommandSender aSender) {
      return "/debugmode - toggles running the drill without power or lubricant";
   }

   public int func_82362_a() {
      return 2;
   }

   public void func_71515_b(ICommandSender aSender, String[] aArgs) {
      Craftiffia.DEBUG = !Craftiffia.DEBUG;
      aSender.func_145747_a(new ChatComponentText("Craftiffia debug mode: " + (Craftiffia.DEBUG ? "ON - drill runs without power/lubricant" : "OFF")));
   }
}
