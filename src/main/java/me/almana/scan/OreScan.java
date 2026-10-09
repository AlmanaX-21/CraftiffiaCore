package me.almana.scan;

import gregapi.block.prefixblock.PrefixBlock;
import gregapi.block.prefixblock.PrefixBlockTileEntity;
import gregapi.data.MT;
import gregapi.data.CS.BlocksGT;
import gregapi.data.MT.OREMATS;
import gregapi.util.OM;
import gregtech.blocks.BlockDiggable;
import gregtech.blocks.BlockSands;
import gregtech.blocks.stone.BlockCrystalOres;
import gregtech.blocks.stone.BlockRockOres;
import gregtech.blocks.stone.BlockVanillaOresA;
import gregtech.tileentity.misc.MultiTileEntityFluidSpring;
import gregtech.tileentity.placeables.MultiTileEntityRock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class OreScan {
   public final List<OreData> scannedOres = new ArrayList();
   public int oresFound = 0;
   public int x_origin;
   public int z_origin;

   public void process(ScanMode mode, World aWorld, int xo, int zo, int chunkSize) {
      this.scannedOres.clear();
      this.oresFound = 0;
      Chunk[][] chunks = getChunksAroundLoc(aWorld, xo, zo, chunkSize);
      this.x_origin = chunks[0][0].field_76635_g << 4;
      this.z_origin = chunks[0][0].field_76647_h << 4;

      for(int i = 0; i < chunkSize; ++i) {
         for(int j = 0; j < chunkSize; ++j) {
            if (mode.isTE()) {
               this.findTileEntityBlocks(chunks[i][j], mode);
            } else {
               for(int k = 0; k < 16; ++k) {
                  for(int l = 0; l < 16; ++l) {
                     int highest_y = chunks[i][j].func_76611_b(k, l);

                     for(int m = highest_y; m >= 0; --m) {
                        Block block1 = chunks[i][j].func_150810_a(k, m, l);
                        int x = chunks[i][j].field_76635_g * 16 + k;
                        int z = chunks[i][j].field_76647_h * 16 + l;
                        if (mode == ScanMode.DENSE_AND_NORMAL) {
                           if (block1 instanceof BlockRockOres) {
                              short matID = BlockRockOres.ORE_MATERIALS[chunks[i][j].func_76628_c(k, m, l)].mID;
                              ++this.oresFound;
                              this.scannedOres.add(new OreData(x, m, z, matID));
                              break;
                           }

                           if (block1 instanceof BlockCrystalOres) {
                              short matID = BlockCrystalOres.ORE_MATERIALS[chunks[i][j].func_76628_c(k, m, l)].mID;
                              ++this.oresFound;
                              this.scannedOres.add(new OreData(x, m, z, matID));
                           } else if (block1 instanceof BlockVanillaOresA) {
                              short matID = BlockVanillaOresA.ORE_MATERIALS[chunks[i][j].func_76628_c(k, m, l)].mID;
                              ++this.oresFound;
                              this.scannedOres.add(new OreData(x, m, z, matID));
                           } else if (block1 instanceof BlockDiggable) {
                              int meta = aWorld.func_72805_g(x, m, z);
                              short matID = 0;
                              switch (meta) {
                                 case 0:
                                 default:
                                    break;
                                 case 1:
                                    matID = MT.ClayBrown.mID;
                                    break;
                                 case 2:
                                    matID = MT.Peat.mID;
                                    break;
                                 case 3:
                                    matID = MT.ClayRed.mID;
                                    break;
                                 case 4:
                                    matID = MT.Bentonite.mID;
                                    break;
                                 case 5:
                                    matID = MT.Palygorskite.mID;
                                    break;
                                 case 6:
                                    matID = MT.Kaolinite.mID;
                              }

                              if (matID != 0) {
                                 this.scannedOres.add(new OreData(x, m, z, matID));
                                 ++this.oresFound;
                              }
                           } else if (block1 instanceof BlockSands) {
                              int meta = aWorld.func_72805_g(x, m, z);
                              short matID = 0;
                              switch (meta) {
                                 case 0:
                                    matID = OREMATS.Magnetite.mID;
                                    break;
                                 case 1:
                                    matID = OREMATS.BasalticMineralSand.mID;
                                    break;
                                 case 2:
                                    matID = OREMATS.GraniticMineralSand.mID;
                              }
                           }
                        } else if (mode == ScanMode.FLUID) {
                           if (block1 == Blocks.field_150353_l) {
                              short matID = MT.Lava.mID;
                              if (aWorld.field_73011_w.field_76575_d) {
                                 this.scannedOres.add(new OreData(x, m, z, matID));
                                 ++this.oresFound;
                                 break;
                              }

                              this.scannedOres.add(new OreData(x, m, z, matID));
                              ++this.oresFound;
                           }

                           if (block1 instanceof IFluidBlock || block1 == Blocks.field_150353_l || block1 == Blocks.field_150355_j) {
                              if (!(block1 instanceof IFluidBlock)) {
                                 break;
                              }

                              Fluid fluid = ((IFluidBlock)block1).getFluid();
                              String fluidName = fluid.getName();
                              String oreName = null;
                              short matID = MT.Air.mID;
                              if (fluidName.contains("natural")) {
                                 matID = MT.MethaneIce.mID;
                              }

                              if (fluidName.contains("oil")) {
                                 matID = MT.Oil.mID;
                                 oreName = (new FluidStack(fluid, 1)).getLocalizedName();
                              }

                              if (fluidName.contains("honey")) {
                                 matID = MT.Honey.mID;
                              }

                              if (fluidName.contains("sulfuric")) {
                                 matID = MT.H2SO4.mID;
                              }

                              if (fluidName.contains("acid")) {
                                 matID = MT.H2SO4.mID;
                              }

                              if (fluidName.contains("poison")) {
                                 matID = MT.DirtyWater.mID;
                              }

                              if (fluidName.contains("infused")) {
                                 matID = MT.InfusedWater.mID;
                              }

                              if (fluidName.contains("mana")) {
                                 matID = MT.Magic.mID;
                              }

                              if (block1 == BlocksGT.WaterGeothermal) {
                                 matID = MT.DistWater.mID;
                              }

                              if (fluidName.contains("water")) {
                                 if (matID == MT.Air.mID) {
                                    matID = MT.Water.mID;
                                    this.scannedOres.add(new OreData(x, m, z, matID));
                                    ++this.oresFound;
                                 }
                                 break;
                              }

                              if (matID != MT.Air.mID) {
                                 this.scannedOres.add(new OreData(x, m, z, matID, oreName));
                                 ++this.oresFound;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public static Chunk[][] getChunksAroundLoc(World aWorld, int posX, int posZ, int chunkSize) {
      Chunk[][] chunks = new Chunk[chunkSize][chunkSize];
      int CENTER_CHUNK_INDEX = (chunkSize - 1) / 2;
      chunks[CENTER_CHUNK_INDEX][CENTER_CHUNK_INDEX] = aWorld.func_72938_d(posX, posZ);
      Chunk PLAYER_CHUNK = chunks[CENTER_CHUNK_INDEX][CENTER_CHUNK_INDEX];
      chunks[0][0] = aWorld.func_72964_e(PLAYER_CHUNK.field_76635_g - (chunkSize - 1) / 2, PLAYER_CHUNK.field_76647_h - (chunkSize - 1) / 2);

      for(int i = 0; i < chunkSize; ++i) {
         for(int j = 0; j < chunkSize; ++j) {
            if ((i != 0 || j != 0) && (i != CENTER_CHUNK_INDEX || j != CENTER_CHUNK_INDEX)) {
               chunks[i][j] = aWorld.func_72964_e(chunks[0][0].field_76635_g + i, chunks[0][0].field_76647_h + j);
            }
         }
      }

      return chunks;
   }

   private void findTileEntityBlocks(Chunk chunk, ScanMode mode) {
      Map<ChunkPosition, TileEntity> tTileMap = chunk.field_150816_i;
      if (tTileMap != null) {
         try {
            for(Map.Entry<ChunkPosition, TileEntity> tEntry : tTileMap.entrySet()) {
               TileEntity tile = (TileEntity)tEntry.getValue();
               if (tile instanceof PrefixBlockTileEntity && (mode == ScanMode.LARGE || mode == ScanMode.SMALL || mode == ScanMode.BEDROCK)) {
                  PrefixBlockTileEntity pTile = (PrefixBlockTileEntity)tile;
                  PrefixBlock pBlock = ScanHelpers.prefixBlock(pTile);
                  boolean isBedrock = false;
                  if (mode == ScanMode.BEDROCK) {
                     isBedrock = pBlock.mNameInternal.contains("bedrock");
                  }

                  if (isBedrock || pBlock.mPrefix.mFamiliarPrefixes.contains(mode.PREFIX)) {
                     short matID = pTile.mMetaData;
                     this.scannedOres.add(new OreData(pTile.getX(), pTile.getY(), pTile.getZ(), matID));
                     ++this.oresFound;
                  }
               }

               if (mode == ScanMode.FLUID_BEDROCK && tile instanceof MultiTileEntityFluidSpring) {
                  FluidStack fluidStack = ((MultiTileEntityFluidSpring)tile).mFluid;
                  String name = fluidStack.getFluid().getName();
                  short matID = 0;
                  String oreName = null;
                  if (name.contains("oil")) {
                     matID = MT.Oil.mID;
                     oreName = fluidStack.getLocalizedName();
                  }

                  if (name.contains("water")) {
                     matID = MT.Water.mID;
                  }

                  if (name.contains("lava")) {
                     matID = MT.Lava.mID;
                  }

                  if (name.contains("natural")) {
                     matID = MT.MethaneIce.mID;
                  }

                  MultiTileEntityFluidSpring spring = (MultiTileEntityFluidSpring)tile;
                  if (matID != 0) {
                     this.scannedOres.add(new OreData(spring.getX(), spring.getY(), spring.getZ(), matID, oreName));
                     ++this.oresFound;
                  }
               }

               if (tile instanceof MultiTileEntityRock && mode == ScanMode.ROCK) {
                  MultiTileEntityRock rock = (MultiTileEntityRock)tile;
                  short matID = OM.anydata_(rock.mRock).mMaterial.mMaterial.mID;
                  this.scannedOres.add(new OreData(rock.getX(), rock.getY(), rock.getZ(), matID));
                  ++this.oresFound;
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }
}
