package lox

import lox.expr.Expr

class AstPrinter : Expr.Visitor<String>{

    fun print(expr: Expr): String = expr.accept(this)

    override fun visitExpr(expr: Expr.Binary): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitExpr(expr: Expr.Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitExpr(expr: Expr.Literal): String {
        return expr.value?.toString() ?: "nil"
    }

    override fun visitExpr(expr: Expr.Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        return buildString {
            append("(")
            append(name)
            for (expr in exprs) {
                append(" ")
                // here need to qualify `this` with a tag b/c we're within a buildString
                append(expr.accept(this@AstPrinter))
            }
            append(")")
        }
    }
}

fun main() {
    val expression = Expr.Binary(
        Expr.Unary(
            Token(TokenType.MINUS, "-", null, 1), Expr.Literal(123)
        ), Token(TokenType.STAR, "*", null, 1), Expr.Grouping(
            Expr.Literal(45.67)
        )
    )
    println(AstPrinter().print(expression)) // (* (- 123) (group 45.67))
}