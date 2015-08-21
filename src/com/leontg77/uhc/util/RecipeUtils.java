package com.leontg77.uhc.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * Recipe utilities class.
 * <p>
 * Contains reciple related methods.
 * 
 * @author ghowden
 */
public class RecipeUtils {

	/**
	 * Check if two recipes are equal
	 * 
	 * @param one The 1st recipe
	 * @param two The 2nd recipe
	 * @return <code>True</code> if they are equal, <code>false</code> otherwise.
	 */
	public static boolean areEqual(Recipe one, Recipe two) {
		return one == two || one != null && two != null && one.getResult().equals(two.getResult()) && match(one, two);
	}

	/**
	 * Check if two recipes are similar
	 * 
	 * @param one The 1st recipe
	 * @param two The 2nd recipe
	 * @return <code>True</code> if they are similar, <code>false</code> otherwise.
	 */
	public static boolean areSimilar(Recipe one, Recipe two) {
		return one == two || one != null && two != null && match(one, two);
	}

	/**
	 * Matches two recipes
	 * 
	 * @param one The 1st recipe
	 * @param two The 2nd recipe
	 * @return <code>True</code> if they match, <code>false</code> otherwise.
	 */
	private static boolean match(Recipe one, Recipe two) {
		if (one instanceof ShapedRecipe) {
			if (!(two instanceof ShapedRecipe)) {
				return false;
			}
			
			ShapedRecipe recipeOne = (ShapedRecipe) one;
			ShapedRecipe recipeTwo = (ShapedRecipe) two;
			
			ItemStack itemOne[] = shapeToMatrix(recipeOne.getShape(), recipeOne.getIngredientMap());
			ItemStack itemTwo[] = shapeToMatrix(recipeTwo.getShape(), recipeTwo.getIngredientMap());
			
			if (!Arrays.equals(itemOne, itemTwo)) {
				mirrorMatrix(itemOne);
				return Arrays.equals(itemOne, itemTwo);
			} else {
				return true;
			}
		}
		
		if (one instanceof ShapelessRecipe) {
			if (!(two instanceof ShapelessRecipe)) {
				return false;
			}
			
			ShapelessRecipe recipeOne = (ShapelessRecipe) one;
			ShapelessRecipe recipeTwo = (ShapelessRecipe) two;
			
			List<ItemStack> listOne = recipeOne.getIngredientList();
			List<ItemStack> listTwo = recipeTwo.getIngredientList();
			
			if (listOne.size() != listTwo.size()) {
				return false;
			}
			
			for (Iterator<ItemStack> iterator = listTwo.iterator(); iterator.hasNext();) {
				ItemStack itemstack = (ItemStack) iterator.next();
				if (!listOne.remove(itemstack)) {
					return false;
				}
			}

			return listOne.isEmpty();
		} else if (one instanceof FurnaceRecipe) {
			if (!(two instanceof FurnaceRecipe)) {
				return false;
			}

			FurnaceRecipe recipeOne = (FurnaceRecipe) one;
			FurnaceRecipe recipeTwo = (FurnaceRecipe) two;
			
			return recipeOne.getInput().getType() == recipeTwo.getInput().getType();
		} else {
			throw new IllegalArgumentException("Unsupported recipe type: '" + one + "', update RecipeUtil.java!");
		}
	}
	
	/**
	 * Idk... xD
	 */
	private static ItemStack[] shapeToMatrix(String as[], Map<Character, ItemStack> map) {
		ItemStack aitemstack[] = new ItemStack[9];
		int i = 0;
		for (int j = 0; j < as.length; j++) {
			char ac[] = as[j].toCharArray();
			int k = ac.length;
			for (int l = 0; l < k; l++) {
				char c = ac[l];
				aitemstack[i] = (ItemStack) map.get(Character.valueOf(c));
				i++;
			}

			i = (j + 1) * 3;
		}

		return aitemstack;
	}

	/**
	 * Idk... xD
	 */
	private static void mirrorMatrix(ItemStack item[]) {
		for (int i = 0; i < 3; i++) {
			ItemStack itemstack = item[i * 3];
			item[i * 3] = item[i * 3 + 2];
			item[i * 3 + 2] = itemstack;
		}
	}
}