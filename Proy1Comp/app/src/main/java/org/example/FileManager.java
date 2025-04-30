package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

public class FileManager {

    // Método para leer un archivo completo como texto
    // Entrada: ruta del archivo (filePath)
    // Salida: contenido del archivo como String
    // Restricción: el archivo debe existir y ser accesible
    public static String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }

    // Método para leer un archivo línea por línea
    // Entrada: ruta del archivo (filePath)
    // Salida: lista de líneas del archivo
    // Restricción: el archivo debe existir y estar bien formado
    public static List<String> readLines(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    // Método para escribir contenido en un archivo
    // Entrada: ruta del archivo y contenido a escribir
    // Salida: archivo creado o sobrescrito con el contenido
    public static void writeFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }
}
