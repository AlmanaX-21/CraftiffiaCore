package me.almana.p2p;

import appeng.api.AEApi;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GTEnergyTunnelItem extends Item implements IPartItem {
   public GTEnergyTunnelItem(CreativeTabs aTab) {
      this.setUnlocalizedName("craftiffia.p2p_gt_eu");
      this.setTextureName("appliedenergistics2:ItemPart.P2PTunnel");
      this.setCreativeTab(aTab);
   }

   public static Item register(CreativeTabs aTab) {
      AEApi.instance().partHelper().registerNewLayer("me.almana.p2p.LayerGTEnergy", "gregapi.tileentity.energy.ITileEntityEnergy");
      GTEnergyTunnelItem tItem = new GTEnergyTunnelItem(aTab);
      GameRegistry.registerItem(tItem, "p2p_gt_eu");
      AEApi.instance().partHelper().setItemBusRenderer(tItem);
      return tItem;
   }

   @Override
   public IPart createPartFromItemStack(ItemStack aStack) {
      return new PartP2PGTEnergy(aStack);
   }

   @Override
   public boolean onItemUse(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide, float aHitX, float aHitY, float aHitZ) {
      return AEApi.instance().partHelper().placeBus(aStack, aX, aY, aZ, aSide, aPlayer, aWorld);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public int getSpriteNumber() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   @Override
   @SuppressWarnings("unchecked")
   public void addInformation(ItemStack aStack, EntityPlayer aPlayer, List aList, boolean aAdvanced) {
      aList.add("Transfers GregTech EU at any voltage");
      aList.add("Buffers up to " + EnergyBuffer.MAX_AMPERES + "A per input");
      aList.add("Link with a Memory Card");
   }
}
