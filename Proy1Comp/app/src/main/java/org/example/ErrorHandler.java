package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ErrorHandler {
    private static final String ERROR_LOG = "app/src/main/resources/errors.log";
    private List<String> errors = new ArrayList<>();
    private boolean panicMode = false;

    public void reportError(int line, int column, String message, String errorType) {
        String errorMsg = String.format("[%s] Línea %d, Columna %d: %s", 
                              errorType, line, column, message);
        errors.add(errorMsg);

        try (FileWriter fw = new FileWriter(ERROR_LOG, true)) {
            fw.write(errorMsg + "\n");
        } catch (IOException e) {
            System.err.println("Error escribiendo en log: " + e.getMessage());
        }

        System.err.println(errorMsg);
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

    private boolean continueOnError = false;

    public void setContinueOnError(boolean continueOnError) {
        this.continueOnError = continueOnError;
    }

    public boolean shouldContinue() {
        return continueOnError;
    }
}
