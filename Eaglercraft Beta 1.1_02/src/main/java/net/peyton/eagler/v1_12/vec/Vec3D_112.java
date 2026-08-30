package net.peyton.eagler.v1_12.vec;

import net.minecraft.src.MathHelper;

public class Vec3D_112 {
	public static final Vec3D_112 ZERO = new Vec3D_112(0.0D, 0.0D, 0.0D);

	/** X coordinate of Vec3D */
	public final double xCoord;

	/** Y coordinate of Vec3D */
	public final double yCoord;

	/** Z coordinate of Vec3D */
	public final double zCoord;

	public Vec3D_112(double x, double y, double z) {
		if (x == -0.0D) {
			x = 0.0D;
		}

		if (y == -0.0D) {
			y = 0.0D;
		}

		if (z == -0.0D) {
			z = 0.0D;
		}

		this.xCoord = x;
		this.yCoord = y;
		this.zCoord = z;
	}

	/**
	 * Returns a new vector with the result of the specified vector minus this.
	 */
	public Vec3D_112 subtractReverse(Vec3D_112 vec) {
		return new Vec3D_112(vec.xCoord - this.xCoord, vec.yCoord - this.yCoord, vec.zCoord - this.zCoord);
	}

	/**
	 * Normalizes the vector to a length of 1 (except if it is the zero vector)
	 */
	public Vec3D_112 normalize() {
		double d0 = (double) MathHelper
				.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
		return d0 < 1.0E-4D ? ZERO : new Vec3D_112(this.xCoord / d0, this.yCoord / d0, this.zCoord / d0);
	}

	public double dotProduct(Vec3D_112 vec) {
		return this.xCoord * vec.xCoord + this.yCoord * vec.yCoord + this.zCoord * vec.zCoord;
	}

	/**
	 * Returns a new vector with the result of this vector x the specified vector.
	 */
	public Vec3D_112 crossProduct(Vec3D_112 vec) {
		return new Vec3D_112(this.yCoord * vec.zCoord - this.zCoord * vec.yCoord,
				this.zCoord * vec.xCoord - this.xCoord * vec.zCoord,
				this.xCoord * vec.yCoord - this.yCoord * vec.xCoord);
	}

