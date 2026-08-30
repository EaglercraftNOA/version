package net.minecraft.client.renderer;

import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderList extends ChunkRenderContainer {
   public void renderChunkLayer(BlockRenderLayer layer) {
      if (this.initialized) {
         for(int i = 0, l = this.renderChunks.size(); i < l; ++i) {
            RenderChunk renderchunk = this.renderChunks.get(i);
            ListedRenderChunk listedrenderchunk = (ListedRenderChunk)renderchunk;
            GlStateManager.pushMatrix();
            this.preRenderChunk(renderchunk);
            renderchunk.multModelviewMatrix();
            GlStateManager.callList(listedrenderchunk.getDisplayList(layer, listedrenderchunk.getCompiledChunk()));
            GlStateManager.popMatrix();
         }

         GlStateManager.resetColor();
         this.renderChunks.clear();
      }
   }
}
