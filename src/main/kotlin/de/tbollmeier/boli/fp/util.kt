package de.tbollmeier.boli.fp

fun <A, B, C> compose(f: (A) -> B, g: (B) -> C) : (A) -> C = { g(f(it)) }