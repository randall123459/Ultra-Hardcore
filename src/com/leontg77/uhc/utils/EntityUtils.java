package com.leontg77.uhc.utils;

import java.util.Set;

import org.bukkit.entity.EntityType;

import com.google.common.collect.ImmutableSet;

/**
 * Block utilities class.
 * <p>
 * Contains block related methods.
 * 
 * @author LeonTG77
 */
public class EntityUtils {
	
	protected static final Set<EntityType> clearable = ImmutableSet.of(
		EntityType.ARROW, EntityType.BAT, EntityType.BLAZE, EntityType.BOAT, EntityType.CAVE_SPIDER,
		EntityType.CREEPER, EntityType.DROPPED_ITEM, EntityType.EGG, EntityType.ENDERMAN, EntityType.ENDERMITE,
		EntityType.ENDER_PEARL, EntityType.EXPERIENCE_ORB, EntityType.FIREBALL, EntityType.FISHING_HOOK,
		EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.IRON_GOLEM, EntityType.MAGMA_CUBE,
		EntityType.MUSHROOM_COW, EntityType.OCELOT, EntityType.PIG_ZOMBIE, EntityType.PRIMED_TNT,
		EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SMALL_FIREBALL,
		EntityType.SNOWBALL, EntityType.SNOWMAN, EntityType.SPIDER, EntityType.VILLAGER, EntityType.WITCH,
		EntityType.WITHER, EntityType.WITHER_SKULL, EntityType.ZOMBIE
	);
	
	public static boolean isClearable(EntityType type) {
		return clearable.contains(type);
	}
}