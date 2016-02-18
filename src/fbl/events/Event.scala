package fbl.events

import scala.collection.immutable.HashSet

/**
  * Created by GregRos on 06/02/2016.
  */
class Event[TOwner, TParam](owner: TOwner) extends AbsEvent[TOwner, TParam] {
    private class Token(f: (TOwner, TParam) => Unit) extends AutoCloseable {
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

    var callbacks = HashSet[(TOwner, TParam) => Unit]()

    override def subscribe(f: (TOwner, TParam) => Unit): AutoCloseable = {
        callbacks += f;
        new Token(f)
    }

    override def unsubscribe(f: (TOwner, TParam) => Unit): Unit = {
        callbacks -= f
    }

    override def raise(arg: TParam) : Unit = {
        if (suppress) return
        callbacks foreach (f => f(owner, arg))
    }

    override def close() = {
        callbacks = HashSet.empty
    }
}
