package com.leontg77.uhc.utils;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 * Name utilities class.
 * <p>
 * Contains name related methods.
 * 
 * @author LeonTG77
 */
public class NameUtils {
	
	/**
	 * Fix the given text with making the first letter captializsed and the rest not.
	 * 
	 * @param text the text fixing.
	 * @param replaceUnderscore True to replace all _ with a space, false otherwise.
	 * @return The new fixed text.
	 */
	public static String fixString(String text, boolean replaceUnderscore) {
		if (text.equalsIgnoreCase("vip")) {
			return "VIP";
		}
		
		String toReturn = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
		
		if (replaceUnderscore) {
			toReturn = toReturn.replace("_", " ");
		} 
		
		return toReturn;
	}

	/**
	 * Get the real potion name of the given potion type.
	 * 
	 * @param type the type.
	 * @return The real potion name.
	 */
	public static String getPotionName(PotionEffectType type) {
		switch (type.getName().toLowerCase()) {
		case "speed":
			return "Speed";
		case "slow":
			return "Slowness";
		case "fast_digging":
			return "Haste";
		case "slow_digging":
			return "Mining fatigue";
		case "increase_damage":
			return "Strength";
		case "heal":
			return "Instant Health";
		case "harm":
			return "Instant Damage";
		case "jump":
			return "Jump Boost";
		case "confusion":
			return "Nausea";
		case "regeneration":
			return "Regeneration";
		case "damage_resistance":
			return "Resistance";
		case "fire_resistance":
			return "Fire Resistance";
		case "water_breathing":
			return "Water breathing";
		case "invisibility":
			return "Invisibility";
		case "blindness":
			return "Blindness";
		case "night_vision":
			return "Night Vision";
		case "hunger":
			return "Hunger";
		case "weakness":
			return "Weakness";
		case "poison":
			return "Poison";
		case "wither":
			return "Wither";
		case "health_boost":
			return "Health Boost";
		case "absorption":
			return "Absorption";
		case "saturation":
			return "Saturaion";
		default:
			return "?";
		}
	}
	
	public static String getMobName(Entity entity) {
		switch (entity.getType()) {
		case ARMOR_STAND:
			return "Armor Stand";
		case ARROW:
			return "Arrow";
		case BAT:
			return "Bat";
		case BLAZE:
			return "Blaze";
		case BOAT:
			return "Boat";
		case CAVE_SPIDER:
			return "Cave Spider";
		case CHICKEN:
			return "Chicken";
		case COW:
			return "Cow";
		case CREEPER:
			return "Creeper";
		case DROPPED_ITEM:
			return "Dropped Item";
		case EGG:
			return "Egg";
		case ENDERMAN:
			return "Enderman";
		case ENDERMITE:
			return "Endermite";
		case ENDER_CRYSTAL:
			return "Ender Crystal";
		case ENDER_DRAGON:
			return "Ender Dragon";
		case ENDER_PEARL:
			return "Ender Pearl";
		case ENDER_SIGNAL:
			return "Ender Signal";
		case EXPERIENCE_ORB:
			return "Exp. Orb";
		case FALLING_BLOCK:
			return "Falling Block";
		case FIREBALL:
			return "Fireball";
		case FIREWORK:
			return "Firework";
		case FISHING_HOOK:
			return "Fish. Hook";
		case GHAST:
			return "Ghast";
		case GIANT:
			return "Giant";
		case GUARDIAN:
			return "Guardian";
		case HORSE:
			return "Horse";
		case IRON_GOLEM:
			return "Iron Golem";
		case ITEM_FRAME:
			return "Item Frame";
		case LEASH_HITCH:
			return "Leash";
		case LIGHTNING:
			return "Lightning";
		case MAGMA_CUBE:
			return "Magma Cube";
		case MINECART:
			return "Minecart";
		case MINECART_CHEST:
			return "Minecart Chest";
		case MINECART_COMMAND:
			return "Minecart CommandBlock";
		case MINECART_FURNACE:
			return "Minecart Furnace";
		case MINECART_HOPPER:
			return "Minecart Hopper";
		case MINECART_MOB_SPAWNER:
			return "Minecart Mob Spawner";
		case MINECART_TNT:
			return "Minecart";
		case MUSHROOM_COW:
			return "Mushroom Cow";
		case OCELOT:
			return "Ocelot";
		case PAINTING:
			return "Painting";
		case PIG:
			return "Pig";
		case PIG_ZOMBIE:
			return "Zombie Pigman";
		case PLAYER:
			return "Player";
		case PRIMED_TNT:
			return "TNT";
		case RABBIT:
			return "Rabbit";
		case SHEEP:
			return "Sheep";
		case SILVERFISH:
			return "Silverfish";
		case SKELETON:
			return "Skeleton";
		case SLIME:
			return "Slime";
		case SMALL_FIREBALL:
			return "Fireball";
		case SNOWBALL:
			return "Snowball";
		case SNOWMAN:
			return "Snow Golem";
		case SPIDER:
			return "Spider";
		case SPLASH_POTION:
			return "Potion";
		case SQUID:
			return "Squid";
		case THROWN_EXP_BOTTLE:
			return "Thrown Exp. Bottle";
		case VILLAGER:
			return "Villager";
		case WITCH:
			return "Witch";
		case WITHER:
			return "Wither";
		case WITHER_SKULL:
			return "Wither";
		case WOLF:
			return "Wolf";
		case ZOMBIE:
			return "Zombie";
		default:
			return "?";
		}
	}
	
	/**
	* Converts an {@link org.bukkit.inventory.ItemStack} to a Json string
	* for sending with {@link net.md_5.bungee.api.chat.BaseComponent}'s.
	*
	* @param itemStack the item to convert
	* @return the Json string representation of the item
	*/
	public static String convertItemStackToJson(ItemStack itemStack) {
	    Class<?> craftitemstack = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
	    Method method = ReflectionUtils.getMethod(craftitemstack, "asNMSCopy", ItemStack.class);

	    Class<?> itemstack = ReflectionUtils.getNMSClass("ItemStack");
	    Class<?> nbt = ReflectionUtils.getNMSClass("NBTTagCompound");
	    Method save = ReflectionUtils.getMethod(itemstack, "save", nbt);

	    Object nbtInstance; 
	    Object nsmcopy; 
	    Object toReturn; 

	    try {
	        nbtInstance = nbt.newInstance();
	        nsmcopy = method.invoke(null, itemStack);
	        toReturn = save.invoke(nsmcopy, nbtInstance);
	    } catch (Throwable t) {
	        Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
	        return null;
	    }

	    return toReturn.toString();
	}
}