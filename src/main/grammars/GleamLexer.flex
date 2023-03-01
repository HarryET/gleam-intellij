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

EOL_WS           = \n | \r | \r\n
LINE_WS          = [\ \t]
WHITE_SPACE_CHAR = {EOL_WS} | {LINE_WS}
WHITE_SPACE      = {WHITE_SPACE_CHAR}+

DOWN_NAME        = [a-z_][_0-9a-z]*
UP_NAME          = [A-Z][0-9a-zA-Z]*
DISCARD_NAME     = _[_0-9a-z]*
IDENTIFIER       = {DOWN_NAME} | {UP_NAME} | {DISCARD_NAME}

INTEGER          = {DECIMAL} | {HEX} | {OCTAL} | {BINARY}
DECIMAL          = -? [0-9][0-9_]*
HEX              = -? 0x[a-fA-F0-9_]*
OCTAL            = -? 0o[0-7_]*
BINARY           = -? 0b[01_]*

STRING           = ('([^'\\]|\\.)*'|\"([^\"\\]|\\\"|\\\'|\\)*\")
EXPONENT         = [eE] [-+]? [0-9_]+
FLOAT            =  '-'? {DECIMAL} '.' [0-9_]+ {DECIMAL}?   // 1.35, 1.35E-9, 0.3, -4.5
                   |   '-'? {DECIMAL} '.'
                   |   '-'? {DECIMAL} {EXPONENT}               // 1e10 -3e4


%%
<YYINITIAL> {

  "as"                  { return AS; }
  "assert"              { return ASSERT; }
  "case"                { return CASE; }
  "const"               { return CONST; }
  "external"            { return EXTERNAL; }
  "fn"                  { return FN; }
  "if"                  { return IF; }
  "import"              { return IMPORT; }
  "let"                 { return LET; }
  "opaque"              { return OPAQUE_KW; }
  "panic"               { return PANIC; }
  "pub"                 { return PUB; }
  "todo"                { return TODO; }
  "try"                 { return TRY; }
  "type"                { return TYPE; }
  "use"                 { return USE; }
  "True"                { return TRUE; }
  "False"               { return FALSE; }
  "("                   { return LEFT_PAREN; }
  ")"                   { return RIGHT_PAREN; }
  "["                   { return LEFT_SQUARE; }
  "]"                   { return RIGHT_SQUARE; }
  "{"                   { return LEFT_BRACE; }
  "}"                   { return RIGHT_BRACE; }
  "+"                   { return PLUS; }
  "-"                   { return MINUS; }
  "*"                   { return STAR; }
  "/"                   { return SLASH; }
  "<"                   { return LESS; }
  ">"                   { return GREATER; }
  "<="                  { return LESS_EQUAL; }
  ">="                  { return GREATER_EQUAL; }
  "%"                   { return PERCENT; }
  "+."                  { return PLUS_DOT; }
  "-."                  { return MINUS_DOT; }
  "*."                  { return STAR_DOT; }
  "/."                  { return SLASH_DOT; }
  "<."                  { return LESS_DOT; }
  ">."                  { return GREATER_DOT; }
  "<=."                 { return LESS_EQUAL_DOT; }
  ">=."                 { return GREATER_EQUAL_DOT; }
  "<>"                  { return LT_GT; }
  ":"                   { return COLON; }
  ","                   { return COMMA; }
  "#"                   { return HASH; }
  "!"                   { return BANG; }
  "="                   { return EQUAL; }
  "=="                  { return EQUAL_EQUAL; }
  "!="                  { return NOT_EQUAL; }
  "|"                   { return VBAR; }
  "||"                  { return VBAR_VBAR; }
  "&&"                  { return AMPER_AMPER; }
  "<<"                  { return LT_LT; }
  ">>"                  { return GT_GT; }
  "|>"                  { return PIPE; }
  "."                   { return DOT; }
  "->"                  { return R_ARROW; }
  "<-"                  { return L_ARROW; }
  ".."                  { return DOT_DOT; }
  "<ID>"                { return ID; }

  "////" .*             { return COMMENT_MODULE; }
  "///" .*              { return COMMENT_DOC; }
  "//" .*               { return COMMENT_NORMAL; }
  {DISCARD_NAME}        { return DISCARD_NAME; }
  {DOWN_NAME}           { return DOWN_NAME; }
  {UP_NAME}             { return UP_NAME; }
  {INTEGER}             { return INTEGER; }
  {STRING}              { return STRING; }
  {FLOAT}               { return FLOAT; }
  {WHITE_SPACE}         { return WHITE_SPACE; }

}

[^] { return BAD_CHARACTER; }
