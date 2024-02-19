package contrib.crafting;

import static java.util.stream.Collectors.toList;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import contrib.item.Item;
import core.utils.logging.CustomLogLevel;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Handles the crafting system.
 *
 * <p>It contains a list of recipes and methods to {@link #addRecipe(Recipe) add} recipes. It also
 * provides the method {@link #recipeByIngredients(CraftingIngredient[])} to get a recipe based on
 * the provided ingredients.
 *
 * <p>It will load the recipes from the files via {@link #loadRecipes()}. Recipes have to be in the
 * 'assets/recipes' directory. This will automatically happen at program start. Call this in your
 * callback method to load recipes.
 */
public final class Crafting {
  private static final HashSet<Recipe> RECIPES = new HashSet<>();
  private static final Logger LOGGER = Logger.getLogger(Crafting.class.getSimpleName());

  /**
   * Get a recipe based on the provided items.
   *
   * <p>If there are multiple recipes that can be made with the provided items, recipes where items
   * must be in a specific order will be prioritized.
   *
   * @param inputs Ingredients used.
   * @return The recipe that can be crafted with the provided ingredients. If none can be crafted,
   *     the returned optional will be empty.
   */
  public static Optional<Recipe> recipeByIngredients(final CraftingIngredient[] inputs) {
    List<Recipe> possibleRecipes = new ArrayList<>();
    for (Recipe recipe : RECIPES) {
      if (recipe.canCraft(inputs)) {
        possibleRecipes.add(recipe);
      }
    }

    if (possibleRecipes.isEmpty()) {
      return Optional.empty();
    }
    if (possibleRecipes.size() == 1) {
      return Optional.of(possibleRecipes.get(0));
    }
    possibleRecipes.sort(Comparator.comparing(c -> !c.ordered()));

    return Optional.of(possibleRecipes.get(0));
  }

  /**
   * Add A recipe to the list of recipes programmatically.
   *
   * @param recipe The recipe to add.
   */
  public static void addRecipe(final Recipe recipe) {
    if (recipe.ingredients().length == 0) {
      throw new InvalidRecipeException("Recipes with no ingredients are not allowed!");
    }
    RECIPES.add(recipe);
  }

  /** Remove all recipes. */
  public static void clearRecipes() {
    RECIPES.clear();
  }

  /**
   * Retrieves a list of resources located at the specified resource path directory.
   *
   * <p>See also <a href="https://stackoverflow.com/a/28985785">StackOverflow question</a>.
   *
   * @param path the path directory of the resources
   * @return a list of URLs representing the resources
   */
  private static List<URL> getResources(final String path) throws IOException {
    final ClassLoader loader = Thread.currentThread().getContextClassLoader();
    try (final InputStream is = loader.getResourceAsStream(path)) {
      assert is != null;
      try (final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
          final BufferedReader br = new BufferedReader(isr)) {
        return br.lines().map(l -> path + "/" + l).map(loader::getResource).collect(toList());
      }
    }
  }

  /**
   * Load recipes from the recipes' folder.
   *
   * <p>If the program is compiled to a jar file, recipes will be loaded from within the jar file.
   */
  public static void loadRecipes() {
    final String dirName = "recipes/";
    try {
      List<URL> resources = getResources(dirName);
      if (resources.isEmpty()) {
        throw new FileNotFoundException(dirName);
      }
      for (URL url : resources) {
        Path file = Paths.get(url.toURI().normalize());
        if (Files.isRegularFile(file) && file.toString().endsWith(".recipe")) {
          RECIPES.add(Objects.requireNonNull(parseRecipe(url.openStream(), file.toString())));
        }
      }
    } catch (Exception e) {
      System.err.println("Dir not found: " + dirName);
      throw new RuntimeException(e);
    }
  }

  /**
   * Parse a recipe from a file.
   *
   * @param stream The stream to read from.
   * @param name The name of the file. Used for error logging only.
   * @return The parsed recipe.
   */
  private static Recipe parseRecipe(final InputStream stream, final String name) {
    try {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      StringBuilder builder = new StringBuilder();
      reader.lines().forEach(builder::append);

      JsonReader jsonReader = new JsonReader();
      JsonValue root = jsonReader.parse(builder.toString());

      boolean orderedRecipe = root.getBoolean("ordered");
      JsonValue ingredients = root.get("ingredients"); // Array of items
      JsonValue results = root.get("results"); // Array of results

      if (ingredients.size <= 0) {
        throw new InvalidRecipeException("Recipes with no ingredients are not allowed!");
      }

      // Load Ingredients
      CraftingIngredient[] ingredientsArray = new CraftingIngredient[ingredients.size];
      for (int i = 0; i < ingredients.size; i++) {

        JsonValue ingredient = ingredients.get(i);
        JsonValue item = ingredient.get("item");
        String id = item.getString("id");

        String type = ingredient.getString("type");
        if (type.equals("item")) {
          CraftingIngredient ci = Item.getItem(id).getDeclaredConstructor().newInstance();
          ingredientsArray[i] = ci;
        } else {
          throw new RuntimeException("Unknown ingredient type: " + type);
        }
      }

      // Load Results
      CraftingResult[] resultsArray = new CraftingResult[results.size];
      for (int i = 0; i < results.size; i++) {
        JsonValue result = results.get(i);
        JsonValue item = result.get("item");
        String id = item.getString("id");

        String type = result.getString("type");
        if (type.equals("item")) {
          CraftingResult cr = Item.getItem(id).getDeclaredConstructor().newInstance();
          resultsArray[i] = cr;
        } else {
          throw new RuntimeException("Unknown result type: " + type);
        }
      }
      Recipe recipe = new Recipe(orderedRecipe, ingredientsArray, resultsArray);
      RECIPES.add(recipe);

      reader.close();

      return recipe;
    } catch (IOException
        | InvocationTargetException
        | InstantiationException
        | IllegalAccessException
        | NoSuchMethodException ex) {
      LOGGER.log(
          CustomLogLevel.ERROR, "Error parsing recipe (" + name + "): " + ex.getMessage()); // Error
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
      LOGGER.log(
          CustomLogLevel.WARNING,
          "Error parsing recipe (" + name + "): " + ex.getMessage()); // Warning
    }

    return null;
  }
}
