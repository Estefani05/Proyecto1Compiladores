/* JFlex example: partial Java language lexer specification */
package ParserLexer;  // <-- AÑADE TU PAQUETE AQUÍ
import java_cup.runtime.*;

%%

%class BasicLexerCup
%public
%unicode
%cup
%line
%column

%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}
TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [:jletter:] [:jletterdigit:]*
DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING

%%

<YYINITIAL> {
  /* keywords */
  "int"       { return symbol(sym.INTEGER_T); }
  "char"      { return symbol(sym.CHAR_T); }
  "=="        { return symbol(sym.COMPARACION); }
  "="         { return symbol(sym.ASIGNA); }
  "+"         { return symbol(sym.SUMA); }
  "?"         { return symbol(sym.FINLINEA); }
  
  /* identifiers */ 
  {Identifier}         { return symbol(sym.IDENTIFIER, yytext()); }
  
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

/* error fallback */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }