package com.mojang.datafixers;

public enum DataFixTypes implements DSL.TypeReference {
   LEVEL("level"),
   PLAYER("player"),
   CHUNK("chunk"),
   HOTBAR("hotbar"),
   OPTIONS("options"),
   STRUCTURE("structure"),
   STATS("stats"),
   SAVED_DATA("saved_data"),
   ADVANCEMENTS("advancements");

   private final String typeName;

   private DataFixTypes(String typeName) {
      this.typeName = typeName;
   }

   public String typeName() {
      return this.typeName;
   }
}
