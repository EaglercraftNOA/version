package net.minecraft.advancements;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.ResourceLocation;
import net.peyton.eagler.resources.AdvancementLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class AdvancementManager {
	private static final Logger field_192782_a = LogManager.getLogger();
	private static final AdvancementList field_192784_c = new AdvancementList();
	private final VFile2 field_192785_d;
	private boolean field_193768_e;

	public AdvancementManager(@Nullable VFile2 p_i47421_1_) {
		this.field_192785_d = p_i47421_1_;
		this.func_192779_a();
	}

	public void func_192779_a() {
		this.field_193768_e = false;
		field_192784_c.func_192087_a();
		Map<ResourceLocation, Advancement.Builder> map = this.func_192781_c();
		this.func_192777_a(map);
		field_192784_c.func_192083_a(map);

		for (Advancement advancement : field_192784_c.func_192088_b()) {
			if (advancement.func_192068_c() != null) {
				AdvancementTreeNode.func_192323_a(advancement);
			}
		}
	}

	public boolean func_193767_b() {
		return this.field_193768_e;
	}

	// TODO: Support for custom advancements
	/*
	 * Will have to generate a json file with every file in the directory for the
	 * custom advancements when the resource pack is added to the client
	 */
	private Map<ResourceLocation, Advancement.Builder> func_192781_c() {
		return Maps.<ResourceLocation, Advancement.Builder>newHashMap();
//    	if (this.field_192785_d == null)
//        {
//            return Maps.<ResourceLocation, Advancement.Builder>newHashMap();
//        }
//        else
//        {
//            Map<ResourceLocation, Advancement.Builder> map = Maps.<ResourceLocation, Advancement.Builder>newHashMap();
//
//            for (VFile2 file1 : field_192785_d.listFilesToArrayEndsWith(true, ".json"))
//            {
//            	String s = this.field_192785_d.getPath().replace(".json", "");
//                String[] astring = s.split("/", 2);
//
//                if (astring.length == 2)
//                {
//                    ResourceLocation resourcelocation = new ResourceLocation(astring[0], astring[1]);
//
//                    try
//                    {
//                    	InputStream is = file1.getInputStream();
//                    	String is_string = IOUtils.inputStreamToString(is, StandardCharsets.UTF_8);
//                        Advancement.Builder advancement$builder = (Advancement.Builder)JSONTypeProvider.deserialize(JSONTypeProvider.parse(is_string), Advancement.Builder.class);
//
//                        if (advancement$builder == null)
//                        {
//                            field_192782_a.error("Couldn't load custom advancement " + resourcelocation + " from " + file1 + " as it's empty or null");
//                        }
//                        else
//                        {
//                            map.put(resourcelocation, advancement$builder);
//                        }
//                    }
//                    catch (IllegalArgumentException | JSONException jsonparseexception)
//                    {
//                        field_192782_a.error("Parsing error loading custom advancement " + resourcelocation, (Throwable)jsonparseexception);
//                        this.field_193768_e = true;
//                    }
//                    catch (IOException ioexception)
//                    {
//                        field_192782_a.error("Couldn't read custom advancement " + resourcelocation + " from " + file1, (Throwable)ioexception);
//                        this.field_193768_e = true;
//                    }
//                }
//            }
//            return map;
//        }
	}

	private void func_192777_a(Map<ResourceLocation, Advancement.Builder> p_192777_1_) {

		try {
			List<String> advancements = AdvancementLoader.loadAdvancements();
			Iterator<String> iterator = advancements.iterator();

			while (iterator.hasNext()) {
				String path1 = iterator.next();

				if (path1.endsWith("json")) {
					String path2 = path1;
					String s = path2.split("advancements/")[1].replace(".json", "");
					ResourceLocation resourcelocation = new ResourceLocation(s);

					if (!p_192777_1_.containsKey(resourcelocation)) {
						String bufferedreader = null;

						try {
							bufferedreader = IOUtils.inputStreamToString(
									EagRuntime.getResourceStream("assets/minecraft/" + path1), StandardCharsets.UTF_8);
							JSONObject object = new JSONObject(bufferedreader);
							Advancement.Builder advancement$builder = (Advancement.Builder) JSONTypeProvider
									.deserialize(object, Advancement.Builder.class);
							p_192777_1_.put(resourcelocation, advancement$builder);
						} catch (JSONException jsonparseexception) {
							jsonparseexception.printStackTrace();
							field_192782_a.error("Parsing error loading built-in advancement " + resourcelocation,
									(Throwable) jsonparseexception);
							this.field_193768_e = true;
						} catch (Exception ioexception) {
							field_192782_a.error("Couldn't read advancement " + resourcelocation + " from " + path1,
									(Throwable) ioexception);
							this.field_193768_e = true;
						}
					}
				}
			}

			return;
		} catch (Exception e) {
			field_192782_a.error("Couldn't get a list of all built-in advancement files", (Throwable) e);
			this.field_193768_e = true;
			return;
		}
	}

	@Nullable
	public Advancement func_192778_a(ResourceLocation p_192778_1_) {
		return field_192784_c.func_192084_a(p_192778_1_);
	}

	public Iterable<Advancement> func_192780_b() {
		return field_192784_c.func_192089_c();
	}
}
