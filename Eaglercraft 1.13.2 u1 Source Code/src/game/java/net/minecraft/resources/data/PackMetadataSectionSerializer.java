package net.minecraft.resources.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class PackMetadataSectionSerializer implements IMetadataSectionSerializer<PackMetadataSection> {
   public PackMetadataSection deserialize(JsonObject json) {
      ITextComponent itextcomponent = JsonUtils.isString(json, "description") ? new TextComponentString(JsonUtils.getString(json, "description")) : ITextComponent.Serializer.fromJson(json.get("description"));
      if (itextcomponent == null) {
         throw new JsonParseException("Invalid/missing description!");
      } else {
         int i = JsonUtils.getInt(json, "pack_format");
         return new PackMetadataSection(itextcomponent, i);
      }
   }

   public String getSectionName() {
      return "pack";
   }
}
