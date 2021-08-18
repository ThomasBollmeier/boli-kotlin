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
        1 - ( 2 + factor_2 *5)
    """.trimIndent()

        val result = parser.parse(code)

        assert(result is Result.Success)

        println( when (result) {
            is Result.Success -> AstXmlFormatter().toXml(result.value)
            is Result.Failure -> result.message
        })

    }

    @Test
    fun parseWithError() {

        val code = """
        1 - ( 2 + factor_!2 *5)
    """.trimIndent()

        val result = parser.parse(code)

        assert(result is Result.Failure)

        println((result as Result.Failure).message)

    }}