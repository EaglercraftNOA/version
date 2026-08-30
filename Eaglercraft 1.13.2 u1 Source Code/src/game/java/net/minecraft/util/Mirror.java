package net.minecraft.util;

public enum Mirror {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   public int mirrorRotation(int rotationIn, int rotationCount) {
      int i = rotationCount / 2;
      int j = rotationIn > i ? rotationIn - rotationCount : rotationIn;
      switch(this) {
      case FRONT_BACK:
         return (rotationCount - j) % rotationCount;
      case LEFT_RIGHT:
         return (i - j + rotationCount) % rotationCount;
      default:
         return rotationIn;
      }
   }

   public Rotation toRotation(EnumFacing facing) {
      EnumFacing.Axis enumfacing$axis = facing.getAxis();
      return (this != LEFT_RIGHT || enumfacing$axis != EnumFacing.Axis.Z) && (this != FRONT_BACK || enumfacing$axis != EnumFacing.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public EnumFacing mirror(EnumFacing facing) {
      if (this == FRONT_BACK && facing.getAxis() == EnumFacing.Axis.X) {
         return facing.getOpposite();
      } else {
         return this == LEFT_RIGHT && facing.getAxis() == EnumFacing.Axis.Z ? facing.getOpposite() : facing;
      }
   }
}
