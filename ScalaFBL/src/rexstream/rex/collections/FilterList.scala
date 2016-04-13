package rexstream.rex.collections

import rexstream._
import rexstream.events._
import scala.collection._

/**
  * Created by GregRos on 13/02/2016.
  */
private[rexstream] class FilterList[T](source: RexPointsList[T], selector: RexTransform[T, Boolean]) extends RexPointsList[T] {
    val filterResults = new MapList[T, Boolean](source, selector)
    val indexList = mutable.ArrayBuffer[Int]()
    private val onInnerResultsChanged: (ItemChanged[RexScalar[T]]) => Unit = (change) => {
        val myMessage: Option[ItemChanged[RexScalar[T]]] = {
            change match {
                case ItemAdded(i, b) => {
                    insertFilter(i, filterResults(i).value).map(i => ItemAdded(i, this (i)))
                }

                case ItemMutated(i, b, innerChange) => {
                    val isFiltered = filterResults(i).value
                    var firstGeq = indexList.indexWhere(n => n >= i)
                    val wasFiltered = firstGeq != -1 && indexList(firstGeq) == i
                    firstGeq = if (firstGeq == -1) indexList.length else firstGeq
                    if (isFiltered && !wasFiltered) {
                        indexList.insert(firstGeq, i)
                        Some(ItemAdded(firstGeq, this (firstGeq)))
                    } else if (!isFiltered && wasFiltered) {
                        indexList.remove(firstGeq)
                        Some(ItemRemoved(firstGeq))
                    } else if (isFiltered && wasFiltered) {
                        Some(ItemMutated(firstGeq, b, innerChange))
                    } else {
                        None
                    }
                }

                case Reset() => {
                    indexList.clear()
                    for (n <- 0 until filterResults.length) {
                        if (filterResults(n).value) {
                            indexList += n
                        }
                    }
                    Some(Reset())
                }

                case ItemRemoved(i) => {
                    removeFilter(i).map(i => ItemRemoved(i))
                }
            }
        }
        myMessage match {
            case Some(m) => _change.raise(m)
            case _ =>
        }
    }
    val innerToken = source.change += onInnerResultsChanged

    onInnerResultsChanged(Reset())


    private def removeFilter(realIndex: Int): Option[Int] = {
        var maybeOldIndex = Option.empty[Int]
        val firstGeq = indexList.indexWhere(n => n >= realIndex)
        if (firstGeq == -1) {
            return None
        }
        if (indexList(firstGeq) == realIndex) {
            indexList.remove(firstGeq)
            maybeOldIndex = Some(firstGeq)
        }
        for (n <- firstGeq until indexList.length) {
            indexList(n) -= 1
        }
        maybeOldIndex
    }

    private def insertFilter(realIndex: Int, passed: Boolean): Option[Int] = {
        var firstGeq = indexList.indexWhere(n => n >= realIndex)
        if (firstGeq == -1) {
            firstGeq = indexList.length
        }
        var maybeNewIndex = Option.empty[Int]
        if (passed) {
            indexList.insert(firstGeq, realIndex)
            maybeNewIndex = Some(firstGeq)
            firstGeq += 1
        }
        for (n <- firstGeq until indexList.length) {
            indexList(n) += 1
        }
        maybeNewIndex
    }

    override def apply(n: Int): RexScalar[T] = {
        source(indexList(n))
    }


    private def getRealIndex(n: Int) : Int = {
        if (indexList.isEmpty && n == 0) {
            0
        } else if (n == indexList.length) {
            source.length
        } else indexList(n)
    }

    override def insert(n: Int, init: RexScalar[T] => Unit): RexScalar[T] = {
        val realIndex = getRealIndex(n)
        val r = source.insert(realIndex, init)
        if (filterResults(realIndex).value) r else null
    }

    override def clear(): Unit = {
        source.clear()
    }

    override def length: Int = indexList.length

    override def remove(n: Int): RexScalar[T] = {
        source.remove(indexList(n))
    }

    override def iterator: Iterator[RexScalar[T]] = {
        indexList.map(i => source(i)).iterator
    }

    override def close(): Unit = {
        filterResults.close()
        indexList.clear()
        innerToken.close()
        super.close()
    }

    override def insertAll(n: Int, inits: Traversable[RexScalar[T] => Unit]): scala.Traversable[RexScalar[T]] = {
        val realIndex = getRealIndex(n)
        source.insertAll(realIndex, inits)
    }

    override def validate(): Unit = {
        val filtered2 = source.zipWithIndex.filter({ case (v, i) => filterResults(i).value }).map(_._1)
        if (filtered2.toList != this.toList) {
            throw Errors.Inconsistency_found("The list hasn't been filtered correctly.")
        }
        //Note that we don't create any sub-bindables, so we don't need to validate them.
    }
}
