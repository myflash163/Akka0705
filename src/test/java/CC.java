public class CC {
    public static void main(String[] args) {
        String[] a = new String[2];
        Object[] b = a;
        a[0] = "hi";
        b[1] = 42;

        System.out.println(b);
    }
}
