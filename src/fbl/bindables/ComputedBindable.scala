package fbl.bindables

import fbl.Bindable
import fbl.infrastructure._

/**
  * Created by GregRos on 12/02/2016.
  */
class ComputedBindable[T](generator : Unit => T) extends Bindable[T] {
    var _lastValue : Option[T] = None
    changed += ((sender, context) => _lastValue = Some(generator.apply()))
    override def setValueWithContext(x: T)(context: ContextualChangeInfo): Unit = throw new CannotWriteException()

    override def value: T = {
        if (_lastValue.isEmpty) {
            _lastValue = Some(generator.apply())
        }
        _lastValue.get
    }
}
