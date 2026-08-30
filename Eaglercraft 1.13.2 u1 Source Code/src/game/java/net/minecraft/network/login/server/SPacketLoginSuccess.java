package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketLoginSuccess implements Packet<INetHandlerLoginClient> {
   private GameProfile profile;
   private int selectedProtocol = 3;

   public SPacketLoginSuccess() {
   }

   public SPacketLoginSuccess(GameProfile profileIn) {
      this(profileIn, 3);
   }

   public SPacketLoginSuccess(GameProfile profileIn, int selectedProtocol) {
      this.profile = profileIn;
      this.selectedProtocol = selectedProtocol;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      String s = buf.readString(36);
      String s1 = buf.readString(16);
      this.selectedProtocol = buf.readableBytes() > 0 ? buf.readShort() : 3;
      UUID uuid = UUID.fromString(s);
      this.profile = new GameProfile(uuid, s1);
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      UUID uuid = this.profile.getId();
      buf.writeString(uuid == null ? "" : uuid.toString());
      buf.writeString(this.profile.getName());
      if(this.selectedProtocol != 3) {
         buf.writeShort(this.selectedProtocol);
      }
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleLoginSuccess(this);
   }

   @OnlyIn(Dist.CLIENT)
   public GameProfile getProfile() {
      return this.profile;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSelectedProtocol() {
      return this.selectedProtocol;
   }
}
