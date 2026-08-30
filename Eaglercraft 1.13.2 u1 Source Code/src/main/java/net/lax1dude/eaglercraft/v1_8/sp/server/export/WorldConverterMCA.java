/*
 * Copyright (c) 2022-2024 lax1dude, ayunami2000. All Rights Reserved.
 */

package net.lax1dude.eaglercraft.v1_8.sp.server.export;

import java.io.IOException;

/**
 * MCA import/export needs java.nio.file-backed RegionFile access. The WASM GC
 * browser bundle does not provide java.nio.file, so keep this path unavailable
 * instead of linking desktop filesystem APIs into TeaVM.
 */
public class WorldConverterMCA {

   public static void importWorld(byte[] archiveContents, String newName, byte gameRules) throws IOException {
      throw new IOException("MCA import is not available in the browser runtime");
   }

   public static byte[] exportWorld(String folderName) throws IOException {
      throw new IOException("MCA export is not available in the browser runtime");
   }

}
