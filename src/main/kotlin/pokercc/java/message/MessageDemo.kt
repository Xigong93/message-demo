package pokercc.java.message

import java.util.*

fun main(args: Array<String>) {
    val threads = arrayListOf<Thread>()
    Thread {

    }.also {
        threads.add(it)
    }
    Thread {
        println("请输入内容")
        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {

            val line = scanner.nextLine()
        }

    }.also {
        threads.add(it)
    }
}