package homework.myTestFramework;

import homework.myTestFramework.annotations.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class TestRunner {
    public void startTests(String className) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> testClass = Class.forName(className);
        var allMethods = testClass.getDeclaredMethods();

        ArrayList<Method> beforeMethods, testMethods, afterMethods;
        beforeMethods = ReflectionHelper.getMethodsByAnnotation(allMethods, Before.class);
        afterMethods = ReflectionHelper.getMethodsByAnnotation(allMethods, After.class);
        testMethods = ReflectionHelper.getMethodsByAnnotation(allMethods, Test.class);

        int allTests = testMethods.size(), completedTests = 0, failTests = 0;
        for (Method testMethod: testMethods) {
            boolean testStatus = runTest(testClass, beforeMethods, testMethod, afterMethods);
            if (testStatus) {
                completedTests++;
            } else {
                failTests++;
            }
        }

        printStat(allTests, completedTests, failTests);
    }
    private void printStat(int allTests, int completedTests, int failTests) {
        System.out.println("_____________________________________");
        System.out.println("| Total     | Done      | Fail      |");
        System.out.printf( "| %d         | %d         | %d         |\n", allTests, completedTests, failTests);
        System.out.println("_____________________________________");
    }

    private void runMethods(Object testObject, ArrayList<Method> methods)
            throws InvocationTargetException, IllegalAccessException {
        for (Method method: methods) {
            method.invoke(testObject);
        }
    }

    private boolean runTest(Class<?> testClass,
                         ArrayList<Method> beforeMethods,
                         Method testMethod,
                         ArrayList<Method> afterMethods) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        var testObject = ReflectionHelper.createObject(testClass);

        boolean testStatus = true;
        try {
            runMethods(testObject, beforeMethods);
            testMethod.invoke(testObject);
        } catch (Exception e) {
            System.out.printf("[Object %s]: exception\n", testObject.hashCode());
            testStatus = false;
        } finally {
            runMethods(testObject, afterMethods);
        }

        return testStatus;
    }
}
