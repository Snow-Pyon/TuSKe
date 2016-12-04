package me.tuke.sktuke.recipe;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import me.tuke.sktuke.TuSKe;

public class RecipeManager implements Listener{

	private HashSet<Recipe> recipes = new HashSet<>();
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPrepare(PrepareItemCraftEvent e){
		long start = System.currentTimeMillis();
		Recipe rec = getIfContainsCustomRecipe(e.getRecipe());
		if (rec != null){
			TuSKe.debug("Passou");
			if (!areArrayItemsEqual(getItems(rec), e.getInventory().getMatrix(), true))
				e.getInventory().setResult(new ItemStack(Material.AIR));
		}
		TuSKe.debug(getItems(e.getRecipe()));
		TuSKe.debug(System.currentTimeMillis() - start);
		
		
	}
	public void registerRecipe(Recipe rec){

		if (rec instanceof CustomShapedRecipe || rec instanceof CustomShapelessRecipe || rec instanceof CustomFurnaceRecipe){
			
			if (getIfContainsCustomRecipe(rec) == null && getItems(rec) != null){
				recipes.add(rec);
				if (recipes.size() == 1)
					Bukkit.getPluginManager().registerEvents(this, TuSKe.getInstance());
				TuSKe.debug(recipes.size());
			}
		}
		Bukkit.addRecipe(rec);
	}
	
	public Recipe getIfContainsCustomRecipe(Recipe rec){
		for (Recipe recipe : recipes)
			if (areEqual(recipe, rec))
				return recipe;
		return null;
	}
	public boolean areEqual(Recipe recipe1, Recipe recipe2){
		return recipe1.getResult().isSimilar(recipe2.getResult()) &&  areArrayItemsEqual(getItems(recipe1), getItems(recipe2), false);
	}
	
	public boolean areArrayItemsEqual(ItemStack[] item1, ItemStack[] item2, boolean sameItemMeta){
		return compareArrayItems(item1, item2, sameItemMeta) != null;
		
		
	}
	public ItemStack[] compareArrayItems(ItemStack[] item1, ItemStack[] item2, boolean sameItemMeta){
		TuSKe.debug(item1, item2);
		if (item1.length != item2.length && item1.length + item2.length < 18)
			return null;
		if (!sameItemMeta)
			for (int x = 0; x < item1.length; x++){
				ItemStack i1 = item1[x];
				ItemStack i2 = item2[x];
				TuSKe.debug(i1, i2);
				if (i1 != null && i1.getDurability() == 32767)
					i1.setDurability((short)0);				
				if (i1 != null && i2 != null && i1.getType() != i2.getType() && i1.getAmount() > i2.getAmount() && i1.getDurability() != i2.getDurability())
					return null;
				else if (i1 == null && i2 != null && i2.getType() == Material.AIR)
					continue;
				else if (i1 == null ^ i2 == null)
					return null;
			}
		else 
			for (int x = 0; x < item1.length; x++){
				ItemStack i1 = item1[x];
				ItemStack i2 = item2[x];
				if (i1 != null && i1.getDurability() == 32767)
					i1.setDurability((short)0);
				if (i1 != null && i2 != null && !i1.isSimilar(i2))
					return null;
				else if (i1 == null && i2 != null && i2.getType() == Material.AIR)
					continue;
				else if (i1 == null ^ i2 == null)
					return null;
			}
		return item1;
		
	}
	public ItemStack[] getItems(Recipe rec){
		if (rec instanceof CustomShapedRecipe)
			return ((CustomShapedRecipe) rec).getIngredients();
		else if (rec instanceof CustomShapelessRecipe)
			return ((CustomShapelessRecipe) rec).getIngredients();
		else if (rec instanceof CustomFurnaceRecipe)
			return new ItemStack[]{((CustomFurnaceRecipe) rec).getSource()};
		else if (rec instanceof ShapedRecipe)
			return ((ShapedRecipe) rec).getIngredientMap().values().toArray(new ItemStack[((ShapedRecipe) rec).getIngredientMap().size()]);
		else if (rec instanceof ShapelessRecipe)
			return ((ShapelessRecipe) rec).getIngredientList().toArray(new ItemStack[((ShapelessRecipe) rec).getIngredientList().size()]);
		else
			return new ItemStack[]{((FurnaceRecipe)rec).getInput()};
	}
}