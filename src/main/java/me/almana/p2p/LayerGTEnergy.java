package me.almana.p2p;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import appeng.api.parts.LayerBase;
import gregapi.code.TagData;
import gregapi.data.CS;
import gregapi.data.TD;
import gregapi.tileentity.energy.ITileEntityEnergy;
import java.util.Collection;
import java.util.Collections;
import net.minecraftforge.common.util.ForgeDirection;

public class LayerGTEnergy extends LayerBase implements ITileEntityEnergy {
   @Override
   public boolean isEnergyType(TagData aEnergyType, byte aSide, boolean aEmitting) {
      return aEnergyType == TD.Energy.EU;
   }

   @Override
   public Collection<TagData> getEnergyTypes(byte aSide) {
      return Collections.singletonList(TD.Energy.EU);
   }

   @Override
   public boolean isEnergyAcceptingFrom(TagData aEnergyType, byte aSide, boolean aTheoretical) {
      PartP2PGTEnergy tTunnel = this.tunnel(aEnergyType, aSide);
      return tTunnel != null && (aTheoretical || !tTunnel.isOutput());
   }

   @Override
   public boolean isEnergyEmittingTo(TagData aEnergyType, byte aSide, boolean aTheoretical) {
      PartP2PGTEnergy tTunnel = this.tunnel(aEnergyType, aSide);
      return tTunnel != null && (aTheoretical || tTunnel.isOutput());
   }

   @Override
   public long doEnergyInjection(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoInject) {
      PartP2PGTEnergy tTunnel = this.tunnel(aEnergyType, aSide);
      return tTunnel == null ? 0L : tTunnel.injectEnergy(aSize, aAmount, aDoInject);
   }

   @Override
   public long getEnergyDemanded(TagData aEnergyType, byte aSide, long aSize) {
      return this.doEnergyInjection(aEnergyType, aSide, aSize, EnergyBuffer.MAX_AMPERES, false);
   }

   @Override
   public long doEnergyExtraction(TagData aEnergyType, byte aSide, long aSize, long aAmount, boolean aDoExtract) {
      return 0L;
   }

   @Override
   public long getEnergyOffered(TagData aEnergyType, byte aSide, long aSize) {
      return 0L;
   }

   @Override
   public long getEnergySizeInputMin(TagData aEnergyType, byte aSide) {
      return 1L;
   }

   @Override
   public long getEnergySizeOutputMin(TagData aEnergyType, byte aSide) {
      return 1L;
   }

   @Override
   public long getEnergySizeInputRecommended(TagData aEnergyType, byte aSide) {
      return CS.VMAX[CS.VMAX.length - 1];
   }

   @Override
   public long getEnergySizeOutputRecommended(TagData aEnergyType, byte aSide) {
      return CS.VMAX[CS.VMAX.length - 1];
   }

   @Override
   public long getEnergySizeInputMax(TagData aEnergyType, byte aSide) {
      return CS.VMAX[CS.VMAX.length - 1];
   }

   @Override
   public long getEnergySizeOutputMax(TagData aEnergyType, byte aSide) {
      return CS.VMAX[CS.VMAX.length - 1];
   }

   @Override
   public boolean isDead() {
      return ((IPartHost)this).getTile().isInvalid();
   }

   private PartP2PGTEnergy tunnel(TagData aEnergyType, byte aSide) {
      IPart tPart = this.getPart(ForgeDirection.getOrientation(aSide));
      return aEnergyType == TD.Energy.EU && tPart instanceof PartP2PGTEnergy ? (PartP2PGTEnergy)tPart : null;
   }
}
