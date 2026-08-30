package net.lax1dude.eaglercraft.internal;

import net.minecraft.src.EnumOS1;

/**
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public enum EnumPlatformOS {
	WINDOWS("Windows", EnumOS1.windows), MACOS("MacOS", EnumOS1.macos), LINUX("Linux", EnumOS1.linux),
	CHROMEBOOK_LINUX("ChromeOS", EnumOS1.linux), OTHER("Unknown", EnumOS1.unknown);

	private final String name;
	private final EnumOS1 minecraftEnum;

	private EnumPlatformOS(String name, EnumOS1 minecraftEnum) {
		this.name = name;
		this.minecraftEnum = minecraftEnum;
	}

	public String getName() {
		return name;
	}

	public EnumOS1 getMinecraftEnum() {
		return minecraftEnum;
	}

	public String toString() {
		return name;
	}

	public static EnumPlatformOS getFromJVM(String osNameProperty) {
		osNameProperty = osNameProperty.toLowerCase();
		if (osNameProperty.contains("chrome")) {
			return CHROMEBOOK_LINUX;
		} else if (osNameProperty.contains("linux")) {
			return LINUX;
		} else if (osNameProperty.contains("windows") || osNameProperty.contains("win32")) {
			return WINDOWS;
		} else if (osNameProperty.contains("macos") || osNameProperty.contains("osx")) {
			return MACOS;
		} else {
			return OTHER;
		}
	}

	public static EnumPlatformOS getFromUA(String ua) {
		ua = " " + ua.toLowerCase();
		if (ua.contains(" cros")) {
			return CHROMEBOOK_LINUX;
		} else if (ua.contains(" linux")) {
			return LINUX;
		} else if (ua.contains(" windows") || ua.contains(" win32") || ua.contains(" win64")) {
			return WINDOWS;
		} else if (ua.contains(" macos") || ua.contains(" osx")) {
			return MACOS;
		} else {
			return OTHER;
		}
	}

}
