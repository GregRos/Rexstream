package fbl.bindables.collection

import fbl.Bindable
import fbl.bindables._
import fbl.events.{ObservableCollection, Reset, ItemMutated, BufferChanged}
import fbl.infrastructure._
import fbl._
/**
  * Created by GregRos on 12/02/2016.
  */
class MappingBindableList[TIn, TOut](inner : BindableList[TIn], convert : BindableMap[TIn, TOut]) extends BindableList[TOut] {
    private val outer = new AutoClosingList(ObservableCollection.empty[Bindable[TOut]])
    private val expectInnerChange = new ExpectEntry()

    private val onOuterItemMutated = (sender : Bindable[TOut], changeInfo : ContextualChangeInfo) => {
        val indexOf = outer.indexOf(sender)
        _change.raise(ItemMutated(indexOf, sender, changeInfo))
    }

    private val onInnerChanged = (sender : Any, changeInfo : BufferChanged[Bindable[TIn]]) => {
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


    private val onOuterChanged = (sender : Any, changeInfo : BufferChanged[Bindable[TOut]]) => {
        changeInfo match {
            case ItemAdded(i, newItem) => newItem.changed += onOuterItemMutated
            case Reset() => //do something...
            case _ =>
        }
    } : Unit


    inner.change += onInnerChanged
    outer.change ++= change
    change += onOuterChanged

    onInnerChanged(null, Reset())
    override def apply(n: Int): Bindable[TOut] = outer(n)

    override def insert(n: Int, init : Bindable[TOut] => Unit): Bindable[TOut] = {
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

    override def remove(n: Int): Bindable[TOut] ={
        val item = outer(n)
        inner.remove(n)
        item
    }

    override def iterator: Iterator[Bindable[TOut]] = outer.iterator
}
