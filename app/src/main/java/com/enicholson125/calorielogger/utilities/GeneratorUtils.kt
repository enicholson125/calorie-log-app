package com.enicholson125.calorielogger.utilities

import kotlin.random.Random

val idCharPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

object GeneratorUtils {

    fun getRandomID(length: Int): String {
        return (1..length)
            .map { i -> Random.nextInt(0, idCharPool.size) }
            .map(idCharPool::get)
            .joinToString("")
    }
}
