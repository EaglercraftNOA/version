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

package net.lax1dude.eaglercraft.v1_8.sp.server;

import net.minecraft.util.math.ChunkPos;

public class EaglerChunkLoader {

	private static final String HEX = "0123456789ABCDEF";

	public static String getChunkPath(int x, int z) {
		int unsignedX = x + 1900000;
		int unsignedZ = z + 1900000;

		char[] path = new char[12];
		for(int i = 5; i >= 0; --i) {
			path[i] = HEX.charAt((unsignedX >>> (i << 2)) & 0xF);
			path[i + 6] = HEX.charAt((unsignedZ >>> (i << 2)) & 0xF);
		}

		return new String(path);
	}

	public static ChunkPos getChunkCoords(String filename) {
		String strX = filename.substring(0, 6);
		String strZ = filename.substring(6);
		int retX = 0;
		int retZ = 0;
		for(int i = 0; i < 6; ++i) {
			retX |= HEX.indexOf(strX.charAt(i)) << (i << 2);
			retZ |= HEX.indexOf(strZ.charAt(i)) << (i << 2);
		}
		return new ChunkPos(retX - 1900000, retZ - 1900000);
	}

}
