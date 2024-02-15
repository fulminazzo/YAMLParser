package it.fulminazzo.yamlparser.configuration;

import it.fulminazzo.fulmicollection.utils.EnumUtils;
import it.fulminazzo.fulmicollection.utils.ReflectionUtils;
import it.fulminazzo.yamlparser.configuration.checkers.ConfigurationChecker;
import it.fulminazzo.yamlparser.configuration.exceptions.CannotBeNullException;
import it.fulminazzo.yamlparser.configuration.exceptions.UnexpectedClassException;
import it.fulminazzo.yamlparser.exceptions.YAMLException;
import it.fulminazzo.yamlparser.parsers.YAMLParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The interface Configuration.
 */
@SuppressWarnings({"unchecked", "unused"})
public interface IConfiguration extends Serializable {

    /**
     * Gets root.
     *
     * @return the root configuration
     */
    default @NotNull IConfiguration getRoot() {
        IConfiguration section = this;
        while (section.getParent() != null) section = section.getParent();
        return section;
    }

    /**
     * Gets keys.
     *
     * @return the keys
     */
    default @NotNull Set<String> getKeys() {
        return getKeys(false);
    }

    /**
     * Gets keys.
     *
     * @param deep if true, gets keys from subsections.
     * @return the keys
     */
    default @NotNull Set<String> getKeys(boolean deep) {
        Map<String, Object> map = toMap();
        List<String> keys = new ArrayList<>(map.keySet());
        if (deep)
            for (Map.Entry<String, Object> entry : new ArrayList<>(map.entrySet())) {
                Object value = entry.getValue();
                if (value instanceof IConfiguration) {
                    keys.addAll(((IConfiguration) value).getKeys(true).stream()
                            .map(c -> entry.getKey() + "." + c).collect(Collectors.toList()));
                }
            }
        return new HashSet<>(keys);
    }

    /**
     * Gets values.
     *
     * @return the values
     */
    default @NotNull Map<String, Object> getValues() {
        return getValues(false);
    }

