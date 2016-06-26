package rexstream.events

import scala.collection.immutable.HashSet
import scala.collection.mutable

/**
  * Created by GregRos on 06/02/2016.
  */
class Event[TParam]() extends AbsEvent[TParam] {
    private class Token(f: (TParam) => Unit) extends AutoCloseable {
        def close(): Unit = {
            unsubscribe(f)
        }
    }

    private var suppress = false

    def withSuppress(f : Unit => Unit) = {
        suppress = true
        f(())
        suppress = false
    }

    val callbacks = mutable.MutableList[(TParam) => Unit]()

    override def subscribe(f: (TParam) => Unit): AutoCloseable = {
        callbacks += f
        new Token(f)
    }

    override def unsubscribe(f: (TParam) => Unit): Unit = {
        callbacks.drop(callbacks.indexWhere(g => g == f))
    }

    override def raise(arg: TParam) : Unit =   {
        if (suppress) return
        callbacks foreach (f => f(arg))
    }

    override def close() = {
        callbacks.clear()
    }
}
