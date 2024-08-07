package lox
import lox.ScannerConstants.ALPHAS
import lox.ScannerConstants.KEYWORDS
import lox.TokenType.*

class Scanner(val source:String) {
    private val tokens:MutableList<Token> = mutableListOf()
    private var start = 0
    private var current = 0
    private var line = 1
    fun scanTokens():List<Token> {
        while (!isAtEnd()){
            start =current;
            scanToken();
        }
        tokens.add(Token(EOF, "", null, line))
        return tokens;
    }
    private fun isAtEnd(): Boolean = current >= source.length

    private fun scanToken(){
        val c = advance()
        when(c){
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' ->{
                if(match('/')){
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else{
                    addToken(SLASH)
                }
            }
            ' ', '\r', '\t' -> {} // ignored whitespace
            '\n' -> line++
            '"' -> string()
            else -> when{
                isDigit(c) -> number()
                isAlpha(c) -> identifier()
                else->Lox.error(line, "Unexpected Character.")
            }
        }

    }
    private fun advance(): Char = source[current++]

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun match(expected:Char):Boolean{
        if(isAtEnd()) return  false
        if(source[current]!= expected) return false;
        current++;
        return true;
    }

    private fun peek(): Char{
        if(isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun string(){
        while (peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++
            advance()
        }

        if(isAtEnd()) {
            Lox.error(line,"Not found closing ")
            return
        }
        advance()
        addToken(STRING,source.substring(start + 1, current-1 ))
    }

    private fun isDigit(c:Char):Boolean{
        return c in '0'..'9'
    }

    private fun number(){
        while (isDigit(peek())) advance()
        if(peek()=='.' && isDigit(peekNext())){
            advance()
            while (isDigit(peek()))advance()
        }
        addToken(NUMBER,source.substring(start, current).toDouble())
    }
    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        val type = KEYWORDS[text] ?: IDENTIFIER
        addToken(type)
    }

    private fun isAlpha(c: Char): Boolean = c in ALPHAS
    private fun isAlphaNumeric(c: Char) = isAlpha(c) || isDigit(c)
}

object ScannerConstants{
    val ALPHAS = ('a'..'z').union('A'..'Z').plus('_')
    val KEYWORDS = mapOf<String, TokenType>(
        "and" to AND,
        "class" to CLASS,
        "else" to ELSE,
        "false" to FALSE,
        "for" to FOR,
        "fun" to FUN,
        "if" to IF,
        "nil" to NIL,
        "or" to OR,
        "print" to PRINT,
        "return" to RETURN,
        "super" to SUPER,
        "this" to THIS,
        "true" to TRUE,
        "var" to VAR,
        "while" to WHILE
    )
}