package me.almana;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.CS;
import gregapi.data.TD.Energy;
import gregapi.item.multiitem.MultiItemRandom;
import gregapi.item.multiitem.energy.EnergyStat;
import gregapi.util.ST;
import gregapi.util.UT.Code;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ProspectorItem extends MultiItemRandom {
   public static final int MV = 0;
   public static final int HV = 1;
   public static final int EV = 2;
   private static final String[] ICONS = new String[]{"prospector_mv", "prospector_hv", "prospector_ev"};

   public ProspectorItem() {
      super("craftiffia", "craftiffia.prospector");
   }

   public void addItems() {
      this.addItem(0, "MV Ore Prospector", "", new Object[]{new Behavior_Prospector(1, false, false, 0), EnergyStat.makeTool(Energy.EU, CS.V[2] * 2000L, CS.V[2], 64L, ST.make(this, 1L, 0L))});
      this.addItem(1, "HV Ore Prospector", "", new Object[]{new Behavior_Prospector(2, false, true, 1), EnergyStat.makeTool(Energy.EU, CS.V[3] * 2000L, CS.V[3], 64L, ST.make(this, 1L, 1L))});
      this.addItem(2, "EV Ore Prospector", "", new Object[]{new Behavior_Prospector(3, true, true, 2), EnergyStat.makeTool(Energy.EU, CS.V[4] * 2000L, CS.V[4], 64L, ST.make(this, 1L, 2L))});
   }

   @SideOnly(Side.CLIENT)
   public void func_94581_a(IIconRegister aRegister) {
      for(int i = 0; i < ICONS.length; ++i) {
         this.mIconList[i][0] = aRegister.func_94245_a("craftiffia:" + ICONS[i]);
      }

   }

   public IIcon func_77650_f(ItemStack aStack) {
      int m = this.getDamage(aStack);
      return Code.exists(m, this.mIconList) ? this.mIconList[m][0] : super.func_77650_f(aStack);
   }

   public IIcon getIcon(ItemStack aStack, int aRenderPass) {
      return this.func_77650_f(aStack);
   }
}
