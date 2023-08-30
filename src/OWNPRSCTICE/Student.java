package OWNPRSCTICE;

public class Student extends Person {
    public void method1() {
        System.out.print("Student 1 ");
        super.method1();
        this.method2();
    }
    public void method2() {
        System.out.print("Student 2 ");
    }
}