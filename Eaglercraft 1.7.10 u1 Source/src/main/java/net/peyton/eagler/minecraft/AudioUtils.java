package net.peyton.eagler.minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lax1dude.eaglercraft.Random;

public class AudioUtils {

	private static List<String> files = new ArrayList<String>();

	private static Map<String, Integer> numOfSounds = new HashMap<String, Integer>();

	private static Random rand = new Random();

	public static void addFile(String name) {
		files.add(name);
	}

	public static int getRandomSound(String name) {
		Integer integer = numOfSounds.get(name);
		if (integer != null) {
			int i = integer.intValue();
			if (i == -1) {
				return i;
			}
			return rand.nextInt(integer.intValue()) + 1;
		} else {
			int num = 0;
			for (int i = 0, j = files.size(); i < j; ++i) {
				String s = files.get(i);
				if (s.contains(name)) {
					num++;
				}
			}
			if (num == 1) {
				num = -1;
				numOfSounds.put(name, Integer.valueOf(num));
				return num;
			}
			numOfSounds.put(name, Integer.valueOf(num));
			return rand.nextInt(num) + 1;
		}
	}

}
