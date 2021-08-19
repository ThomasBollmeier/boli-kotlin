package de.tbollmeier.boli.frontend

import de.tbollmeier.boli.fp.pipe
import de.tbollmeier.grammarous.*

class Parser {

    private val lexer = makeLexer()
    private val parser = SyntaxParser(makeGrammar())

    val parse = ::createStringCharStream pipe lexer::scan pipe parser::parse

    private fun makeLexer(): Lexer {

        val lexerGrammar = LexerGrammar().apply {

            defineToken(Token.IDENT, "[a-z][_a-z0-9]*")
            defineToken(Token.NUMBER, "\\d+")
            defineToken(Token.PLUS, "\\+")
            defineToken(Token.MINUS, "-")
            defineToken(Token.MULT, "\\*")
            defineToken(Token.DIV, "/")
            defineToken(Token.LPAR, "\\(")
            defineToken(Token.RPAR, "\\)")
            defineToken(Token.ASSIGN, "<-")

            defineKeyword("defconst", Token.KW_DEF_CONST)

        }

        return createLexer(lexerGrammar)
    }

    private fun makeGrammar(): Grammar {

        return grammar {

            // stmts -> stmt*
            ruleDef("stmts") {
                many { rule("stmt") }
            }

            // stmt -> vardef | expr
            ruleDef("stmt") {
                oneOf {
                    rule("constdef")
                    rule("expr")
                }
            }

            // constdef -> 'defconst' IDENT ASSIGN expr
            ruleDef("constdef") {
                terminal(Token.KW_DEF_CONST)
                terminal(Token.IDENT, "ident")
                terminal(Token.ASSIGN)
                rule("expr", "value")
            } transformBy ::tramsformConstDef

            // expr -> term ((PLUS|MINUS) term)*
            ruleDef("expr") {
                rule("term")
                many {
                    oneOf {
                        terminal(Token.PLUS)
                        terminal(Token.MINUS)
                    }
                    rule("term")
                }
            } transformBy ::transformOperations

            ruleDef("term") {
                rule("factor")
                many {
                    oneOf {
                        terminal(Token.MULT)
                        terminal(Token.DIV)
                    }
                    rule("factor")
                }
            } transformBy ::transformOperations

            ruleDef("factor") {
                oneOf {
                    terminal(Token.IDENT)
                    terminal(Token.NUMBER)
                    sequence {
                        terminal(Token.LPAR)
                        rule("expr")
                        terminal(Token.RPAR)
                    }
                }
            } transformBy ::transformFactor

        }

    }

    private fun tramsformConstDef(ast: Ast): Ast {
        val result = Ast("constdef")
        val ident = ast.getChildrenById("ident")[0]
        val rhs = ast.getChildrenById("value")[0]
        rhs.id = ""
        result.attrs["name"] = ident.value
        result.addChild(rhs)
        return result
    }

    private fun createBinOp(op: Ast, left: Ast, right: Ast) : Ast {
        val result = Ast(when(op.value) {
            "+" -> "add"
            "-" -> "subtract"
            "*" -> "multiply"
            "/" -> "divide"
            else -> "binop"
        })
        left.id = ""
        result.addChild(left)
        right.id = ""
        result.addChild(right)
        return result
    }

    private fun transformOperations(ast: Ast) : Ast {
        return if (ast.children.size == 1) {
            val child = ast.children[0]
            child.id = ""
            child
        } else {
            val numOperators = (ast.children.size - 1) / 2
            var result = createBinOp(ast.children[1], ast.children[0], ast.children[2])
            for (i in 2..numOperators) {
                val idx = 1 + 2 * (i - 1)
                result = createBinOp(ast.children[idx], result, ast.children[idx + 1])
            }
            return result
        }
    }

    private fun transformFactor(ast: Ast) : Ast {
        return if (ast.children.size == 1) {
            ast.children[0]
        } else {
            ast.children[1]
        }
    }

}