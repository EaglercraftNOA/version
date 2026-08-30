package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import net.lax1dude.eaglercraft.EaglercraftUUID;

public interface IAttributeInstance {
	/**
	 * Get the Attribute this is an instance of
	 */
	IAttribute getAttribute();

	double getBaseValue();

	void setBaseValue(double p_111128_1_);

	Collection func_111122_c();

	/**
	 * Returns attribute modifier, if any, by the given EaglercraftUUID
	 */
	AttributeModifier getModifier(EaglercraftUUID p_111127_1_);

	void applyModifier(AttributeModifier p_111121_1_);

	void removeModifier(AttributeModifier p_111124_1_);

	void removeAllModifiers();

	double getAttributeValue();
}
