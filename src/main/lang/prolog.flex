package tech.phosphorus.intellij.prolog;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static tech.phosphorus.intellij.prolog.PrologTokenType.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%{
  public PrologLexer() { this((java.io.Reader)null); }
%}

%public
%class PrologLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%eof{ return;
%eof}

WHITE_SPACE=[\ \t\f\r\n]+
UNIVERSE = \:\-
IDENTIFIER_UPCASE =[A-Z][a-zA-Z0-9]*
IDENTIFIER_DOWNCASE = [a-z][a-zA-Z0-9]*
COMMENTS = \%[^\n\r]*

%%

= { return EQ(); }
_ { return WILDCARD(); }
; { return SEMI_OR(); }
, { return COMMA_AND(); }
\. { return DOT(); }
\( { return LPAREN(); }
\) { return RPAREN(); }
\[ { return LBRACKET(); }
\] { return RBRACKET(); }

is { return ARITHMETIC_EVAL(); }

{UNIVERSE} { return DEFINITION(); }
{COMMENTS} { return LINE_COMMENT(); }
{IDENTIFIER_UPCASE} { return IDENTIFIER_UPCASE(); }
{IDENTIFIER_DOWNCASE} { return IDENTIFIER_DOWNCASE(); }
{WHITE_SPACE} { return WHITE_SPACE; }

[^] { return BAD_CHARACTER; }