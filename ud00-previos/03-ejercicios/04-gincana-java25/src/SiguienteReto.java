import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Base64;
import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SiguienteReto {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.out.println("Uso: java -cp out SiguienteReto <retoResuelto> [clave]");
            System.out.println("Sin clave como argumento, lee CLAVE=... de la salida del programa anterior.");
            return;
        }

        try {
            int numero = Integer.parseInt(args[0]);
            if (numero < 1 || numero > 6) {
                System.out.println("El numero de reto debe estar entre 1 y 6.");
                return;
            }

            String clave = args.length == 2 ? args[1].trim()
                    : extraerClave(new String(System.in.readAllBytes(), StandardCharsets.UTF_8));
            if (clave.isEmpty()) {
                System.out.println("Falta la clave. La ultima linea de tu programa debe ser CLAVE=...");
                return;
            }

            Path archivo = Path.of("retos-cifrados", "reto%02d.enc".formatted(numero + 1));
            String[] partes = Files.readString(archivo, StandardCharsets.UTF_8).trim().split(":", -1);
            if (partes.length != 3) {
                System.out.println("El archivo del reto esta danado.");
                return;
            }
            Base64.Decoder base64 = Base64.getDecoder();
            byte[] sal = base64.decode(partes[0]);
            byte[] nonce = base64.decode(partes[1]);
            byte[] cifrado = base64.decode(partes[2]);

            PBEKeySpec derivacion = new PBEKeySpec(clave.toCharArray(), sal, 120_000, 256);
            byte[] bytesClave;
            try {
                bytesClave = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                        .generateSecret(derivacion).getEncoded();
            } finally {
                derivacion.clearPassword();
            }
            Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
            aes.init(Cipher.DECRYPT_MODE, new SecretKeySpec(bytesClave, "AES"),
                    new GCMParameterSpec(128, nonce));
            System.out.println(new String(aes.doFinal(cifrado), StandardCharsets.UTF_8));
        } catch (NumberFormatException e) {
            System.out.println("Indica un numero de reto valido.");
        } catch (AEADBadTagException e) {
            System.out.println("Clave incorrecta. Comprueba los calculos o consulta una pista.");
        } catch (IOException e) {
            System.out.println("No se encuentra el reto cifrado. Ejecuta desde la carpeta de la gincana.");
        } catch (IllegalArgumentException | GeneralSecurityException e) {
            System.out.println("No se ha podido descifrar el reto: archivo incorrecto o incompleto.");
        }
    }

    private static String extraerClave(String salida) {
        String ultima = salida.lines().filter(linea -> !linea.isBlank())
                .reduce((anterior, actual) -> actual).orElse("").trim();
        return ultima.startsWith("CLAVE=") ? ultima.substring("CLAVE=".length()).trim() : "";
    }
}
