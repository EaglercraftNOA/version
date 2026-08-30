package net.lax1dude.eaglercraft.v1_8.profile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.zip.CRC32;

import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerZLIB;
import net.lax1dude.eaglercraft.v1_8.sp.server.export.EPKDecompiler;
import net.lax1dude.eaglercraft.v1_8.sp.server.export.EPKDecompiler.FileEntry;
import net.lax1dude.eaglercraft.v1_8.sp.server.export.EPKCompiler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;

public class ProfileBackupUtil {

	private ProfileBackupUtil() {
	}

	public static byte[] exportProfile() throws IOException {
		byte[] profileData = EaglerProfile.write();
		if (profileData == null) {
			throw new IOException("Could not write profile data");
		}

		EaglerOutputStream osb = new EaglerOutputStream();
		osb.write(new byte[] { (byte) 69, (byte) 65, (byte) 71, (byte) 80, (byte) 75, (byte) 71, (byte) 36,
				(byte) 36 });
		osb.write(new byte[] { (byte) 6, (byte) 118, (byte) 101, (byte) 114, (byte) 50, (byte) 46, (byte) 48 });

		byte[] filename = "profile.epk".getBytes(StandardCharsets.UTF_8);
		osb.write(filename.length);
		osb.write(filename);

		byte[] comment = ("\n\n #  Eaglercraft profile backup - \"" + EaglerProfile.getName() + "\"\n\n")
				.getBytes(StandardCharsets.UTF_8);
		osb.write((comment.length >>> 8) & 255);
		osb.write(comment.length & 255);
		osb.write(comment);
		EPKCompiler.writeLong((new Date()).getTime(), osb);

		int lengthIntegerOffset = osb.size();
		osb.write(new byte[] { (byte) 0, (byte) 0, (byte) 0, (byte) 3 });
		osb.write('G');
		try (OutputStream os = EaglerZLIB.newGZIPOutputStream(osb)) {
			writeHead(os, "file-type", "epk/profile188".getBytes(StandardCharsets.US_ASCII));
			writeHead(os, "file-exports", new byte[] { 1 });
			writeFile(os, "_eaglercraftX.p", profileData);
			os.write(new byte[] { (byte) 69, (byte) 78, (byte) 68, (byte) 36 });
		}

		osb.write(new byte[] { (byte) 58, (byte) 58, (byte) 58, (byte) 89, (byte) 69, (byte) 69, (byte) 58,
				(byte) 62 });
		byte[] ret = osb.toByteArray();
		ret[lengthIntegerOffset] = 0;
		ret[lengthIntegerOffset + 1] = 0;
		ret[lengthIntegerOffset + 2] = 0;
		ret[lengthIntegerOffset + 3] = 3;
		return ret;
	}

	public static byte[] readProfile(byte[] data) throws IOException {
		try (EPKDecompiler epk = new EPKDecompiler(data)) {
			FileEntry entry;
			boolean profileBackup = false;
			while ((entry = epk.readFile()) != null) {
				if ("HEAD".equals(entry.type) && "file-type".equals(entry.name)) {
					String fileType = EPKDecompiler.readASCII(entry.data);
					profileBackup = "epk/profile188".equals(fileType) || "epk/profile117".equals(fileType);
				} else if ("FILE".equals(entry.type) && "_eaglercraftX.p".equals(entry.name)) {
					return entry.data;
				}
			}
			if (profileBackup) {
				throw new IOException("Profile backup did not contain profile data");
			}
		} catch (IOException ex) {
			if (looksLikeRawProfile(data)) {
				return data;
			}
			throw ex;
		}
		throw new IOException("Not a profile backup");
	}

	private static boolean looksLikeRawProfile(byte[] data) {
		try {
			NBTTagCompound profile = CompressedStreamTools.readCompressed(new EaglerInputStream(data));
			return profile != null && profile.contains("username", 8) && profile.contains("presetSkin", 99)
					&& profile.contains("customSkin", 99);
		} catch (IOException ex) {
			return false;
		}
	}

	private static void writeHead(OutputStream os, String name, byte[] data) throws IOException {
		os.write(new byte[] { (byte) 72, (byte) 69, (byte) 65, (byte) 68 });
		byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
		os.write(nameBytes.length);
		os.write(nameBytes);
		EPKCompiler.writeInt(data.length, os);
		os.write(data);
		os.write('>');
	}

	private static void writeFile(OutputStream os, String name, byte[] data) throws IOException {
		CRC32 crc32 = new CRC32();
		crc32.update(data);
		os.write(new byte[] { (byte) 70, (byte) 73, (byte) 76, (byte) 69 });
		byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
		os.write(nameBytes.length);
		os.write(nameBytes);
		EPKCompiler.writeInt(data.length + 5, os);
		EPKCompiler.writeInt((int) crc32.getValue(), os);
		os.write(data);
		os.write(':');
		os.write('>');
	}
}
