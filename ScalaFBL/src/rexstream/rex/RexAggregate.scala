package rexstream.rex
import rexstream._
import rexstream.collections._
import rexstream.events._
import rexstream.operations.Operator

import scala.collection._
import scala.collection.mutable.ArrayBuffer

private[rexstream] class RexAggregate[T, TOut](inner : RexVector[T], var operatorProvider: RexScalar[Operator[T, TOut]])
    extends RexScalar[TOut] with DefaultRex {
    private val _valueCache = mutable.ArrayBuffer[T]()
    private var _value : TOut = _
    override def canWrite = false
    override def canRead = true
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.vectorAggregate
    }
    private val onInnerChanged = (changeInfo : ItemChanged[RexScalar[T]]) => {
        val operator = operatorProvider.value
        val info = if (!operator.isOrderInvariant) Reset() else changeInfo
        val oldValue = _value
        info match {
            case ItemAdded(i, b) =>
                _valueCache.insert(i, b.value)
                _value = operator.plus(_value, b.value)
            case ItemRemoved(i) =>
                val oldValue = _valueCache(i)
                _valueCache.remove(i)
                _value = operator.minus(_value, oldValue)
            case ItemMutated(i, b, mutationInfo) =>
                val oldValue = _valueCache(i)
                _valueCache.update(i, b.value)
                _value = operator.plus(operator.minus(_value, oldValue), b.value)
            case ItemUpdated(i, b) =>
                val oldValue = _valueCache(i)
                _valueCache(i) = b.value
                _value = operator.plus(operator.minus(_value, oldValue), b.value)
            case Reset() =>
                _value = operator.zero
                for (item <- inner) {
                    _valueCache += item
                    _value = operator.plus(_value, item)
                }
        }
        if (operator.equal(oldValue, _value)) {
            changed.raise(LastValueData(oldValue))
        }
    } : Unit

    private val onOperatorChanged = (changeInfo : Any) => {
        onInnerChanged(Reset())
    }

    inner.elements.change += onInnerChanged
    operatorProvider.changed += onOperatorChanged
    onInnerChanged(Reset())
    override def value_=(x: TOut): Unit = {
        throw Errors.Cannot_write
    }

    override def value: TOut = {
        makeSureNotClosed()
        _value
    }

    override def close(): Unit = {
        inner.elements.change -= onInnerChanged
        operatorProvider.changed -= onOperatorChanged
        super.close()
    }
    override val depends = DependencyProvider.sourceAndProvider(inner, operatorProvider)

    /**
      * Validates this bindable's integrity. This is a debugging feature.
      *
      * @return
      */
    override def consistencyCheck(): Unit = {
        val operator = operatorProvider.value
        var value = operator.zero
        for (i <- inner.indices) {
            if (_valueCache(i) != inner(i)) {
                throw Errors.Inconsistency_found(s"The cached value at index $i doesn't match the true value at that index.")
            }
            value = operator.plus(value, inner(i))
        }
        if (value != _value) {
            throw Errors.Inconsistency_found(s"All cached values match, but the result isn't up to date.")
        }
        inner.consistencyCheck()
    }
}


