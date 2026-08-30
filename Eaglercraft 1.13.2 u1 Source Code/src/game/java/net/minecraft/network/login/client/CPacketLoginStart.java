package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class CPacketLoginStart implements Packet<INetHandlerLoginServer> {
   private GameProfile profile;
   private byte[] skin;
   private byte[] cape;
   private byte[] protocols;
   private EaglercraftUUID brandUUID;

   public CPacketLoginStart() {
   }

   public CPacketLoginStart(GameProfile profileIn) {
      this(profileIn, new byte[0], new byte[0], new byte[0], new EaglercraftUUID(0L, 0L));
   }

   public CPacketLoginStart(GameProfile profileIn, byte[] skin, byte[] cape, byte[] protocols, EaglercraftUUID brandUUID) {
      this.profile = profileIn;
      this.skin = skin;
      this.cape = cape;
      this.protocols = protocols;
      this.brandUUID = brandUUID;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.profile = new GameProfile((UUID)null, buf.readString(16));
      this.skin = buf.readableBytes() > 0 ? buf.readByteArray() : null;
      this.cape = buf.readableBytes() > 0 ? buf.readByteArray() : null;
      this.protocols = buf.readableBytes() > 0 ? buf.readByteArray() : null;
      this.brandUUID = buf.readableBytes() > 0 ? new EaglercraftUUID(buf.readLong(), buf.readLong()) : null;
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.profile.getName());
      if(this.skin != null) {
         buf.writeByteArray(this.skin);
         buf.writeByteArray(this.cape != null ? this.cape : new byte[0]);
         buf.writeByteArray(this.protocols != null ? this.protocols : new byte[0]);
         if(this.brandUUID != null) {
            buf.writeLong(this.brandUUID.msb);
            buf.writeLong(this.brandUUID.lsb);
         }
      }
   }

   public void processPacket(INetHandlerLoginServer handler) {
      handler.processLoginStart(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public byte[] getSkin() {
      return this.skin;
   }

   public byte[] getCape() {
      return this.cape;
   }

   public byte[] getProtocols() {
      return this.protocols;
   }

   public EaglercraftUUID getBrandUUID() {
      return this.brandUUID;
   }
}
