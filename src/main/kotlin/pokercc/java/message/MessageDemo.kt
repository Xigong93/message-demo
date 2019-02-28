package pokercc.java.message

import java.util.*

fun main(args: Array<String>) {
    val handler = Handler()

    Thread {
        println("请输入内容")
        val scanner = Scanner(System.`in`)
        while (scanner.hasNextLine()) {
            val line = scanner.nextLine()
            val message = Message(0, runnable = Runnable {
                print("${Thread.currentThread().name}  接收到:$line")
            })
            handler.send(message)
        }

    }.also {
        it.name = "子线程"
        it.start()
    }
    Looper.prepareMainLooper()
    Looper.loop()

}