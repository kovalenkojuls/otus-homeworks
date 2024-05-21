package homework.myTestFramework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectionHelper {
    private ReflectionHelper() {}

    public static ArrayList<Method> getMethodsByAnnotation(Method[] methods, Class<? extends Annotation> annotationClass) {
        ArrayList<Method> methodsByAnnotation = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                methodsByAnnotation.add(method);
            }
        }

        return methodsByAnnotation;
    }

    public static Object createObject(Class<?> clazz)throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        Constructor<?> constructor = clazz.getConstructor();
        return constructor.newInstance();
    }
}
