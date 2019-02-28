package pokercc.java.message

import org.omg.CORBA.Object
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class Message(
    var what: Int,
    var obj: Object? = null,
    var runnable: Runnable? = null

) {
    lateinit var target: Handler

}

open class Handler(private val looper: Looper = Looper.myLooper()!!) {
    open fun handleMessage(message: Message) {
        if (message.runnable != null) {
            message.runnable!!.run()
        }

    }

    fun send(message: Message) {
        message.target = this
        looper.messageQueue.enqueue(message)

    }
}

class Looper private constructor() {
    var messageQueue = MessageQueue()

    fun quit() {
        messageQueue.quit()
    }

    companion object {
        private val looperThreadLocal = ThreadLocal<Looper>()
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
                val message = myLooper.messageQueue.next() ?: break
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
    private val queue: Deque<Message> = LinkedList<Message>()
    //    private val lock = ReentrantLock()
    private var quit = false
    private val lock = Object()
    fun enqueue(message: Message) {

        val empty = queue.isEmpty()
        queue.add(message)
        if (empty) {

            synchronized(lock) {
                println("有新消息，已经解锁")
                lock.notifyAll()
            }
        }

//        if (lock.isLocked) {
//
//            lock.unlock()
//            println("有信消息，已经解锁")
//        }
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
//            lock.lock()
        }
        return queue.poll()

    }

    fun quit() {
        quit = true
    }
}