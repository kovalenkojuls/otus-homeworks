package ru.otus.appcontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {
    private final static String ERROR_MSG_NOT_FOUND = "Component %s not found in context.";
    private final static String ERROR_MSG_DUPLICATED = "Component %s is duplicated in context.";
    private final static String ERROR_MSG_NOT_CONFIG = "Given class is not config %s";

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        processManyConfigs(initialConfigClasses);
    }

    public AppComponentsContainerImpl(String initialConfigClassesPath) {
        Class<?>[] initialConfigClasses = getInitialConfigClassesFromPath(initialConfigClassesPath);
        processManyConfigs(initialConfigClasses);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> components = appComponents
                .stream()
                .filter(c -> componentClass.isAssignableFrom(c.getClass()))
                .toList();

        if (components.size() > 1) {
            throw new RuntimeException(String.format(ERROR_MSG_DUPLICATED, componentClass.getName()));
        }

        if (components.isEmpty()) {
            throw new RuntimeException(String.format(ERROR_MSG_NOT_FOUND, componentClass.getName()));
        }

        return (C) components.getFirst();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getAppComponent(String componentName) {
        var component = appComponentsByName.get(componentName);
        if (Objects.isNull(component)) {
            throw new RuntimeException(String.format(ERROR_MSG_NOT_FOUND, componentName));
        }
        return (C) component;
    }

    private void processManyConfigs(Class<?>[] initialConfigClasses) {
        List<Class<?>> sortedInitialConfigClasses = sortConfigsByOrder(initialConfigClasses);
        for (Class<?> config : sortedInitialConfigClasses) {
            processConfig(config);
        }
    }

    private Class<?>[] getInitialConfigClassesFromPath(String initialConfigClassesPath) {
        return new Reflections(initialConfigClassesPath, TypesAnnotated)
                .getTypesAnnotatedWith(AppComponentsContainerConfig.class)
                .toArray(new Class<?>[0]);
    }

    private List<Class<?>> sortConfigsByOrder(Class<?>... initialConfigClasses) {
        return Arrays.stream(initialConfigClasses)
                .filter(c -> c.isAnnotationPresent(AppComponentsContainerConfig.class))
                .sorted(Comparator.comparingInt(c -> c.getAnnotation(AppComponentsContainerConfig.class).order()))
                .toList();
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        List<Method> sortedMethods = sortMethodsByOrder(configClass);

        try {
            var configInstance = configClass.getConstructor().newInstance();
            for (Method method : sortedMethods) {
                createNewComponent(configInstance, method);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void createNewComponent(Object configInstance, Method method)
            throws InvocationTargetException, IllegalAccessException {

        String componentName = method.getAnnotation(AppComponent.class).name();
        Object[] parameters = getArrayOfParams(method);
        Object newComponent = method.invoke(configInstance, parameters);

        if (appComponentsByName.containsKey(componentName)) {
            throw new RuntimeException(String.format(ERROR_MSG_DUPLICATED, componentName));
        }

        appComponents.add(newComponent);
        appComponentsByName.put(componentName, newComponent);
    }

    private Object[] getArrayOfParams(Method method) {
        List<Object> parameters = new ArrayList<>();
        Class<?>[] parameterTypes = method.getParameterTypes();

        for (Class<?> parameterType: parameterTypes) {
            var componentFromContext = appComponents
                    .stream()
                    .filter(c -> parameterType.isAssignableFrom(c.getClass()))
                    .findFirst()
                    .orElseThrow(() ->new RuntimeException(String.format(ERROR_MSG_NOT_FOUND, parameterType)));

            parameters.add(componentFromContext);
        }

        return parameters.toArray();
    }

    private List<Method> sortMethodsByOrder(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order()))
                .toList();
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format(ERROR_MSG_NOT_CONFIG, configClass.getName()));
        }
    }
}
