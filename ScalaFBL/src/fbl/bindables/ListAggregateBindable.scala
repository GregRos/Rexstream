package fbl.bindables
import fbl._
import fbl.collections._
import fbl.events._
import scala.collection._



private[fbl] class ListAggregateBindable[T, TOut](inner : CollectionBindable[T], var operator: Operator[T, TOut])
    extends ValueBindable[TOut] {
    private val _valueCache = mutable.ArrayBuffer[T]()
    private var _value : TOut = operator.zero
    override def canWrite = false
    override def canRead = true
    override val parent = inner
    private val onInnerChanged = (changeInfo : ItemChanged[ValueBindable[T]]) => {
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
            changed.raise(LastValueInfo(oldValue))
        }
    } : Unit

    inner.bindingPoints.change += onInnerChanged
    onInnerChanged(Reset())
    override def setValueWithContext(x: TOut)(context: ContextualChangeInfo): Unit = {
        throw Errors.Cannot_write
    }

    override def value: TOut = if (isDisposed) throw Errors.Object_closed(this) else _value
}


