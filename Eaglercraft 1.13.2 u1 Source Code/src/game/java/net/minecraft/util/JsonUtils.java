package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JsonUtils {
   private static final Gson field_212747_a = (new GsonBuilder()).create();

   public static boolean isString(JsonObject json, String memberName) {
      return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isString();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isString(JsonElement json) {
      return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isString();
   }

   public static boolean isNumber(JsonElement json) {
      return !json.isJsonPrimitive() ? false : json.getAsJsonPrimitive().isNumber();
   }

   @OnlyIn(Dist.CLIENT)
   public static boolean isBoolean(JsonObject json, String memberName) {
      return !isJsonPrimitive(json, memberName) ? false : json.getAsJsonPrimitive(memberName).isBoolean();
   }

   public static boolean isJsonArray(JsonObject json, String memberName) {
      return !hasField(json, memberName) ? false : json.get(memberName).isJsonArray();
   }

   public static boolean isJsonPrimitive(JsonObject json, String memberName) {
      return !hasField(json, memberName) ? false : json.get(memberName).isJsonPrimitive();
   }

   public static boolean hasField(JsonObject json, String memberName) {
      if (json == null) {
         return false;
      } else {
         return json.get(memberName) != null;
      }
   }

   public static String getString(JsonElement json, String memberName) {
      if (json.isJsonPrimitive()) {
         return json.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + toString(json));
      }
   }

   public static String getString(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getString(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
      }
   }

   public static String getString(JsonObject json, String memberName, String fallback) {
      return json.has(memberName) ? getString(json.get(memberName), memberName) : fallback;
   }

   public static Item getItem(JsonElement json, String memberName) {
      if (json.isJsonPrimitive()) {
         String s = json.getAsString();
         Item item = IRegistry.ITEM.get(new ResourceLocation(s));
         if (item == null) {
            throw new JsonSyntaxException("Expected " + memberName + " to be an item, was unknown string '" + s + "'");
         } else {
            return item;
         }
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be an item, was " + toString(json));
      }
   }

   public static Item getItem(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getItem(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find an item");
      }
   }

   public static boolean getBoolean(JsonElement json, String memberName) {
      if (json.isJsonPrimitive()) {
         return json.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Boolean, was " + toString(json));
      }
   }

   public static boolean getBoolean(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getBoolean(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
      }
   }

   public static boolean getBoolean(JsonObject json, String memberName, boolean fallback) {
      return json.has(memberName) ? getBoolean(json.get(memberName), memberName) : fallback;
   }

   public static float getFloat(JsonElement json, String memberName) {
      if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
         return json.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Float, was " + toString(json));
      }
   }

   public static float getFloat(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getFloat(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Float");
      }
   }

   public static float getFloat(JsonObject json, String memberName, float fallback) {
      return json.has(memberName) ? getFloat(json.get(memberName), memberName) : fallback;
   }

   public static int getInt(JsonElement json, String memberName) {
      if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
         return json.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + toString(json));
      }
   }

   public static int getInt(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getInt(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
      }
   }

   public static int getInt(JsonObject json, String memberName, int fallback) {
      return json.has(memberName) ? getInt(json.get(memberName), memberName) : fallback;
   }

   public static byte getByte(JsonElement json, String memberName) {
      if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
         return json.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a Byte, was " + toString(json));
      }
   }

   public static byte getByte(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getByte(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Byte");
      }
   }

   public static JsonObject getJsonObject(JsonElement json, String memberName) {
      if (json.isJsonObject()) {
         return json.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + toString(json));
      }
   }

   public static JsonObject getJsonObject(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getJsonObject(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonObject");
      }
   }

   public static JsonObject getJsonObject(JsonObject json, String memberName, JsonObject fallback) {
      return json.has(memberName) ? getJsonObject(json.get(memberName), memberName) : fallback;
   }

   public static JsonArray getJsonArray(JsonElement json, String memberName) {
      if (json.isJsonArray()) {
         return json.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + memberName + " to be a JsonArray, was " + toString(json));
      }
   }

   public static JsonArray getJsonArray(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return getJsonArray(json.get(memberName), memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + ", expected to find a JsonArray");
      }
   }

   public static JsonArray getJsonArray(JsonObject json, String memberName, @Nullable JsonArray fallback) {
      return json.has(memberName) ? getJsonArray(json.get(memberName), memberName) : fallback;
   }

   public static <T> T deserializeClass(@Nullable JsonElement json, String memberName, JsonDeserializationContext context, Class<? extends T> adapter) {
      if (json != null) {
         return context.deserialize(json, adapter);
      } else {
         throw new JsonSyntaxException("Missing " + memberName);
      }
   }

   public static <T> T deserializeClass(JsonObject json, String memberName, JsonDeserializationContext context, Class<? extends T> adapter) {
      if (json.has(memberName)) {
         return deserializeClass(json.get(memberName), memberName, context, adapter);
      } else {
         throw new JsonSyntaxException("Missing " + memberName);
      }
   }

   public static <T> T deserializeClass(JsonObject json, String memberName, T fallback, JsonDeserializationContext context, Class<? extends T> adapter) {
      return (T)(json.has(memberName) ? deserializeClass(json.get(memberName), memberName, context, adapter) : fallback);
   }

   public static <T> T[] deserializeArray(JsonElement json, String memberName, JsonDeserializationContext context, Class<T> adapter, IntFunction<T[]> factory) {
      JsonArray jsonarray = getJsonArray(json, memberName);
      T[] at = factory.apply(jsonarray.size());

      for(int i = 0; i < at.length; ++i) {
         at[i] = context.deserialize(jsonarray.get(i), adapter);
      }

      return at;
   }

   public static <T> T[] deserializeArray(JsonObject json, String memberName, JsonDeserializationContext context, Class<T> adapter, IntFunction<T[]> factory) {
      if (json.has(memberName)) {
         return deserializeArray(json.get(memberName), memberName, context, adapter, factory);
      } else {
         throw new JsonSyntaxException("Missing " + memberName);
      }
   }

   public static <T> T[] deserializeArray(JsonObject json, String memberName, T[] fallback, JsonDeserializationContext context, Class<T> adapter, IntFunction<T[]> factory) {
      return json.has(memberName) ? deserializeArray(json.get(memberName), memberName, context, adapter, factory) : fallback;
   }

   public static String toString(JsonElement json) {
      String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf((Object)json), "...", 10);
      if (json == null) {
         return "null (missing)";
      } else if (json.isJsonNull()) {
         return "null (json)";
      } else if (json.isJsonArray()) {
         return "an array (" + s + ")";
      } else if (json.isJsonObject()) {
         return "an object (" + s + ")";
      } else {
         if (json.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = json.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               return "a number (" + s + ")";
            }

            if (jsonprimitive.isBoolean()) {
               return "a boolean (" + s + ")";
            }
         }

         return s;
      }
   }

   @Nullable
   public static <T> T fromJson(Gson gsonIn, Reader readerIn, Class<T> adapter, boolean lenient) {
      try {
         JsonReader jsonreader = new JsonReader(readerIn);
         jsonreader.setLenient(lenient);
         return gsonIn.getAdapter(adapter).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter, boolean lenient) {
      return fromJson(gsonIn, new StringReader(json), adapter, lenient);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static <T> T fromJson(Gson gson, Reader reader, Class<T> jsonClass) {
      return fromJson(gson, reader, jsonClass, false);
   }

   @Nullable
   public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter) {
      return fromJson(gsonIn, json, adapter, false);
   }

   public static JsonObject fromJson(String p_212746_0_, boolean p_212746_1_) {
      return fromJson(new StringReader(p_212746_0_), p_212746_1_);
   }

   public static JsonObject fromJson(Reader p_212744_0_, boolean p_212744_1_) {
      return fromJson(field_212747_a, p_212744_0_, JsonObject.class, p_212744_1_);
   }

   public static JsonObject fromJson(String p_212745_0_) {
      return fromJson(p_212745_0_, false);
   }

   public static JsonObject fromJson(Reader p_212743_0_) {
      return fromJson(p_212743_0_, false);
   }
}
