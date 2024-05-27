package homework;

import homework.myTestFramework.TestRunner;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {

        new TestRunner().startTests("homework.tests.DemoTest");
    }
}
