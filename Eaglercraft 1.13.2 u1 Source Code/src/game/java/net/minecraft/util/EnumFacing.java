package net.minecraft.util;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final EnumFacing.Axis axis;
   private final EnumFacing.AxisDirection axisDirection;
   private final Vec3i directionVec;
   public static final EnumFacing[] _VALUES = values();
   private static final Map<String, EnumFacing> NAME_LOOKUP = Arrays.stream(_VALUES).collect(Collectors.toMap(EnumFacing::getName2, (p_199787_0_) -> {
      return p_199787_0_;
   }));
   private static final EnumFacing[] BY_INDEX = Arrays.stream(_VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
      return p_199790_0_.index;
   })).toArray((p_199788_0_) -> {
      return new EnumFacing[p_199788_0_];
   });
   private static final EnumFacing[] BY_HORIZONTAL_INDEX = Arrays.stream(_VALUES).filter((p_199786_0_) -> {
      return p_199786_0_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_199789_0_) -> {
      return p_199789_0_.horizontalIndex;
   })).toArray((p_199791_0_) -> {
      return new EnumFacing[p_199791_0_];
   });

   private EnumFacing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, EnumFacing.AxisDirection axisDirectionIn, EnumFacing.Axis axisIn, Vec3i directionVecIn) {
      this.index = indexIn;
      this.horizontalIndex = horizontalIndexIn;
      this.opposite = oppositeIn;
      this.name = nameIn;
      this.axis = axisIn;
      this.axisDirection = axisDirectionIn;
      this.directionVec = directionVecIn;
   }

   public static EnumFacing[] getFacingDirections(Entity entityIn) {
      float f = entityIn.getPitch(1.0F) * ((float)Math.PI / 180F);
      float f1 = -entityIn.getYaw(1.0F) * ((float)Math.PI / 180F);
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      EnumFacing enumfacing = flag ? EAST : WEST;
      EnumFacing enumfacing1 = flag1 ? UP : DOWN;
      EnumFacing enumfacing2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return compose(enumfacing1, enumfacing, enumfacing2);
         } else {
            return f10 > f7 ? compose(enumfacing, enumfacing2, enumfacing1) : compose(enumfacing, enumfacing1, enumfacing2);
         }
      } else if (f7 > f10) {
         return compose(enumfacing1, enumfacing2, enumfacing);
      } else {
         return f9 > f7 ? compose(enumfacing2, enumfacing, enumfacing1) : compose(enumfacing2, enumfacing1, enumfacing);
      }
   }

   private static EnumFacing[] compose(EnumFacing first, EnumFacing second, EnumFacing third) {
      return new EnumFacing[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public EnumFacing getOpposite() {
      return byIndex(this.opposite);
   }

   @OnlyIn(Dist.CLIENT)
   public EnumFacing rotateAround(EnumFacing.Axis axis) {
      switch(axis) {
      case X:
         if (this != WEST && this != EAST) {
            return this.rotateX();
         }

         return this;
      case Y:
         if (this != UP && this != DOWN) {
            return this.rotateY();
         }

         return this;
      case Z:
         if (this != NORTH && this != SOUTH) {
            return this.rotateZ();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   public EnumFacing rotateY() {
      switch(this) {
      case NORTH:
         return EAST;
      case EAST:
         return SOUTH;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   @OnlyIn(Dist.CLIENT)
   private EnumFacing rotateX() {
      switch(this) {
      case NORTH:
         return DOWN;
      case EAST:
      case WEST:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
         return UP;
      case UP:
         return NORTH;
      case DOWN:
         return SOUTH;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private EnumFacing rotateZ() {
      switch(this) {
      case EAST:
         return DOWN;
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         return UP;
      case UP:
         return EAST;
      case DOWN:
         return WEST;
      }
   }

   public EnumFacing rotateYCCW() {
      switch(this) {
      case NORTH:
         return WEST;
      case EAST:
         return NORTH;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getXOffset() {
      return this.axis == EnumFacing.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   public int getYOffset() {
      return this.axis == EnumFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   public int getZOffset() {
      return this.axis == EnumFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   public String getName2() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public static EnumFacing byName(@Nullable String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
   }

   public static EnumFacing byIndex(int index) {
      return BY_INDEX[MathHelper.abs(index % BY_INDEX.length)];
   }

   public static EnumFacing byHorizontalIndex(int horizontalIndexIn) {
      return BY_HORIZONTAL_INDEX[MathHelper.abs(horizontalIndexIn % BY_HORIZONTAL_INDEX.length)];
   }

   public static EnumFacing fromAngle(double angle) {
      return byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
   }

   public static EnumFacing getFacingFromAxisDirection(EnumFacing.Axis axisIn, EnumFacing.AxisDirection axisDirectionIn) {
      switch(axisIn) {
      case X:
         return axisDirectionIn == EnumFacing.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return axisDirectionIn == EnumFacing.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return axisDirectionIn == EnumFacing.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float getHorizontalAngle() {
      return (float)((this.horizontalIndex & 3) * 90);
   }

   public static EnumFacing random(Random rand) {
      return _VALUES[rand.nextInt(_VALUES.length)];
   }

   public static EnumFacing getFacingFromVector(double x, double y, double z) {
      return getFacingFromVector((float)x, (float)y, (float)z);
   }

   public static EnumFacing getFacingFromVector(float x, float y, float z) {
      EnumFacing enumfacing = NORTH;
      float f = Float.MIN_VALUE;

      for(EnumFacing enumfacing1 : _VALUES) {
         float f1 = x * (float)enumfacing1.directionVec.getX() + y * (float)enumfacing1.directionVec.getY() + z * (float)enumfacing1.directionVec.getZ();
         if (f1 > f) {
            f = f1;
            enumfacing = enumfacing1;
         }
      }

      return enumfacing;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public static EnumFacing getFacingFromAxis(EnumFacing.AxisDirection axisDirectionIn, EnumFacing.Axis axisIn) {
      for(EnumFacing enumfacing : _VALUES) {
         if (enumfacing.getAxisDirection() == axisDirectionIn && enumfacing.getAxis() == axisIn) {
            return enumfacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   public static enum Axis implements Predicate<EnumFacing>, IStringSerializable {
      X("x") {
         public int getCoordinate(int x, int y, int z) {
            return x;
         }

         public double getCoordinate(double x, double y, double z) {
            return x;
         }
      },
      Y("y") {
         public int getCoordinate(int x, int y, int z) {
            return y;
         }

         public double getCoordinate(double x, double y, double z) {
            return y;
         }
      },
      Z("z") {
         public int getCoordinate(int x, int y, int z) {
            return z;
         }

         public double getCoordinate(double x, double y, double z) {
            return z;
         }
      };

      private static final Map<String, EnumFacing.Axis> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(EnumFacing.Axis::getName2, (p_199785_0_) -> {
         return p_199785_0_;
      }));
      private final String name;

      private Axis(String nameIn) {
         this.name = nameIn;
      }

      @Nullable
      @OnlyIn(Dist.CLIENT)
      public static EnumFacing.Axis byName(String name) {
         return NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
      }

      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public boolean test(@Nullable EnumFacing p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      public EnumFacing.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return EnumFacing.Plane.HORIZONTAL;
         case Y:
            return EnumFacing.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getName() {
         return this.name;
      }

      public abstract int getCoordinate(int x, int y, int z);

      public abstract double getCoordinate(double x, double y, double z);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }
   }

   public static enum Plane implements Iterable<EnumFacing>, Predicate<EnumFacing> {
      HORIZONTAL(new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST}, new EnumFacing.Axis[]{EnumFacing.Axis.X, EnumFacing.Axis.Z}),
      VERTICAL(new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN}, new EnumFacing.Axis[]{EnumFacing.Axis.Y});

      private final EnumFacing[] facingValues;
      private final EnumFacing.Axis[] axisValues;

      private Plane(EnumFacing[] facingValuesIn, EnumFacing.Axis[] axisValuesIn) {
         this.facingValues = facingValuesIn;
         this.axisValues = axisValuesIn;
      }

      public EnumFacing random(Random rand) {
         return this.facingValues[rand.nextInt(this.facingValues.length)];
      }

      public boolean test(@Nullable EnumFacing p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<EnumFacing> iterator() {
         return Iterators.forArray(this.facingValues);
      }
   }
}
