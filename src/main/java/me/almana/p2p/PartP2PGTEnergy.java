package me.almana.p2p;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.me.GridAccessException;
import appeng.parts.p2p.PartP2PTunnel;
import appeng.parts.p2p.PartP2PTunnelStatic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.TD;
import gregapi.tileentity.energy.ITileEntityEnergy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

public class PartP2PGTEnergy extends PartP2PTunnelStatic<PartP2PGTEnergy> implements IGridTickable {
   private final EnergyBuffer mBuffer = new EnergyBuffer();

   public PartP2PGTEnergy(ItemStack aStack) {
      super(aStack);
   }

   public long injectEnergy(long aVoltage, long aAmperage, boolean aDoInject) {
      if (this.isOutput() || this.getFrequency() == 0L || !this.getProxy().isActive()) return 0L;
      boolean tWasEmpty = this.mBuffer.amperes() <= 0L;
      long tAccepted = this.mBuffer.offer(aVoltage, aAmperage, aDoInject);
      if (aDoInject && tWasEmpty && tAccepted > 0L) this.wake();
      return tAccepted;
   }

   @Override
   public TickingRequest getTickingRequest(IGridNode aNode) {
      return new TickingRequest(1, 20, false, true);
   }

   @Override
   public TickRateModulation tickingRequest(IGridNode aNode, int aTicksSinceLastCall) {
      if (this.isOutput() || this.getFrequency() == 0L) return TickRateModulation.IDLE;
      try {
         for (PartP2PTunnel tTunnel : this.getProxy().getP2P().getOutputs(this.getFrequency(), PartP2PGTEnergy.class)) {
            long tAmperes = this.mBuffer.amperes();
            if (tAmperes <= 0L) break;
            this.mBuffer.take(((PartP2PGTEnergy)tTunnel).emit(this.mBuffer.voltage(), tAmperes));
         }
      } catch (GridAccessException ignored) {
      }
      return this.mBuffer.amperes() > 0L ? TickRateModulation.URGENT : TickRateModulation.IDLE;
   }

   @Override
   public boolean onPartActivate(EntityPlayer aPlayer, Vec3 aPos) {
      if (super.onPartActivate(aPlayer, aPos)) return true;
      if (aPlayer.getCurrentEquippedItem() != null || aPlayer.worldObj.isRemote) return false;
      aPlayer.addChatMessage(new ChatComponentText((this.isOutput() ? "Output" : "Input") + " | Freq " + this.getFrequency() + " | " + this.mBuffer.voltage() + " EU x " + this.mBuffer.amperes() + "A buffered"));
      return true;
   }

   @Override
   public void readFromNBT(NBTTagCompound aNBT) {
      super.readFromNBT(aNBT);
      this.mBuffer.load(aNBT);
   }

   @Override
   public void writeToNBT(NBTTagCompound aNBT) {
      super.writeToNBT(aNBT);
      this.mBuffer.save(aNBT);
   }

   @SideOnly(Side.CLIENT)
   @Override
   public IIcon getTypeTexture() {
      return Blocks.iron_block.getIcon(0, 0);
   }

   private long emit(long aVoltage, long aAmperes) {
      return ITileEntityEnergy.Util.emitEnergyToSide(TD.Energy.EU, (byte)this.getSide().ordinal(), aVoltage, aAmperes, this.getTile());
   }

   private void wake() {
      try {
         this.getProxy().getTick().alertDevice(this.getProxy().getNode());
      } catch (GridAccessException ignored) {
      }
   }
}