    /**
     * Gets values.
     *
     * @param deep if true, gets values from subsections.
     * @return the values
     */
    default @NotNull Map<String, Object> getValues(boolean deep) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : toMap().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof IConfiguration)
                if (deep) value = ((IConfiguration) value).getValues(true);
                else continue;
            result.put(entry.getKey(), value);
        }
        return result;
    }

    /**
     * Checks if an object is present at the given path.
     *
     * @param path the path
     * @return true if an object is found.
     */
    default boolean contains(@Nullable String path) {
        if (path == null) return false;
        List<String> sectionPath = parseSectionPath(path);
        if (sectionPath.isEmpty()) return toMap().containsKey(path);
        else {
            String p = sectionPath.get(0);
            IConfiguration section = getConfigurationSection(p);
            return section != null && section.contains(removeFirstPath(path));
        }
    }

    /**
     * Creates a configuration section.
     *
     * @param path the path
     * @return the configuration section
     */
    default ConfigurationSection createSection(@NotNull String path) {
        return createSection(path, null);
    }

    /**
     * Creates a configuration section and fills it with the given map.
     *
     * @param path the path
     * @param map  the map
     * @return the configuration section
     */
    default ConfigurationSection createSection(@NotNull String path, @Nullable Map<?, ?> map) {
        List<String> sectionPath = parseSectionPath(path);
        if (sectionPath.isEmpty()) {
            ConfigurationSection section = new ConfigurationSection(this, path);
            toMap().put(path, section);
            if (map != null) map.forEach((k, v) -> section.set(k.toString(), v));
            return section;
        } else {
            String p = sectionPath.get(0);
            IConfiguration section = getConfigurationSection(p);
            if (section == null) section = createSection(p);
            return section.createSection(removeFirstPath(path), map);
        }
    }

    /**
     * Sets an object to the given path.
     *
     * @param <O>  the type of the object
     * @param path the path
     * @param o    the object
     */
    default <O> void set(@NotNull String path, @Nullable O o) {
        path = unquote(path);
        List<String> sectionPath = parseSectionPath(path);
        IConfiguration section = this;
        if (!sectionPath.isEmpty()) {
            String p = String.join(".", sectionPath);
            section = getConfigurationSection(p);
            if (section == null) section = createSection(p);
            path = getNameFromPath(path);
        }
        if (o == null) section.toMap().remove(path);
        else {
            YAMLParser<O> parser = (YAMLParser<O>) FileConfiguration.getParser(o.getClass());
            if (!isPrimitiveOrWrapper(o) && parser != null)
                try {parser.dump(section, path, o);}
                catch (NullPointerException ignored) {}
            else section.toMap().put(path, o);
        }
    }

    /**
     * Converts the given object using its associated YAML parser (if found).
     *
     * @param <T>    the type of the object
     * @param path   the path
     * @param object the object
     * @param clazz  the class of the object
     * @return the final object
     */
    default <T> @Nullable T convertObjectToYAMLObject(@Nullable String path, @Nullable Object object, @Nullable Class<T> clazz) {
        if (object == null) return null;
        if (clazz != null) {
            if (isPrimitiveOrWrapper(clazz)) {
                if (clazz.isAssignableFrom(object.getClass())) return (T) object;
                if (clazz.isAssignableFrom(Character.class)) {
                    String objectString = object.toString();
                    if (objectString.isEmpty()) return (T) new Character((char) 0);
                    return (T) new Character(objectString.charAt(0));
                }
                try {
                    Class<?> tmp = ReflectionUtils.getWrapperClass(clazz);
                    object = object.toString();
                    if (tmp.equals(String.class)) return (T) object;
                    Method method = tmp.getMethod("valueOf", object.getClass());
                    if (Number.class.isAssignableFrom(tmp) && object.toString().contains("E"))
                        object = new BigDecimal(object.toString()).toBigInteger().toString();
                    if (tmp.equals(Integer.class)) {
                        String str = object.toString();
                        final Matcher matcher = Pattern.compile(".*(\\.0+)").matcher(str);
                        if (matcher.matches()) str = str.substring(0, (str.length()) - matcher.group(1).length());
                        object = str;
                    }
                    return (T) method.invoke(tmp, object);
                } catch (Exception e) {
                    throwException(path, object, e);
                }
            }
            if (clazz.getCanonicalName().equals("java.util.Arrays.ArrayList") && object instanceof List)
                return (T) new ArrayList<>((Collection<?>) object);
        }
        return convertObjectToYAMLObject(path, object, Collections.singletonList(FileConfiguration.getParser(clazz)));
    }

    /**
     * Converts the given object using its associated YAML parser (if found).
     *
     * @param <T>     the type of the object
     * @param path    the path
     * @param object  the object
     * @param parsers the parsers
     * @return the final object
     */
    default <T> @Nullable T convertObjectToYAMLObject(@Nullable String path, @Nullable Object object,
                                                      @NotNull List<YAMLParser<?>> parsers) {
        if (path == null || object == null) return (T) object;
        for (YAMLParser<?> parser : parsers)
            if (parser != null)
                try {
                    return (T) parser.load(this, path);
                } catch (NullPointerException | IllegalArgumentException | UnexpectedClassException |
                         CannotBeNullException e) {
                    if (parsers.size() == 1) {
                        if (e instanceof IllegalArgumentException) throw e;
                        throwException(path, object, e);
                    }
                } catch (Exception e) {
                    if (e instanceof RuntimeException) throw (RuntimeException) e;
                    throwException(path, object, e);
                }
        return (T) object;
    }

    /**
     * Gets configuration section.
     *
     * @param <C>  the type of the section
     * @param path the path
     * @return the configuration section
     */
    default <C extends IConfiguration> @Nullable C getConfigurationSection(@NotNull String path) {
        return (C) get(path, IConfiguration.class);
    }

    /**
     * Check if is configuration section.
     *
     * @param path the path
     * @return the configuration section
     */
    default boolean isConfigurationSection(@NotNull String path) {
        return is(path, IConfiguration.class);
    }

    /**
     * Gets enum.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param eClass the e class
     * @return the enum
     */
    default <E extends Enum<E>> @Nullable E getEnum(@NotNull String path, @NotNull Class<E> eClass) {
        return getEnum(path, null, eClass);
    }

    /**
     * Gets enum.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param def    the def
     * @param eClass the e class
     * @return the enum
     */
    default <E extends Enum<E>> @Nullable E getEnum(@NotNull String path, E def, @NotNull Class<E> eClass) {
        String name = getString(path);
        if (name == null) return null;
        E e = EnumUtils.valueOf(eClass, name);
        if (e == null) e = def;
        if (e == null && checkNonNull()) throw new CannotBeNullException(getCurrentPath(), name, name);
        return e;
    }

    /**
     * Check if is enum.
     *
     * @param <E>    the type parameter
     * @param path   the path
     * @param eClass the e class
     * @return the enum
     */
    default <E extends Enum<E>> boolean isEnum(@NotNull String path, @NotNull Class<E> eClass) {
        return is(path, eClass);
    }

    /**
     * Gets uuid.
     *
     * @param path the path
     * @return the uuid
     */
    default @Nullable UUID getUUID(@NotNull String path) {
        return getUUID(path, null);
    }

    /**
     * Gets uuid.
     *
     * @param path the path
     * @param def  the def
     * @return the uuid
     */
    default @Nullable UUID getUUID(@NotNull String path, @Nullable UUID def) {
        return get(path, def, UUID.class);
    }

    /**
     * Check if is uuid.
     *
     * @param path the path
     * @return the uuid
     */
    default boolean isUUID(@NotNull String path) {
        return is(path, UUID.class);
    }

    /**
     * Gets date.
     *
     * @param path the path
     * @return the date
     */
    default @Nullable Date getDate(@NotNull String path) {
        return getDate(path, null);
    }

    /**
     * Gets date.
     *
     * @param path the path
     * @param def  the def
     * @return the date
     */
    default @Nullable Date getDate(@NotNull String path, @Nullable Date def) {
        return get(path, def, Date.class);
    }

    /**
     * Check if is date.
     *
     * @param path the path
     * @return the date
     */
    default boolean isDate(@NotNull String path) {
        return is(path, Date.class);
    }

    /**
     * Gets string.
     *
     * @param path the path
     * @return the string
     */
    default @Nullable String getString(@NotNull String path) {
        return getString(path, null);
    }

    /**
     * Gets string.
     *
     * @param path the path
     * @param def  the def
     * @return the string
     */
    default @Nullable String getString(@NotNull String path, @Nullable String def) {
        return get(path, def, String.class);
    }

    /**
     * Check if is string.
     *
     * @param path the path
     * @return the string
     */
    default boolean isString(@NotNull String path) {
        return is(path, String.class);
    }

    /**
     * Gets integer.
     *
     * @param path the path
     * @return the integer
     */
    default @Nullable Integer getInteger(@NotNull String path) {
        return getInteger(path, null);
    }

    /**
     * Gets integer.
     *
     * @param path the path
     * @param def  the def
     * @return the integer
     */
    default @Nullable Integer getInteger(@NotNull String path, @Nullable Integer def) {
        return get(path, def, Integer.class);
    }

    /**
     * Check if is integer.
     *
     * @param path the path
     * @return the integer
     */
    default boolean isInteger(@NotNull String path) {
        return is(path, Integer.class);
    }

    /**
     * Gets double.
     *
     * @param path the path
     * @return the double
     */
    default @Nullable Double getDouble(@NotNull String path) {
        return getDouble(path, null);
    }

    /**
     * Gets double.
     *
     * @param path the path
     * @param def  the def
     * @return the double
     */
    default @Nullable Double getDouble(@NotNull String path, @Nullable Double def) {
        return get(path, def, Double.class);
    }

    /**
     * Check if is double.
     *
     * @param path the path
     * @return the double
     */
    default boolean isDouble(@NotNull String path) {
        return is(path, Double.class);
    }

    /**
     * Gets float.
     *
     * @param path the path
     * @return the float
     */
    default @Nullable Float getFloat(@NotNull String path) {
        return getFloat(path, null);
    }

    /**
     * Gets float.
     *
     * @param path the path
     * @param def  the def
     * @return the float
     */
    default @Nullable Float getFloat(@NotNull String path, @Nullable Float def) {
        return get(path, def, Float.class);
    }

    /**
     * Check if is float.
     *
     * @param path the path
     * @return the float
     */
    default boolean isFloat(@NotNull String path) {
        return is(path, Float.class);
    }

    /**
     * Gets long.
     *
     * @param path the path
     * @return the long
     */
    default @Nullable Long getLong(@NotNull String path) {
        return getLong(path, null);
    }

    /**
     * Gets long.
     *
     * @param path the path
     * @param def  the def
     * @return the long
     */
    default @Nullable Long getLong(@NotNull String path, @Nullable Long def) {
        return get(path, def, Long.class);
    }

    /**
     * Check if is long.
     *
     * @param path the path
     * @return the long
     */
    default boolean isLong(@NotNull String path) {
        return is(path, Long.class);
    }

    /**
     * Gets short.
     *
     * @param path the path
     * @return the short
     */
    default @Nullable Short getShort(@NotNull String path) {
        return getShort(path, null);
    }

    /**
     * Gets short.
     *
     * @param path the path
     * @param def  the def
     * @return the short
     */
    default @Nullable Short getShort(@NotNull String path, @Nullable Short def) {
        return get(path, def, Short.class);
    }

    /**
     * Check if is short.
     *
     * @param path the path
     * @return the short
     */
    default boolean isShort(@NotNull String path) {
        return is(path, Short.class);
    }

    /**
     * Gets boolean.
     *
     * @param path the path
     * @return the boolean
     */
    default @Nullable Boolean getBoolean(@NotNull String path) {
        return getBoolean(path, null);
    }

    /**
     * Gets boolean.
     *
     * @param path the path
     * @param def  the def
     * @return the boolean
     */
    default @Nullable Boolean getBoolean(@NotNull String path, @Nullable Boolean def) {
        return get(path, def, Boolean.class);
    }

    /**
     * Check if is boolean.
     *
     * @param path the path
     * @return the boolean
     */
    default boolean isBoolean(@NotNull String path) {
        return is(path, Boolean.class);
    }

    /**
     * Gets character.
     *
     * @param path the path
     * @return the character
     */
    default @Nullable Character getCharacter(@NotNull String path) {
        return getCharacter(path, null);
    }

    /**
     * Gets character.
     *
     * @param path the path
     * @param def  the def
     * @return the character
     */
    default @Nullable Character getCharacter(@NotNull String path, @Nullable Character def) {
        return get(path, def, Character.class);
    }

    /**
     * Check if is character.
     *
     * @param path the path
     * @return the character
     */
    default boolean isCharacter(@NotNull String path) {
        return is(path, Character.class);
    }

    /**
     * Gets byte.
     *
     * @param path the path
     * @return the byte
     */
    default @Nullable Byte getByte(@NotNull String path) {
        return getByte(path, null);
    }

    /**
     * Gets byte.
     *
     * @param path the path
     * @param def  the def
     * @return the byte
     */
    default @Nullable Byte getByte(@NotNull String path, @Nullable Byte def) {
        return get(path, def, Byte.class);
    }

    /**
     * Check if is byte.
     *
     * @param path the path
     * @return the byte
     */
    default boolean isByte(@NotNull String path) {
        return is(path, Byte.class);
    }

    /**
     * Gets object.
     *
     * @param path the path
     * @return the object
     */
    default @Nullable Object getObject(@NotNull String path) {
        return getObject(path, null);
    }

    /**
     * Gets object.
     *
     * @param path the path
     * @param def  the def
     * @return the object
     */
    default @Nullable Object getObject(@NotNull String path, @Nullable Object def) {
        return get(path, def, Object.class);
    }

    /**
     * Gets enum list.
     *
     * @param <E>    the type of the enum
     * @param path   the path
     * @param eClass the e class
     * @return the enum list
     */
    default <E extends Enum<E>> @Nullable List<E> getEnumList(@NotNull String path, @NotNull Class<? extends E> eClass) {
        return (List<E>) getList(path, eClass);
    }

    /**
     * Gets uuid list.
     *
     * @param path the path
     * @return the uuid list
     */
    default @Nullable List<UUID> getUUIDList(@NotNull String path) {
        return getList(path, UUID.class);
    }

    /**
     * Gets date list.
     *
     * @param path the path
     * @return the date list
     */
    default @Nullable List<Date> getDateList(@NotNull String path) {
        return getList(path, Date.class);
    }

    /**
     * Gets string list.
     *
     * @param path the path
     * @return the string list
     */
    default @Nullable List<String> getStringList(@NotNull String path) {
        return getList(path, String.class);
    }

    /**
     * Gets integer list.
     *
     * @param path the path
     * @return the integer list
     */
    default @Nullable List<Integer> getIntegerList(@NotNull String path) {
        return getList(path, Integer.class);
    }

    /**
     * Gets double list.
     *
     * @param path the path
     * @return the double list
     */
    default @Nullable List<Double> getDoubleList(@NotNull String path) {
        return getList(path, Double.class);
    }

    /**
     * Gets float list.
     *
     * @param path the path
     * @return the float list
     */
    default @Nullable List<Float> getFloatList(@NotNull String path) {
        return getList(path, Float.class);
    }

    /**
     * Gets long list.
     *
     * @param path the path
     * @return the long list
     */
    default @Nullable List<Long> getLongList(@NotNull String path) {
        return getList(path, Long.class);
    }

    /**
     * Gets short list.
     *
     * @param path the path
     * @return the short list
     */
    default @Nullable List<Short> getShortList(@NotNull String path) {
        return getList(path, Short.class);
    }

    /**
     * Gets boolean list.
     *
     * @param path the path
     * @return the boolean list
     */
    default @Nullable List<Boolean> getBooleanList(@NotNull String path) {
        return getList(path, Boolean.class);
    }

    /**
     * Gets character list.
     *
     * @param path the path
     * @return the character list
     */
    default @Nullable List<Character> getCharacterList(@NotNull String path) {
        return getList(path, Character.class);
    }

    /**
     * Gets byte list.
     *
     * @param path the path
     * @return the byte list
     */
    default @Nullable List<Byte> getByteList(@NotNull String path) {
        return getList(path, Byte.class);
    }

    /**
     * Gets object list.
     *
     * @param path the path
     * @return the object list
     */
    default @Nullable List<?> getObjectList(@NotNull String path) {
        return getObjectList(path, null);
    }

    /**
     * Gets object list.
     *
     * @param path the path
     * @param def  the def
     * @return the object list
     */
    default @Nullable List<?> getObjectList(@NotNull String path, @Nullable List<?> def) {
        return get(path, def, List.class);
    }

    /**
     * Check if is list.
     *
     * @param path the path
     * @return the list
     */
    default boolean isList(@NotNull String path) {
        return is(path, List.class);
    }

    /**
     * Gets list.
     *
     * @param <T>   the type of the elements in the list
     * @param path  the path
     * @param clazz the clazz
     * @return the list
     */
    default <T> @Nullable List<T> getList(@NotNull String path, @NotNull Class<T> clazz) {
        List<?> list = getObjectList(path);
        if (list == null) return null;
        return list.stream()
                .filter(Objects::nonNull)
                .map(o -> clazz.isAssignableFrom(o.getClass()) ? clazz.cast(o) :
                        convertObjectToYAMLObject(path, o, clazz))
                .filter(o -> check(path, o, clazz))
                .collect(Collectors.toList());
    }

    /**
     * Gets an object of the specified type
     * and checks if it is valid.
     *
     * @param <T>   the type of the object
     * @param path  the path
     * @param clazz the class
     * @return the result
     */
    default <T> @Nullable T get(@NotNull String path, @NotNull Class<T> clazz) {
        return get(path, null, clazz);
    }

    /**
     * Gets an object of the specified type
     * and checks if it is valid. If is null,
     * return the def argument.
     *
     * @param <T>   the type of the object
     * @param path  the path
     * @param def   the default object
     * @param clazz the class
     * @return the result
     */
    default <T> @Nullable T get(@NotNull String path, @Nullable T def, @NotNull Class<T> clazz) {
        List<String> parsedPath = parseSectionPath(path);
        if (parsedPath.isEmpty()) {
            Object object = toMap().get(path);
            if (object == null) object = def;
            object = convertObjectToYAMLObject(path, object, clazz);
            check(path, object, clazz);
            return (T) object;
        } else {
            IConfiguration section = getConfigurationSection(parsedPath.get(0));
            if (section == null) return def;
            else return section.get(removeFirstPath(path), def, clazz);
        }
    }

    /**
     * Checks if the object at the given
     * path is of the given class.
     *
     * @param <T>   the type of the class
     * @param path  the path
     * @param clazz the class
     * @return true if is an instance of class
     */
    default <T> boolean is(@NotNull String path, @NotNull Class<T> clazz) {
        try {
            T t = get(path, clazz);
            return t != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the given Object is of the
     * expected class and if is not null (or
     * nullability is set to false).
     *
     * @param <T>    the type of the class
     * @param name   the name
     * @param object the object
     * @param clazz  the class
     * @return true if no exceptions are thrown
     */
    default <T> boolean check(@NotNull String name, @Nullable Object object, @NotNull Class<T> clazz) {
        // java.util.ArrayList != java.util.Arrays.ArrayList
        if (clazz.getCanonicalName().equals(Arrays.class.getCanonicalName() + ".ArrayList"))
            clazz = (Class<T>) ArrayList.class;
        if (object == null && checkNonNull()) throw new CannotBeNullException(getCurrentPath(), name, name);
        if (object == null) return true;
        if (clazz.isAssignableFrom(object.getClass())) return true;
        Class<?> tmp = ReflectionUtils.getPrimitiveClass(clazz);
        if (tmp != null && tmp.isAssignableFrom(ReflectionUtils.getPrimitiveClass(object.getClass()))) return true;
        throw new UnexpectedClassException(getCurrentPath(), name, object, clazz);
    }

    /**
     * Compares this configuration with another.
     *
     * @param configuration the configuration
     * @param ignore        the keys to ignore during checking
     * @return the result of the comparison as configuration checker
     */
    default @NotNull ConfigurationChecker compare(@NotNull IConfiguration configuration, @Nullable String... ignore) {
        return new ConfigurationChecker(this, configuration, ignore);
    }

    /**
     * Removes the first element from the path.
     *
     * @param path the path
     * @return the string
     */
    default @NotNull String removeFirstPath(@NotNull String path) {
        String[] tmp = path.split("\\.");
        return String.join(".", Arrays.copyOfRange(tmp, 1, tmp.length));
    }

    /**
     * Gets name from the path.
     *
     * @param path the path
     * @return the name
     */
    default String getNameFromPath(@NotNull String path) {
        String[] tmp = path.split("\\.");
        return tmp[tmp.length - 1];
    }

    /**
     * Converts a path into a list of elements
     * and removes the last element.
     *
     * @param path the path
     * @return the list
     */
    default @NotNull List<String> parseSectionPath(@NotNull String path) {
        List<String> parsedPath = parsePath(path);
        if (!parsedPath.isEmpty()) parsedPath.remove(parsedPath.size() - 1);
        return parsedPath;
    }

    /**
     * Converts a path into a list of elements.
     *
     * @param path the path
     * @return the list
     */
    default @NotNull List<String> parsePath(@NotNull String path) {
        List<String> list = new ArrayList<>();
        Matcher matcher = Pattern.compile("((?:\\\\\\.|[^.])+)(?:\\.|$)").matcher(path);
        while (matcher.find())
            list.add(matcher.group(1));
        return list;
    }

    /**
     * Gets the current path.
     *
     * @return the current path
     */
    default String getCurrentPath() {
        StringBuilder path = new StringBuilder(getName());
        IConfiguration parent = getParent();
        while (parent != null) {
            String parentName = parent.getName();
            if (!parentName.isEmpty()) path.insert(0, parentName + ".");
            parent = parent.getParent();
        }
        return path.toString();
    }

    default void throwException(String path, Object object, Throwable e) {
        if (e instanceof RuntimeException || e instanceof InvocationTargetException) e = e.getCause();
        path = path == null ? "null" : path;
        String currentPath = getCurrentPath();
        if (currentPath != null && !currentPath.isEmpty()) path = currentPath + "." + path;
        throw new YAMLException(path, object, e.getClass().getSimpleName() + " " + e.getMessage());
    }

    /**
     * Sets the nullability of the configuration.
     * If set to true, the plugin will not accept null objects
     * when calling get methods.
     *
     * @param nonNull the non-null boolean
     */
    default void setNonNull(boolean nonNull) {
        toMap().values().stream()
                .filter(v -> v instanceof IConfiguration)
                .map(v -> ((IConfiguration) v))
                .forEach(v -> v.setNonNull(nonNull));
    }

    /**
     * Checks if an object is primitive or wrapper.
     *
     * @param object the object
     * @return true if is null, primitive, wrapper or a string
     */
    static boolean isPrimitiveOrWrapper(@Nullable Object object) {
        if (object instanceof List) {
            List<?> list = (List<?>) object;
            if (list.isEmpty()) return true;
            else object = list.stream().filter(Objects::nonNull).findFirst().orElse(null);
        }
        return object != null && isPrimitiveOrWrapper(object.getClass());
    }

    /**
     * Checks if a class is primitive or wrapper.
     *
     * @param clazz the class
     * @return true if is null, primitive, wrapper or a string
     */
    static boolean isPrimitiveOrWrapper(@Nullable Class<?> clazz) {
        if (clazz == null) return false;
        return ReflectionUtils.isPrimitiveOrWrapper(clazz) || clazz.equals(String.class);
    }

    /**
     * Print the current configuration keys and values.
     */
    default void print() {
        System.out.println(this);
        System.out.println(toString(""));
    }

    /**
     * To string method.
     *
     * @param start the head start for the print
     * @return the result string
     */
    default @NotNull String toString(@Nullable String start) {
        if (start == null) start = "";
        StringBuilder result = new StringBuilder();
        Map<String, Object> map = toMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result.append(String.format("%s%s: ", start, key));
            if (value instanceof IConfiguration) {
                result.append("\n");
                result.append(((IConfiguration) value).toString(start + "  "));
            } else result.append(String.format("%s\n", value));
        }
        return result.toString();
    }

    /**
     * Converts a general map to a configuration map.
     *
     * @param parent the parent configuration.
     * @param map    the general map
     * @return the configuration map
     */
    static @NotNull Map<String, Object> generalToConfigMap(@NotNull IConfiguration parent, @Nullable Map<?, ?> map) {
        LinkedHashMap<String, Object> treeMap = new LinkedHashMap<>();
        if (map == null) return treeMap;
        map.forEach((k, v) -> {
            final String key = unquote(k.toString().replace(".", "\\."));
            if (v instanceof Map) treeMap.put(key, new ConfigurationSection(parent, key, (Map<?, ?>) v));
            else {
                if (v instanceof List) {
                    List<Object> list = (List<Object>) v;
                    for (int i = 0; i < list.size(); i++) {
                        Object v2 = list.get(i);
                        if (v2 instanceof Map)
                            list.set(i, new ConfigurationSection(parent, String.valueOf(i), (Map<?, ?>) v2));
                    }
                }
                treeMap.put(key, v);
            }
        });
        return treeMap;
    }

    /**
     * Converts a configuration map to a general map.
     *
     * @param config the configuration
     * @return the general map
     */
    static @Nullable Map<String, Object> configToGeneralMap(@Nullable IConfiguration config) {
        if (config == null) return null;
        LinkedHashMap<String, Object> treeMap = new LinkedHashMap<>();
        Map<String, Object> map = config.toMap();
        map.forEach((k, v) -> {
            k = unquote(k);
            k = k.replace("\\.", ".");
            if (v instanceof IConfiguration) v = configToGeneralMap((IConfiguration) v);
            else if (v instanceof List) {
                List<Object> list = new LinkedList<>((Collection<?>) v);
                for (int i = 0; i < list.size(); i++) {
                    Object v2 = list.get(i);
                    if (v2 instanceof ConfigurationSection)
                        list.set(i, configToGeneralMap((IConfiguration) v2));
                }
                v = list;
            }
            treeMap.put(k, v);
        });
        return treeMap;
    }

    static String unquote(String string) {
        if (string == null) return null;
        while (string.length() > 2 && string.startsWith("\"") && string.endsWith("\""))
            string = string.substring(1, string.length() - 1);
        while (string.length() > 2 && string.startsWith("'") && string.endsWith("'"))
            string = string.substring(1, string.length() - 1);
        return string;
    }

    /**
     * Check if nullability is allowed.
     *
     * @return the boolean
     */
    boolean checkNonNull();

    /**
     * Gets the current configuration name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the parent configuration.
     *
     * @return the parent
     */
    @Nullable IConfiguration getParent();

    /**
     * Converts the current configuration to a map.
     *
     * @return the map
     */
    Map<String, Object> toMap();
}
