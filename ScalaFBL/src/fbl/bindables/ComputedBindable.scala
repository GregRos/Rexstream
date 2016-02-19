package fbl.bindables

import fbl.Bindable
import fbl.events.ContextualChangeInfo
/**
  * Created by GregRos on 12/02/2016.
  */
class ComputedBindable[T](generator : Unit => T) extends Bindable[T] {
    var _lastValue : Option[T] = None
    changed += (context => _lastValue = Some(generator(())))
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit = throw new CannotWriteException()

    override def value: T = {
        if (_lastValue.isEmpty) {
            _lastValue = Some(generator(()))
        }
        _lastValue.get
    }
}
