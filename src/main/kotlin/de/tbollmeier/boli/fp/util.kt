package de.tbollmeier.boli.fp

infix fun <A, B, C> ((A) -> B).pipe(f: (B) -> C) : (A) -> C = { f(this(it)) }