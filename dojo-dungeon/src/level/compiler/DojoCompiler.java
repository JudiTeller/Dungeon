package level.compiler;

import core.Entity;
import core.components.DrawComponent;
import core.utils.components.path.SimpleIPath;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import level.rooms.Room;

public class DojoCompiler {
  public record TestResult(String testName, boolean passed, List<String> messages) {}

  private final List<String> messages = new ArrayList<>();
  private String source;
  private Class<?> cls;
  private Method method1;
  private Method method2;

  public TestResult spawnMonsterToOpenTheDoor(String fileName, String className, Room currentRoom) {
    String testName = "spawnMonster";

    if (!testInternal1(fileName) || !testInternal2(className)) {
      return new TestResult(testName, false, messages);
    }

    Method method;
    try {
      method = cls.getMethod("spawnMonster", DrawComponent.class, int.class, float.class);
    } catch (NoSuchMethodException e) {
      messages.add("method not found");
      return new TestResult(testName, false, messages);
    }
    messages.add("method ok");

    Object instance;
    try {
      instance = cls.getConstructor(Room.class).newInstance(currentRoom);
    } catch (InstantiationException
        | NoSuchMethodException
        | InvocationTargetException
        | IllegalAccessException e) {
      messages.add("instance not found");
      return new TestResult(testName, false, messages);
    }
    messages.add("instance ok");

    Entity entity;
    try {
      entity =
          (Entity)
              method.invoke(
                  instance,
                  new DrawComponent(new SimpleIPath("character/monster/pumpkin_dude")),
                  10,
                  10.0f);
    } catch (IllegalAccessException | IOException | InvocationTargetException e) {
      messages.add("entity not found");
      return new TestResult(testName, false, messages);
    }
    messages.add("entity ok");

    currentRoom.addEntityImmediately(entity);

    // All ok.
    return new TestResult(testName, true, messages);
  }

  public TestResult test1(String fileName, String className) {
    String testName = "test1";
    if (testInternal1(fileName)
        && testInternal2(className)
        && testInternal3()
        && testInternal4()
        && testInternal5()
        && testInternal6()) {
      return new TestResult(testName, true, messages);
    }
    return new TestResult(testName, false, messages);
  }

  public TestResult test2(String fileName, String className) {
    String testName = "test2";
    if (testInternal1(fileName)
        && testInternal1_2()
        && testInternal2(className)
        && testInternal3()
        && testInternal4()
        && testInternal5()
        && testInternal6()) {
      return new TestResult(testName, true, messages);
    }
    return new TestResult(testName, false, messages);
  }

  public TestResult test3(String fileName, String className) {
    String testName = "test3";
    if (testInternal1(fileName)
        && testInternal1_2()
        && testInternal2(className)
        && testInternal3()
        && testInternal4()
        && testInternal5()
        && testInternal6()
        && testInternal6_2()) {
      return new TestResult(testName, true, messages);
    }
    return new TestResult(testName, false, messages);
  }

  public TestResult testRoom8() {
    try {
      Class<?> cls2 = compile(getSource("../dojo-dungeon/todo-assets/r8/Cuboid.java"), "Cuboid");
      Constructor<?> tor2 = cls2.getConstructor(float.class, float.class, float.class);
      Object inst2 = tor2.newInstance(10.0f, 30.0f, 20.0f);
      Method m1 = cls2.getMethod("calculateArea");
      Method m2 = cls2.getMethod("calculatePerimeter");
      Method m3 = cls2.getMethod("calculateVolume");
      float f1 = (float) m1.invoke(inst2);
      float f2 = (float) m2.invoke(inst2);
      float f3 = (float) m3.invoke(inst2);
      if (Math.round(f1) != 2200 || Math.round(f2) != 240 || Math.round(f3) != 6000) {
        throw new NoSuchElementException("wrong values ...");
      }
      messages.add("testRoom8 ok");
      return new TestResult("testRoom8", true, messages);
    } catch (Exception ex) {
      messages.add("testRoom8 not ok");
      messages.add(ex.getMessage());
    }
    return new TestResult("testRoom8", false, messages);
  }

