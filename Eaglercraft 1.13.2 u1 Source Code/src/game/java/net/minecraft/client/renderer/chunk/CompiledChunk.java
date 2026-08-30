package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CompiledChunk {
   public static final CompiledChunk DUMMY = new CompiledChunk() {
      protected void setLayerUsed(BlockRenderLayer layer) {
         throw new UnsupportedOperationException();
      }

      public void setLayerStarted(BlockRenderLayer layer) {
         throw new UnsupportedOperationException();
      }

      public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
         return false;
      }
   };
   private int layersUsed;
   private int layersStarted;
   private boolean empty = true;
   private List<TileEntity> tileEntities;
   private SetVisibility setVisibility = new SetVisibility();
   private BufferBuilder.State state;

   public boolean isEmpty() {
      return this.empty;
   }

   protected void setLayerUsed(BlockRenderLayer layer) {
      this.empty = false;
      this.layersUsed |= 1 << layer.ordinal();
   }

   public boolean isLayerEmpty(BlockRenderLayer layer) {
      return (this.layersUsed & 1 << layer.ordinal()) == 0;
   }

   public void setLayerStarted(BlockRenderLayer layer) {
      this.layersStarted |= 1 << layer.ordinal();
   }

   public boolean isLayerStarted(BlockRenderLayer layer) {
      return (this.layersStarted & 1 << layer.ordinal()) != 0;
   }

   public List<TileEntity> getTileEntities() {
      return this.tileEntities == null ? Collections.emptyList() : this.tileEntities;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      if (this.tileEntities == null) {
         this.tileEntities = Lists.newArrayList();
      }

      this.tileEntities.add(tileEntityIn);
   }

   public boolean isVisible(EnumFacing facing, EnumFacing facing2) {
      return this.setVisibility.isVisible(facing, facing2);
   }

   public void setVisibility(SetVisibility visibility) {
      this.setVisibility = visibility;
   }

   public BufferBuilder.State getState() {
      return this.state;
   }

   public void setState(BufferBuilder.State stateIn) {
      this.state = stateIn;
   }
}
