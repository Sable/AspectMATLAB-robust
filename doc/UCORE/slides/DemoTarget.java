public class DemoTarget {
    public static void foo() {}

    public static void goo() {hoo();}

    public static void hoo() {}

    public static void main(String[] args) {
        foo();
        goo();
    }
}