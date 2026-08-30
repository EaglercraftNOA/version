package net.lax1dude.eaglercraft.v1_8.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TextureCompass extends EaglerTextureAtlasSprite {

	public double currentAngle;
	public double angleDelta;
	public static String field_176608_l;

	public TextureCompass(String iconName) {
		super(iconName);
		field_176608_l = iconName;
	}

	@Override
	public void updateAnimation() {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.world != null && minecraft.player != null) {
			this.updateCompass(minecraft.world, minecraft.player.posX, minecraft.player.posZ,
					(double) minecraft.player.rotationYaw, false, false);
		} else {
			this.updateCompass(null, 0.0D, 0.0D, 0.0D, true, false);
		}
	}

	public void updateCompass(World level, double x, double z, double yaw, boolean missingLevel, boolean instant) {
		if (!this.framesTextureData.isEmpty()) {
			double targetAngle = 0.0D;
			if (level != null && !missingLevel) {
				BlockPos spawn = level.getSpawnPoint();
				double dx = (double) spawn.getX() - x;
				double dz = (double) spawn.getZ() - z;
				yaw = yaw % 360.0D;
				targetAngle = -((yaw - 90.0D) * Math.PI / 180.0D - Math.atan2(dz, dx));
				if (!level.getDimension().isSurfaceWorld()) {
					targetAngle = Math.random() * Math.PI * 2.0D;
				}
			}

			if (instant) {
				this.currentAngle = targetAngle;
			} else {
				double delta;
				for (delta = targetAngle - this.currentAngle; delta < -Math.PI; delta += Math.PI * 2.0D) {
				}

				while (delta >= Math.PI) {
					delta -= Math.PI * 2.0D;
				}

				delta = MathHelper.clamp(delta, -1.0D, 1.0D);
				this.angleDelta += delta * 0.1D;
				this.angleDelta *= 0.8D;
				this.currentAngle += this.angleDelta;
			}

			int frameCount = this.framesTextureData.size();
			int frame;
			for (frame = (int) ((this.currentAngle / (Math.PI * 2.0D) + 1.0D) * (double) frameCount)
					% frameCount; frame < 0; frame = (frame + frameCount) % frameCount) {
			}

			if (frame != this.frameCounter) {
				this.frameCounter = frame;
				currentAnimUpdater = (mapWidth, mapHeight, mapLevel) -> animationCache.copyFrameToTex2D(this.frameCounter,
						mapLevel, this.originX >> mapLevel, this.originY >> mapLevel, this.width >> mapLevel,
						this.height >> mapLevel, mapWidth, mapHeight);
			} else {
				currentAnimUpdater = null;
			}
		} else {
			currentAnimUpdater = null;
		}
	}
}
