package net.minecraft.client.util;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISearchTree<T> {
   List<T> search(String searchText);
}
