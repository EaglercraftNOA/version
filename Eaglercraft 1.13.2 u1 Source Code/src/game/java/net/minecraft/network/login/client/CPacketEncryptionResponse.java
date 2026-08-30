package net.minecraft.network.login.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketEncryptionResponse implements Packet<INetHandlerLoginServer> {
   private byte[] secretKeyEncrypted = new byte[0];
   private byte[] verifyTokenEncrypted = new byte[0];

   public CPacketEncryptionResponse() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketEncryptionResponse(byte[] secret, byte[] key, byte[] verifyToken) {
      this.secretKeyEncrypted = secret;
      this.verifyTokenEncrypted = verifyToken;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.secretKeyEncrypted = buf.readByteArray();
      this.verifyTokenEncrypted = buf.readByteArray();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByteArray(this.secretKeyEncrypted);
      buf.writeByteArray(this.verifyTokenEncrypted);
   }

   public void processPacket(INetHandlerLoginServer handler) {
      handler.processEncryptionResponse(this);
   }

   public byte[] getSecretKey() {
      return this.secretKeyEncrypted;
   }

   public byte[] getVerifyToken() {
      return this.verifyTokenEncrypted;
   }
}
