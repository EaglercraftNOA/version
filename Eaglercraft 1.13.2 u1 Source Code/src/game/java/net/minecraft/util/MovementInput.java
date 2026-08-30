package net.minecraft.util;

import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovementInput {
   public float moveStrafe;
   public float moveForward;
   public boolean forwardKeyDown;
   public boolean backKeyDown;
   public boolean leftKeyDown;
   public boolean rightKeyDown;
   public boolean jump;
   public boolean sneak;

   public void updatePlayerMoveState() {
   }

   public Vec2f getMoveVector() {
      return new Vec2f(this.moveStrafe, this.moveForward);
   }
}
