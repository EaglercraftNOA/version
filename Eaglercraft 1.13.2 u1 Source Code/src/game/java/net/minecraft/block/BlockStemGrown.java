package net.minecraft.block;

public abstract class BlockStemGrown extends Block {
   public BlockStemGrown(Block.Properties properties) {
      super(properties);
   }

   public abstract BlockStem getStem();

   public abstract BlockAttachedStem getAttachedStem();
}
