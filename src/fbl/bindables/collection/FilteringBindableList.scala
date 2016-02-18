package fbl.bindables.collection

import fbl.Bindable
import fbl.bindables._
import fbl.api._
import fbl.events._
import fbl.infrastructure._
import scala.collection._
/**
  * Created by GregRos on 13/02/2016.
  */
class FilteringBindablesList[T](inner : BindableList[T], selector : BindableMap[T, Boolean]) extends BindableList[T] {
    val filterResults = new MappingBindableList[T, Boolean](inner, selector)
    val indexList = mutable.ArrayBuffer[Int]()
    private val onInnerResultsChanged: (BufferChangedNotifier[Bindable[Boolean]], BufferChanged[Bindable[Boolean]]) => Unit = (_, change) => {
        val myMessage : Option[BufferChanged[Bindable[T]]] =
            change match {
                case ItemAdded(i, b) => {
                    insertFilter(i, b.value).map(i => ItemAdded(i, this(i)))
                }
                case ItemMutated(i, b, _) => {
                    var firstGeq = indexList.indexWhere(n => n >= i)
                    val wasFiltered = firstGeq != -1 && indexList(firstGeq) == i
                    firstGeq = if (firstGeq == -1) indexList.length else firstGeq
                    if (b.value && wasFiltered) {
                        indexList.insert(firstGeq, i)
                        Some(ItemAdded(firstGeq, this(firstGeq)))
                    } else if (!b.value && wasFiltered) {
                        indexList.remove(firstGeq)
                        Some(ItemRemoved(firstGeq))
                    } else {
                        None
                    }
                }

                case Reset() => {
                    indexList.clear()
                    for (n <- 0 until filterResults.length ) {
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
        myMessage match {
            case Some(m) => _change.raise(m)
            case _ =>
        }
    }
    filterResults.change += onInnerResultsChanged

    onInnerResultsChanged(filterResults, Reset())

    private def removeFilter(realIndex : Int): Option[Int] = {
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

    private def insertFilter(realIndex : Int, passed : Boolean) : Option[Int] = {
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

    override def apply(n: Int): Bindable[T] = {
        inner(indexList(n))
    }

    override def insert(n: Int, init : Bindable[T] => Unit): Bindable[T] = {
        val realIndex ={
            if (indexList.isEmpty && n == 0) {
                0
            } else if (n == indexList.length) {
                indexList.last + 1
            } else indexList(n)
        }
        inner.insert(realIndex, init)
    }

    override def clear(): Unit = {
        inner.clear()
    }

    override def length: Int = indexList.length

    override def remove(n: Int): Bindable[T] = {
        inner.remove(indexList(n))
    }

    override def iterator: Iterator[Bindable[T]] = {
        indexList.map(i => inner(i)).iterator
    }
}
