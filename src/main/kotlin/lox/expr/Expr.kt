package lox.expr

import lox.Token

abstract class Expr{
    class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr,
    ) : Expr() {

    }
}