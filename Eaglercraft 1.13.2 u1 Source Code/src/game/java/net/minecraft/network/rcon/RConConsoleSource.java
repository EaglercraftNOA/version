package net.minecraft.network.rcon;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;

public class RConConsoleSource implements ICommandSource {
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RConConsoleSource(MinecraftServer serverIn) {
      this.server = serverIn;
   }

   public void resetLog() {
      this.buffer.setLength(0);
   }

   public String getLogContents() {
      return this.buffer.toString();
   }

   public CommandSource getCommandSource() {
      WorldServer worldserver = this.server.getWorld(DimensionType.OVERWORLD);
      return new CommandSource(this, new Vec3d(worldserver.getSpawnPoint()), Vec2f.ZERO, worldserver, 4, "Recon", new TextComponentString("Rcon"), this.server, (Entity)null);
   }

   public void sendMessage(ITextComponent component) {
      this.buffer.append(component.getString());
   }

   public boolean shouldReceiveFeedback() {
      return true;
   }

   public boolean shouldReceiveErrors() {
      return true;
   }

   public boolean allowLogging() {
      return this.server.allowLoggingRcon();
   }
}
