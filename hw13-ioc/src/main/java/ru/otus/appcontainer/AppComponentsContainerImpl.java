package ru.otus.appcontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {
    private final static String ERROR_MSG_NOT_FOUND = "Component %s not found in context.";
    private final static String ERROR_MSG_DUBLICATED = "Component %s is duplicated in context.";

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
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
            throw new RuntimeException(String.format(ERROR_MSG_DUBLICATED, componentClass.getName()));
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

    private void processManyConfigs(Class<?>... initialConfigClasses) {
        var sortedConfigs = sortConfigsByOrder(initialConfigClasses);
        for (int order = 0; order <= sortedConfigs.size(); order++) {
            if (!sortedConfigs.containsKey(order)) {
                continue;
            }

            for (Class<?> config : sortedConfigs.get(order)) {
                processConfig(config);
            }
        }
    }

    private TreeMap<Integer, List<Class<?>>> sortConfigsByOrder(Class<?>... initialConfigClasses) {
        TreeMap<Integer, List<Class<?>>> sortedConfigs = new TreeMap<>();

        for (Class<?> initialConfigClass: initialConfigClasses) {
            var appComponentsContainerConfig = initialConfigClass.getAnnotation(AppComponentsContainerConfig.class);
            if (Objects.isNull(appComponentsContainerConfig)) {
                throw new RuntimeException(
                        String.format("Annotation @AppComponentsContainerConfig not found in config class %s",
                                initialConfigClass.getName()));
            }

            int order = appComponentsContainerConfig.order();
            if (!sortedConfigs.containsKey(order)) {
                sortedConfigs.put(order, new ArrayList<Class<?>>());
            }
            sortedConfigs.get(order).add(initialConfigClass);
        }

        return sortedConfigs;
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        TreeMap<Integer, List<Method>> sortedMethods = sortMethodsByOrder(configClass);

        try {
            var configInstance = configClass.getConstructor().newInstance();
            for (int order = 0; order <= sortedMethods.size(); order++) {
                if (!sortedMethods.containsKey(order)) {
                    continue;
                }

                for (Method method : sortedMethods.get(order)) {
                    createNewComponent(configInstance, method);
                }
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
            throw new RuntimeException(String.format(ERROR_MSG_DUBLICATED, componentName));
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

    private TreeMap<Integer, List<Method>> sortMethodsByOrder(Class<?> configClass) {
        TreeMap<Integer, List<Method>> sortedMethods = new TreeMap<>();
        for (Method method: configClass.getMethods()) {
            var appComponentAnnotation = method.getAnnotation(AppComponent.class);
            if (Objects.isNull(appComponentAnnotation)) {
                continue;
            }

            int order = appComponentAnnotation.order();
            if (!sortedMethods.containsKey(order)) {
                sortedMethods.put(order, new ArrayList<Method>());
            }
            sortedMethods.get(order).add(method);
        }
        return sortedMethods;
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }
}
