package fbl.bindables

import fbl.{AnyBindable, ListBindable, BindingPointsList}

/**
  * Created by GregRos on 26/02/2016.
  */
abstract class BaseListBindable[T](pointsList : BindingPointsList[T]) extends ListBindable[T] {
    protected val itemsList = pointsList.unbind

    override val bindingPoints: BindingPointsList[T] = pointsList

    override def update(n: Int, newelem: T): Unit = itemsList.update(n, newelem)

    override def clear(): Unit = itemsList.clear()

    override def length: Int = itemsList.length

    override def remove(n: Int): T = itemsList.remove(n)

    override def +=:(elem: T): BaseListBindable.this.type = {
        itemsList.+=:(elem)
        this
    }

    override def +=(elem: T): BaseListBindable.this.type = {
        itemsList.+=(elem)
        this
    }

    override def apply(n: Int): T = itemsList.apply(n)

    override def insertAll(n: Int, elems: Traversable[T]): Unit = itemsList.insertAll(n, elems)

    override def iterator: Iterator[T] = itemsList.iterator

    override def canWrite: Boolean = true

    override def canRead: Boolean = true
}
