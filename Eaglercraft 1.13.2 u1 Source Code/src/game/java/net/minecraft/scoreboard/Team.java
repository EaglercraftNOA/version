package net.minecraft.scoreboard;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Team {
   public boolean isSameTeam(@Nullable Team other) {
      if (other == null) {
         return false;
      } else {
         return this == other;
      }
   }

   public abstract String getName();

   public abstract ITextComponent format(ITextComponent p_200540_1_);

   @OnlyIn(Dist.CLIENT)
   public abstract boolean getSeeFriendlyInvisiblesEnabled();

   public abstract boolean getAllowFriendlyFire();

   @OnlyIn(Dist.CLIENT)
   public abstract Team.EnumVisible getNameTagVisibility();

   public abstract TextFormatting getColor();

   public abstract Collection<String> getMembershipCollection();

   public abstract Team.EnumVisible getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map<String, Team.CollisionRule> nameMap = Arrays.stream(values()).collect(Collectors.toMap((p_199871_0_) -> {
         return p_199871_0_.name;
      }, (p_199870_0_) -> {
         return p_199870_0_;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static Team.CollisionRule getByName(String nameIn) {
         return nameMap.get(nameIn);
      }

      private CollisionRule(String nameIn, int idIn) {
         this.name = nameIn;
         this.id = idIn;
      }

      public ITextComponent getDisplayName() {
         return new TextComponentTranslation("team.collision." + this.name);
      }
   }

   public static enum EnumVisible {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map<String, Team.EnumVisible> nameMap = Arrays.stream(values()).collect(Collectors.toMap((p_199873_0_) -> {
         return p_199873_0_.internalName;
      }, (p_199872_0_) -> {
         return p_199872_0_;
      }));
      public final String internalName;
      public final int id;

      @Nullable
      public static Team.EnumVisible getByName(String nameIn) {
         return nameMap.get(nameIn);
      }

      private EnumVisible(String nameIn, int idIn) {
         this.internalName = nameIn;
         this.id = idIn;
      }

      public ITextComponent getDisplayName() {
         return new TextComponentTranslation("team.visibility." + this.internalName);
      }
   }
}
