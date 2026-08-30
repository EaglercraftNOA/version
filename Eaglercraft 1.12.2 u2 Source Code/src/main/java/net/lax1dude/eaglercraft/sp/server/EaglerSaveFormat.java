package net.lax1dude.eaglercraft.sp.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.peyton.eagler.fs.FileUtils;

public class EaglerSaveFormat extends SaveFormatOld {

	public EaglerSaveFormat(VFile2 savesDirectoryIn, DataFixer dataFixerIn) {
		super(savesDirectoryIn, dataFixerIn);
	}

	@Override
	public String getName() {
		return "eagler";
	}

	@Override
	public ISaveHandler getSaveLoader(String s, boolean flag) {
		return new EaglerSaveHandler(this.savesDirectory, s, flag, this.dataFixer);
	}

	protected int getSaveVersion() {
		return 19133; // why notch?
	}

	@Override
	public List<WorldSummary> getSaveList() throws AnvilConverterException {
		return FileUtils.getSaveList(this.savesDirectory, this);
	}

}
