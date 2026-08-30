package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.WorldServer;

public class EntitySelector {
   private final int limit;
   private final boolean includeNonPlayers;
   private final boolean currentWorldOnly;
   private final Predicate<Entity> filter;
   private final MinMaxBounds.FloatBound distance;
   private final Function<Vec3d, Vec3d> positionGetter;
   @Nullable
   private final AxisAlignedBB aabb;
   private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
   private final boolean self;
   @Nullable
   private final String username;
   @Nullable
   private final UUID uuid;
   private final Class<? extends Entity> type;
   private final boolean checkPermission;

   public EntitySelector(int limitIn, boolean includeNonPlayersIn, boolean currentWorldOnlyIn, Predicate<Entity> filterIn, MinMaxBounds.FloatBound distanceIn, Function<Vec3d, Vec3d> positionGetterIn, @Nullable AxisAlignedBB aabbIn, BiConsumer<Vec3d, List<? extends Entity>> sorterIn, boolean selfIn, @Nullable String usernameIn, @Nullable UUID uuidIn, Class<? extends Entity> typeIn, boolean checkPermissionIn) {
      this.limit = limitIn;
      this.includeNonPlayers = includeNonPlayersIn;
      this.currentWorldOnly = currentWorldOnlyIn;
      this.filter = filterIn;
      this.distance = distanceIn;
      this.positionGetter = positionGetterIn;
      this.aabb = aabbIn;
      this.sorter = sorterIn;
      this.self = selfIn;
      this.username = usernameIn;
      this.uuid = uuidIn;
      this.type = typeIn;
      this.checkPermission = checkPermissionIn;
   }

   public int getLimit() {
      return this.limit;
   }

   public boolean includesEntities() {
      return this.includeNonPlayers;
   }

   public boolean isSelfSelector() {
      return this.self;
   }

   public boolean isWorldLimited() {
      return this.currentWorldOnly;
   }

   private void checkPermission(CommandSource source) throws CommandSyntaxException {
      if (this.checkPermission && !source.hasPermissionLevel(2)) {
         throw EntityArgument.SELECTOR_NOT_ALLOWED.create();
      }
   }

   public Entity selectOne(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      List<? extends Entity> list = this.select(source);
      if (list.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else if (list.size() > 1) {
         throw EntityArgument.TOO_MANY_ENTITIES.create();
      } else {
         return list.get(0);
      }
   }

   public List<? extends Entity> select(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      if (!this.includeNonPlayers) {
         return this.selectPlayers(source);
      } else if (this.username != null) {
         EntityPlayerMP entityplayermp = source.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List<? extends Entity>)(entityplayermp == null ? Collections.emptyList() : Lists.newArrayList(entityplayermp));
      } else if (this.uuid != null) {
         for(WorldServer worldserver1 : source.getServer().getWorlds()) {
            Entity entity = worldserver1.getEntityFromUuid(this.uuid);
            if (entity != null) {
               return Lists.newArrayList(entity);
            }
         }

         return Collections.emptyList();
      } else {
         Vec3d vec3d = this.positionGetter.apply(source.getPos());
         Predicate<Entity> predicate = this.updateFilter(vec3d);
         if (this.self) {
            return (List<? extends Entity>)(source.getEntity() != null && predicate.test(source.getEntity()) ? Lists.newArrayList(source.getEntity()) : Collections.emptyList());
         } else {
            List<Entity> list = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.getEntities(list, source.getWorld(), vec3d, predicate);
            } else {
               for(WorldServer worldserver : source.getServer().getWorlds()) {
                  this.getEntities(list, worldserver, vec3d, predicate);
               }
            }

            return this.sortAndLimit(vec3d, list);
         }
      }
   }

   private void getEntities(List<Entity> result, WorldServer worldIn, Vec3d pos, Predicate<Entity> predicate) {
      if (this.aabb != null) {
         result.addAll(worldIn.getEntitiesWithinAABB(this.type, this.aabb.offset(pos), predicate::test));
      } else {
         result.addAll(worldIn.getEntities(this.type, predicate::test));
      }

   }

   public EntityPlayerMP selectOnePlayer(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      List<EntityPlayerMP> list = this.selectPlayers(source);
      if (list.size() != 1) {
         throw EntityArgument.PLAYER_NOT_FOUND.create();
      } else {
         return list.get(0);
      }
   }

   public List<EntityPlayerMP> selectPlayers(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      if (this.username != null) {
         EntityPlayerMP entityplayermp2 = source.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List<EntityPlayerMP>)(entityplayermp2 == null ? Collections.emptyList() : Lists.newArrayList(entityplayermp2));
      } else if (this.uuid != null) {
         EntityPlayerMP entityplayermp1 = source.getServer().getPlayerList().getPlayerByUUID(this.uuid);
         return (List<EntityPlayerMP>)(entityplayermp1 == null ? Collections.emptyList() : Lists.newArrayList(entityplayermp1));
      } else {
         Vec3d vec3d = this.positionGetter.apply(source.getPos());
         Predicate<Entity> predicate = this.updateFilter(vec3d);
         if (this.self) {
            if (source.getEntity() instanceof EntityPlayerMP) {
               EntityPlayerMP entityplayermp3 = (EntityPlayerMP)source.getEntity();
               if (predicate.test(entityplayermp3)) {
                  return Lists.newArrayList(entityplayermp3);
               }
            }

            return Collections.emptyList();
         } else {
            List<EntityPlayerMP> list;
            if (this.isWorldLimited()) {
               list = source.getWorld().getPlayers(EntityPlayerMP.class, predicate::test);
            } else {
               list = Lists.newArrayList();

               for(EntityPlayerMP entityplayermp : source.getServer().getPlayerList().getPlayers()) {
                  if (predicate.test(entityplayermp)) {
                     list.add(entityplayermp);
                  }
               }
            }

            return this.sortAndLimit(vec3d, list);
         }
      }
   }

   private Predicate<Entity> updateFilter(Vec3d pos) {
      Predicate<Entity> predicate = this.filter;
      if (this.aabb != null) {
         AxisAlignedBB axisalignedbb = this.aabb.offset(pos);
         predicate = predicate.and((p_197344_1_) -> {
            return axisalignedbb.intersects(p_197344_1_.getBoundingBox());
         });
      }

      if (!this.distance.isUnbounded()) {
         predicate = predicate.and((p_211376_2_) -> {
            return this.distance.testSquared(p_211376_2_.getDistanceSq(pos));
         });
      }

      return predicate;
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3d pos, List<T> entities) {
      if (entities.size() > 1) {
         this.sorter.accept(pos, entities);
      }

      return entities.subList(0, Math.min(this.limit, entities.size()));
   }

   public static ITextComponent joinNames(List<? extends Entity> entities) {
      return TextComponentUtils.makeList(entities, Entity::getDisplayName);
   }
}
