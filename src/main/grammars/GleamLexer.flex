package run.gleam.lang.core.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static run.gleam.lang.core.psi.GleamTypes.*;

%%

%{
  public _GleamLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _GleamLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+


%%
<YYINITIAL> {
  {WHITE_SPACE}      { return WHITE_SPACE; }

  "{"                { return LBRACE; }
  "}"                { return RBRACE; }
  "["                { return LBRACK; }
  "]"                { return RBRACK; }
  "("                { return LPAREN; }
  ")"                { return RPAREN; }
  ":"                { return COLON; }
  ";"                { return SEMICOLON; }
  ","                { return COMMA; }
  "="                { return EQ; }
  "!="               { return BANGEQ; }
  "=="               { return EQEQ; }
  "!"                { return BANG; }
  "+="               { return PLUSEQ; }
  "+"                { return PLUS; }
  "-="               { return MINUSEQ; }
  "-"                { return MINUS; }
  "||"               { return OR; }
  "<"                { return LT; }
  "*"                { return MUL; }
  "/"                { return DIV; }
  "//"               { return DIVDIV; }
  ">"                { return GT; }
  ".."               { return DOTDOT; }
  "=>"               { return FAT_ARROW; }
  "->"               { return ARROW; }
  "?"                { return Q; }
  "ALPHA"            { return ALPHA; }
  "WSP"              { return WSP; }


}

[^] { return BAD_CHARACTER; }
