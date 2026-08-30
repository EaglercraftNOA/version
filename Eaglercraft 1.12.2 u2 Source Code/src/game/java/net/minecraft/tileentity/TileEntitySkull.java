package net.minecraft.tileentity;

import com.mojang.authlib.GameProfile;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockSkull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;

public class TileEntitySkull extends TileEntity implements ITickable {
	private int skullType;
	private int skullRotation;
	private GameProfile playerProfile;
	private int dragonAnimatedTicks;
	private boolean dragonAnimated;

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setByte("SkullType", (byte) (this.skullType & 255));
		compound.setByte("Rot", (byte) (this.skullRotation & 255));

		if (this.playerProfile != null) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTUtil.writeGameProfile(nbttagcompound, this.playerProfile);
			compound.setTag("Owner", nbttagcompound);
		}

		return compound;
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.skullType = compound.getByte("SkullType");
		this.skullRotation = compound.getByte("Rot");

		if (this.skullType == 3) {
			if (compound.hasKey("Owner", 10)) {
				this.playerProfile = NBTUtil.readGameProfileFromNBT(compound.getCompoundTag("Owner"));
			} else if (compound.hasKey("ExtraType", 8)) {
				String s = compound.getString("ExtraType");

				if (!StringUtils.isNullOrEmpty(s)) {
					this.playerProfile = new GameProfile((EaglercraftUUID) null, s);
					this.updatePlayerProfile();
				}
			}
		}
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.skullType == 5) {
			if (this.world.isBlockPowered(this.pos)) {
				this.dragonAnimated = true;
				++this.dragonAnimatedTicks;
			} else {
				this.dragonAnimated = false;
			}
		}
	}

	public float getAnimationProgress(float p_184295_1_) {
		return this.dragonAnimated ? (float) this.dragonAnimatedTicks + p_184295_1_ : (float) this.dragonAnimatedTicks;
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return this.playerProfile;
	}

	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 4, this.getUpdateTag());
	}

	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	public void setType(int type) {
		this.skullType = type;
		this.playerProfile = null;
	}

	public void setPlayerProfile(@Nullable GameProfile playerProfile) {
		this.skullType = 3;
		this.playerProfile = playerProfile;
		this.updatePlayerProfile();
	}

	private void updatePlayerProfile() {
		this.playerProfile = updateGameprofile(this.playerProfile);
		this.markDirty();
	}

	public static GameProfile updateGameprofile(GameProfile input) {
		return input;
	}

	public int getSkullType() {
		return this.skullType;
	}

	public int getSkullRotation() {
		return this.skullRotation;
	}

	public void setSkullRotation(int rotation) {
		this.skullRotation = rotation;
	}

	public void mirror(Mirror p_189668_1_) {
		if (this.world != null
				&& this.world.getBlockState(this.getPos()).getValue(BlockSkull.FACING) == EnumFacing.UP) {
			this.skullRotation = p_189668_1_.mirrorRotation(this.skullRotation, 16);
		}
	}

	public void rotate(Rotation p_189667_1_) {
		if (this.world != null
				&& this.world.getBlockState(this.getPos()).getValue(BlockSkull.FACING) == EnumFacing.UP) {
			this.skullRotation = p_189667_1_.rotate(this.skullRotation, 16);
		}
	}
}
