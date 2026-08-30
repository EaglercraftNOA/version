package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.<String, Class<? extends StructureStart>>newHashMap();
	private static final Map<String, Supplier<? extends StructureStart>> startNameToSupplierMap = Maps.<String, Supplier<? extends StructureStart>>newHashMap();
	private static final Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.<Class<? extends StructureStart>, String>newHashMap();
	private static final Map<String, Class<? extends StructureComponent>> componentNameToClassMap = Maps.<String, Class<? extends StructureComponent>>newHashMap();
	private static final Map<String, Supplier<? extends StructureComponent>> componentNameToSupplierMap = Maps.<String, Supplier<? extends StructureComponent>>newHashMap();
	private static final Map<Class<? extends StructureComponent>, String> componentClassToNameMap = Maps.<Class<? extends StructureComponent>, String>newHashMap();

	private static void registerStructure(Class<? extends StructureStart> startClass, Supplier<? extends StructureStart> supplierClass, String structureName) {
		startNameToSupplierMap.put(structureName, supplierClass);
		startNameToClassMap.put(structureName, startClass);
		startClassToNameMap.put(startClass, structureName);
	}

	static void registerStructureComponent(Class<? extends StructureComponent> componentClass, Supplier<? extends StructureComponent> supplierClass, String componentName) {
		componentNameToClassMap.put(componentName, componentClass);
		componentNameToSupplierMap.put(componentName, supplierClass);
		componentClassToNameMap.put(componentClass, componentName);
	}

	public static String getStructureStartName(StructureStart start) {
		return startClassToNameMap.get(start.getClass());
	}

	public static String getStructureComponentName(StructureComponent component) {
		return componentClassToNameMap.get(component.getClass());
	}

	@Nullable
	public static StructureStart getStructureStart(NBTTagCompound tagCompound, World worldIn) {
		StructureStart structurestart = null;

		try {
			Supplier<? extends StructureStart> oclass = (Supplier) startNameToSupplierMap.get(tagCompound.getString("id"));

			if (oclass != null) {
				structurestart = (StructureStart) oclass.get();
			}
		} catch (Exception exception) {
			LOGGER.warn("Failed Start with id {}", (Object) tagCompound.getString("id"));
			exception.printStackTrace();
		}

		if (structurestart != null) {
			structurestart.readStructureComponentsFromNBT(worldIn, tagCompound);
		} else {
			LOGGER.warn("Skipping Structure with id {}", (Object) tagCompound.getString("id"));
		}

		return structurestart;
	}

	public static StructureComponent getStructureComponent(NBTTagCompound tagCompound, World worldIn) {
		StructureComponent structurecomponent = null;

		try {
			Supplier<? extends StructureComponent> oclass = (Supplier) componentNameToSupplierMap.get(tagCompound.getString("id"));

			if (oclass != null) {
				structurecomponent = oclass.get();
			}
		} catch (Exception exception) {
			LOGGER.warn("Failed Piece with id {}", (Object) tagCompound.getString("id"));
			exception.printStackTrace();
		}

		if (structurecomponent != null) {
			structurecomponent.readStructureBaseNBT(worldIn, tagCompound);
		} else {
			LOGGER.warn("Skipping Piece with id {}", (Object) tagCompound.getString("id"));
		}

		return structurecomponent;
	}

	static {
		registerStructure(StructureMineshaftStart.class, StructureMineshaftStart::new, "Mineshaft");
		registerStructure(MapGenVillage.Start.class, MapGenVillage.Start::new, "Village");
		registerStructure(MapGenNetherBridge.Start.class, MapGenNetherBridge.Start::new, "Fortress");
		registerStructure(MapGenStronghold.Start.class, MapGenStronghold.Start::new, "Stronghold");
		registerStructure(MapGenScatteredFeature.Start.class, MapGenScatteredFeature.Start::new, "Temple");
		registerStructure(StructureOceanMonument.StartMonument.class, StructureOceanMonument.StartMonument::new, "Monument");
		registerStructure(MapGenEndCity.Start.class, MapGenEndCity.Start::new, "EndCity");
		registerStructure(WoodlandMansion.Start.class, WoodlandMansion.Start::new, "Mansion");
		StructureMineshaftPieces.registerStructurePieces();
		StructureVillagePieces.registerVillagePieces();
		StructureNetherBridgePieces.registerNetherFortressPieces();
		StructureStrongholdPieces.registerStrongholdPieces();
		ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
		StructureOceanMonumentPieces.registerOceanMonumentPieces();
		StructureEndCityPieces.registerPieces();
		WoodlandMansionPieces.func_191153_a();
	}
}
