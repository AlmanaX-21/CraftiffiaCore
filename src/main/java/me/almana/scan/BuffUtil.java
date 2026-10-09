package me.almana.scan;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

public class BuffUtil {
   public static void writeOreData(ByteBuf buffer, OreData data) {
      buffer.writeInt(data.x);
      buffer.writeInt(data.y);
      buffer.writeInt(data.z);
      buffer.writeShort(data.matID);
      buffer.writeBoolean(data.name != null);
      if (data.name != null) {
         ByteBufUtils.writeUTF8String(buffer, data.name);
      }

   }

   public static OreData readOreData(ByteBuf buffer) {
      int x = buffer.readInt();
      int y = buffer.readInt();
      int z = buffer.readInt();
      short matID = buffer.readShort();
      String name = buffer.readBoolean() ? ByteBufUtils.readUTF8String(buffer) : null;
      return new OreData(x, y, z, matID, name);
   }
}
