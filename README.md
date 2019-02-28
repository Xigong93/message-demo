#  安卓的消息队列机制demo
本demo是基于java，使用kotlin，实现了安卓的消息队列机制。
功能比较简陋
有一点与android的不同，在没有消息时，android使用linux内核epoll机制 实现线程睡眠，而本demo使用的线程锁
