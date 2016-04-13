package rexstream.rex.collections

import rexstream._
import rexstream.events._
import rexstream.util._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Created by GregRos on 09/03/2016.
  */

private[rexstream] class SortList[T](source: RexPointsList[T], orderingProvider : RexScalar[Ordering[T]]) extends RexPointsList[T] {
    val indexList = new ArrayBuffer[Int]()

    val onInnerChanged = (change: ItemChanged[RexScalar[T]]) => {
        val myMessages : List[ItemChanged[RexScalar[T]]]  =
            change match {
                case ItemAdded(i, newItem) => {
                    List(ItemAdded(insertSort(i), newItem))
                }
                case ItemRemoved(i) => {
                    List(ItemRemoved(removeSort(i)))
                }
                case ItemMutated(i, v, chInfo) => {
                    val pair = repositionItem(i)
                    List(ItemRemoved(pair._1), ItemAdded(pair._2, v))
                }
                case Reset() => {
                    resetOrdering()
                    List(Reset())
                }
                case _ => {
                    throw new Exception()
                }
            }
        myMessages.foreach(x => _change.raise(x))
    }
    onInnerChanged(Reset())

    val onOrderingChanged = (a : Any) => {
        onInnerChanged(Reset())
    }

    val innerToken = source.change += onInnerChanged
    val providerToken = orderingProvider.changed += onOrderingChanged

    private def resetOrdering(): Unit = {
        indexList.clear()
        indexList ++= getResetOrdering()
    }

    private def getResetOrdering()= {
        val ordering = orderingProvider.value
        source.unbind.indices.sortBy(i => source.unbind(i))(ordering)
    }

    private def repositionItem(realIndex: Int) = {
        val oldPos = indexList.indexOf(realIndex)
        indexList.remove(oldPos)
        //we assume the list is sorted
        val rightPos = insertAtRightPosition(realIndex)
        (oldPos, rightPos)
    }

    private def insertSort(realIndex: Int) : Int = {

        for (ix <- indexList.indices.toList) {
            if (indexList(ix) >= realIndex) {
                indexList(ix) += 1
            }
        }
        insertAtRightPosition(realIndex)
    }

    private def insertAtRightPosition(realIndex : Int): Int = {
        val ordering = orderingProvider.value
        val items = source.unbind
        val realItem = items(realIndex)
        val rightPos = {
            val maybeIndex = indexList.indexWhere(x => ordering.compare(realItem, items(x)) <= 0)
            if (maybeIndex < 0) indexList.length else maybeIndex
        }

        indexList.insert(rightPos, realIndex)
        rightPos
    }

    private def removeSort(realIndex: Int): Int = {
        val oldIndex = indexList.indexOf(realIndex)
        indexList.dropItem(realIndex)

        for (ixIx <- indexList.indices) {
            if (indexList(ixIx) >= realIndex) {
                indexList(ixIx) -= 1
            }
        }
        oldIndex
    }

    override def apply(n: Int): RexScalar[T] = source(indexList.indexOf(n))

    /**
      * Constructs a new bindable, initializes it with the `init` initializer, adds it to the `n`-th index in the list, and returns it.
      *
      * @param n The index at which to add.
      * @param init
      * @return
      */
    override def insert(n: Int, init: (RexScalar[T]) => Unit): RexScalar[T] = {
        source.insert(n, init)
    }

    override def clear(): Unit = {
        source.clear()
    }

    override def length: Int = source.length

    override def remove(n: Int): RexScalar[T] = {
        val realIndex = indexList.indexOf(n)
        source.remove(realIndex)
    }

    override def iterator: Iterator[RexScalar[T]] = {
        indexList.map (i => source(i)).iterator
    }

    override def validate(): Unit = {
        val ordered = getResetOrdering()
        if (this.unbind.toList != ordered.map(i => source(i).value).toList) {
            throw Errors.Inconsistency_found("Incorrect ordering")
        }
    }

    override def close(): Unit = {
        innerToken.close()
        providerToken.close()
        super.close()
    }
}
