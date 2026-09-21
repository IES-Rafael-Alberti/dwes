import figuras.Circulo;
import figuras.Cuadrado;
import figuras.Figura;

public class Main {
    public static void main(String[] args) {
        Figura cuadrado = new Cuadrado(10);
        Figura circulo = new Circulo(5);
        System.out.println(cuadrado.area());
        System.out.println(circulo.area());
    }
}
