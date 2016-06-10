package rexstream.rex

import rexstream.RexVector

/**
  * Created by lifeg on 08/06/2016.
  */
trait DefaultRexVector[T] extends RexVector[T] {
    protected def itemsList = elements.unbind

    override def update(n: Int, newelem: T): Unit = {
        makeSureNotClosed()
        itemsList.update(n, newelem)
    }

    override def clear(): Unit = {
        makeSureNotClosed()
        itemsList.clear()
    }

    override def length: Int = {
        makeSureNotClosed()
        itemsList.length
    }

    override def remove(n: Int): T = {
        makeSureNotClosed()
        itemsList.remove(n)
    }

    override def +=:(elem: T) = {
        makeSureNotClosed()
        itemsList.+=:(elem)
        this
    }

    override def +=(elem: T) = {
        makeSureNotClosed()
        itemsList.+=(elem)
        this
    }

    override def apply(n: Int): T = {
        makeSureNotClosed()
        itemsList.apply(n)
    }

    override def insertAll(n: Int, elems: Traversable[T]): Unit = {
        makeSureNotClosed()
        itemsList.insertAll(n, elems)
    }

    override def iterator: Iterator[T] = {
        makeSureNotClosed()
        itemsList.iterator
    }

    abstract override def close(): Unit = {
        elements.close()
        super.close()
    }


    override def consistencyCheck(): Unit = {
        elements.validate()
    }
}
