package org.example.ParserLexer;

import java_cup.runtime.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.example.ErrorHandler;

%%
%public
%class BasicLexerCup
%cup
%unicode
%line
%column

%{
    StringBuffer string = new StringBuffer();
    private FileWriter tokenWriter;

    // Declaración de la tabla de símbolos
    private Map<String, Simbolo> tablaSimbolos = new HashMap<>();

    // Definición de la clase Simbolo
    public class Simbolo {
        public String nombre;
        public String tipo;
        public int linea;
        public int columna;

        public Simbolo(String nombre, String tipo, int linea, int columna) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.linea = linea;
            this.columna = columna;
        }
    }

    // Método para inicializar el logger
    private void initTokenLogger() {
        try {
            tokenWriter = new FileWriter("app/src/main/resources/tokens.log");
            tokenWriter.write("=== TOKENS ENCONTRADOS ===\n");
            tokenWriter.write(String.format("%-20s %-20s %-10s %-10s\n", 
                                "TOKEN", "LEXEMA", "LÍNEA", "COLUMNA"));
            tokenWriter.flush();
        } catch (IOException e) {
            System.err.println("Error inicializando archivo de tokens: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void logToken(String tokenType, String lexeme) {
        if (tokenWriter != null) {
            try {
                tokenWriter.write(String.format("%-20s %-20s %-10d %-10d\n", 
                    tokenType, lexeme, yyline + 1, yycolumn + 1));
                tokenWriter.flush();

                // Agregar a la tabla de símbolos si es identificador o literal
                if (tokenType.equals("IDENTIFIER") || tokenType.endsWith("_LITERAL") || tokenType.equals("CHAR_T")) {
                    agregarSimbolo(lexeme, tokenType);
                }
            } catch (IOException e) {
                System.err.println("Error escribiendo token: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void agregarSimbolo(String nombre, String tipo) {
        if (!tablaSimbolos.containsKey(nombre)) {
            tablaSimbolos.put(nombre, new Simbolo(nombre, tipo, yyline + 1, yycolumn + 1));
        }
    }

    public void exportarTablaSimbolos() {
        try (FileWriter writer = new FileWriter("app/src/main/resources/tabla_simbolos.log")) {
            writer.write("=== TABLA DE SÍMBOLOS ===\n");
            writer.write(String.format("%-20s %-20s %-10s %-10s\n", "NOMBRE", "TIPO", "LÍNEA", "COLUMNA"));

            for (Simbolo s : tablaSimbolos.values()) {
                writer.write(String.format("%-20s %-20s %-10d %-10d\n", 
                    s.nombre, s.tipo, s.linea, s.columna));
            }
            writer.flush();
            System.out.println("Tabla de símbolos exportada correctamente.");
        } catch (IOException e) {
            System.err.println("Error exportando tabla de símbolos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Symbol symbol(int type) {
        String tokenName = (type >= 0 && type < sym.terminalNames.length) ? sym.terminalNames[type] : "UNKNOWN";
        logToken(tokenName, yytext());
        return new Symbol(type, yyline, yycolumn);
    }

    private Symbol symbol(int type, Object value) {
        String tokenName = (type >= 0 && type < sym.terminalNames.length) ? sym.terminalNames[type] : "UNKNOWN";
        logToken(tokenName, yytext());
        return new Symbol(type, yyline, yycolumn, value);
    }

    // Método para cerrar el archivo y exportar tabla de símbolos
    public void closeTokenLogger() {
        if (tokenWriter != null) {
            try {
                tokenWriter.close();
                // Exportar tabla de símbolos al finalizar
                exportarTablaSimbolos();
            } catch (IOException e) {
                System.err.println("Error cerrando archivo de tokens: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler handler) {
        this.errorHandler = handler;
    }

    // Método handleError CORREGIDO
    private Symbol handleLexicalError() {
        String mensaje = "Carácter ilegal '" + yytext() + "'";
        int linea = yyline + 1;
        int columna = yycolumn + 1;

        if (errorHandler != null) {
            errorHandler.reportError(linea, columna, mensaje, "LÉXICO");
        } else {
            System.err.println("Error léxico: " + mensaje + " en la línea " + linea + ", columna " + columna);
        }

        yybegin(YYINITIAL);  // Reinicia análisis
        return new Symbol(sym.error, yyline, yycolumn, yytext());
    }
%}

%init{
    System.out.println("Inicializando analizador léxico...");
    initTokenLogger();
    System.out.println("Analizador léxico inicializado correctamente.");
%init}

%eof{
    System.out.println("Cerrando analizador léxico...");
    closeTokenLogger();
    System.out.println("Analizador léxico cerrado correctamente.");
%eof}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Comment = "@" {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "@" "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "{" {CommentContent} "*"+ "}"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [a-zA-Z]([a-zA-Z0-9])*
DecIntegerLiteral = 0 | [1-9][0-9]*
int=[0-9]+
float=[0-9]+ "."? [0-9]+

%state STRING

%%

<YYINITIAL> {
  /* keywords */
  "int"       { return symbol(sym.INTEGER_T); }
  "float"       { return symbol(sym.FLOAT); }
  "char"      { return symbol(sym.CHAR_T); }
  "string"    { return symbol(sym.STRING_T); }
  "=="        { return symbol(sym.COMPARACION); }
  "="         { return symbol(sym.ASIGNACION); }
  "+"         { return symbol(sym.SUMA); }
  "-"         { return symbol(sym.RESTA); }
  "*"         { return symbol(sym.MULTI); }
  "//"         { return symbol(sym.DIV); }
  "~"         { return symbol(sym.MOD); }
  "**"         { return symbol(sym.POTE); }
  ">"         { return symbol(sym.MAYQUE); }
  "<"         { return symbol(sym.MENQUE); }
  ">="         { return symbol(sym.MAYEQUQUE); }
  "<="         { return symbol(sym.MENEQUQUE); }
  "!="         { return symbol(sym.DIFE); }
  "++"         { return symbol(sym.SUMUN); }
  "--"         { return symbol(sym.RESUN); }
  "?"         { return symbol(sym.FINLINEA); }
  "."         { return symbol(sym.DOT); }
  ":"         { return symbol(sym.DOSDOT); }
  ","         { return symbol(sym.COMMA); }
  "ʃ"         { return symbol(sym.PARENTIZ); }
  "ʅ"         { return symbol(sym.PARENTDE); }
  "|"         { return symbol(sym.PIPE); }
  "\\"         { return symbol(sym.INITBLOC); }
  "/"         { return symbol(sym.ENDBLOC); }
  "if"         { return symbol(sym.IF); }
  "elif"         { return symbol(sym.ELIF); }
  "else"         { return symbol(sym.ELSE); }
  "do"         { return symbol(sym.DO); }
  "while"         { return symbol(sym.WHILE); }
  "break"         { return symbol(sym.BREAK); }
  "for"         { return symbol(sym.FOR); }
  "return"         { return symbol(sym.RETURN); }
  "leer"         { return symbol(sym.LEER); }
  "impr"         { return symbol(sym.IMPR); }
  "arrx"         { return symbol(sym.ARRX); }
  "matrx"         { return symbol(sym.MATRX); }
  "func"         { return symbol(sym.FUNC); }
  "void"         { return symbol(sym.VOID); }
  "param"         { return symbol(sym.PARAM); }
  "global"         { return symbol(sym.GLOBAL); }
  "struct"         { return symbol(sym.STRUCT); }
  "\\"         { return symbol(sym.STBLOC); }
  "/"         { return symbol(sym.ENDBLOC); }
  "{"         { return symbol(sym.STBCOMMENT); }
  "}"         { return symbol(sym.ENDBCOMMENT); } 
  "!"         { return symbol(sym.NEGACION); } 
  "^"         { return symbol(sym.CONJUNCION); } 
  "#"         { return symbol(sym.DISJUNCION); } 
  "@"         { return symbol(sym.ARROBA); } 


  /* identifiers */ 
  {Identifier} { return symbol(sym.IDENTIFIER, yytext()); }

  /* literals */
  {DecIntegerLiteral}  { return symbol(sym.INTEGER_LITERAL, yytext()); }
  \"                   { string.setLength(0); yybegin(STRING); }

  /* comments */
  {Comment}            { /* ignore */ }

  /* whitespace */
  {WhiteSpace}         { /* ignore */ }
}

<STRING> {
  \"                   { yybegin(YYINITIAL); 
                         return symbol(sym.STRING_LITERAL, string.toString()); }
  [^\n\r\"\\]+         { string.append(yytext()); }
  \\t                  { string.append('\t'); }
  \\n                  { string.append('\n'); }
  \\r                  { string.append('\r'); }
  \\\"                 { string.append('\"'); }
  \\                   { string.append('\\'); }
}

[^] { 
    return handleLexicalError(); 
} 