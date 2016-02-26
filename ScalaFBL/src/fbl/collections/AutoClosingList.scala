package fbl.collections

import fbl.events._
import fbl.util._

import scala.collection._
/**
  * Created by GregRos on 13/02/2016.
  */
private[fbl] class AutoClosingList[T] (inner : ObservableList[T]) extends mutable.Buffer[T] with ItemChangedNotifier[T] with AutoCloseable {

    def this() = this(new ObservableList[T]())

    inner.change += ((change: ItemChanged[T]) => {
        _change.raise(change)
    })

    override def +=(elem: T): AutoClosingList.this.type = {
        inner += elem
        this
    }

    override def update(n: Int, newelem: T): Unit = {
        val oldElem = this(n)
        inner.update(n, newelem)
        oldElem match {
            case (x : AutoCloseable) => x.close()
            case _ => //not closeable
        }
    }

    override def clear(): Unit = {
        val closeable = inner.ofType[AutoCloseable].toList
        inner.clear()
        closeable.foreach(x => x.close())
    }

    override def length: Int = inner.length

    override def remove(n: Int): T = {
        val item = inner(n)
        inner.remove(n)
        item match {
            case (item : AutoCloseable) => item.close()
            case _ =>
        }
        item
    }

    override def +=:(elem: T): AutoClosingList.this.type = {
        inner.+=:(elem)
        this
    }

    override def apply(n: Int): T = inner(n)

    override def insertAll(n: Int, elems: scala.Traversable[T]): Unit = {
        inner.insertAll(n, elems)
    }

    override def iterator: scala.Iterator[T] = inner.iterator

    override def close(): Unit = {
        _change.close()
        clear()
    }
}
