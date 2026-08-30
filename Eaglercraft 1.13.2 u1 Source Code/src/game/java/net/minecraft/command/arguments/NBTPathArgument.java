package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCollection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentTranslation;

public class NBTPathArgument implements ArgumentType<NBTPathArgument.NBTPath> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar", "foo[0]", "[0]", ".");
   private static final DynamicCommandExceptionType NO_CHILD = new DynamicCommandExceptionType((p_208665_0_) -> {
      return new TextComponentTranslation("arguments.nbtpath.child.invalid", p_208665_0_);
   });
   private static final DynamicCommandExceptionType NO_ELEMENT = new DynamicCommandExceptionType((p_208666_0_) -> {
      return new TextComponentTranslation("arguments.nbtpath.element.invalid", p_208666_0_);
   });
   private static final SimpleCommandExceptionType PATH_MALFORMED = new SimpleCommandExceptionType(new TextComponentTranslation("arguments.nbtpath.node.invalid"));

   public static NBTPathArgument nbtPath() {
      return new NBTPathArgument();
   }

   public static NBTPathArgument.NBTPath getNBTPath(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, NBTPathArgument.NBTPath.class);
   }

   public NBTPathArgument.NBTPath parse(StringReader p_parse_1_) throws CommandSyntaxException {
      List<NBTPathArgument.INode> list = Lists.newArrayList();
      int i = p_parse_1_.getCursor();

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         switch(p_parse_1_.peek()) {
         case '"':
            list.add(new NBTPathArgument.ChildNode(p_parse_1_.readString()));
            break;
         case '[':
            p_parse_1_.skip();
            list.add(new NBTPathArgument.ElementNode(p_parse_1_.readInt()));
            p_parse_1_.expect(']');
            break;
         default:
            list.add(new NBTPathArgument.ChildNode(this.readTagName(p_parse_1_)));
         }

         if (p_parse_1_.canRead()) {
            char c0 = p_parse_1_.peek();
            if (c0 != ' ' && c0 != '[') {
               p_parse_1_.expect('.');
            }
         }
      }

      return new NBTPathArgument.NBTPath(p_parse_1_.getString().substring(i, p_parse_1_.getCursor()), list.toArray(new NBTPathArgument.INode[0]));
   }

   private String readTagName(StringReader reader) throws CommandSyntaxException {
      int i = reader.getCursor();

      while(reader.canRead() && isSimpleNameChar(reader.peek())) {
         reader.skip();
      }

      if (reader.getCursor() == i) {
         throw PATH_MALFORMED.createWithContext(reader);
      } else {
         return reader.getString().substring(i, reader.getCursor());
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static boolean isSimpleNameChar(char ch) {
      return ch != ' ' && ch != '"' && ch != '[' && ch != ']' && ch != '.';
   }

   static class ChildNode implements NBTPathArgument.INode {
      private final String name;

      public ChildNode(String nameIn) {
         this.name = nameIn;
      }

      public INBTBase get(INBTBase nbt) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCompound) {
            return ((NBTTagCompound)nbt).get(this.name);
         } else {
            throw NBTPathArgument.NO_CHILD.create(this.name);
         }
      }

      public INBTBase getOrDefault(INBTBase nbt, Supplier<INBTBase> defaultSupplier) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbt;
            if (nbttagcompound.contains(this.name)) {
               return nbttagcompound.get(this.name);
            } else {
               INBTBase inbtbase = defaultSupplier.get();
               nbttagcompound.put(this.name, inbtbase);
               return inbtbase;
            }
         } else {
            throw NBTPathArgument.NO_CHILD.create(this.name);
         }
      }

      public INBTBase createEmptyElement() {
         return new NBTTagCompound();
      }

      public void set(INBTBase nbt, INBTBase value) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbt;
            nbttagcompound.put(this.name, value);
         } else {
            throw NBTPathArgument.NO_CHILD.create(this.name);
         }
      }

      public void remove(INBTBase nbt) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCompound) {
            NBTTagCompound nbttagcompound = (NBTTagCompound)nbt;
            if (nbttagcompound.contains(this.name)) {
               nbttagcompound.remove(this.name);
               return;
            }
         }

         throw NBTPathArgument.NO_CHILD.create(this.name);
      }
   }

   static class ElementNode implements NBTPathArgument.INode {
      private final int index;

      public ElementNode(int indexIn) {
         this.index = indexIn;
      }

      public INBTBase get(INBTBase nbt) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCollection) {
            NBTTagCollection<?> nbttagcollection = (NBTTagCollection)nbt;
            if (nbttagcollection.size() > this.index) {
               return nbttagcollection.getTag(this.index);
            }
         }

         throw NBTPathArgument.NO_ELEMENT.create(this.index);
      }

      public INBTBase getOrDefault(INBTBase nbt, Supplier<INBTBase> defaultSupplier) throws CommandSyntaxException {
         return this.get(nbt);
      }

      public INBTBase createEmptyElement() {
         return new NBTTagList();
      }

      public void set(INBTBase nbt, INBTBase value) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCollection) {
            NBTTagCollection<?> nbttagcollection = (NBTTagCollection)nbt;
            if (nbttagcollection.size() > this.index) {
               nbttagcollection.setTag(this.index, value);
               return;
            }
         }

         throw NBTPathArgument.NO_ELEMENT.create(this.index);
      }

      public void remove(INBTBase nbt) throws CommandSyntaxException {
         if (nbt instanceof NBTTagCollection) {
            NBTTagCollection<?> nbttagcollection = (NBTTagCollection)nbt;
            if (nbttagcollection.size() > this.index) {
               nbttagcollection.removeTag(this.index);
               return;
            }
         }

         throw NBTPathArgument.NO_ELEMENT.create(this.index);
      }
   }

   interface INode {
      INBTBase get(INBTBase nbt) throws CommandSyntaxException;

      INBTBase getOrDefault(INBTBase nbt, Supplier<INBTBase> defaultSupplier) throws CommandSyntaxException;

      INBTBase createEmptyElement();

      void set(INBTBase nbt, INBTBase value) throws CommandSyntaxException;

      void remove(INBTBase nbt) throws CommandSyntaxException;
   }

   public static class NBTPath {
      private final String rawText;
      private final NBTPathArgument.INode[] nodes;

      public NBTPath(String rawTextIn, NBTPathArgument.INode[] nodesIn) {
         this.rawText = rawTextIn;
         this.nodes = nodesIn;
      }

      public INBTBase get(INBTBase nbt) throws CommandSyntaxException {
         for(NBTPathArgument.INode nbtpathargument$inode : this.nodes) {
            nbt = nbtpathargument$inode.get(nbt);
         }

         return nbt;
      }

      public INBTBase set(INBTBase nbt, INBTBase value) throws CommandSyntaxException {
         for(int i = 0; i < this.nodes.length; ++i) {
            NBTPathArgument.INode nbtpathargument$inode = this.nodes[i];
            if (i < this.nodes.length - 1) {
               int j = i + 1;
               nbt = nbtpathargument$inode.getOrDefault(nbt, () -> {
                  return this.nodes[j].createEmptyElement();
               });
            } else {
               nbtpathargument$inode.set(nbt, value);
            }
         }

         return nbt;
      }

      public String toString() {
         return this.rawText;
      }

      public void remove(INBTBase nbt) throws CommandSyntaxException {
         for(int i = 0; i < this.nodes.length; ++i) {
            NBTPathArgument.INode nbtpathargument$inode = this.nodes[i];
            if (i < this.nodes.length - 1) {
               nbt = nbtpathargument$inode.get(nbt);
            } else {
               nbtpathargument$inode.remove(nbt);
            }
         }

      }
   }
}
