package pokercc.java.message

import java.util.*

fun main(args: Array<String>) {
    Looper.prepareMainLooper()
    val handler = Handler()

    Thread {
        println("请输入内容(输入q退出)")
        val scanner = Scanner(System.`in`)
        var quit = false
        while (scanner.hasNextLine() && !quit) {
            val line = scanner.nextLine()
            println("${Thread.currentThread().name}  输入的:$line")

            handler.send(Message(0, runnable = Runnable {
                println("${Thread.currentThread().name}  接收到:$line")
            }))

            when (line) {
                "q" -> {
                    Looper.mainLooper().quit()
                    quit = true
                }
            }
        }

    }.also {
        it.name = "子线程"
        it.start()
    }
    Looper.loop()

}