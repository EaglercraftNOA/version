/*
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.sp.server;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class EaglerSaveHandler extends SaveHandler {

	public EaglerSaveHandler(VFile2 savesDirectory, String directoryName) {
		super(savesDirectory, directoryName);
	}

	public IChunkLoader getChunkLoader(WorldProvider p_75763_1_) {
		// Use EaglerChunkLoader for all worlds
		net.lax1dude.eaglercraft.internal.vfs2.VFile2 worldDirectory = this.getWorldDirectory();
		
		if (p_75763_1_ instanceof net.minecraft.world.WorldProviderHell) {
			return new EaglerChunkLoader(
				new net.lax1dude.eaglercraft.internal.vfs2.VFile2(worldDirectory, "DIM-1"));
		} else if (p_75763_1_ instanceof net.minecraft.world.WorldProviderEnd) {
			return new EaglerChunkLoader(
				new net.lax1dude.eaglercraft.internal.vfs2.VFile2(worldDirectory, "DIM1"));
		} else {
			return new EaglerChunkLoader(worldDirectory);
		}
	}
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
		System.err.println("[EaglerSaveHandler] saveWorldInfoWithPlayer called for: " + worldInformation.getWorldName());
		System.err.println("  worldDirectory: " + this.getWorldDirectory().getPath());
		worldInformation.setSaveVersion(19133);
		try {
			super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
			System.err.println("  level.dat saved successfully!");
		} catch (Exception e) {
			System.err.println("  ERROR saving level.dat:");
			e.printStackTrace();
		}
	}
	
	/**
	 * Called to flush all changes to disk, waiting for them to complete.
	 */
	public void flush() {
		System.err.println("[EaglerSaveHandler] flush() called");
		// Region files now use RandomAccessMemoryFile which auto-flushes
		// No need for manual cache clearing
	}
}