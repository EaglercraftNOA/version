package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEntityHeadLook implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private byte yaw;

   public SPacketEntityHeadLook() {
   }

   public SPacketEntityHeadLook(Entity entityIn, byte yawIn) {
      this.entityId = entityIn.getEntityId();
      this.yaw = yawIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarInt();
      this.yaw = buf.readByte();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.entityId);
      buf.writeByte(this.yaw);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityHeadLook(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   @OnlyIn(Dist.CLIENT)
   public byte getYaw() {
      return this.yaw;
   }
}
