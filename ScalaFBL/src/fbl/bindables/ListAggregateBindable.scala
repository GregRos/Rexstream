package fbl.bindables
import fbl._
import fbl.collections._
import fbl.events._
import scala.collection._



private[fbl] class ListAggregateBindable[T, TOut](inner : ListBindable[T], var operator: Operator[T, TOut]) extends Bindable[TOut] {
    private val _valueCache = mutable.ArrayBuffer[T]()
    private var _value : TOut = operator.zero
    private val onInnerChanged = (changeInfo : ItemChanged[Bindable[T]]) => {
        val info = if (!operator.isOrderInvariant) Reset() else changeInfo
        val oldValue = _value
        info match {
            case ItemAdded(i, b : Bindable[T]) =>
                _valueCache.insert(i, b.value)
                _value = operator.plus(_value, b.value)
            case ItemRemoved(i) =>
                val oldValue = _valueCache(i)
                _valueCache.remove(i)
                _value = operator.minus(_value, oldValue)
            case ItemMutated(i, b : Bindable[T], mutationInfo) =>
                val oldValue = _valueCache(i)
                _valueCache.update(i, b.value)
                _value = operator.plus(operator.minus(_value, oldValue), b.value)
            case ItemUpdated(i, b : Bindable[T]) =>
                val oldValue = _valueCache(i)
                _valueCache(i) = b.value
                _value = operator.plus(operator.minus(_value, oldValue), b.value)
            case Reset() =>
                _value = operator.zero
                for (item <- inner.value) {
                    _valueCache += item
                    _value = operator.plus(_value, item)
                }
        }
        if (operator.equal(oldValue, _value)) {
            changed.raise(LastValueInfo(oldValue))
        }
    } : Unit

    inner.bindingPoints.change += onInnerChanged
    onInnerChanged(Reset())
    override def setValueWithContext(x: TOut)(context: ContextualChangeInfo): Unit = throw Errors.Cannot_write

    override def value: TOut = _value
}


