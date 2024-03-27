package level.rooms;

import contrib.level.generator.graphBased.RoomGenerator;
import core.level.utils.DesignLabel;
import core.level.utils.LevelSize;
import core.utils.components.path.IPath;

public class RoomBuilder {
  private LevelRoom levelRoom;
  private RoomGenerator gen;
  private Room nextRoom;
  private LevelSize levelSize;
  private DesignLabel designLabel;

  private int monsterCount;
  private IPath[] monsterPaths;

  private String keyType;
  private String keyDescription;
  private IPath keyTexture;

  public RoomBuilder() {}

  public RoomBuilder levelRoom(LevelRoom levelRoom) {
    this.levelRoom = levelRoom;
    return this;
  }

  public RoomBuilder roomGenerator(RoomGenerator gen) {
    this.gen = gen;
    return this;
  }

  public RoomBuilder nextRoom(Room nextRoom) {
    this.nextRoom = nextRoom;
    return this;
  }

  public RoomBuilder levelSize(LevelSize levelSize) {
    this.levelSize = levelSize;
    return this;
  }

  public RoomBuilder designLabel(DesignLabel designLabel) {
    this.designLabel = designLabel;
    return this;
  }

  public Room buildRoom() {
    return new Room(levelRoom, gen, nextRoom, levelSize, designLabel);
  }

  public Room buildTaskRoom() {
    return new TaskRoom(levelRoom, gen, nextRoom, levelSize, designLabel);
  }

  public RoomBuilder monsterCount(int monsterCount) {
    this.monsterCount = monsterCount;
    return this;
  }

  public RoomBuilder monsterPaths(IPath[] monsterPaths) {
    this.monsterPaths = monsterPaths;
    return this;
  }

  public Room buildMonsterRoom() {
    return new MonsterRoom(
        levelRoom, gen, nextRoom, levelSize, designLabel, monsterCount, monsterPaths);
  }

  public RoomBuilder keyType(String keyType) {
    this.keyType = keyType;
    return this;
  }

  public RoomBuilder keyDescription(String keyDescription) {
    this.keyDescription = keyDescription;
    return this;
  }

  public RoomBuilder keyTexture(IPath keyTexture) {
    this.keyTexture = keyTexture;
    return this;
  }

  public Room buildKeyRoom() {
    return new KeyRoom(
        levelRoom,
        gen,
        nextRoom,
        levelSize,
        designLabel,
        monsterCount,
        monsterPaths,
        keyType,
        keyDescription,
        keyTexture);
  }

  public Room buildRoom2() {
    return new Room2(levelRoom, gen, nextRoom, levelSize, designLabel);
  }

  public Room buildRoom3() {
    return new Room3(levelRoom, gen, nextRoom, levelSize, designLabel);
  }

  public Room buildRoom5() {
    return new Room5(levelRoom, gen, nextRoom, levelSize, designLabel);
  }

  public Room buildRoom6() {
    return new Room6(levelRoom, gen, nextRoom, levelSize, designLabel);
  }
}
