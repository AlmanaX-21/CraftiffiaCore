package me.almana.scan;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;

public class ScanResponse implements IMessage {
   List<OreData> scannedOres;
   List<OreData> bedrockOres;
   List<OreData> fluidOres;
   int x;
   int z;
   int mode;
   int chunkSize;
   boolean bedrockCapable;
   boolean fluidCapable;

   public ScanResponse(List<OreData> scannedOres, List<OreData> bedrockOres, int x, int z, int mode, int chunkSize, boolean bedrockCapable) {
      this(scannedOres, bedrockOres, new ArrayList(), x, z, mode, chunkSize, bedrockCapable, false);
   }

   public ScanResponse(List<OreData> scannedOres, List<OreData> bedrockOres, List<OreData> fluidOres, int x, int z, int mode, int chunkSize, boolean bedrockCapable, boolean fluidCapable) {
      this.scannedOres = scannedOres;
      this.bedrockOres = bedrockOres;
      this.fluidOres = fluidOres;
      this.x = x;
      this.z = z;
      this.mode = mode;
      this.chunkSize = chunkSize;
      this.bedrockCapable = bedrockCapable;
      this.fluidCapable = fluidCapable;
   }

   public ScanResponse() {
      this.scannedOres = new ArrayList();
      this.bedrockOres = new ArrayList();
      this.fluidOres = new ArrayList();
   }

   public void fromBytes(ByteBuf buf) {
      this.scannedOres = new ArrayList();
      this.bedrockOres = new ArrayList();
      this.x = buf.readInt();
      this.z = buf.readInt();
      this.chunkSize = buf.readInt();
      int size = buf.readInt();
      this.mode = buf.readInt();

      for(int i = 0; i < size; ++i) {
         this.scannedOres.add(BuffUtil.readOreData(buf));
      }

      this.bedrockCapable = buf.readBoolean();
      int bedSize = buf.readInt();

      for(int i = 0; i < bedSize; ++i) {
         this.bedrockOres.add(BuffUtil.readOreData(buf));
      }

      this.fluidCapable = buf.readBoolean();
      int fluidSize = buf.readInt();

      for(int i = 0; i < fluidSize; ++i) {
         this.fluidOres.add(BuffUtil.readOreData(buf));
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.x);
      buf.writeInt(this.z);
      buf.writeInt(this.chunkSize);
      buf.writeInt(this.scannedOres.size());
      buf.writeInt(this.mode);

      for(OreData ore : this.scannedOres) {
         BuffUtil.writeOreData(buf, ore);
      }

      buf.writeBoolean(this.bedrockCapable);
      buf.writeInt(this.bedrockOres.size());

      for(OreData ore : this.bedrockOres) {
         BuffUtil.writeOreData(buf, ore);
      }

      buf.writeBoolean(this.fluidCapable);
      buf.writeInt(this.fluidOres.size());

      for(OreData ore : this.fluidOres) {
         BuffUtil.writeOreData(buf, ore);
      }

   }
}
