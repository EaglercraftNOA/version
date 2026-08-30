package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCommandList implements Packet<INetHandlerPlayClient> {
   private RootCommandNode<ISuggestionProvider> root;

   public SPacketCommandList() {
   }

   public SPacketCommandList(RootCommandNode<ISuggestionProvider> rootIn) {
      this.root = rootIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      SPacketCommandList.Entry[] aspacketcommandlist$entry = new SPacketCommandList.Entry[buf.readVarInt()];

      for(int i = 0; i < aspacketcommandlist$entry.length; ++i) {
         aspacketcommandlist$entry[i] = this.readEntry(buf);
      }

      boolean[] aboolean = new boolean[aspacketcommandlist$entry.length];
      int j = aspacketcommandlist$entry.length;
      while(j > 0) {
         boolean flag = false;
         for(int k = 0; k < aspacketcommandlist$entry.length; ++k) {
            if (!aboolean[k] && aspacketcommandlist$entry[k].createCommandNode(aspacketcommandlist$entry)) {
               aboolean[k] = true;
               --j;
               flag = true;
            }
         }

         if (!flag) {
            throw new IllegalStateException("Server sent an impossible command tree");
         }
      }

      this.root = (RootCommandNode)aspacketcommandlist$entry[buf.readVarInt()].node;
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      Map<CommandNode<ISuggestionProvider>, Integer> map = Maps.newHashMap();
      Deque<CommandNode<ISuggestionProvider>> deque = new ArrayDeque<>();
      deque.add(this.root);

      while(!deque.isEmpty()) {
         CommandNode<ISuggestionProvider> commandnode = deque.pollFirst();
         if (!map.containsKey(commandnode)) {
            int i = map.size();
            map.put(commandnode, i);
            deque.addAll(commandnode.getChildren());
            if (commandnode.getRedirect() != null) {
               deque.add(commandnode.getRedirect());
            }
         }
      }

      CommandNode<ISuggestionProvider>[] commandnode2 = new CommandNode[map.size()];

      for(Map.Entry<CommandNode<ISuggestionProvider>, Integer> entry : map.entrySet()) {
         commandnode2[entry.getValue()] = entry.getKey();
      }

      buf.writeVarInt(commandnode2.length);

      for(CommandNode<ISuggestionProvider> commandnode1 : commandnode2) {
         this.writeCommandNode(buf, commandnode1, map);
      }

      buf.writeVarInt(map.get(this.root));
   }

   private SPacketCommandList.Entry readEntry(PacketBuffer buf) {
      byte b0 = buf.readByte();
      int[] aint = buf.readVarIntArray();
      int i = (b0 & 8) != 0 ? buf.readVarInt() : 0;
      ArgumentBuilder<ISuggestionProvider, ?> argumentbuilder = this.readArgumentBuilder(buf, b0);
      return new SPacketCommandList.Entry(argumentbuilder, b0, i, aint);
   }

   @Nullable
   private ArgumentBuilder<ISuggestionProvider, ?> readArgumentBuilder(PacketBuffer buf, byte flags) {
      int i = flags & 3;
      if (i == 2) {
         String s = buf.readString(32767);
         ArgumentType<?> argumenttype = ArgumentTypes.deserialize(buf);
         if (argumenttype == null) {
            return null;
         } else {
            RequiredArgumentBuilder<ISuggestionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);
            if ((flags & 16) != 0) {
               requiredargumentbuilder.suggests(SuggestionProviders.get(buf.readResourceLocation()));
            }

            return requiredargumentbuilder;
         }
      } else {
         return i == 1 ? LiteralArgumentBuilder.literal(buf.readString(32767)) : null;
      }
   }

   private void writeCommandNode(PacketBuffer buf, CommandNode<ISuggestionProvider> node, Map<CommandNode<ISuggestionProvider>, Integer> nodeIds) {
      byte b0 = 0;
      if (node.getRedirect() != null) {
         b0 = (byte)(b0 | 8);
      }

      if (node.getCommand() != null) {
         b0 = (byte)(b0 | 4);
      }

      if (node instanceof RootCommandNode) {
         b0 = (byte)(b0 | 0);
      } else if (node instanceof ArgumentCommandNode) {
         b0 = (byte)(b0 | 2);
         if (((ArgumentCommandNode)node).getCustomSuggestions() != null) {
            b0 = (byte)(b0 | 16);
         }
      } else {
         if (!(node instanceof LiteralCommandNode)) {
            throw new UnsupportedOperationException("Unknown node type " + node);
         }

         b0 = (byte)(b0 | 1);
      }

      buf.writeByte(b0);
      buf.writeVarInt(node.getChildren().size());

      for(CommandNode<ISuggestionProvider> commandnode : node.getChildren()) {
         buf.writeVarInt(nodeIds.get(commandnode));
      }

      if (node.getRedirect() != null) {
         buf.writeVarInt(nodeIds.get(node.getRedirect()));
      }

      if (node instanceof ArgumentCommandNode) {
         ArgumentCommandNode<ISuggestionProvider, ?> argumentcommandnode = (ArgumentCommandNode)node;
         buf.writeString(argumentcommandnode.getName());
         ArgumentTypes.serialize(buf, argumentcommandnode.getType());
         if (argumentcommandnode.getCustomSuggestions() != null) {
            buf.writeResourceLocation(SuggestionProviders.getId(argumentcommandnode.getCustomSuggestions()));
         }
      } else if (node instanceof LiteralCommandNode) {
         buf.writeString(((LiteralCommandNode)node).getLiteral());
      }

   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleCommandList(this);
   }

   @OnlyIn(Dist.CLIENT)
   public RootCommandNode<ISuggestionProvider> getRoot() {
      return this.root;
   }

   static class Entry {
      @Nullable
      private final ArgumentBuilder<ISuggestionProvider, ?> argBuilder;
      private final byte flags;
      private final int redirectTarget;
      private final int[] children;
      private CommandNode<ISuggestionProvider> node;

      private Entry(@Nullable ArgumentBuilder<ISuggestionProvider, ?> argBuilderIn, byte flagsIn, int redirectTargetIn, int[] childrenIn) {
         this.argBuilder = argBuilderIn;
         this.flags = flagsIn;
         this.redirectTarget = redirectTargetIn;
         this.children = childrenIn;
      }

      public boolean createCommandNode(SPacketCommandList.Entry[] nodeArray) {
         if (this.node == null) {
            if (this.argBuilder == null) {
               this.node = new RootCommandNode<>();
            } else {
               if ((this.flags & 8) != 0) {
                  if (nodeArray[this.redirectTarget].node == null) {
                     return false;
                  }

                  this.argBuilder.redirect(nodeArray[this.redirectTarget].node);
               }

               if ((this.flags & 4) != 0) {
                  this.argBuilder.executes((p_197724_0_) -> {
                     return 0;
                  });
               }

               this.node = this.argBuilder.build();
            }
         }

         for(int i : this.children) {
            if (nodeArray[i].node == null) {
               return false;
            }
         }

         for(int j : this.children) {
            CommandNode<ISuggestionProvider> commandnode = nodeArray[j].node;
            if (!(commandnode instanceof RootCommandNode)) {
               this.node.addChild(commandnode);
            }
         }

         return true;
      }
   }
}
