package net.minecraft.client.main;

import com.google.common.collect.HashMultimap;
import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
	private static final java.lang.reflect.Type field_152370_a = new ParameterizedType() {
		private static final String __OBFID = "CL_00000828";

		public java.lang.reflect.Type[] getActualTypeArguments() {
			return new java.lang.reflect.Type[] { String.class, new ParameterizedType() {
				private static final String __OBFID = "CL_00001836";

				public java.lang.reflect.Type[] getActualTypeArguments() {
					return new java.lang.reflect.Type[] { String.class };
				}

				public java.lang.reflect.Type getRawType() {
					return Collection.class;
				}

				public java.lang.reflect.Type getOwnerType() {
					return null;
				}
			} };
		}

		public java.lang.reflect.Type getRawType() {
			return Map.class;
		}

		public java.lang.reflect.Type getOwnerType() {
			return null;
		}
	};
	private static final String __OBFID = "CL_00001461";

	public static void main(String[] p_main_0_) {

	}

	private static boolean func_110121_a(String p_110121_0_) {
		return p_110121_0_ != null && !p_110121_0_.isEmpty();
	}
}