  private boolean testInternal1(String fileName) {
    try {
      source = getSource(fileName);
    } catch (IOException ex) {
      messages.add("source not ok");
      return false;
    }
    if (source == null || source.isEmpty()) {
      messages.add("empty source");
      return false;
    }
    messages.add("source ok");
    return true;
  }

  private boolean testInternal1_2() {
    // --- Replace 10 with yuppie ---
    source = source.replace("10", "yuppie");
    messages.add("replace ok");
    return true;
  }

  private boolean testInternal2(String className) {
    try {
      cls = compile(source, className);
    } catch (Exception ex) {
      messages.add("compile not ok");
      return false;
    }
    messages.add("compile ok");
    return true;
  }

  private boolean testInternal3() {
    try {
      method1 = cls.getDeclaredMethod("testExpectedOutput7", PrintWriter.class);
    } catch (NoSuchMethodException ex) {
      messages.add("method1 not ok");
      return false;
    }
    messages.add("method1 ok");
    return true;
  }

  private boolean testInternal4() {
    ByteArrayOutputStream buf1 = new ByteArrayOutputStream();
    try (PrintWriter writer = new PrintWriter(buf1, true)) {
      try {
        method1.invoke(null, writer);
      } catch (IllegalAccessException | InvocationTargetException ex) {
        messages.add("invocation1 not ok");
        return false;
      }
      messages.add("invocation1 ok");
    }

    String actualOutput1 = buf1.toString(Charset.defaultCharset());
    if (actualOutput1 == null || !actualOutput1.contains("Die Summe ist: 7")) {
      messages.add("output1 wrong: " + actualOutput1);
      return false;
    }
    messages.add("output1 ok");

    return true;
  }

  private boolean testInternal5() {
    try {
      method2 = cls.getDeclaredMethod("testExpectedOutput8", PrintWriter.class);
    } catch (NoSuchMethodException ex) {
      messages.add("method2 not ok");
      return false;
    }
    messages.add("method2 ok");
    return true;
  }

  private boolean testInternal6() {
    ByteArrayOutputStream buf2 = new ByteArrayOutputStream();
    try (PrintWriter writer = new PrintWriter(buf2, true)) {
      try {
        method2.invoke(null, writer);
      } catch (IllegalAccessException | InvocationTargetException ex) {
        messages.add("invocation2 not ok");
        return false;
      }
      messages.add("invocation2 ok");
    }

    String actualOutput2 = buf2.toString(Charset.defaultCharset());
    if (actualOutput2 == null || !actualOutput2.contains("Die dritte Zahl ist: 8")) {
      messages.add("output2 wrong: " + actualOutput2);
      return false;
    }
    messages.add("output2 ok");

    return true;
  }

  private boolean testInternal6_2() {
    Pattern pattern = Pattern.compile("try.+catch", Pattern.DOTALL);
    Matcher matcher = pattern.matcher(source);
    boolean b3 = matcher.find();
    if (b3) {
      messages.add("try-catch ok");
    } else {
      messages.add("try-catch not ok");
      return false;
    }

    return true;
  }

  private String getSource(String fn) throws IOException {
    return Files.readString(Paths.get(fn));
  }

  private Class<?> compile(String source, String className) throws Exception {
    // Save source in .java file.
    File root = Files.createTempDirectory("java").toFile();
    File sourceFile = new File(root, className + ".java");
    assert sourceFile.getParentFile().mkdirs();
    Files.writeString(sourceFile.toPath(), source);

    // Compile source file.
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    compiler.run(null, null, null, sourceFile.getPath());

    // Load and instantiate compiled class.
    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {root.toURI().toURL()});

    return Class.forName(className, true, classLoader);
  }
}
