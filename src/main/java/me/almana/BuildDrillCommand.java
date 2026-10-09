package me.almana;

import gregapi.block.multitileentity.MultiTileEntityRegistry;
import gregapi.data.CS;
import gregapi.util.WD;
import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BuildDrillCommand extends CommandBase {
   private final boolean mHorizontalOnly;

   public BuildDrillCommand() {
      this(false);
   }

   public BuildDrillCommand(boolean aHorizontalOnly) {
      this.mHorizontalOnly = aHorizontalOnly;
   }

   public String func_71517_b() {
      return this.mHorizontalOnly ? "builddrillh" : "builddrill";
   }

   public String func_71518_a(ICommandSender aSender) {
      return this.mHorizontalOnly ? "/builddrillh - places a Horizontal Mining Drill multiblock in front of you" : "/builddrill [horizontal] - places a Mining Drill multiblock in front of you";
   }

   public List func_71514_a() {
      return this.mHorizontalOnly ? Arrays.asList("buidldrillh") : super.func_71514_a();
   }

   public int func_82362_a() {
      return 2;
   }

   public void func_71515_b(ICommandSender aSender, String[] aArgs) {
      if (!(aSender instanceof EntityPlayerMP)) {
         aSender.func_145747_a(new ChatComponentText("Players only."));
      } else {
         EntityPlayerMP tPlayer = (EntityPlayerMP)aSender;
         World tWorld = tPlayer.field_70170_p;
         int tDir = MathHelper.func_76128_c((double)(tPlayer.field_70177_z * 4.0F / 360.0F) + (double)0.5F) & 3;
         int tDX = tDir == 1 ? -1 : (tDir == 3 ? 1 : 0);
         int tDZ = tDir == 2 ? -1 : (tDir == 0 ? 1 : 0);
         if (!this.mHorizontalOnly && (aArgs.length <= 0 || !aArgs[0].toLowerCase().startsWith("h"))) {
            int tCX = MathHelper.func_76128_c(tPlayer.field_70165_t) + tDX * 3;
            int tCZ = MathHelper.func_76128_c(tPlayer.field_70161_v) + tDZ * 3;
            int tBY = MathHelper.func_76128_c(tPlayer.field_70163_u);
            MultiTileEntityRegistry tOurs = Craftiffia.REGISTRY;
            MultiTileEntityRegistry tGT = MultiTileEntityRegistry.getRegistry("gt.multitileentity");

            for(int dy = 0; dy < 3; ++dy) {
               for(int i = -1; i < 2; ++i) {
                  for(int j = -1; j < 2; ++j) {
                     if (i == 0 && j == 0) {
                        WD.set(tWorld, tCX, tBY + dy, tCZ, tOurs.getItem(2));
                     } else {
                        WD.set(tWorld, tCX + i, tBY + dy, tCZ + j, tGT.getItem(18006));
                     }
                  }
               }
            }

            WD.set(tWorld, tCX, tBY + 3, tCZ, tOurs.getItem(3));
            boolean tFormed = false;
            TileEntity tTE = tWorld.func_147438_o(tCX, tBY + 3, tCZ);
            if (tTE instanceof MiningDrillController) {
               tFormed = ((MiningDrillController)tTE).checkStructure(true);
            }

            aSender.func_145747_a(new ChatComponentText("Craftiffia: built at " + tCX + ", " + (tBY + 3) + ", " + tCZ + " - structure formed: " + tFormed + (tFormed ? ". No GUI (like the Bedrock Drill) - just feed RU to the side walls + Lubricant to the Controller." : ". Structure check FAILED - tell the dev.")));
         } else {
            this.buildHorizontal(aSender, tPlayer, tWorld, tDir);
         }
      }
   }

   private void buildHorizontal(ICommandSender aSender, EntityPlayerMP aPlayer, World aWorld, int aDir) {
      byte tBore = (byte)(aDir == 0 ? 3 : (aDir == 1 ? 4 : (aDir == 2 ? 2 : 5)));
      byte tFacing = CS.OPOS[tBore];
      boolean tAxisX = CS.SIDES_AXIS_X[tBore];
      int fx = CS.OFFX[tBore];
      int fz = CS.OFFZ[tBore];
      int rx = tAxisX ? 0 : 1;
      int rz = tAxisX ? 1 : 0;
      int cx = MathHelper.func_76128_c(aPlayer.field_70165_t) + fx * 3;
      int cy = MathHelper.func_76128_c(aPlayer.field_70163_u) + 2;
      int cz = MathHelper.func_76128_c(aPlayer.field_70161_v) + fz * 3;
      MultiTileEntityRegistry tOurs = Craftiffia.REGISTRY;
      MultiTileEntityRegistry tGT = MultiTileEntityRegistry.getRegistry("gt.multitileentity");

      for(int u = -1; u <= 1; ++u) {
         for(int r = -1; r <= 1; ++r) {
            for(int depth = 0; depth <= 5; ++depth) {
               int x = cx + fx * depth + rx * r;
               int y = cy + u;
               int z = cz + fz * depth + rz * r;
               if (depth == 0) {
                  if (u == 0 && r == 0) {
                     WD.set(aWorld, x, y, z, tOurs.getItem(6));
                  }
               } else if (r == 0 && u == 0) {
                  WD.set(aWorld, x, y, z, tOurs.getItem(5));
               } else {
                  WD.set(aWorld, x, y, z, tGT.getItem(18026));
               }
            }
         }
      }

      boolean tFormed = false;
      TileEntity tTE = aWorld.func_147438_o(cx, cy, cz);
      if (tTE instanceof HorizontalDrillController) {
         ((HorizontalDrillController)tTE).mFacing = tFacing;
         tFormed = ((HorizontalDrillController)tTE).checkStructure(true);
      }

      aSender.func_145747_a(new ChatComponentText("Craftiffia: horizontal drill at " + cx + ", " + cy + ", " + cz + " facing " + tFacing + " - formed: " + tFormed + (tFormed ? ". Feed RU to the top/bottom wall rows + Lubricant to a Drill Head. Bores forever, chunkloaded." : ". Structure check FAILED - tell the dev.")));
   }
}
