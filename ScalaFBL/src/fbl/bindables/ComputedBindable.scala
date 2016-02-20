package fbl.bindables

import fbl.{Errors, Bindable}
import fbl.events.ContextualChangeInfo
/**rgd
  *
  * Created by GregRos on 12/02/2016.
  * ==blah==
  *
  */
private[fbl] class ComputedBindable[T](generator : Unit => T) extends Bindable[T] {
    var _lastValue : Option[T] = None
    changed += (context => _lastValue = Some(generator(())))
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit = throw Errors.Cannot_write

    override def value: T = {
        if (_lastValue.isEmpty) {
            _lastValue = Some(generator(()))
        }
        _lastValue.get
    }
}
