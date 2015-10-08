package com.leontg77.uhc.utils;

import org.bukkit.entity.EntityType;

/**
 * Block utilities class.
 * <p>
 * Contains block related methods.
 * 
 * @author LeonTG77
 */
public class EntityUtils {
	
	public static boolean isClearable(EntityType type) {
		switch (type) {
		case ARROW:
			return true;
		case BAT:
			return true;
		case BLAZE:
			return true;
		case BOAT:
			return true;
		case CAVE_SPIDER:
			return true;
		case CREEPER:
			return true;
		case DROPPED_ITEM:
			return true;
		case EGG:
			return true;
		case ENDERMAN:
			return true;
		case ENDERMITE:
			return true;
		case ENDER_PEARL:
			return true;
		case EXPERIENCE_ORB:
			return true;
		case FIREBALL:
			return true;
		case FISHING_HOOK:
			return true;
		case GHAST:
			return true;
		case GIANT:
			return true;
		case GUARDIAN:
			return true;
		case IRON_GOLEM:
			return true;
		case MAGMA_CUBE:
			return true;
		case MUSHROOM_COW:
			return true;
		case OCELOT:
			return true;
		case PIG_ZOMBIE:
			return true;
		case PRIMED_TNT:
			return true;
		case SILVERFISH:
			return true;
		case SKELETON:
			return true;
		case SLIME:
			return true;
		case SMALL_FIREBALL:
			return true;
		case SNOWBALL:
			return true;
		case SNOWMAN:
			return true;
		case SPIDER:
			return true;
		case VILLAGER:
			return true;
		case WITCH:
			return true;
		case WITHER:
			return true;
		case WITHER_SKULL:
			return true;
		case ZOMBIE:
			return true;
		default:
			return false;
		}
	}
}