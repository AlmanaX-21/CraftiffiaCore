package me.almana.scan;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.BufferBuilder;
import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.widget.sizer.StandardResizer;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.TextWidget;
import com.cleanroommc.modularui.widgets.layout.Grid;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregapi.data.MT;
import gregapi.oredict.OreDictMaterial;
import gregapi.util.UT.Code;
import gregapi.util.UT.Entities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class HandlerClient implements IMessageHandler<ScanResponse, IMessage> {
   int chunkSize;
   int x_origin;
   int z_origin;
   ScanMode mode;
   ScanMode surfaceMode;
   int[][] blockColorStorage;
   short[][] blockMatStorage;
   String[][] blockNameStorage;
   Map<String, Integer> sortedOres;
   Map<String, Short> sortedOreMat;
   Map<String, Integer>[][] chunkOreCounts;
   int hoverChunkX;
   int hoverChunkZ;
   List<OreData> scannedOres;
   List<OreData> surfaceOres;
   List<OreData> bedrockOres;
   List<OreData> fluidOres;
   boolean bedrockCapable;
   boolean fluidCapable;
   ProspectorView view;
   EntityPlayer mPlayer;

   public HandlerClient() {
      this.mode = ScanMode.LARGE;
      this.surfaceMode = ScanMode.LARGE;
      this.sortedOres = new HashMap();
      this.sortedOreMat = new HashMap();
      this.hoverChunkX = -1;
      this.hoverChunkZ = -1;
      this.scannedOres = new ArrayList();
      this.surfaceOres = new ArrayList();
      this.bedrockOres = new ArrayList();
      this.fluidOres = new ArrayList();
      this.bedrockCapable = false;
      this.fluidCapable = false;
      this.view = ProspectorView.SURFACE;
   }

   public IMessage onMessage(ScanResponse message, MessageContext ctx) {
      if (ctx.side != Side.CLIENT) {
         return null;
      } else {
         Minecraft minecraft = Minecraft.func_71410_x();
         this.surfaceOres = message.scannedOres;
         this.bedrockOres = message.bedrockOres;
         this.fluidOres = message.fluidOres;
         this.bedrockCapable = message.bedrockCapable;
         this.fluidCapable = message.fluidCapable;
         this.surfaceMode = ScanMode.values()[message.mode];
         this.x_origin = message.x;
         this.z_origin = message.z;
         this.chunkSize = message.chunkSize;
         this.mPlayer = minecraft.field_71439_g;
         this.blockColorStorage = new int[16 * this.chunkSize][16 * this.chunkSize];
         this.blockMatStorage = new short[16 * this.chunkSize][16 * this.chunkSize];
         this.blockNameStorage = new String[16 * this.chunkSize][16 * this.chunkSize];
         this.applyView();
         this.refresh();
         this.openScreen();
         return null;
      }
   }

   private void applyView() {
      if (this.view == ProspectorView.BEDROCK && this.bedrockCapable) {
         this.scannedOres = this.bedrockOres;
         this.mode = ScanMode.BEDROCK;
      } else if (this.view == ProspectorView.FLUID_SOURCES && this.fluidCapable) {
         this.scannedOres = this.fluidOres;
         this.mode = ScanMode.FLUID_BEDROCK;
      } else {
         this.scannedOres = this.surfaceOres;
         this.mode = this.surfaceMode;
      }

   }

   private void setView(ProspectorView aView) {
      this.view = aView;
      this.applyView();
      this.refresh();
      this.openScreen();
   }

   private void openScreen() {
      ClientGUI.open(new ModularScreen(this.buildMapPanel()));
   }

   private static String resolveName(OreData data) {
      if (data.name != null) {
         return data.name;
      } else {
         OreDictMaterial mat = OreDictMaterial.MATERIAL_ARRAY[data.matID];
         return mat == MT.MethaneIce ? "Natural Gas" : mat.mNameLocal;
      }
   }

   private static int resolveColor(OreDictMaterial mat) {
      return mat == MT.MethaneIce ? ScanHelpers.col(MT.Milk) : ScanHelpers.col(mat);
   }

   private void refresh() {
      this.blockMatStorage = new short[this.chunkSize * 16][this.chunkSize * 16];
      this.blockNameStorage = new String[this.chunkSize * 16][this.chunkSize * 16];
      int borderColor = ScanHelpers.col(MT.Gray);
      int backgroundColor = ScanHelpers.col(MT.White);
      int oreColor = 0;
      this.sortedOres.clear();
      this.sortedOreMat.clear();
      this.chunkOreCounts = new HashMap[this.chunkSize][this.chunkSize];

      for(int chunkGridX = 0; chunkGridX < this.chunkSize; ++chunkGridX) {
         for(int chunkGridZ = 0; chunkGridZ < this.chunkSize; ++chunkGridZ) {
            this.chunkOreCounts[chunkGridX][chunkGridZ] = new HashMap();

            for(int z = 0; z < 16; ++z) {
               for(int x = 0; x < 16; ++x) {
                  boolean isOre = false;
                  int blockGridX = x + chunkGridX * 16;
                  int blockGridZ = z + chunkGridZ * 16;
                  int blockWorldX = this.x_origin + blockGridX;
                  int blockWorldZ = this.z_origin + blockGridZ;
                  int lastY = 0;

                  try {
                     if (this.scannedOres != null && !this.scannedOres.isEmpty()) {
                        for(OreData data : this.scannedOres) {
                           if (data.x == blockWorldX && data.z == blockWorldZ) {
                              String dispName = resolveName(data);
                              if (lastY <= data.y) {
                                 oreColor = resolveColor(OreDictMaterial.MATERIAL_ARRAY[data.matID]);
                                 lastY = data.y;
                              }

                              if (this.sortedOres.containsKey(dispName)) {
                                 int lastCount = (Integer)this.sortedOres.get(dispName);
                                 this.sortedOres.put(dispName, lastCount + 1);
                              } else {
                                 this.sortedOres.put(dispName, 1);
                              }

                              this.sortedOreMat.put(dispName, data.matID);
                              Map<String, Integer> chunkCounts = this.chunkOreCounts[chunkGridX][chunkGridZ];
                              if (chunkCounts.containsKey(dispName)) {
                                 chunkCounts.put(dispName, (Integer)chunkCounts.get(dispName) + 1);
                              } else {
                                 chunkCounts.put(dispName, 1);
                              }

                              this.blockMatStorage[blockGridX][blockGridZ] = data.matID;
                              this.blockNameStorage[blockGridX][blockGridZ] = dispName;
                              isOre = true;
                           }
                        }
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }

                  this.blockColorStorage[blockGridX][blockGridZ] = backgroundColor;
                  if (isOre) {
                     this.blockColorStorage[blockGridX][blockGridZ] = oreColor;
                  }

                  if (x == 15 || z == 15 || x == 0 || z == 0) {
                     this.blockColorStorage[blockGridX][blockGridZ] = borderColor;
                  }

                  if (chunkGridZ == (this.chunkSize - 1) / 2 && chunkGridX == (this.chunkSize - 1) / 2 && x == 7 && z == 7) {
                     this.blockColorStorage[blockGridX][blockGridZ] = ScanHelpers.col(MT.Red);
                  }
               }
            }
         }
      }

   }

   private ModularPanel buildMapPanel() {
      EntityPlayer player = this.mPlayer;
      ModularPanel mainPanel = ModularPanel.defaultPanel("Scanner");
      ((StandardResizer)mainPanel.flex().align(Alignment.Center)).size(300, 166);
      IWidget mapWidget = ((IDrawable)(context, x, y, width, height, widgetTheme) -> {
         GL11.glDisable(3553);
         GL11.glEnable(3042);
         GL11.glDisable(3008);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);

         for(int i = 0; i < 16 * this.chunkSize; ++i) {
            for(int j = 16 * this.chunkSize - 1; j >= 0; --j) {
               if (this.blockColorStorage[i][j] != ScanHelpers.col(MT.White)) {
                  GL11.glShadeModel(7425);
                  int color = this.blockColorStorage[i][j];
                  Tessellator.field_78398_a.func_78382_b();
                  BufferBuilder.bufferbuilder.pos((double)i, (double)j, (double)0.0F).color(Color.getRed(color), Color.getGreen(color), Color.getBlue(color), Color.getAlpha(color)).endVertex();
                  BufferBuilder.bufferbuilder.pos((double)i, (double)(j + 1), (double)0.0F).color(Color.getRed(color), Color.getGreen(color), Color.getBlue(color), Color.getAlpha(color)).endVertex();
                  BufferBuilder.bufferbuilder.pos((double)(i + 1), (double)(j + 1), (double)0.0F).color(Color.getRed(color), Color.getGreen(color), Color.getBlue(color), Color.getAlpha(color)).endVertex();
                  BufferBuilder.bufferbuilder.pos((double)(i + 1), (double)j, (double)0.0F).color(Color.getRed(color), Color.getGreen(color), Color.getBlue(color), Color.getAlpha(color)).endVertex();
                  Tessellator.field_78398_a.func_78381_a();
               }
            }
         }

         GL11.glShadeModel(7424);
         GL11.glDisable(3042);
         GL11.glEnable(3008);
         GL11.glEnable(3553);
         int mouse_x = mainPanel.getScreen().getContext().getAbsMouseX();
         int mouse_y = mainPanel.getScreen().getContext().getAbsMouseY();
         int world_x = mouse_x - mainPanel.getArea().x - x + this.x_origin - 9;
         int world_z = mouse_y - mainPanel.getArea().y - y + this.z_origin - 9;
         String corN = " ";
         int text_color = ScanHelpers.col(MT.White);
         int blockLocalX = mouse_x - mainPanel.getArea().x - x - 10;
         int blockLocalZ = mouse_y - mainPanel.getArea().y - y - 10;

         try {
            short mat = this.blockMatStorage[blockLocalX][blockLocalZ];
            if (mat != 0) {
               corN = this.blockNameStorage[blockLocalX][blockLocalZ];
               text_color = ScanHelpers.col(OreDictMaterial.MATERIAL_ARRAY[mat]);
            } else {
               corN = "NaN";
            }
         } catch (Exception var32) {
            corN = "NaN";
         }

         int newHoverChunkX = blockLocalX / 16;
         int newHoverChunkZ = blockLocalZ / 16;
         if (blockLocalX >= 0 && blockLocalZ >= 0 && newHoverChunkX < this.chunkSize && newHoverChunkZ < this.chunkSize) {
            this.hoverChunkX = newHoverChunkX;
            this.hoverChunkZ = newHoverChunkZ;
         } else {
            this.hoverChunkX = -1;
            this.hoverChunkZ = -1;
         }

         if (Mouse.isButtonDown(1) && !corN.equalsIgnoreCase("nan")) {
            Entities.sendchat(player, new String[]{String.format("x: %d, z: %d, has material %s", world_x, world_z, corN)});
         }

         if (this.hoverChunkX >= 0 && this.hoverChunkZ >= 0) {
            Map<String, Integer> chunkCounts = this.chunkOreCounts[this.hoverChunkX][this.hoverChunkZ];
            if (chunkCounts != null && !chunkCounts.isEmpty()) {
               List<Map.Entry<String, Integer>> tChunkList = new ArrayList(chunkCounts.entrySet());
               Collections.sort(tChunkList, (a, b) -> (Integer)b.getValue() - (Integer)a.getValue());
               List<String> tLines = new ArrayList();
               List<Integer> tColors = new ArrayList();

               for(Map.Entry<String, Integer> tEntry : tChunkList) {
                  String tName = (String)tEntry.getKey();
                  OreDictMaterial tMat = OreDictMaterial.MATERIAL_ARRAY[(Short)this.sortedOreMat.get(tName)];
                  int tRawColor = resolveColor(tMat);
                  tLines.add(tName + ": " + tEntry.getValue());
                  tColors.add(-16777216 | Color.getRed(tRawColor) << 16 | Color.getGreen(tRawColor) << 8 | Color.getBlue(tRawColor));
               }

               FontRenderer tFont = Minecraft.func_71410_x().field_71466_p;
               int tLineHeight = tFont.field_78288_b + 2;
               int tMaxWidth = 0;

               for(String tLine : tLines) {
                  tMaxWidth = Math.max(tMaxWidth, tFont.func_78256_a(tLine));
               }

               int tBoxX = blockLocalX + 8;
               int tBoxY = blockLocalZ + 8;
               int tBoxW = tMaxWidth + 6;
               int tBoxH = tLines.size() * tLineHeight + 2;
               GL11.glDisable(3553);
               GL11.glEnable(3042);
               OpenGlHelper.func_148821_a(770, 771, 1, 0);
               Tessellator.field_78398_a.func_78382_b();
               BufferBuilder.bufferbuilder.pos((double)tBoxX, (double)tBoxY, (double)0.0F).color(0, 0, 0, 200).endVertex();
               BufferBuilder.bufferbuilder.pos((double)tBoxX, (double)(tBoxY + tBoxH), (double)0.0F).color(0, 0, 0, 200).endVertex();
               BufferBuilder.bufferbuilder.pos((double)(tBoxX + tBoxW), (double)(tBoxY + tBoxH), (double)0.0F).color(0, 0, 0, 200).endVertex();
               BufferBuilder.bufferbuilder.pos((double)(tBoxX + tBoxW), (double)tBoxY, (double)0.0F).color(0, 0, 0, 200).endVertex();
               Tessellator.field_78398_a.func_78381_a();
               GL11.glDisable(3042);
               GL11.glEnable(3553);
               int tTextY = tBoxY + 2;

               for(int i = 0; i < tLines.size(); ++i) {
                  tFont.func_78276_b((String)tLines.get(i), tBoxX + 3, tTextY, (Integer)tColors.get(i));
                  tTextY += tLineHeight;
               }
            }
         }

      }).asWidget().pos(10, 10).right(10).bottom(26);
      Grid listWidget = (Grid)((Grid)(new Grid()).scrollable().size(150, 150)).pos(155, 0);
      List<Map.Entry<String, Integer>> tOreList = new ArrayList(this.sortedOres.entrySet());
      Collections.sort(tOreList, (a, b) -> (Integer)b.getValue() - (Integer)a.getValue());

      for(Map.Entry<String, Integer> tEntry : tOreList) {
         String name = (String)tEntry.getKey();
         int amount = (Integer)tEntry.getValue();
         OreDictMaterial mat = OreDictMaterial.MATERIAL_ARRAY[(Short)this.sortedOreMat.get(name)];
         IWidget itemWidget = ModularUIUtils.wItem(mat, this.mode);
         int color = mat == MT.MethaneIce ? Code.getRGBaInt(MT.Milk.fRGBaSolid) : Code.getRGBaInt(mat.fRGBaSolid);
         IWidget nameWidget = (new TextWidget(IKey.str(name + ": " + amount))).color(color).shadow(false);
         listWidget.row(new IWidget[]{itemWidget, nameWidget}).nextRow();
      }

      mainPanel.child(mapWidget);
      mainPanel.child(listWidget);
      if (this.bedrockCapable) {
         mainPanel.child(this.viewButton(ProspectorView.SURFACE, 12));
         mainPanel.child(this.viewButton(ProspectorView.BEDROCK, 74));
      }

      if (this.fluidCapable) {
         if (!this.bedrockCapable) {
            mainPanel.child(this.viewButton(ProspectorView.SURFACE, 12));
         }

         mainPanel.child(this.viewButton(ProspectorView.FLUID_SOURCES, this.bedrockCapable ? 136 : 74));
      }

      return mainPanel;
   }

   private IWidget viewButton(ProspectorView aView, int aLeft) {
      return ((ButtonWidget)((ButtonWidget)((ButtonWidget)((ButtonWidget)(new ButtonWidget()).size(aView == ProspectorView.FLUID_SOURCES ? 86 : 58, 12)).left(aLeft)).bottom(8)).overlay(new IDrawable[]{IKey.str(this.view == aView ? "[" + aView.label + "]" : aView.label).shadow(false)})).onMousePressed((btn) -> {
         this.setView(aView);
         return true;
      });
   }
}
