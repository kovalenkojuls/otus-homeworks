package homework.tests;

import homework.myTestFramework.annotations.After;
import homework.myTestFramework.annotations.Before;
import homework.myTestFramework.annotations.Test;

public class DemoTest {
    @Before
    public void before1() {
        System.out.printf("[Object %s]: before1\n", this.hashCode());
    }

    @Before
    public void before2() {
         System.out.printf("[Object %s]: before2\n", this.hashCode());
    }

    @Test
    public void test1() {
        System.out.printf("[Object %s]: test1\n", this.hashCode());
    }

    @Test
    public void test2() {
        System.out.printf("[Object %s]: test2\n", this.hashCode());
        throw new RuntimeException();
    }

    @After
    public void after1() {
        System.out.printf("[Object %s]: after1\n", this.hashCode());
    }

    @After
    public void after2() {
        System.out.printf("[Object %s]: after2\n", this.hashCode());
    }

    @After
    public void after3() {
        System.out.printf("[Object %s]: after3\n", this.hashCode());
    }
}
