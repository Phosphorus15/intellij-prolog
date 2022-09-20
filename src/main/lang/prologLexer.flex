package tech.phosphorus.intellij.prolog;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static tech.phosphorus.intellij.prolog.psi.PrologTypes.*;

%%

%{
  public PrologLexer() {
    this((java.io.Reader)null);
  }
%}

%{}
    private int blockCommentDepth = 0;
%}

%public
%class PrologLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SPACE=[ \t\n\x0B\f\r]+
COMMENT=(%.*)
BLOCK_COMMENT="/*" !([^]* "*/" [^]*) ("*/")?
INTEGER=[0-9]+
FLOAT=[0-9]+(\.d+)?([Ee][0-9]+)?
CONST_ID=(([:lowercase:])(([:letter:]|[:digit:])|_|-|:)*)
ATOM_ID=(([:uppercase:])|_)(([:letter:]|[:digit:])|_)*
OPERATOR_ID=[<=>:!+\\\-*/]+
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")

%s IN_BLOCK_COMMENT

%%
<YYINITIAL> {
  {WHITE_SPACE}      { return WHITE_SPACE; }

  ";"                { return SEMI; }
  ","                { return COMMA; }
  "."                { return DOT; }
  ":-"               { return UNIFY; }
  "-->"              { return EXPAND; }
  "/-"               { return NOT; }
  "("                { return LP; }
  ")"                { return RP; }
  "["                { return LB; }
  "]"                { return RB; }
  "{"                { return LBR; }
  "}"                { return RBR; }
  "|"                { return LIST_CONS; }
  "_"                { return WILDCARD; }
  "is"               { return ARITH_EVAL; }

  "/*"               { yybegin(IN_BLOCK_COMMENT); yypushback(2); }

  {SPACE}            { return SPACE; }
  {COMMENT}          { return COMMENT; }
  {INTEGER}          { return INTEGER; }
  {FLOAT}            { return FLOAT; }
  {CONST_ID}         { return CONST_ID; }
  {ATOM_ID}          { return ATOM_ID; }
  {OPERATOR_ID}      { return OPERATOR_ID; }
  {STRING}           { return STRING; }

}

<IN_BLOCK_COMMENT> {

  "/*"    { blockCommentDepth ++; }

  "*/"    { if(-- blockCommentDepth <= 0) { yybegin(YYINITIAL); return BLOCK_COMMENT; } } // treat as comment also

  <<EOF>> { blockCommentDepth = 0; yybegin(YYINITIAL); return BLOCK_COMMENT; }

  [^]     { }
}

[^] { return BAD_CHARACTER; }
