package rexstream.rex

import rexstream.{AnyRex, RexPointsList, RexVector, NaryDependency}

/**
  * Created by GregRos on 26/02/2016.
  */
abstract class BaseRexVector[T](pointsList : RexPointsList[T], parent : AnyRex = null) extends RexVector[T] {
    protected val itemsList = pointsList.unbind

    protected[rexstream] override val points: RexPointsList[T] = pointsList

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

    override def +=:(elem: T): BaseRexVector.this.type = {
        makeSureNotClosed()
        itemsList.+=:(elem)
        this
    }

    override def +=(elem: T): BaseRexVector.this.type = {
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

    override def close(): Unit = {
        pointsList.close()
        super.close()
    }

    override val dependency: NaryDependency = if (parent == null) NaryDependency.empty else NaryDependency.source(parent)

    /**
      * Validates this bindable's integrity. This is a debugging feature.
      *
      * @return
      */
    override def consistencyCheck(): Unit = {
        pointsList.validate()
        if (parent != null) {
            parent.consistencyCheck()
        }
    }

    override def canWrite: Boolean = true

    override def canRead: Boolean = true
}
