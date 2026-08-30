package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.IOException;

public interface IDataProvider {
   HashFunction HASH_FUNCTION = Hashing.sha1();

   void act(DirectoryCache cache) throws IOException;

   String getName();
}
