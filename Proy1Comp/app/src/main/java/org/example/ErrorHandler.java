package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private final String errorLogPath;
    private final List<String> errors = new ArrayList<>();
    private boolean panicMode = false;
    private boolean continueOnError = false;
    
    public ErrorHandler(String logPath) {
        this.errorLogPath = logPath;
        initializeLogFile();
    }
    
    private void initializeLogFile() {
        try {
            // Crear directorios si no existen
            Paths.get(errorLogPath).getParent().toFile().mkdirs();
            
            // Limpiar archivo existente o crear nuevo
            Files.deleteIfExists(Paths.get(errorLogPath));
            Files.createFile(Paths.get(errorLogPath));
            
            System.out.println("Archivo de errores inicializado en: " + 
                Paths.get(errorLogPath).toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error inicializando archivo de errores: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void reportError(int line, int column, String message, String errorType) {
        String errorMsg = String.format("[%s] Línea %d, Columna %d: %s", 
                              errorType, line, column, message);
        errors.add(errorMsg);
        
        // Escribir en archivo con verificación explícita
        try {
            FileWriter fw = new FileWriter(errorLogPath, true);
            fw.write(errorMsg + "\n");
            fw.flush(); // Forzar escritura inmediata
            fw.close();
            
            System.out.println("Error registrado en archivo: " + errorMsg);
        } catch (IOException e) {
            System.err.println("Error crítico escribiendo en log:");
            System.err.println("Ruta intentada: " + Paths.get(errorLogPath).toAbsolutePath());
            e.printStackTrace();
        }
        
        System.err.println(errorMsg);
    }

    public boolean checkFileAccess() {
        try {
            return Files.isWritable(Paths.get(errorLogPath));
        } catch (Exception e) {
            System.err.println("Error verificando permisos: " + e.getMessage());
            return false;
        }
    }

    public void enterPanicMode() {
        this.panicMode = true;
    }

    public void exitPanicMode() {
        this.panicMode = false;
    }

    public boolean isInPanicMode() {
        return panicMode;
    }

    public String getErrorSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== RESUMEN DE ERRORES ===\n");
        sb.append("Total errores: ").append(errors.size()).append("\n");
        errors.forEach(e -> sb.append(e).append("\n"));
        return sb.toString();
    }

    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    public boolean shouldContinue() {
        return continueOnError;
    }
}
