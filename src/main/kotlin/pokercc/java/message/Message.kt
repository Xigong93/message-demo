package pokercc.java.message

import org.omg.CORBA.Object
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class Message(
    var what: Int,
    var obj: Object? = null,
    var runnable: Runnable? = null

) {
    // 标记了是从哪个handler发出的
    lateinit var target: Handler

}

open class Handler(private val looper: Looper = Looper.myLooper()!!) {
    /**
     * 处理消息
     */
    open fun handleMessage(message: Message) {
        if (message.runnable != null) {
            message.runnable!!.run()
        }

    }

    /**
     * 发送消息
     */
    fun send(message: Message) {
        message.target = this
        looper.messageQueue.enqueue(message)

    }
}

class Looper private constructor() {
    // 消息队列
    var messageQueue = MessageQueue()

    fun quit() {
        messageQueue.quit()
    }

    companion object {
        // 使用ThreadLocal 保存所有的Looper
        private val looperThreadLocal = ThreadLocal<Looper>()
        // 主线程的looper，也就是第一个初始化的
        private var mainLooper: Looper? = null

        @JvmStatic
        fun myLooper(): Looper? {
            return looperThreadLocal.get()
        }

        @JvmStatic
        fun mainLooper(): Looper {
            return mainLooper!!

        }

        @JvmStatic
        fun prepare() {
            if (looperThreadLocal.get() == null) {
                looperThreadLocal.set(Looper())
            } else {
                throw IllegalStateException("looper exists in ${Thread.currentThread().name} thread")
            }

        }

        @JvmStatic
        fun loop() {
            val myLooper: Looper = myLooper() ?: throw IllegalStateException("looper not exists")
            while (true) {
                // 没有消息，是会睡眠的哟
                val message = myLooper.messageQueue.next() ?: break
                // 注意这个target是谁
                message.target.handleMessage(message)

            }
        }


        @JvmStatic
        fun prepareMainLooper() {
            if (mainLooper == null) {
                mainLooper = Looper()
                looperThreadLocal.set(mainLooper)
            } else {
                throw IllegalStateException("looper exists in main thread")
            }
        }
    }
}

class MessageQueue {
    // 消息队列
    private val queue: Deque<Message> = LinkedList<Message>()
    private var quit = false
    // 线程锁
    private val lock = Object()

    fun enqueue(message: Message) {

        val empty = queue.isEmpty()
        queue.add(message)
        // 如果之前消息队列，为空，则唤醒线程
        if (empty) {
            synchronized(lock) {
                println("有新消息，已经解锁")
                lock.notifyAll()
            }
        }


    }

    fun next(): Message? {
        if (queue.isEmpty()) {
            if (quit) {
                println("已退出")
                return null
            }
            println("消息队列为空，已经锁定")
            synchronized(lock) {
                lock.wait()
            }
        }
        return queue.poll()

    }

    fun quit() {
        quit = true
    }
}