package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList {
	protected static final Logger field_152693_a = LogManager.getLogger();
	private final File field_152695_c;
	private final Map field_152696_d = Maps.newHashMap();
	private boolean field_152697_e = true;

	private static final String __OBFID = "CL_00001876";

	public UserList(File p_i1144_1_) {
		this.field_152695_c = p_i1144_1_;
		this.func_152679_g();
	}

	private void func_152679_g() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(this.field_152695_c), Charsets.UTF_8));
			StringBuilder jsonStr = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				jsonStr.append(line);
			}
			JSONArray arr = new JSONArray(jsonStr.toString());
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject obj = arr.getJSONObject(i);
				UserListEntry entry = this.func_152682_a(obj);
				if (entry != null) {
					this.field_152696_d.put(this.func_152681_a(entry.func_152640_f()), entry);
				}
			}
		} catch (Exception e) {

		} finally {
			net.lax1dude.eaglercraft.IOUtils.closeQuietly(reader);
		}
	}

	public boolean func_152689_b() {
		return this.field_152697_e;
	}

	public void func_152686_a(boolean p_152686_1_) {
		this.field_152697_e = p_152686_1_;
	}

	public void func_152687_a(UserListEntry p_152687_1_) {
		this.field_152696_d.put(this.func_152681_a(p_152687_1_.func_152640_f()), p_152687_1_);

		try {
			this.func_152678_f();
		} catch (IOException var3) {
			field_152693_a.warn("Could not save the list after adding a user.", var3);
		}
	}

	public UserListEntry func_152683_b(Object p_152683_1_) {
		this.func_152680_h();
		return (UserListEntry) this.field_152696_d.get(this.func_152681_a(p_152683_1_));
	}

	public void func_152684_c(Object p_152684_1_) {
		this.field_152696_d.remove(this.func_152681_a(p_152684_1_));

		try {
			this.func_152678_f();
		} catch (IOException var3) {
			field_152693_a.warn("Could not save the list after removing a user.", var3);
		}
	}

	public String[] func_152685_a() {
		return (String[]) this.field_152696_d.keySet().toArray(new String[this.field_152696_d.size()]);
	}

	protected String func_152681_a(Object p_152681_1_) {
		return p_152681_1_.toString();
	}

	protected boolean func_152692_d(Object p_152692_1_) {
		return this.field_152696_d.containsKey(this.func_152681_a(p_152692_1_));
	}

	private void func_152680_h() {
		ArrayList var1 = Lists.newArrayList();
		Iterator var2 = this.field_152696_d.values().iterator();

		while (var2.hasNext()) {
			UserListEntry var3 = (UserListEntry) var2.next();

			if (var3.hasBanExpired()) {
				var1.add(var3.func_152640_f());
			}
		}

		var2 = var1.iterator();

		while (var2.hasNext()) {
			Object var4 = var2.next();
			this.field_152696_d.remove(var4);
		}
	}

	protected UserListEntry func_152682_a(JSONObject p_152682_1_) {
		return new UserListEntry((Object) null, p_152682_1_);
	}

	protected Map func_152688_e() {
		return this.field_152696_d;
	}

	public void func_152678_f() throws IOException {
		Collection var1 = this.field_152696_d.values();
		JSONArray arr = new JSONArray();
		for (Object entryObj : var1) {
			UserListEntry entry = (UserListEntry) entryObj;
			JSONObject obj = new JSONObject();
			entry.func_152641_a(obj);
			arr.put(obj);
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(this.field_152695_c), Charsets.UTF_8));
			writer.write(arr.toString());
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}

}
