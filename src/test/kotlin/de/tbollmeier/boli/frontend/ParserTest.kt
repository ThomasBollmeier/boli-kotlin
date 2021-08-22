package de.tbollmeier.boli.frontend

import de.tbollmeier.grammarous.AstXmlFormatter
import de.tbollmeier.grammarous.Result
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test

internal class ParserTest {

    private lateinit var parser: Parser

    @BeforeTest
    fun setUp() {
        parser = Parser()
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun parse() {

        val code = """
            defconst pi <- 3
            defvar answer <- 40 + 2 
            1 - (2 + answer * 5)
    """.trimIndent()

        val result = parser.parse(code)

        assert(result is Result.Success)

        println(AstXmlFormatter().toXml((result as Result.Success).value))
    }

    @Test
    fun parseWithError() {

        val code = """
        1 - ( 2 + factor_!2 *5)
    """.trimIndent()

        val result = parser.parse(code)

        assert(result is Result.Failure)

        println((result as Result.Failure).message)
    }
}