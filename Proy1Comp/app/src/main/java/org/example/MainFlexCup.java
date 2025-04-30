package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.example.ParserLexer.BasicLexerCup;

import java_cup.internal_error;
import java_cup.parser;
import java_cup.runtime.Symbol;
import jflex.exceptions.SilentExit;

public class MainFlexCup {

    // Inicializa el análisis léxico y sintáctico con las rutas dadas
    // Entrada: ruta del archivo .jflex y del archivo .cup
    // Salida: archivos del lexer y parser generados
    public void iniLexerParser(String rutaLexer, String rutaParser) throws internal_error, Exception {
        GenerateLexer(rutaLexer);
        Generateparser(rutaParser);
    }

   // Genera el archivo del lexer usando JFlex
   // Entrada: ruta del archivo .jflex
   // Salida: archivo Java generado del lexer
    public void GenerateLexer(String ruta) throws IOException, SilentExit{
        String[] strArr = {ruta};
        jflex.Main.generate(strArr);
    }

    // Crea una instancia del parser con el lexer dado
    // Entrada: instancia de BasicLexerCup
    // Salida: instancia del parser con el lexer conectado
    public parser crearParser(BasicLexerCup lexer) throws Exception {
        parser p = new parser(lexer);
        return p;
    }

    // Genera los archivos del parser usando CUP
    // Entrada: ruta del archivo .cup
    // Salida: archivos Java generados del parser y símbolos
    public void Generateparser(String ruta) throws internal_error, IOException, Exception{
        String[] strArr = {ruta};
        java_cup.Main.main(strArr);
    }
    
    // Ejecuta el lexer sobre un archivo para imprimir los tokens encontrados
    // Entrada: ruta del archivo fuente a escanear
    // Salida: tokens leídos impresos en consola
    public void ejercicioLexer(String rutaScanear) throws IOException
    {
        Reader reader = new BufferedReader(new FileReader (rutaScanear));
        reader.read();
        BasicLexerCup lex = new BasicLexerCup(reader);
        int i = 0;
        Symbol token;
        while(true){

            token = lex.next_token();
            if(token.sym != 0){
                System.out.println("Token: " + token.sym + ", Valor: " + (token.value == null ? lex.yytext() : token.value.toString()));
            }else{
                System.out.println("Cantidad de lexemas encontrados: "+i);
                return;
            }
            i++;
        }
    }
}
