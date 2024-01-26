**Welcome to the official documentation of YAMLParser!**

This project aims to provide a simple way of **accessing data** from **YAML files**.
It uses the following **libraries**: 
- [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml/src/master/)
- [FulmiCollection](https://github.com/Fulminazzo/FulmiCollection)

To start using the project, import it with Maven or Gradle:
- **Maven**:
```xml
<repository>
    <id>fulminazzo-repo</id>
    <url>https://repo.fulminazzo.it/releases</url>
</repository>
```
```xml
<dependency>
    <groupId>it.fulminazzo</groupId>
    <artifactId>YAMLParser</artifactId>
    <version>LATEST</version>
</dependency>
```
- **Gradle**:
```groovy
repositories {
    maven { url = "https://repo.fulminazzo.it/releases" }
}

dependencies {
  implementation 'it.fulminazzo:YAMLParser:latest.release'
}
```

| **Table of Contents**                                         |
|---------------------------------------------------------------|
| [How does it work](#how-does-it-work)                         |
| [IConfiguration](#iconfiguration)                             |
| [FileConfiguration](#fileconfiguration)                       |
| [ConfigurationSection](#configurationsection)                 |
| [YAMLParser](#yamlparser)                                     |
| [Creating your own YAMLParser](#creating-your-own-yamlparser) |

## How does it work
The main idea of this project comes from [Minecraft Bukkit FileConfiguration system](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/configuration/file/FileConfiguration.html),
a way of interfacing with YAML files and retrieving **non-primitive data objects**.

YAMLParser mimics it by providing its own implementation (therefore becoming **independent** of the Bukkit library), while also giving a way to access [user defined objects](#yamlparser).

## IConfiguration
The base of **YAMLParser** is [IConfiguration](https://github.com/Fulminazzo/YAMLParser/blob/master/src/main/java/it/fulminazzo/yamlparser/interfaces/IConfiguration.java).
This interface presents a variety of **methods** and **functions** to work with data. Here are some of the most important ones:
- ```getKeys(boolean deep)```: returns a **Set** containing every **key** in the YAML file. If ```deep``` is set to ```true```, it returns also the **keys** from every **subsection**;
- ```getValues(boolean deep)```: returns a **Map** containing every **key-value** pair in the YAML file. If ```deep``` is set to ```true```, it returns also the **pairs** from every **subsection**;
- ```contains(String path)```: returns **true** if the specified path **exists**;
- ```createSection(String path, Map<?, ?> map)```: **creates** a **section** at the specified path and **fills** it with the given **map**;
- ```set(String path, O o)```: **sets** the object **O** to the given **path**. If the path contains ".", it **creates sections** accordingly. Uses [YAMLParsers](#yamlparser) for saving objects; 
- ```getConfigurationSection(String path)```: returns the **configuration section** at the given path;
- ```isConfigurationSection(String path)```: **checks** if the object at the given path is a **configuration section**;
- ```getEnum(String path, E def, Class<E> eClass)```: returns the **enum of class eClass** at the given path. If not found, return **def**;
- ```isEnum(String path, Class<E> eClass)```: **checks** if the object** at the given path is an enum of class eClass;
- ```getUUID(String path, UUID def)```: returns the **UUID** at the given path. If not found, return **def**;
- ```isUUID(String path)```: **checks** if the object at the given path is a **UUID**;
- ```getDate(String path, Date def)```: returns the **Date** at the given path. If not found, return **def**;
- ```isDate(String path)```: **checks** if the object at the given path is a **Date**;
- ```getString(String path, String def)```: returns the **String** at the given path. If not found, return **def**;
- ```isString(String path)```: **checks** if the object at the given path is a **String**;
- ```getInteger(String path, Integer def)```: returns the **Integer** at the given path. If not found, return **def**;
- ```isInteger(String path)```: **checks** if the object at the given path is an **Integer**;
- ```getDouble(String path, Double def)```: returns the **Double** at the given path. If not found, return **def**;
- ```isDouble(String path)```: **checks** if the object at the given path is a **Double**;
- ```getFloat(String path, Float def)```: returns the **Float** at the given path. If not found, return **def**;
- ```isFloat(String path)```: **checks** if the object at the given path is a **Float**;
- ```getLong(String path, Long def)```: returns the **Long** at the given path. If not found, return **def**;
- ```isLong(String path)```: **checks** if the object at the given path is a **Long**;
- ```getShort(String path, Short def)```: returns the **Short** at the given path. If not found, return **def**;
- ```isShort(String path)```: **checks** if the object at the given path is a **Short**;
- ```getBoolean(String path, Boolean def)```: returns the **Boolean** at the given path. If not found, return **def**;
- ```isBoolean(String path)```: **checks** if the object at the given path is a **Boolean**;
- ```getCharacter(String path, Character def)```: returns the **Character** at the given path. If not found, return **def**;
- ```isCharacter(String path)```: **checks** if the object at the given path is a **Character**;
- ```getByte(String path, Byte def)```: returns the **Byte** at the given path. If not found, return **def**;
- ```isByte(String path)```: **checks** if the object at the given path is a **Byte**;
- ```getObject(String path, Object def)```: returns the **Object** at the given path. If not found, return **def**;
- ```isObject(String path)```: **checks** if the object at the given path is an **Object**;
- ```getEnumList(String path, Class<? extends E> eClass)```: returns the **enum list of type eClass** at the given path; 
- ```getUUIDList(String path)```: returns the **UUID list** at the given path;
- ```getDateList(String path)```: returns the **Date list** at the given path;
- ```getStringList(String path)```: returns the **String list** at the given path;
- ```getIntegerList(String path)```: returns the **Integer list** at the given path;
- ```getDoubleList(String path)```: returns the **Double list** at the given path;
- ```getFloatList(String path)```: returns the **Float list** at the given path;
- ```getLongList(String path)```: returns the **Long list** at the given path;
- ```getShortList(String path)```: returns the **Short list** at the given path;
- ```getBooleanList(String path)```: returns the **Boolean list** at the given path;
- ```getCharacterList(String path)```: returns the **Character list** at the given path;
- ```getByteList(String path)```: returns the **Byte list** at the given path;
- ```getObjectList(String path)```: returns the **Object list** at the given path;
- ```getObjectList(String path, List<?> def)```: returns the **Object list** at the given path. If not found, return **def**;
- ```isList(String path)```: **checks** if the object at the given path is a **List**;
- ```getList(String path, Class<T> clazz)```: returns the **object of type clazz list** at the given path;
- ```get(String path, T def, Class<T> clazz)```: returns the **object of type clazz** at the given path. If not found, return **def**;
- ```print()```: **prints** the **contents** of the configuration.

When invoking a method of type ```get```, the library will also check:
- if the **object** is **found**, but is of **different type** from expected. This will throw an [UnexpectedClassException](https://github.com/Fulminazzo/YAMLParser/blob/master/src/main/java/it/fulminazzo/yamlparser/exceptions/yamlexceptions/UnexpectedClassException.java);
- by default, it will **ignore null objects**. However, if you use ```setNonNull(true)```, every null object will throw a [CannotBeNullException](https://github.com/Fulminazzo/YAMLParser/blob/master/src/main/java/it/fulminazzo/yamlparser/exceptions/yamlexceptions/CannotBeNullException.java);.

Now that we have covered the basics, we can look into some implementations of the **IConfiguration** interface.

### FileConfiguration
This class **links** the **in memory configuration** map and the **corresponding file**.
It allows loading configurations from **File** or **InputStreams** and allows saving them with the ```save()``` method.
When invoking the ```getRoot()``` method on a proper **IConfiguration** object, a **FileConfiguration** is always returned.

### ConfigurationSection
This class represents any **YAML section**. Essentially:
```yaml
example-section:
  obj1: 1
  obj2: "String"
```
will be loaded in a **ConfigurationSection** named ```example-section``` that contains the **key-value pairs** ```obj1, 1``` and ```obj2, "String"```.

## YAMLParser
The real power of this project comes from **YAMLParsers**.
A **YAMLParser** is a class that allows **conversion** of an **object** to its **YAML form** and vice versa.
For example, every **primitive type** (or **String**) conversion is just the **object itself**, since YAML supports these types.
However, for more **complex objects**, like UUIDs and Dates, **additional code is required**. 
Thanks to **YAMLParser**, you will not have to worry about it and **only focus** on **retrieving** the **object**.

Here is a list with every **YAMLParser**, its target class and how it converts it:

| **YAMLParsers**                                                      | **Class**                                                                                   |
|----------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| [ArrayYAMLParser](#arrayyamlparser-t)                                | &#60;T&#62;[]                                                                               |
| [CollectionYAMLParser](#collectionyamlparser-javautilcollection)     | [java.util.Collection](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html) |
| [DateYAMLParser](#dateyamlparser-javautildate)                       | [java.util.Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html)             |
| [ListYAMLParser](#listyamlparser-javautillist)                       | [java.util.List](https://docs.oracle.com/javase/8/docs/api/java/util/List.html)             |
| [SetYAMLParser](#setyamlparser-javautilset)                          | [java.util.Set](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html)               |
| [UUIDYAMLParser](#uuidyamlparser-javautiluuid)                       | [java.util.UUID](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html)             |
| [SerializableYAMLParser](#serializableyamlparser-javaioserializable) | [java.io.Serializable](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html) |
| [CallableYAMLParser](#callableyamlparser-t)                          | &#60;T&#62;                                                                                 |

### ArrayYAMLParser (&#60;T&#62;[])
Convert an **array** of type **T**.

**WARNING**: because of how Java works, it is **not** possible to load an **empty array**.
Therefore, the use of arrays is **discouraged**.

**Conversion:**
```yaml
# Primitive type or String
simple-array:
- t1
- t2
- t3
# Other types
complex-array:
'0': t1
'1': t1
'2': t1
value-class: rO0ABXQADGEuamF2YS5jbGFzcw==
```

### CollectionYAMLParser ([java.util.Collection](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html))
Convert a **collection** of **any type**.

**Conversion:**
```yaml
# Primitive type or String
simple-collection:
- t1
- t2
- t3
# Other types
complex-collection:
'0': t1
'1': t1
'2': t1
value-class: rO0ABXQADGEuamF2YS5jbGFzcw==
```

### DateYAMLParser ([java.util.Date](https://docs.oracle.com/javase/8/docs/api/java/util/Date.html))
Convert a **Date** into a valid **long**.

**Conversion:**
```yaml
date: 1701960652515
```

### ListYAMLParser ([java.util.List](https://docs.oracle.com/javase/8/docs/api/java/util/List.html))
Convert a **list** of **any type**.

**Conversion:**
```yaml
# Primitive type or String
simple-list:
- t1
- t2
- t3
# Other types
complex-list:
'0': t1
'1': t1
'2': t1
value-class: rO0ABXQADGEuamF2YS5jbGFzcw==
```

### SetYAMLParser ([java.util.Set](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html))
Convert a **set** of **any type**.

**Conversion:**
```yaml
# Primitive type or String
simple-set:
- t1
- t2
- t3
# Other types
complex-set:
'0': t1
'1': t1
'2': t1
value-class: rO0ABXQADGEuamF2YS5jbGFzcw==
```

### UUIDYAMLParser ([java.util.UUID](https://docs.oracle.com/javase/8/docs/api/java/util/UUID.html))
Convert a **UUID** into a valid **string**.

**Conversion:**
```yaml
uuid: dd6398ec-8461-4b20-9fa7-3dbae60c01b6
```

### SerializableYAMLParser ([java.io.Serializable](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html))
Convert an object that **implements Serializable** into a **Base64 string**.

**Conversion:**
```yaml
object: rO0ABXQAGFlvdSBzaG91bGQgbm90IHNlZSB0aGlzIQ==
```

### CallableYAMLParser (&#60;T&#62;)
This parser allows defining **custom objects** to be **parsed**. Say you have a **Person** class:
```java
class Person {
    String name;
    int age;
}
```
All you have to do is invoke the **CallableYAMLParser** constructor:
```java
public CallableYAMLParser(Class<T> tClass, FunctionException<ConfigurationSection, T> function);
```
and pass it the **target class** and a **function** that returns an **empty Person**.
The parser will automatically **save every field** and **load them** from file.

**Conversion:**
```yaml
# Vary, in the previous example:
person:
  name: Alexander
  age: 10
```

### Creating your own YAMLParser
While the [CallableYAMLParser](#callableyamlparser-t) is able to cover many cases, sometimes you may want more **control** for **saving** and **loading** custom objects.
In this section we will look into **creating** your own **YAMLParser** and loading it.

Let's start from creating a class that we are interested in:
```java
class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```
As you can see, this example is different from the one in [CallableYAMLParser](#callableyamlparser-t) because we cannot create an empty **Person**, nor set its variables later (since they are declared as **final**).

We can solve this problem by defining a new **PersonYAMLParser** which **extends YAMLParser**:
```java
class PersonYAMLParser extends YAMLParser<Person> {
    
}
```
To complete our parser, we need three ingredients:
- a **constructor**, that simply invokes the ```super()``` method and passes it our **class of interest**:
```java
    public PersonYAMLParser() {
        super(Person.class);
    }
```
- a **dumper**, which is a **function** responsible for **saving** the object that accepts **three parameters**: the **previous configuration**, the **path** where to save the object and the **object** itself.
In this case, we remove the previous object and stop if the new one is null. 
Otherwise, we create the corresponding section and save the desired into it:
```java
    @Override
    protected TriConsumer<@NotNull IConfiguration, @NotNull String, @NotNull Person> getDumper() {
        return (configuration, path, person) -> {
            if (configuration == null || path == null) return;
            if (person == null) return;
            ConfigurationSection personSection = configuration.createSection(path);
            personSection.set("name", person.getName());
            personSection.set("age", person.getAge());
        };
    }
``` 
- a **loader**, a **function** responsible for **loading** the object that accepts **two parameters**: the **previous configuration** and the **path**.
In this case, we try to retrieve the person section. If none is found, return null.
Otherwise, load the name and the age and create a new person with these values (since we are using a **BiFunctionException**, every **exception** will be thrown as a **RuntimeException**. 
**It is up to the user to handle any exception accordingly**, for example if age was null):
```java
    @Override
    protected BiFunctionException<@NotNull IConfiguration, @NotNull String, @Nullable Person> getLoader() {
        return (configuration, path) -> {
            if (configuration == null || path == null) return null;
            ConfigurationSection personSection = configuration.getConfigurationSection(path);
            if (personSection == null) return null;
            String name = personSection.getString("name");
            // You should always use wrapper types.
            // In case the getInteger method returns null, using
            // "int" as type will throw an error.
            Integer age = personSection.getInteger("age");
            return new Person(name, age);
        };
    }
```

Now, all you have to do is simply **add** this bit of code in your **main class**, **before loading or saving any object**:
```java
FileConfiguration.addParsers(new PersonYAMLParser());
```
If you have **more** than one parser, but they all share the **same package**, you can also use:
```java
FileConfiguration.addParsers(packageName);
```
That's it! You successfully created your own YAML parser, and it is now ready to use. 