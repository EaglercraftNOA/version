package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketEncryptionRequest implements Packet<INetHandlerLoginClient> {
   private String hashedServerId;
   private byte[] publicKey = new byte[0];
   private byte[] verifyToken;

   public SPacketEncryptionRequest() {
   }

   public SPacketEncryptionRequest(String serverIdIn, byte[] publicKeyIn, byte[] verifyTokenIn) {
      this.hashedServerId = serverIdIn;
      this.publicKey = publicKeyIn;
      this.verifyToken = verifyTokenIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.hashedServerId = buf.readString(20);
      this.publicKey = buf.readByteArray();
      this.verifyToken = buf.readByteArray();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.hashedServerId);
      buf.writeByteArray(this.publicKey);
      buf.writeByteArray(this.verifyToken);
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleEncryptionRequest(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getServerId() {
      return this.hashedServerId;
   }

   @OnlyIn(Dist.CLIENT)
   public byte[] getPublicKey() {
      return this.publicKey;
   }

   @OnlyIn(Dist.CLIENT)
   public byte[] getVerifyToken() {
      return this.verifyToken;
   }
}
