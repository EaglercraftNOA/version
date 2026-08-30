package net.minecraft.state;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;

public interface IStateHolder<C> {
   Collection<IProperty<?>> getProperties();

   <T extends Comparable<T>> boolean has(IProperty<T> property);

   <T extends Comparable<T>> T get(IProperty<T> property);

   <T extends Comparable<T>, V extends T> C with(IProperty<T> property, V value);

   <T extends Comparable<T>> C cycle(IProperty<T> property);

   ImmutableMap<IProperty<?>, Comparable<?>> getValues();
}
