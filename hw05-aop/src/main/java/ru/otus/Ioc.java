package ru.otus;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.annotation.Log;

class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    static TestLoggingInterface createTestLogging() {
        InvocationHandler logInvocationHandler =
                new LogInvocationHandler(new TestLogging());

        return (TestLoggingInterface) Proxy.newProxyInstance(
                Ioc.class.getClassLoader(),
                new Class<?>[] {TestLoggingInterface.class},
                logInvocationHandler);
    }

    static class LogInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface testLogging;
        private static final ArrayList<String> methodsForLog = new ArrayList<>();

        LogInvocationHandler(TestLoggingInterface testLogging) {
            this.testLogging = testLogging;

            Method[] allMethods = testLogging.getClass().getMethods();
            for(Method method: allMethods) {
                if(method.getAnnotation(Log.class) != null) {
                    methodsForLog.add(method.getName());
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodsForLog.contains(method.getName())) {
                logger.info("executed method:{}, params:{}", method, Arrays.toString(args));
            }
            return method.invoke(testLogging, args);
        }

        @Override
        public String toString() {
            return "LogInvocationHandler{" + "TestLogging=" + testLogging + '}';
        }
    }
}
