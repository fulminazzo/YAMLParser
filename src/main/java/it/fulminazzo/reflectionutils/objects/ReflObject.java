package it.fulminazzo.reflectionutils.objects;

import it.fulminazzo.reflectionutils.utils.ReflUtil;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
@SuppressWarnings({"unchecked", "unused"})
public class ReflObject<A> {
    protected final Class<A> aClass;
    protected final A object;
    protected boolean showErrors;

    public ReflObject() {
        this.showErrors = true;
        this.aClass = null;
        this.object = null;
    }

    public ReflObject(String classPath, Object... params) {
        this.showErrors = true;
        Class<A> aClass = null;
        A object = null;
        try {
            Class<?>[] paramTypes = ReflUtil.objectsToClasses(params);
            aClass = ReflUtil.getClass(classPath, paramTypes);
            if (aClass == null) throw new ClassNotFoundException(classPath);
            Constructor<A> constructor = ReflUtil.getConstructor(aClass, paramTypes);
            if (constructor == null) throw new NoSuchMethodException(String.format("Constructor not found %s(%s)", aClass.getName(),
                    ReflUtil.classesToString(paramTypes)));
            constructor.setAccessible(true);
            object = constructor.newInstance(params);
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        this.aClass = aClass;
        this.object = object;
    }

    public ReflObject(String classPath, Class<?>[] paramTypes, Object... params) {
        this.showErrors = true;
        Class<A> aClass = null;
        A object = null;
        try {
            aClass = ReflUtil.getClass(classPath, paramTypes);
            if (aClass == null) throw new ClassNotFoundException(classPath);
            Constructor<A> constructor = ReflUtil.getConstructor(aClass, paramTypes);
            if (constructor == null) throw new NoSuchMethodException("Constructor not found");
            constructor.setAccessible(true);
            object = constructor.newInstance(params);
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        this.aClass = aClass;
        this.object = object;
    }

    public ReflObject(Class<A> aClass, Object... params) {
        this.showErrors = true;
        A object = null;
        try {
            Class<?>[] paramTypes = ReflUtil.objectsToClasses(params);
            Constructor<A> constructor = ReflUtil.getConstructor(aClass, paramTypes);
            if (constructor == null) throw new NoSuchMethodException(String.format("Constructor not found %s(%s)", aClass.getName(),
                    ReflUtil.classesToString(paramTypes)));
            constructor.setAccessible(true);
            object = constructor.newInstance(params);
        } catch (NullPointerException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        this.aClass = aClass;
        this.object = object;
    }

    public ReflObject(Class<A> aClass, Class<?>[] paramTypes, Object... params) {
        this.showErrors = true;
        A object = null;
        try {
            Constructor<A> constructor = ReflUtil.getConstructor(aClass, paramTypes);
            if (constructor == null) throw new NoSuchMethodException("Constructor not found");
            constructor.setAccessible(true);
            object = constructor.newInstance(params);
        } catch (NullPointerException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        this.aClass = aClass;
        this.object = object;
    }

    public ReflObject(String classPath, boolean initiate) {
        this.showErrors = true;
        Class<A> aClass = null;
        A object = null;
        try {
            aClass = ReflUtil.getClass(classPath);
            if (aClass == null) throw new ClassNotFoundException(classPath);
            if (initiate) {
                Constructor<A> constructor = ReflUtil.getConstructor(aClass);
                if (constructor == null) throw new NoSuchMethodException("Constructor not found");
                constructor.setAccessible(true);
                object = constructor.newInstance();
            }
        } catch (NullPointerException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        }
        this.aClass = aClass;
        this.object = object;
    }

    public ReflObject(A object, Class<A> aClass) {
        this.showErrors = true;
        this.aClass = aClass;
        this.object = object;
    }

    public ReflObject(A object) {
        this.showErrors = true;
        this.aClass = object == null ? null : (Class<A>) object.getClass();
        this.object = object;
    }

    public Object[] getArray(ReflObject<?>... contents) {
        Object[] objects = Arrays.stream(contents)
                .map(o -> o.getObject() == null ? o.getaClass() : o.getObject())
                .toArray();
        return getArray(objects);
    }

    public Object[] getArray(Object... contents) {
        if (aClass == null) return null;
        Object[] array = getArray(contents.length);
        for (int i = 0; i < contents.length; i++) Array.set(array, i, contents[i]);
        return array;
    }

    public Object[] getArray(int size) {
        return (Object[]) Array.newInstance(aClass, size);
    }

    public Field getField(String name) {
        if (aClass == null) return null;
        try {
            Field field = ReflUtil.getField(aClass, name);
            if (field == null) throw new NoSuchFieldException(String.format("Field %s not found in class %s", name, aClass));
            return field;
        } catch (NoSuchFieldException e) {
            if (showErrors) e.printStackTrace();
            return null;
        }
    }

    public Field getFieldNameless(Class<?> type) {
        if (aClass == null) return null;
        try {
            Field field = ReflUtil.getFieldNameless(aClass, type);
            if (field == null) throw new NoSuchFieldException(String.format("Field of type %s not found in class %s", type, aClass));
            return field;
        } catch (NoSuchFieldException e) {
            if (showErrors) e.printStackTrace();
            return null;
        }
    }

    public Field getFieldNameless(String typeName) {
        if (aClass == null) return null;
        try {
            Field field = ReflUtil.getFieldNameless(aClass, typeName);
            if (field == null) throw new NoSuchFieldException(String.format("Field of type %s not found in class %s", typeName, aClass));
            return field;
        } catch (NoSuchFieldException e) {
            if (showErrors) e.printStackTrace();
            return null;
        }
    }

    public <O> void setField(String name, O object) {
        if (this.object == null) return;
        Field field = getField(name);
        if (field == null) return;
        try {
            field.setAccessible(true);
            field.set(this.object, object);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            if (showErrors) e.printStackTrace();
        }
    }

    public <O> ReflObject<O> obtainField(String name) {
        return obtainField(getField(name));
    }

    public <O> ReflObject<O> obtainFieldNameless(Class<?> type) {
        return obtainField(getFieldNameless(type));
    }

    public <O> ReflObject<O> obtainFieldNameless(String typeName) {
        return obtainField(getFieldNameless(typeName));
    }

    private <O> ReflObject<O> obtainField(Field field) {
        Object obj = object == null ? aClass : object;
        if (field == null) return new ReflObject<>();
        try {
            field.setAccessible(true);
            return new ReflObject<>((O) field.get(obj));
        } catch (IllegalAccessException e) {
            if (showErrors) e.printStackTrace();
            return new ReflObject<>();
        }
    }

    public <O> O getFieldObject(String name) {
        return (O) obtainField(name).getObject();
    }

    public <O> O getFieldObjectNameless(Class<?> type) {
        return (O) obtainFieldNameless(type).getObject();
    }

    public <O> O getFieldObjectNameless(String typeName) {
        return (O) obtainFieldNameless(typeName).getObject();
    }

    public List<Field> getFields() {
        List<Field> fields = new ArrayList<>();
        if (aClass != null)
            for (Class<?> clz : ReflUtil.getClassAndSuperClasses(aClass)) {
                Collections.addAll(fields, clz.getFields());
                Collections.addAll(fields, clz.getDeclaredFields());
            }
        return fields.stream().distinct().collect(Collectors.toList());
    }

    public Method getMethod(String name, Object... params) {
        if (aClass == null) return null;
        return getMethod(name, ReflUtil.objectsToClasses(params));
    }

    public Method getMethod(String name, Class<?>... paramTypes) {
        if (aClass == null) return null;
        try {
            Method method = ReflUtil.getMethod(aClass, name, null, paramTypes);
            if (method == null)
                throw new NoSuchMethodException(String.format("Method %s(%s) not found in class %s", name,
                        ReflUtil.classesToString(paramTypes), aClass));
            return method;
        } catch (NoSuchMethodException e) {
            if (showErrors) e.printStackTrace();
            return null;
        }
    }

    public <O> ReflObject<O> callMethod(String name, Object... params) {
        return callMethod(name, ReflUtil.objectsToClasses(params), params);
    }

    public <O> ReflObject<O> callMethod(String name, Class<?>[] paramTypes, Object... params) {
        Object obj = object == null ? aClass : object;
        Method method = getMethod(name, paramTypes);
        if (method == null) return new ReflObject<>();
        method.setAccessible(true);
        try {
            return new ReflObject<>((O) method.invoke(obj, params));
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            if (showErrors) e.printStackTrace();
            return new ReflObject<>();
        }
    }

    public <O> O getMethodObject(String name, Object... params) {
        return getMethodObject(name, ReflUtil.objectsToClasses(params), params);
    }

    public <O> O getMethodObject(String name, Class<?>[] paramTypes, Object... params) {
        ReflObject<O> reflObject = callMethod(name, paramTypes, params);
        return reflObject == null ? null : reflObject.getObject();
    }

    public Method getMethodFromReturnType(Class<?> returnType, Object... params) {
        return getMethodFromReturnType(returnType, ReflUtil.objectsToClasses(params));
    }

    public Method getMethodFromReturnType(Class<?> returnType, Class<?>... paramTypes) {
        if (aClass == null) return null;
        try {
            Method method = ReflUtil.getMethod(aClass, null, returnType, paramTypes);
            if (method == null)
                throw new NoSuchMethodException(String.format("Method %s(%s)->%s not found in class %s", "<?>", "<?>",
                        returnType, aClass));
            return method;
        } catch (NoSuchMethodException e) {
            if (showErrors) e.printStackTrace();
            return null;
        }
    }

    public <O> ReflObject<O> callMethodFromReturnType(Class<?> returnType, Object... params) {
        Object obj = object == null ? aClass : object;
        Method method = getMethodFromReturnType(returnType, params);
        return invokeMethod(obj, method, params);
    }

    public <O> O getMethodObjectFromReturnType(Class<?> returnType, Object... params) {
        ReflObject<O> reflObject = callMethodFromReturnType(returnType, params);
        return reflObject == null ? null : reflObject.getObject();
    }

    public Method getMethodNameless(Object... params) {
        if (aClass == null) return null;
        return getMethodNameless(ReflUtil.objectsToClasses(params));
    }

    public Method getMethodNameless(Class<?>... paramTypes) {
        if (aClass == null) return null;
        try {
            Method method = ReflUtil.getMethod(aClass, null, null, paramTypes);
            if (method == null)
                throw new NoSuchMethodException(String.format("Method %s(%s) not found in class %s", "",
                        ReflUtil.classesToString(paramTypes), aClass));
            return method;
        } catch (NoSuchMethodException e) {
            if (showErrors) e.printStackTrace();
            return null;
        }
    }

    public <O> ReflObject<O> callMethodNameless(Object... params) {
        return callMethodNameless(ReflUtil.objectsToClasses(params), params);
    }

    public <O> ReflObject<O> callMethodNameless(Class<?>[] paramTypes, Object... params) {
        Object obj = object == null ? aClass : object;
        Method method = getMethodNameless(paramTypes);
        return invokeMethod(obj, method, params);
    }

    private <O> ReflObject<O> invokeMethod(Object obj, Method method, Object[] params) {
        if (method == null) return new ReflObject<>();
        method.setAccessible(true);
        try {
            return new ReflObject<>((O) method.invoke(obj, params));
        } catch (IllegalAccessException | InvocationTargetException | NullPointerException e) {
            if (showErrors) e.printStackTrace();
            return new ReflObject<>();
        }
    }

    public <O> O getMethodNamelessObject(Object... params) {
        return getMethodNamelessObject(ReflUtil.objectsToClasses(params), params);
    }

    public <O> O getMethodNamelessObject(Class<?>[] paramTypes, Object... params) {
        ReflObject<O> reflObject = callMethodNameless(paramTypes, params);
        return reflObject == null ? null : reflObject.getObject();
    }

    public List<Method> getMethods() {
        List<Method> methods = new ArrayList<>();
        if (aClass != null)
            for (Class<?> clz : ReflUtil.getClassAndSuperClasses(aClass)) {
                Collections.addAll(methods, clz.getMethods());
                Collections.addAll(methods, clz.getDeclaredMethods());
            }
        return methods.stream().distinct().collect(Collectors.toList());
    }

    public void setShowErrors(boolean showErrors) {
        this.showErrors = showErrors;
    }

    public boolean isShowingErrors() {
        return showErrors;
    }

    public void printFields() {
        getFields().forEach(f -> System.out.printf("%s: %s%n", f, getFieldObject(f.getName())));
    }

    public Class<A> getaClass() {
        return aClass;
    }

    public A getObject() {
        return object;
    }

    @Override
    public String toString() {
        return object == null ? (aClass == null ? null : aClass.toString()) : object.toString();
    }
}