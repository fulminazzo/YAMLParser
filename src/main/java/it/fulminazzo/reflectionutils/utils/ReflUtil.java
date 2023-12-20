package it.fulminazzo.reflectionutils.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
@SuppressWarnings({"unchecked", "unused"})
public class ReflUtil {
    public static <O> Class<O> getClass(String className, Class<?>... paramTypes) {
        try {
            return (Class<O>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            Class<O> aClazz = getInnerClass(className, paramTypes);
            if (aClazz == null) aClazz = getInnerInterface(className);
            return aClazz;
        }
    }

    public static <O> Class<O> getInnerClass(String classPath, Class<?>... paramTypes) {
        Class<O> aClass = null;
        String[] tmp = classPath.split("\\.");
        StringBuilder primClass = new StringBuilder();
        String clazz = tmp[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) primClass.append(tmp[i]).append(".");
        if (primClass.toString().endsWith("."))
            primClass = new StringBuilder(primClass.substring(0, primClass.length() - 1));
        try {
            Class<?> primClazz = Class.forName(primClass.toString());
            aClass = (Class<O>) Stream.concat(Arrays.stream(primClazz.getClasses()), Arrays.stream(primClazz.getDeclaredClasses()))
                    .distinct()
                    .filter(c -> c.getSimpleName().equals(clazz))
                    .filter(c -> c.isInterface() || getConstructor(c, paramTypes) != null)
                    .findAny().orElse(null);
        } catch (ClassNotFoundException ignored) {}
        return aClass;
    }

    public static <O> Class<O> getInnerInterface(String classPath) {
        Class<O> aClass = null;
        String[] tmp = classPath.split("\\.");
        StringBuilder primClass = new StringBuilder();
        String clazz = tmp[tmp.length - 1];
        for (int i = 0; i < tmp.length - 1; i++) primClass.append(tmp[i]).append(".");
        if (primClass.toString().endsWith("."))
            primClass = new StringBuilder(primClass.substring(0, primClass.length() - 1));
        try {
            Class<?> primClazz = Class.forName(primClass.toString());
            aClass = (Class<O>) Arrays.stream(primClazz.getInterfaces())
                    .filter(c -> c.getSimpleName().equals(clazz))
                    .findAny().orElse(null);
        } catch (ClassNotFoundException ignored) {}
        return aClass;
    }

    public static Field[] getDeclaredFields(Class<?> aClass) {
        return Arrays.stream(getClassAndSuperClasses(aClass))
                .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                .toArray(Field[]::new);
    }

    public static Field getField(Class<?> aClass, String name) {
        return Arrays.stream(getClassAndSuperClasses(aClass)).map(c -> {
            try {
                return c.getField(name);
            } catch (NoSuchFieldException e) {
                try {
                    return c.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    return null;
                }
            }
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static Field getFieldNameless(Class<?> aClass, Class<?> type) {
        if (type == null) return null;
        return Arrays.stream(getClassAndSuperClasses(aClass)).map(c ->
                Stream.concat(Arrays.stream(c.getFields()), Arrays.stream(c.getDeclaredFields()))
                        .filter(f -> f.getType().equals(type)).findFirst().orElse(null)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static Field getFieldNameless(Class<?> aClass, String typeName) {
        if (typeName == null) return null;
        return Arrays.stream(getClassAndSuperClasses(aClass)).map(c ->
                Stream.concat(Arrays.stream(c.getFields()), Arrays.stream(c.getDeclaredFields()))
                        .filter(f -> f.getType().getSimpleName().equals(typeName)).findFirst().orElse(null)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public static Method getMethod(Class<?> aClass, String name, Class<?> returnType, Object... objects) {
        return getMethod(aClass, name, returnType, objectsToClasses(objects));
    }

    public static Method getMethod(Class<?> aClass, String name, Class<?> returnType, Class<?>... classes) {
        Method method = null;
        if (aClass != null)
            for (Class<?> clz : getClassAndSuperClasses(aClass)) {
                method = getMethodRecursive(clz, name, returnType, new LinkedList<>(),
                        new LinkedList<>(Arrays.stream(classes)
                                .map(ReflUtil::getClassAndSuperClasses)
                                .collect(Collectors.toList())));
                if (method != null) break;
            }
        return method;
    }

    private static Method getMethodRecursive(Class<?> aClass, String name, Class<?> returnType, 
                                             LinkedList<Class<?>> savedClasses, LinkedList<Class<?>[]> remainingClasses) {
        LinkedList<Class<?>> tmp1 = new LinkedList<>(savedClasses);
        if (remainingClasses.isEmpty()) {
            return getMethodLambda(aClass, name, returnType, new LinkedList<>());
        } else if (remainingClasses.size() == 1) {
            Class<?>[] tmp2 = remainingClasses.get(0);
            for (Class<?> clz : tmp2) {
                tmp1.addLast(clz);
                Method method = getMethodLambda(aClass, name, returnType, tmp1);

                if (method == null) {
                    if (!tmp1.isEmpty()) tmp1.removeLast();
                } else return method;
            }
        } else {
            LinkedList<Class<?>[]> tmp2 = new LinkedList<>(remainingClasses);
            tmp2.removeFirst();
            for (Class<?> clz : remainingClasses.get(0)) {
                tmp1.addLast(clz);
                Method method = getMethodRecursive(aClass, name, returnType, tmp1, tmp2);
                if (method != null) return method;
                tmp1.removeLast();
            }
        }
        return null;
    }

    private static Method getMethodLambda(Class<?> aClass, String name, Class<?> returnType, LinkedList<Class<?>> tmp1) {
        return Stream.concat(Arrays.stream(aClass.getMethods()), Arrays.stream(aClass.getDeclaredMethods()))
                .filter(m -> name == null || m.getName().equalsIgnoreCase(name))
                .filter(m -> m.getParameterCount() == tmp1.size())
                .filter(m -> {
                    if (returnType == null) return true;
                    else return Arrays.stream(getClassAndSuperClasses(returnType)).anyMatch(c -> c.equals(m.getReturnType()));
                })
                .filter(m -> isValidExecutable(m, tmp1))
                .findFirst().orElse(null);
    }

    public static <O> Constructor<O> getConstructor(Class<O> aClass, Object... objects) {
        return getConstructor(aClass, objectsToClasses(objects));
    }

    public static <O> Constructor<O> getConstructor(Class<O> aClass, Class<?>... classes) {
        Constructor<O> constructor = null;
        if (aClass != null)
            for (Class<?> clz : getClassAndSuperClasses(aClass)) {
                constructor = (Constructor<O>) getConstructorRecursive(clz, new ArrayList<>(),
                        Arrays.stream(classes).map(ReflUtil::getClassAndSuperClasses).collect(Collectors.toList()));
                if (constructor != null) break;
            }
        return constructor;
    }

    private static <O> Constructor<O> getConstructorRecursive(Class<O> aClass, List<Class<?>> savedClasses,
                                                          List<Class<?>[]> remainingClasses) {
        if (remainingClasses.isEmpty()) {
            try {
                return aClass.getConstructor();
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        else if (remainingClasses.size() == 1) {
            LinkedList<Class<?>> tmp1 = new LinkedList<>(savedClasses);
            for (Class<?> clz : remainingClasses.get(0)) {
                tmp1.addLast(clz);
                Constructor<O> constructor = (Constructor<O>) Arrays.stream(aClass.getConstructors())
                        .filter(c -> c.getParameterCount() == tmp1.size())
                        .filter(c -> isValidExecutable(c, tmp1)).findFirst().orElse(null);
                if (constructor == null) tmp1.removeLast();
                else return constructor;
            }
        } else {
            LinkedList<Class<?>> tmp1 = new LinkedList<>(savedClasses);
            LinkedList<Class<?>[]> tmp2 = new LinkedList<>(remainingClasses);
            tmp2.removeFirst();
            for (Class<?> clz : remainingClasses.get(0)) {
                tmp1.addLast(clz);
                Constructor<O> constructor = getConstructorRecursive(aClass, tmp1, tmp2);
                if (constructor != null) return constructor;
                tmp1.removeLast();
            }
        }
        return null;
    }

    /*
        A method to check if a Constructor or a Method
        has valid parameters.
     */
    private static boolean isValidExecutable(Executable executable, LinkedList<Class<?>> parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            Class<?> t = parameters.get(i);
            if (t != null && executable.getParameterTypes()[i] != t && executable.getParameterTypes()[i] != ReflUtil.getPrimitiveClass(t))
                return false;
        }
        return true;
    }

    public static Class<?>[] getClassAndSuperClasses(Class<?> aClass) {
        if (aClass == null) return new Class<?>[]{null};
        return Stream.concat(Stream.of(getClassAndInterfaces(aClass)), Arrays.stream(getSuperClasses(aClass))).toArray(Class<?>[]::new);
    }

    public static Class<?>[] getClassAndInterfaces(Class<?> aClass) {
        if (aClass == null) return new Class<?>[]{null};
        return Stream.concat(Stream.of(aClass), Arrays.stream(aClass.getInterfaces())).toArray(Class<?>[]::new);
    }

    public static Class<?>[] getSuperClasses(Class<?> aClass) {
        LinkedList<Class<?>> classes = new LinkedList<>();
        Class<?> tmp = aClass == null ? null : aClass.getSuperclass();
        while (tmp != null) {
            Arrays.stream(getClassAndInterfaces(tmp)).forEach(classes::addLast);
            tmp = tmp.getSuperclass();
        }
        return classes.toArray(new Class[0]);
    }

    public static Class<?>[] objectsToClasses(Object... objects) {
        return Arrays.stream(objects).map(o -> o == null ? null : o.getClass()).toArray(Class<?>[]::new);
    }

    public static String classesToString(Class<?>... classes) {
        return classesToString(Arrays.asList(classes));
    }

    public static String classesToString(List<Class<?>> classes) {
        return classes.stream().map(c -> c == null ? "null" : c.toString()).collect(Collectors.joining(", "));
    }

    public static Class<?> getPrimitiveClass(Class<?> aClass) {
        if (aClass == null) return null;
        else if (aClass.equals(Boolean.class))
            return boolean.class;
        else if (aClass.equals(Byte.class))
            return byte.class;
        else if (aClass.equals(Short.class))
            return short.class;
        else if (aClass.equals(Character.class))
            return char.class;
        else if (aClass.equals(Integer.class))
            return int.class;
        else if (aClass.equals(Long.class))
            return long.class;
        else if (aClass.equals(Float.class))
            return float.class;
        else if (aClass.equals(Double.class))
            return double.class;
        return aClass;
    }

    public static boolean isPrimitiveOrWrapper(Class<?> aClass) {
        if (aClass == null) return false;
        for (Class<?> clazz : getWrapperClasses()) if (aClass.equals(clazz)) return true;
        if (aClass.equals(String.class)) return true;
        return isPrimitive(aClass);
    }

    public static boolean isPrimitive(Class<?> aClass) {
        if (aClass == null) return false;
        else if (aClass.equals(boolean.class)) return true;
        else if (aClass.equals(byte.class)) return true;
        else if (aClass.equals(short.class)) return true;
        else if (aClass.equals(char.class)) return true;
        else if (aClass.equals(int.class)) return true;
        else if (aClass.equals(long.class)) return true;
        else if (aClass.equals(float.class)) return true;
        else return aClass.equals(double.class);
    }

    public static Class<?>[] getWrapperClasses() {
        return new Class[]{Boolean.class, Byte.class, Short.class, Character.class,
                Integer.class, Long.class, Float.class, Double.class};
    }
}
