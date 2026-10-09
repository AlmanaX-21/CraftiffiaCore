package me.almana;

import gregapi.render.ITexture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

final class RotatedTexture implements ITexture {
   private final ITexture mTexture;

   RotatedTexture(ITexture aTexture) {
      this.mTexture = aTexture;
   }

   public void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
      aRenderer.field_147871_s = 2;
      this.mTexture.renderXPos(aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
      aRenderer.field_147871_s = 0;
   }

   public void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
      aRenderer.field_147869_t = 1;
      this.mTexture.renderXNeg(aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
      aRenderer.field_147869_t = 0;
   }

   public void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
      aRenderer.field_147867_u = 1;
      this.mTexture.renderYPos(aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
      aRenderer.field_147867_u = 0;
   }

   public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
      aRenderer.field_147865_v = 2;
      this.mTexture.renderYNeg(aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
      aRenderer.field_147865_v = 0;
   }

   public void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
      aRenderer.field_147873_r = 1;
      this.mTexture.renderZPos(aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
      aRenderer.field_147873_r = 0;
   }

   public void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ, int aBrightness, boolean aChangedBlockBounds) {
      aRenderer.field_147875_q = 2;
      this.mTexture.renderZNeg(aRenderer, aBlock, aX, aY, aZ, aBrightness, aChangedBlockBounds);
      aRenderer.field_147875_q = 0;
   }

   public boolean isValidTexture() {
      return this.mTexture.isValidTexture();
   }
}
