package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TextComponentScore extends TextComponentBase {
   private final String name;
   @Nullable
   private final EntitySelector selector;
   private final String objective;
   private String value = "";

   public TextComponentScore(String nameIn, String objectiveIn) {
      this.name = nameIn;
      this.objective = objectiveIn;
      EntitySelector entityselector = null;

      try {
         EntitySelectorParser entityselectorparser = new EntitySelectorParser(new StringReader(nameIn));
         entityselector = entityselectorparser.parse();
      } catch (CommandSyntaxException var5) {
         ;
      }

      this.selector = entityselector;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public EntitySelector getSelector() {
      return this.selector;
   }

   public String getObjective() {
      return this.objective;
   }

   public void setValue(String valueIn) {
      this.value = valueIn;
   }

   public String getUnformattedComponentText() {
      return this.value;
   }

   public void resolve(CommandSource p_197665_1_) {
      MinecraftServer minecraftserver = p_197665_1_.getServer();
      if (minecraftserver != null && minecraftserver.isAnvilFileSet() && StringUtils.isNullOrEmpty(this.value)) {
         Scoreboard scoreboard = minecraftserver.getScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjective(this.objective);
         if (scoreboard.entityHasObjective(this.name, scoreobjective)) {
            Score score = scoreboard.getOrCreateScore(this.name, scoreobjective);
            this.setValue(String.format("%d", score.getScorePoints()));
         } else {
            this.value = "";
         }
      }

   }

   public TextComponentScore shallowCopy() {
      TextComponentScore textcomponentscore = new TextComponentScore(this.name, this.objective);
      textcomponentscore.setValue(this.value);
      return textcomponentscore;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof TextComponentScore)) {
         return false;
      } else {
         TextComponentScore textcomponentscore = (TextComponentScore)p_equals_1_;
         return this.name.equals(textcomponentscore.name) && this.objective.equals(textcomponentscore.objective) && super.equals(p_equals_1_);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }
}
