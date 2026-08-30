package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureIO {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.newHashMap();
   private static final Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.newHashMap();
   private static final Map<String, Class<? extends StructurePiece>> componentNameToClassMap = Maps.newHashMap();
   private static final Map<Class<? extends StructurePiece>, String> componentClassToNameMap = Maps.newHashMap();

   private static void registerStructure(Class<? extends StructureStart> startClass, String structureName) {
      startNameToClassMap.put(structureName, startClass);
      startClassToNameMap.put(startClass, structureName);
   }

   public static void registerStructureComponent(Class<? extends StructurePiece> componentClass, String componentName) {
      componentNameToClassMap.put(componentName, componentClass);
      componentClassToNameMap.put(componentClass, componentName);
   }

   public static String getStructureStartName(StructureStart start) {
      return startClassToNameMap.get(start.getClass());
   }

   public static String getStructureComponentName(StructurePiece component) {
      return componentClassToNameMap.get(component.getClass());
   }

   @Nullable
   private static StructureStart createStructureStart(Class<? extends StructureStart> startClass) {
      if (startClass == MineshaftStructure.Start.class) return new MineshaftStructure.Start();
      if (startClass == VillageStructure.Start.class) return new VillageStructure.Start();
      if (startClass == FortressStructure.Start.class) return new FortressStructure.Start();
      if (startClass == StrongholdStructure.Start.class) return new StrongholdStructure.Start();
      if (startClass == JunglePyramidStructure.Start.class) return new JunglePyramidStructure.Start();
      if (startClass == OceanRuinStructure.Start.class) return new OceanRuinStructure.Start();
      if (startClass == DesertPyramidStructure.Start.class) return new DesertPyramidStructure.Start();
      if (startClass == IglooStructure.Start.class) return new IglooStructure.Start();
      if (startClass == SwampHutStructure.Start.class) return new SwampHutStructure.Start();
      if (startClass == OceanMonumentStructure.Start.class) return new OceanMonumentStructure.Start();
      if (startClass == EndCityStructure.Start.class) return new EndCityStructure.Start();
      if (startClass == WoodlandMansionStructure.Start.class) return new WoodlandMansionStructure.Start();
      if (startClass == BuriedTreasureStructure.Start.class) return new BuriedTreasureStructure.Start();
      if (startClass == ShipwreckStructure.Start.class) return new ShipwreckStructure.Start();
      return null;
   }

   @Nullable
   private static StructurePiece createStructurePiece(Class<? extends StructurePiece> componentClass) {
      if (componentClass == BuriedTreasurePieces.Piece.class) return new BuriedTreasurePieces.Piece();
      if (componentClass == DesertPyramidPiece.class) return new DesertPyramidPiece();
      if (componentClass == EndCityPieces.CityTemplate.class) return new EndCityPieces.CityTemplate();
      if (componentClass == FortressPieces.Crossing3.class) return new FortressPieces.Crossing3();
      if (componentClass == FortressPieces.End.class) return new FortressPieces.End();
      if (componentClass == FortressPieces.Straight.class) return new FortressPieces.Straight();
      if (componentClass == FortressPieces.Corridor3.class) return new FortressPieces.Corridor3();
      if (componentClass == FortressPieces.Corridor4.class) return new FortressPieces.Corridor4();
      if (componentClass == FortressPieces.Entrance.class) return new FortressPieces.Entrance();
      if (componentClass == FortressPieces.Crossing2.class) return new FortressPieces.Crossing2();
      if (componentClass == FortressPieces.Corridor.class) return new FortressPieces.Corridor();
      if (componentClass == FortressPieces.Corridor5.class) return new FortressPieces.Corridor5();
      if (componentClass == FortressPieces.Corridor2.class) return new FortressPieces.Corridor2();
      if (componentClass == FortressPieces.NetherStalkRoom.class) return new FortressPieces.NetherStalkRoom();
      if (componentClass == FortressPieces.Throne.class) return new FortressPieces.Throne();
      if (componentClass == FortressPieces.Crossing.class) return new FortressPieces.Crossing();
      if (componentClass == FortressPieces.Stairs.class) return new FortressPieces.Stairs();
      if (componentClass == FortressPieces.Start.class) return new FortressPieces.Start();
      if (componentClass == IglooPieces.Piece.class) return new IglooPieces.Piece();
      if (componentClass == JunglePyramidPiece.class) return new JunglePyramidPiece();
      if (componentClass == MineshaftPieces.Corridor.class) return new MineshaftPieces.Corridor();
      if (componentClass == MineshaftPieces.Cross.class) return new MineshaftPieces.Cross();
      if (componentClass == MineshaftPieces.Room.class) return new MineshaftPieces.Room();
      if (componentClass == MineshaftPieces.Stairs.class) return new MineshaftPieces.Stairs();
      if (componentClass == OceanMonumentPieces.MonumentBuilding.class) return new OceanMonumentPieces.MonumentBuilding();
      if (componentClass == OceanMonumentPieces.MonumentCoreRoom.class) return new OceanMonumentPieces.MonumentCoreRoom();
      if (componentClass == OceanMonumentPieces.DoubleXRoom.class) return new OceanMonumentPieces.DoubleXRoom();
      if (componentClass == OceanMonumentPieces.DoubleXYRoom.class) return new OceanMonumentPieces.DoubleXYRoom();
      if (componentClass == OceanMonumentPieces.DoubleYRoom.class) return new OceanMonumentPieces.DoubleYRoom();
      if (componentClass == OceanMonumentPieces.DoubleYZRoom.class) return new OceanMonumentPieces.DoubleYZRoom();
      if (componentClass == OceanMonumentPieces.DoubleZRoom.class) return new OceanMonumentPieces.DoubleZRoom();
      if (componentClass == OceanMonumentPieces.EntryRoom.class) return new OceanMonumentPieces.EntryRoom();
      if (componentClass == OceanMonumentPieces.Penthouse.class) return new OceanMonumentPieces.Penthouse();
      if (componentClass == OceanMonumentPieces.SimpleRoom.class) return new OceanMonumentPieces.SimpleRoom();
      if (componentClass == OceanMonumentPieces.SimpleTopRoom.class) return new OceanMonumentPieces.SimpleTopRoom();
      if (componentClass == OceanRuinPieces.Piece.class) return new OceanRuinPieces.Piece();
      if (componentClass == ShipwreckPieces.Piece.class) return new ShipwreckPieces.Piece();
      if (componentClass == StrongholdPieces.ChestCorridor.class) return new StrongholdPieces.ChestCorridor();
      if (componentClass == StrongholdPieces.Corridor.class) return new StrongholdPieces.Corridor();
      if (componentClass == StrongholdPieces.Crossing.class) return new StrongholdPieces.Crossing();
      if (componentClass == StrongholdPieces.LeftTurn.class) return new StrongholdPieces.LeftTurn();
      if (componentClass == StrongholdPieces.Library.class) return new StrongholdPieces.Library();
      if (componentClass == StrongholdPieces.PortalRoom.class) return new StrongholdPieces.PortalRoom();
      if (componentClass == StrongholdPieces.Prison.class) return new StrongholdPieces.Prison();
      if (componentClass == StrongholdPieces.RightTurn.class) return new StrongholdPieces.RightTurn();
      if (componentClass == StrongholdPieces.RoomCrossing.class) return new StrongholdPieces.RoomCrossing();
      if (componentClass == StrongholdPieces.Stairs.class) return new StrongholdPieces.Stairs();
      if (componentClass == StrongholdPieces.Stairs2.class) return new StrongholdPieces.Stairs2();
      if (componentClass == StrongholdPieces.Straight.class) return new StrongholdPieces.Straight();
      if (componentClass == StrongholdPieces.StairsStraight.class) return new StrongholdPieces.StairsStraight();
      if (componentClass == SwampHutPiece.class) return new SwampHutPiece();
      if (componentClass == VillagePieces.House1.class) return new VillagePieces.House1();
      if (componentClass == VillagePieces.Field1.class) return new VillagePieces.Field1();
      if (componentClass == VillagePieces.Field2.class) return new VillagePieces.Field2();
      if (componentClass == VillagePieces.Torch.class) return new VillagePieces.Torch();
      if (componentClass == VillagePieces.Hall.class) return new VillagePieces.Hall();
      if (componentClass == VillagePieces.House4Garden.class) return new VillagePieces.House4Garden();
      if (componentClass == VillagePieces.WoodHut.class) return new VillagePieces.WoodHut();
      if (componentClass == VillagePieces.Church.class) return new VillagePieces.Church();
      if (componentClass == VillagePieces.House2.class) return new VillagePieces.House2();
      if (componentClass == VillagePieces.Start.class) return new VillagePieces.Start();
      if (componentClass == VillagePieces.Path.class) return new VillagePieces.Path();
      if (componentClass == VillagePieces.House3.class) return new VillagePieces.House3();
      if (componentClass == VillagePieces.Well.class) return new VillagePieces.Well();
      if (componentClass == WoodlandMansionPieces.MansionTemplate.class) return new WoodlandMansionPieces.MansionTemplate();
      return null;
   }

   @Nullable
   public static StructureStart func_202602_a(NBTTagCompound p_202602_0_, IWorld p_202602_1_) {
      StructureStart structurestart = null;
      String s = p_202602_0_.getString("id");
      if ("INVALID".equals(s)) {
         return Structure.NO_STRUCTURE;
      } else {
         try {
            Class<? extends StructureStart> oclass = startNameToClassMap.get(s);
            if (oclass != null) {
               structurestart = createStructureStart(oclass);
            }
         } catch (Exception exception) {
            LOGGER.warn("Failed Start with id {}", (Object)s);
            exception.printStackTrace();
         }

         if (structurestart != null) {
            structurestart.read(p_202602_1_, p_202602_0_);
         } else {
            LOGGER.warn("Skipping Structure with id {}", (Object)s);
         }

         return structurestart;
      }
   }

   public static StructurePiece getStructureComponent(NBTTagCompound tagCompound, IWorld worldIn) {
      StructurePiece structurepiece = null;

      try {
         Class<? extends StructurePiece> oclass = componentNameToClassMap.get(tagCompound.getString("id"));
         if (oclass != null) {
            structurepiece = createStructurePiece(oclass);
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed Piece with id {}", (Object)tagCompound.getString("id"));
         exception.printStackTrace();
      }

      if (structurepiece != null) {
         structurepiece.read(worldIn, tagCompound);
      } else {
         LOGGER.warn("Skipping Piece with id {}", (Object)tagCompound.getString("id"));
      }

      return structurepiece;
   }

   static {
      registerStructure(MineshaftStructure.Start.class, "Mineshaft");
      registerStructure(VillageStructure.Start.class, "Village");
      registerStructure(FortressStructure.Start.class, "Fortress");
      registerStructure(StrongholdStructure.Start.class, "Stronghold");
      registerStructure(JunglePyramidStructure.Start.class, "Jungle_Pyramid");
      registerStructure(OceanRuinStructure.Start.class, "Ocean_Ruin");
      registerStructure(DesertPyramidStructure.Start.class, "Desert_Pyramid");
      registerStructure(IglooStructure.Start.class, "Igloo");
      registerStructure(SwampHutStructure.Start.class, "Swamp_Hut");
      registerStructure(OceanMonumentStructure.Start.class, "Monument");
      registerStructure(EndCityStructure.Start.class, "EndCity");
      registerStructure(WoodlandMansionStructure.Start.class, "Mansion");
      registerStructure(BuriedTreasureStructure.Start.class, "Buried_Treasure");
      registerStructure(ShipwreckStructure.Start.class, "Shipwreck");
      MineshaftPieces.registerStructurePieces();
      VillagePieces.registerVillagePieces();
      FortressPieces.registerNetherFortressPieces();
      StrongholdPieces.registerStrongholdPieces();
      JunglePyramidPiece.registerJunglePyramidPieces();
      OceanRuinPieces.registerPieces();
      IglooPieces.registerPieces();
      SwampHutPiece.registerPieces();
      DesertPyramidPiece.registerPieces();
      OceanMonumentPieces.registerOceanMonumentPieces();
      EndCityPieces.registerPieces();
      WoodlandMansionPieces.registerWoodlandMansionPieces();
      BuriedTreasurePieces.registerBuriedTreasurePieces();
      ShipwreckPieces.registerShipwreckPieces();
   }
}
