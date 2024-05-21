package homework.myTestFramework;

import homework.myTestFramework.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TestRunner {
    private Class<?> testClass;

    private int allTests = 0,
            failTests = 0,
            doneTests = 0;

    private void printStat() {
        System.out.println("_____________________________________");
        System.out.println("| Total     | Done      | Fail      |");
        System.out.printf( "| %d         | %d         | %d         |\n", allTests, doneTests, failTests);
        System.out.println("_____________________________________");
    }

    private void runMethods(Object testObject, ArrayList<Method> methods)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method: methods) {
            method.invoke(testObject);
        }
    }

    private void runTest(ArrayList<Method> beforeMethods,
                         Method testMethod,
                         ArrayList<Method> afterMethods) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        var testObject = ReflectionHelper.createObject(testClass);

        try {
            runMethods(testObject, beforeMethods);
            testMethod.invoke(testObject);
            doneTests++;
        } catch (Exception e) {
            System.out.printf("[Object %s]: exception\n", testObject.hashCode());
            failTests++;
        } finally {
            runMethods(testObject, afterMethods);
        }
    }

    public void startTests(String className) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        testClass = Class.forName(className);
        var allMethods = testClass.getDeclaredMethods();

        ArrayList<Method> beforeMethods, testMethods, afterMethods;
        beforeMethods = ReflectionHelper.getMethodsByAnnotation(allMethods, Before.class);
        afterMethods = ReflectionHelper.getMethodsByAnnotation(allMethods, After.class);
        testMethods = ReflectionHelper.getMethodsByAnnotation(allMethods, Test.class);

        allTests = testMethods.size();
        for (Method testMethod: testMethods) {
            runTest(beforeMethods, testMethod, afterMethods);
        }

        printStat();
    }
}
