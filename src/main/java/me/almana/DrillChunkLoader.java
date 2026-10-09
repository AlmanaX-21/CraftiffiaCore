package me.almana;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Type;

public class DrillChunkLoader {
   private static final Map<String, ForgeChunkManager.Ticket> PENDING = new HashMap();

   public static void init() {
      ForgeChunkManager.setForcedChunkLoadingCallback(Craftiffia.INSTANCE, new Callback());
   }

   public static ForgeChunkManager.Ticket request(World aWorld, int aX, int aY, int aZ) {
      ForgeChunkManager.Ticket tTicket = ForgeChunkManager.requestTicket(Craftiffia.INSTANCE, aWorld, Type.NORMAL);
      if (tTicket == null) {
         return null;
      } else {
         tTicket.getModData().func_74768_a("x", aX);
         tTicket.getModData().func_74768_a("y", aY);
         tTicket.getModData().func_74768_a("z", aZ);
         return tTicket;
      }
   }

   public static ForgeChunkManager.Ticket claim(World aWorld, int aX, int aY, int aZ) {
      return (ForgeChunkManager.Ticket)PENDING.remove(key(aWorld, aX, aY, aZ));
   }

   private static String key(World aWorld, int aX, int aY, int aZ) {
      return aWorld.field_73011_w.field_76574_g + ":" + aX + ":" + aY + ":" + aZ;
   }

   static class Callback implements ForgeChunkManager.LoadingCallback {
      public void ticketsLoaded(List<ForgeChunkManager.Ticket> aTickets, World aWorld) {
         for(ForgeChunkManager.Ticket tTicket : aTickets) {
            int x = tTicket.getModData().func_74762_e("x");
            int y = tTicket.getModData().func_74762_e("y");
            int z = tTicket.getModData().func_74762_e("z");
            ForgeChunkManager.forceChunk(tTicket, new ChunkCoordIntPair(x >> 4, z >> 4));
            TileEntity tTE = aWorld.func_147438_o(x, y, z);
            if (tTE instanceof HorizontalDrillController) {
               ((HorizontalDrillController)tTE).adoptTicket(tTicket);
            } else {
               DrillChunkLoader.PENDING.put(DrillChunkLoader.key(aWorld, x, y, z), tTicket);
            }
         }

      }
   }
}
