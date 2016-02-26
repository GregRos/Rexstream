package fbl.bindables.collections

import fbl.bindables._
import fbl.events._
import fbl.collections._
import fbl._
/**
  * Created by GregRos on 12/02/2016.
  */
private[fbl] class MapList[TIn, TOut](inner : BindingPointsList[TIn], convert : BindableMap[TIn, TOut]) extends BindingPointsList[TOut] {
    private val outer = new AutoClosingList[ValueBindable[TOut]]()
    private val expectInnerChange = new ExpectEntry()

    private val onOuterItemMutated = (sender : ValueBindable[TOut]) => (changeInfo : ContextualChangeInfo) => {
        val indexOf = outer.indexOf(sender)
        _change.raise(ItemMutated(indexOf, sender, changeInfo))
    }

    private val onInnerChanged = (changeInfo : ItemChanged[ValueBindable[TIn]]) => {
        if (expectInnerChange.canEnter) {
            changeInfo match {
                case ItemAdded(i, v) => outer.insert(i, convert(v))
                case ItemRemoved(i) => outer.remove(i)
                case ItemMutated(_, _, _) => //this is handled elsewhere
                case ItemUpdated(_, _) => throw new Exception() //not supposed to happen
                case Reset() => {
                    outer.clear()
                    for (item <- inner) {
                        outer += convert(item)
                    }
                }
                case _ =>
            }
        }
    } : Unit


    private val onOuterChanged = (changeInfo : ItemChanged[ValueBindable[TOut]]) => {
        changeInfo match {
            case ItemAdded(i, newItem) => newItem.changed += onOuterItemMutated(newItem)
            case Reset() => //do something...
            case _ =>
        }
    } : Unit


    inner.change += onInnerChanged
    outer.change ++= change
    change += onOuterChanged

    onInnerChanged(Reset())
    override def apply(n: Int): ValueBindable[TOut] = outer(n)

    override def insert(n: Int, init : ValueBindable[TOut] => Unit): ValueBindable[TOut] = {
        //suppress change because we handle ItemAdded here, instead of in the handler.
        expectInnerChange.noEntry(_ => {
            inner.insert(n, b => {
                val outerB = convert(b)
                init(outerB)
                outer.insert(n, outerB)
            })
        })
        outer(n)
    }

    override def clear(): Unit = inner.clear()

    override def length: Int = inner.length

    override def remove(n: Int): ValueBindable[TOut] ={
        val item = outer(n)
        inner.remove(n)
        item
    }

    override def iterator: Iterator[ValueBindable[TOut]] = outer.iterator
}
