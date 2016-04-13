package rexstream.rex.collections

import rexstream.rex._
import rexstream.events._
import rexstream.collections._
import rexstream._
/**
  * Created by GregRos on 12/02/2016.
  */
private[rexstream] class MapList[TIn, TOut](val source : RexPointsList[TIn], mapper : RexTransform[TIn, TOut]) extends RexPointsList[TOut] {
    private val outer = new AutoClosingList[RexScalar[TOut]]()
    private val expectInnerChange = new ExpectEntry()

    private val onOuterItemMutated = (sender : RexScalar[TOut]) => (changeInfo : ContextualChangeInfo) => {
        val indexOf = outer.indexOf(sender)
        _change.raise(ItemMutated(indexOf, sender, changeInfo))
    }

    private def convert(b : RexScalar[TIn]) = {
        mapper(b.boundary_>)
    }

    private val onInnerChanged = (changeInfo : ItemChanged[RexScalar[TIn]]) => {
        if (expectInnerChange.canEnter) {
            changeInfo match {
                case ItemAdded(i, v) => outer.insert(i, convert(v))
                case ItemRemoved(i) => outer.remove(i)
                case ItemMutated(_, _, _) => //this is handled elsewhere
                case ItemUpdated(_, _) => throw new Exception() //not supposed to happen
                case Reset() => {
                    outer.clear()
                    for (item <- source) {
                        outer += convert(item)
                    }
                }
                case _ =>
            }
        }
    } : Unit

    private val onOuterChanged = (changeInfo : ItemChanged[RexScalar[TOut]]) => {
        changeInfo match {
            case ItemAdded(i, newItem) => newItem.changed += onOuterItemMutated(newItem)
            case Reset() => //do something...
            case _ =>
        }
    } : Unit


    val innerToken = source.change += onInnerChanged
    change += onOuterChanged
    outer.change ++= change


    onInnerChanged(Reset())
    override def apply(n: Int): RexScalar[TOut] = outer(n)

    override def insert(n: Int, init : RexScalar[TOut] => Unit): RexScalar[TOut] = {
        //suppress change because we handle ItemAdded here, instead of in the handler.
        var outBindable : RexScalar[TOut] = null
        expectInnerChange.noEntry(_ => {
            val result = source.insert(n, b => {
                outBindable = convert(b)
                init(outBindable)
            })
            if (result != null) {
                outer.insert(n, outBindable)
            } else {
                outBindable.close()
                outBindable = null
            }
        })
        outBindable
    }

    override def clear(): Unit = source.clear()

    override def length: Int = source.length

    override def remove(n: Int): RexScalar[TOut] ={
        val item = outer(n)
        source.remove(n)
        item
    }

    override def iterator: Iterator[RexScalar[TOut]] = outer.iterator

    override def close(): Unit = {
        innerToken.close()
        outer.close()
        super.close()
    }

    override def validate(): Unit = {
        outer.foreach(b => b.consistencyCheck())
    }
}
