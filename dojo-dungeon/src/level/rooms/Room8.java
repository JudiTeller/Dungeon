package level.rooms;

import contrib.components.*;
import contrib.entities.AIFactory;
import contrib.entities.EntityFactory;
import contrib.hud.dialogs.OkDialog;
import contrib.item.concreteItem.ItemResourceBerry;
import contrib.level.generator.graphBased.RoomGenerator;
import contrib.utils.components.draw.ChestAnimations;
import contrib.utils.components.health.Damage;
import contrib.utils.components.health.DamageType;
import contrib.utils.components.interaction.DropItemsInteraction;
import core.Entity;
import core.Game;
import core.components.DrawComponent;
import core.components.PlayerComponent;
import core.components.PositionComponent;
import core.components.VelocityComponent;
import core.level.utils.DesignLabel;
import core.level.utils.LevelSize;
import core.utils.components.path.SimpleIPath;
import java.io.IOException;
import java.util.Set;
import level.compiler.DojoCompiler;

public class Room8 extends TaskRoom {
  private int impHealth = 10;

  Room8(
      LevelRoom levelRoom,
      RoomGenerator gen,
      Room nextRoom,
      LevelSize levelSize,
      DesignLabel designLabel) {
    super(levelRoom, gen, nextRoom, levelSize, designLabel);

    try {
      generate();
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to generate: " + getClass().getName() + ": " + e.getMessage(), e);
    }
  }

  private void generate() throws IOException {
    addRoomEntities(Set.of(generateChest()));
  }

  private Entity generateChest() throws IOException {
    Entity chest = EntityFactory.newChest();
    chest.add(
        new InteractionComponent(
            1,
            true,
            (entity1, entity2) ->
                OkDialog.showOkDialog(
                    "Bitte spreche mit dem Imp, aber besiege ihn noch nicht.",
                    "Mit Imp sprechen",
                    () -> addEntityImmediately(generateImp()))));
    chest.fetch(DrawComponent.class).orElseThrow().currentAnimation(ChestAnimations.OPEN_FULL);
    return chest;
  }

  /**
   * Genrates a new imp.
   *
   * <p>The imp can be talked to.
   *
   * <p>If the imps dies, a new one will be spawned, and the player health will be decreased by 25,
   * and the new imp health will be increased by 10, and a berry item will be dropped.
   *
   * @return the new generated imp.
   */
  private Entity generateImp() {
    Entity monster1 = new Entity("imp");

    InventoryComponent ic = new InventoryComponent(1);
    monster1.add(ic);
    ic.add(new ItemResourceBerry());

    monster1.add(
        new HealthComponent(
            impHealth,
            (e) -> {
              DojoCompiler.TestResult testResult = new DojoCompiler().testRoom8();
              if (testResult.passed()) {
                OkDialog.showOkDialog(
                    "Danke, du hast die Aufgabe gelöst.",
                    "Aufgabe in diesem Raum:",
                    this::openDoors);
              } else {
                OkDialog.showOkDialog(
                    "Leider nicht gelöst. 25 HP verloren. Versuch's noch einmal. ... "
                        + testResult.messages(),
                    "Aufgabe in diesem Raum:",
                    () ->
                        Game.entityStream(Set.of(PlayerComponent.class, HealthComponent.class))
                            .findFirst()
                            .orElseThrow()
                            .fetch(HealthComponent.class)
                            .orElseThrow()
                            .receiveHit(new Damage(25, DamageType.MAGIC, null)));
              }

              new DropItemsInteraction().accept(e, null);
              impHealth += 10;
              addEntityImmediately(generateImp());
            }));
    monster1.add(new PositionComponent());
    monster1.add(new AIComponent(AIFactory.randomFightAI(), AIFactory.randomIdleAI(), e -> false));
    try {
      monster1.add(new DrawComponent(new SimpleIPath("character/monster/imp")));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    monster1.add(new VelocityComponent(4, 4));
    monster1.add(new CollideComponent());

    // Tell the tasks of this room ...
    monster1.add(
        new InteractionComponent(
            1,
            true,
            (entity1, entity2) ->
                OkDialog.showOkDialog(
                    "Finde und verbessere die Fehler in der Klasse: todo-assets/r8/Cuboid.java.",
                    "Aufgabe in diesem Raum:",
                    () -> {
                      OkDialog.showOkDialog(
                          "Besiege mich, wenn du das fertig hast.",
                          "Aufgabe in diesem Raum:",
                          () -> {});
                    })));

    return monster1;
  }
}