	public Vec3D_112 subtract(Vec3D_112 vec) {
		return this.subtract(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	public Vec3D_112 subtract(double x, double y, double z) {
		return this.addVector(-x, -y, -z);
	}

	public Vec3D_112 add(Vec3D_112 vec) {
		return this.addVector(vec.xCoord, vec.yCoord, vec.zCoord);
	}

	/**
	 * Adds the specified x,y,z vector components to this vector and returns the
	 * resulting vector. Does not change this vector.
	 */
	public Vec3D_112 addVector(double x, double y, double z) {
		return new Vec3D_112(this.xCoord + x, this.yCoord + y, this.zCoord + z);
	}

	/**
	 * Euclidean distance between this and the specified vector, returned as double.
	 */
	public double distanceTo(Vec3D_112 vec) {
		double d0 = vec.xCoord - this.xCoord;
		double d1 = vec.yCoord - this.yCoord;
		double d2 = vec.zCoord - this.zCoord;
		return (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
	}

	/**
	 * The square of the Euclidean distance between this and the specified vector.
	 */
	public double squareDistanceTo(Vec3D_112 vec) {
		double d0 = vec.xCoord - this.xCoord;
		double d1 = vec.yCoord - this.yCoord;
		double d2 = vec.zCoord - this.zCoord;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public double squareDistanceTo(double xIn, double yIn, double zIn) {
		double d0 = xIn - this.xCoord;
		double d1 = yIn - this.yCoord;
		double d2 = zIn - this.zCoord;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public Vec3D_112 scale(double p_186678_1_) {
		return new Vec3D_112(this.xCoord * p_186678_1_, this.yCoord * p_186678_1_, this.zCoord * p_186678_1_);
	}

	/**
	 * Returns the length of the vector.
	 */
	public double lengthVector() {
		return (double) MathHelper
				.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
	}

	public double lengthSquared() {
		return this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord;
	}

	/**
	 * Returns a new vector with x value equal to the second parameter, along the
	 * line between this vector and the passed in vector, or null if not possible.
	 */
	public Vec3D_112 getIntermediateWithXValue(Vec3D_112 vec, double x) {
		double d0 = vec.xCoord - this.xCoord;
		double d1 = vec.yCoord - this.yCoord;
		double d2 = vec.zCoord - this.zCoord;

		if (d0 * d0 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d3 = (x - this.xCoord) / d0;
			return d3 >= 0.0D && d3 <= 1.0D
					? new Vec3D_112(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3)
					: null;
		}
	}

	/**
	 * Returns a new vector with y value equal to the second parameter, along the
	 * line between this vector and the passed in vector, or null if not possible.
	 */
	public Vec3D_112 getIntermediateWithYValue(Vec3D_112 vec, double y) {
		double d0 = vec.xCoord - this.xCoord;
		double d1 = vec.yCoord - this.yCoord;
		double d2 = vec.zCoord - this.zCoord;

		if (d1 * d1 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d3 = (y - this.yCoord) / d1;
			return d3 >= 0.0D && d3 <= 1.0D
					? new Vec3D_112(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3)
					: null;
		}
	}

	/**
	 * Returns a new vector with z value equal to the second parameter, along the
	 * line between this vector and the passed in vector, or null if not possible.
	 */
	public Vec3D_112 getIntermediateWithZValue(Vec3D_112 vec, double z) {
		double d0 = vec.xCoord - this.xCoord;
		double d1 = vec.yCoord - this.yCoord;
		double d2 = vec.zCoord - this.zCoord;

		if (d2 * d2 < 1.0000000116860974E-7D) {
			return null;
		} else {
			double d3 = (z - this.zCoord) / d2;
			return d3 >= 0.0D && d3 <= 1.0D
					? new Vec3D_112(this.xCoord + d0 * d3, this.yCoord + d1 * d3, this.zCoord + d2 * d3)
					: null;
		}
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof Vec3D_112)) {
			return false;
		} else {
			Vec3D_112 vec3d = (Vec3D_112) p_equals_1_;

			if (Double.compare(vec3d.xCoord, this.xCoord) != 0) {
				return false;
			} else if (Double.compare(vec3d.yCoord, this.yCoord) != 0) {
				return false;
			} else {
				return Double.compare(vec3d.zCoord, this.zCoord) == 0;
			}
		}
	}

	public int hashCode() {
		long j = Double.doubleToLongBits(this.xCoord);
		int i = (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.yCoord);
		i = 31 * i + (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.zCoord);
		i = 31 * i + (int) (j ^ j >>> 32);
		return i;
	}

	public String toString() {
		return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
	}

	public Vec3D_112 rotatePitch(float pitch) {
		float f = MathHelper.cos(pitch);
		float f1 = MathHelper.sin(pitch);
		double d0 = this.xCoord;
		double d1 = this.yCoord * (double) f + this.zCoord * (double) f1;
		double d2 = this.zCoord * (double) f - this.yCoord * (double) f1;
		return new Vec3D_112(d0, d1, d2);
	}

	public Vec3D_112 rotateYaw(float yaw) {
		float f = MathHelper.cos(yaw);
		float f1 = MathHelper.sin(yaw);
		double d0 = this.xCoord * (double) f + this.zCoord * (double) f1;
		double d1 = this.yCoord;
		double d2 = this.zCoord * (double) f - this.xCoord * (double) f1;
		return new Vec3D_112(d0, d1, d2);
	}

	/**
	 * returns a Vec3d from given pitch and yaw degrees
	 */
	public static Vec3D_112 fromPitchYaw(float p_189986_0_, float p_189986_1_) {
		float f = MathHelper.cos(-p_189986_1_ * 0.017453292F - (float) Math.PI);
		float f1 = MathHelper.sin(-p_189986_1_ * 0.017453292F - (float) Math.PI);
		float f2 = -MathHelper.cos(-p_189986_0_ * 0.017453292F);
		float f3 = MathHelper.sin(-p_189986_0_ * 0.017453292F);
		return new Vec3D_112((double) (f1 * f2), (double) f3, (double) (f * f2));
	}
}