/**
  * Created by GregRos on 06/02/2016.
  */
package fbl.bindables


import fbl._
import fbl.events._

class ConvertBindable[TIn, TOut](
    inner: ValueBindable[TIn],
    convertIn: Option[TOut => TIn],
    convertOut: Option[TIn => TOut]) extends ValueBindable[TOut] {
    private var _lastValue: Option[TOut] = None
    private val token = inner.changed.subscribe(args => {
        _lastValue = None
        this.changed.raise(null)
    })

    override def canRead = convertOut.isDefined && inner.canRead

    override def canWrite = convertIn.isDefined && inner.canWrite

    override def parent = inner

    override def close() ={
        token.close()
        super.close()
    }

    override def value =
        _lastValue match {
            case Some(v) => v
            case None =>
                val v = convertOut.get(inner.value)
                _lastValue = Some(v)
                v
        }

    override def setValueWithContext(v: TOut)(context: ContextualChangeInfo) = {
        _lastValue = Some(v)
        inner.value = convertIn.get(v)
    }
}




