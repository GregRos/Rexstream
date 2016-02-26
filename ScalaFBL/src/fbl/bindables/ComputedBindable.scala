package fbl.bindables

import fbl._
import fbl.events.ContextualChangeInfo
/**rgd
  *
  * Created by GregRos on 12/02/2016.
  * ==blah==
  *
  */
private[fbl] class ComputedBindable[T](generator : Unit => T) extends ValueBindable[T] {
    var _lastValue : Option[T] = None
    changed += (context => _lastValue = Some(generator(())))
    override def canWrite = false
    override def canRead = true
    override val parent = null
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit ={
        throw Errors.Cannot_write
    }

    override def value: T = {
        if (isDisposed) throw Errors.Object_closed(this)
        if (_lastValue.isEmpty) {
            _lastValue = Some(generator(()))
        }
        _lastValue.get
    }
}
