package org.example;
//RuntimeException Linea 933
import java.nio.file.*;
import java.io.*;
import org.example.ParserLexer.BasicLexerCup;
import org.example.ErrorHandler;
import org.example.ParserLexer.sym;
import java_cup.runtime.*;

public class App {
    // Rutas constantes
    private static final String INPUT_FILE = "app/src/main/resources/ejemplo1.txt";
    private static final String OUTPUT_FILE = "app/src/main/resources/output.txt";
    private static final String ERROR_FILE = "app/src/main/resources/errors.log";
    private static final String TOKENS_FILE = "app/src/main/resources/tokens.log";

    public static void GenerarLexerParser() throws Exception {
        // 1. Generar analizadores (esto solo crea las clases)
        String basePath = System.getProperty("user.dir");
        //System.out.println("Directorio base: " + basePath);
        
        MainFlexCup mfjc = new MainFlexCup();
        String fullPathLexer = Paths.get(basePath, "app", "src", "Adicionales", "BasicLexerCup.jflex").toString();
        String fullPathParser = Paths.get(basePath, "app", "src", "Adicionales", "BasicParser.cup").toString();
        
        mfjc.iniLexerParser(fullPathLexer, fullPathParser);
        
        // 2. Mover archivos generados
        Path destDir = Paths.get(basePath, "app", "src", "main", "java", "org", "example", "ParserLexer");
        Files.createDirectories(destDir);
        
        Files.move(Paths.get(basePath, "sym.java"), destDir.resolve("sym.java"), StandardCopyOption.REPLACE_EXISTING);
        Files.move(Paths.get(basePath, "parser.java"), destDir.resolve("parser.java"), StandardCopyOption.REPLACE_EXISTING);
        Files.move(Paths.get(basePath, "app", "src", "Adicionales", "BasicLexerCup.java"), 
                 destDir.resolve("BasicLexerCup.java"), StandardCopyOption.REPLACE_EXISTING);
        
        // 3. Ahora ANALIZAR el código fuente con el lexer generado
        analizarCodigoFuente(INPUT_FILE, TOKENS_FILE);
        
        // 4. Escribir resultados
        FileManager.writeFile(OUTPUT_FILE, "Análisis completado correctamente");
        System.out.println("\nTokens registrados en: " + TOKENS_FILE);
        Path tokenPath = Paths.get(TOKENS_FILE);
        if (Files.exists(tokenPath)) {
            System.out.println(Files.readString(tokenPath));
        } else {
            System.out.println("No se encontraron tokens generados.");
        }

    }

    private static void analizarCodigoFuente(String inputFile, String tokensFile) throws Exception {
        // Limpiar archivo de tokens previo
        Files.deleteIfExists(Paths.get(tokensFile));
        
        // Leer el código fuente
        String sourceCode = FileManager.readFile(inputFile);
        System.out.println("=== CÓDIGO FUENTE ===");
        System.out.println(sourceCode);
        
        // Crear el lexer
        try (Reader reader = new StringReader(sourceCode)) {
            BasicLexerCup lexer = new BasicLexerCup(reader);
            ErrorHandler errorHandler = new ErrorHandler("app/src/main/resources/errors.log");
            lexer.setErrorHandler(errorHandler);
            
            // Analizar token por token
            Symbol token;
            do {
                token = lexer.next_token();
                // El propio lexer escribe en tokens.log 
            } while (token.sym != sym.EOF);
        }
    }

    /* 
    public static void PruebasLexerParser() throws Exception{
        String basePath, fullPathScanner, fullPathParser, fullPathParserII2024, fullPathParserV2;
        MainFlexCup mfjc;
        
        basePath = System.getProperty("user.dir");
        fullPathScanner = basePath+"/app/src/main/resources/ejemplo1.txt";
        fullPathParser = basePath+"/app/src/main/resources/ejemploParser.txt";
        fullPathParserV2 = basePath+"/app/src/main/resources/ejemploParserII2024.txt";
        
        fullPathParserV2 = basePath+"app/src/main/resources/ejemploParserII2024.txt";
        //fullPathParserV2 =basePath+"app/src/main/resources/ejemploParserII2024.base.txt";
        
        mfjc = new MainFlexCup();
        
        //ejercicio de prueba scanner
        
        mfjc.ejercicioLexer(fullPathScanner); //lexer cup ajustado V 2024
        
        //mfjc.ejercicioParserV2024(fullPathParserV2024); //lexer cup ajustado V 2024
    }
   */
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        try {
            GenerarLexerParser();
            //PruebasLexerParser();
            System.out.println("Proceso completado exitosamente");
        } catch (Exception e) {
            try {
                FileManager.writeFile(ERROR_FILE, "Error: " + e.getMessage());
            } catch (IOException ioEx) {
                System.err.println("Error al escribir log: " + ioEx.getMessage());
            }
            System.err.println("Error durante el análisis: " + e.getMessage());
            e.printStackTrace();
        }
    }

}