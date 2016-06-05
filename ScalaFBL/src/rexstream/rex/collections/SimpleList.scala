package rexstream.rex.collections

import rexstream._
import rexstream.rex._
import rexstream.collections._
import rexstream.events._
import scala.collection._
/**
  * Created by GregRos on 06/02/2016.
  */

private[rexstream] class SimpleList[T](ctor : Unit => RexScalar[T]) extends RexPointsList[T] {
    protected val inner = new AutoClosingList[RexScalar[T]]
    val innerToken = inner.change ++= _change
    private val onBindableChanged = (SingleValueBindable : RexScalar[T]) => (changeInfo : ContextualChangeData) => {
        val indexOf = inner.indexOf(SingleValueBindable)
        _change.raise(ItemMutated(indexOf, SingleValueBindable, changeInfo))
    }

    private def register(newBindable : RexScalar[T]) = {
        newBindable.changed += onBindableChanged(newBindable)
    }

    override def insert(n : Int, init : RexScalar[T] => Unit): RexScalar[T] = {
        val newBindable = ctor(())
        init(newBindable)
        register(newBindable)
        inner.insert(n, newBindable)

        newBindable
    }

    override def remove(n : Int) : RexScalar[T] = {
        val oldItem = inner(n)
        inner.remove(n)
        oldItem
    }

    override def length = inner.length

    override def clear() = {
        inner.clear()
    }

    override def apply(n : Int) ={
        inner(n)
    }

    override def iterator: Iterator[RexScalar[T]] = inner.iterator

    override def close(): Unit = {
        inner.close()
        super.close()
    }

    override protected[rexstream] def validate(): Unit = {
        inner.foreach(b => b.consistencyCheck())
    }
}
