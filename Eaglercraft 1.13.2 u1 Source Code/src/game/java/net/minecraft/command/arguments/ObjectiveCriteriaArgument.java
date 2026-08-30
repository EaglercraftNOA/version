package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import com.arlenh7.eaglercraft.v1_17.java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ObjectiveCriteriaArgument implements ArgumentType<ScoreCriteria> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo.bar.baz", "minecraft:foo");
   public static final DynamicCommandExceptionType OBJECTIVE_INVALID_CRITERIA = new DynamicCommandExceptionType((p_208672_0_) -> {
      return new TextComponentTranslation("argument.criteria.invalid", p_208672_0_);
   });

   public static ObjectiveCriteriaArgument objectiveCriteria() {
      return new ObjectiveCriteriaArgument();
   }

   public static ScoreCriteria getObjectiveCriteria(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, ScoreCriteria.class);
   }

   public ScoreCriteria parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();

      while(p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
         p_parse_1_.skip();
      }

      String s = p_parse_1_.getString().substring(i, p_parse_1_.getCursor());
      ScoreCriteria scorecriteria = ScoreCriteria.byName(s);
      if (scorecriteria == null) {
         p_parse_1_.setCursor(i);
         throw OBJECTIVE_INVALID_CRITERIA.create(s);
      } else {
         return scorecriteria;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      List<String> list = Lists.newArrayList(ScoreCriteria.INSTANCES.keySet());

      for(StatType<?> stattype : IRegistry.STATS) {
         for(Object object : stattype.getRegistry()) {
            String s = this.makeStatName(stattype, object);
            list.add(s);
         }
      }

      return ISuggestionProvider.suggest(list, p_listSuggestions_2_);
   }

   public <T> String makeStatName(StatType<T> type, Object value) {
      return Stat.buildName(type, (T)value);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
