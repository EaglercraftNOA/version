package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketSpawnPainting implements Packet<INetHandlerPlayClient> {
   private int entityID;
   private UUID uniqueId;
   private BlockPos position;
   private EnumFacing facing;
   private int title;

   public SPacketSpawnPainting() {
   }

   public SPacketSpawnPainting(EntityPainting painting) {
      this.entityID = painting.getEntityId();
      this.uniqueId = painting.getUniqueID();
      this.position = painting.getHangingPosition();
      this.facing = painting.facingDirection;
      this.title = IRegistry.MOTIVE.getId(painting.art);
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityID = buf.readVarInt();
      this.uniqueId = buf.readUniqueId();
      this.title = buf.readVarInt();
      this.position = buf.readBlockPos();
      this.facing = EnumFacing.byHorizontalIndex(buf.readUnsignedByte());
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityID);
      buf.writeUniqueId(this.uniqueId);
      buf.writeVarInt(this.title);
      buf.writeBlockPos(this.position);
      buf.writeByte(this.facing.getHorizontalIndex());
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnPainting(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntityID() {
      return this.entityID;
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos getPosition() {
      return this.position;
   }

   @OnlyIn(Dist.CLIENT)
   public EnumFacing getFacing() {
      return this.facing;
   }

   @OnlyIn(Dist.CLIENT)
   public PaintingType getType() {
      return IRegistry.MOTIVE.get(this.title);
   }
}
