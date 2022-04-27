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

IDENTIFIER=[_a-z][_0-9a-z]*
UPPER_IDENTIFIER=[A-Z][_0-9a-zA-Z]*
COMMENT="//"([^\n]*)?
DOC_COMMENT="///"([^\n]*)?
MODULE_COMMENT="////"([^\n]*)?
NUMBER=[0-9]*
STRING_CONTENT=\"([^\\\\\\\"]|\\\\[^efnrt\\\"\\\\])+\"

%%
<YYINITIAL> {
  {WHITE_SPACE}           { return WHITE_SPACE; }

  "pub"                   { return PUB; }
  "fn"                    { return FN; }
  "let"                   { return LET; }
  "case"                  { return CASE; }
  "import"                { return IMPORT; }
  "type"                  { return TYPE; }
  "assert"                { return ASSERT; }
  "todo"                  { return TODO; }
  "const"                 { return CONST; }
  "external"              { return EXTERNAL; }
  "{"                     { return LBRACE; }
  "}"                     { return RBRACE; }
  "["                     { return LBRACK; }
  "]"                     { return RBRACK; }
  "("                     { return LPAREN; }
  ")"                     { return RPAREN; }
  ":"                     { return COLON; }
  ","                     { return COMMA; }
  "="                     { return EQ; }
  "=="                    { return EQEQ; }
  "!"                     { return BANG; }
  "+"                     { return PLUS; }
  "-"                     { return MINUS; }
  "||"                    { return OR; }
  "&&"                    { return AND; }
  "<"                     { return LT; }
  ">"                     { return GT; }
  "*"                     { return MUL; }
  "/"                     { return DIV; }
  "//"                    { return DIVDIV; }
  "."                     { return DOT; }
  ".."                    { return DOTDOT; }
  "=>"                    { return FAT_ARROW; }
  "->"                    { return ARROW; }
  "\""                    { return QUOTE; }
  "|>"                    { return PIPE; }

  {IDENTIFIER}            { return IDENTIFIER; }
  {UPPER_IDENTIFIER}      { return UPPER_IDENTIFIER; }
  {COMMENT}               { return COMMENT; }
  {DOC_COMMENT}           { return DOC_COMMENT; }
  {MODULE_COMMENT}        { return MODULE_COMMENT; }
  {NUMBER}                { return NUMBER; }
  {STRING_CONTENT}        { return STRING_CONTENT; }

}

[^] { return BAD_CHARACTER; }
