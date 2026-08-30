package net.lax1dude.eaglercraft.v1_8.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

public class TextureClock extends EaglerTextureAtlasSprite {

	private double smoothAngle;
	private double smoothDelta;

	public TextureClock(String iconName) {
		super(iconName);
	}

	@Override
	public void updateAnimation() {
		if (!this.framesTextureData.isEmpty()) {
			Minecraft minecraft = Minecraft.getInstance();
			double angle = 0.0D;
			if (minecraft.world != null && minecraft.player != null) {
				angle = (double) minecraft.world.getCelestialAngle(1.0F);
				if (!minecraft.world.getDimension().isSurfaceWorld()) {
					angle = Math.random();
				}
			}

			double delta;
			for (delta = angle - this.smoothAngle; delta < -0.5D; ++delta) {
			}

			while (delta >= 0.5D) {
				--delta;
			}

			delta = MathHelper.clamp(delta, -1.0D, 1.0D);
			this.smoothDelta += delta * 0.1D;
			this.smoothDelta *= 0.8D;
			this.smoothAngle += this.smoothDelta;

			int frameCount = this.framesTextureData.size();
			int frame;
			for (frame = (int) ((this.smoothAngle + 1.0D) * (double) frameCount) % frameCount; frame < 0; frame = (frame
					+ frameCount) % frameCount) {
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
