package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ReuseableStream<T> {
   private final List<T> field_212762_a = Lists.newArrayList();

   public ReuseableStream(Stream<T> p_i49816_1_) {
      Iterator<T> iterator = p_i49816_1_.iterator();

      while(iterator.hasNext()) {
         this.field_212762_a.add(iterator.next());
      }

   }

   public Stream<T> func_212761_a() {
      return this.field_212762_a.stream();
   }
}
