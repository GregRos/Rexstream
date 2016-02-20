package fbl.bindables
import fbl._
import fbl.bindables.collections._
import fbl.events._
import scala.collection._
/**
  * Created by GregRos on 19/02/2016.
  */
private[fbl] class SimpleListBindable[T](inner : SimpleList[T]) extends ListBindable[T] {
    def this() = this(new SimpleList[T](Unit => new VarBindable[T]()))
    protected val itemsList = inner.unbind
    itemsList.change += ((change) => changed.raise(change))
    changed.raise(Reset())
    override def setValueWithContext(x: mutable.Buffer[T])(context: ContextualChangeInfo): Unit = {
        val change =
            context match {
                case change : ItemChanged[T] => change
                case _ => Reset[T]()
            }
        change.applyOn(itemsList, Unit => x)
    }

    override def value: mutable.Buffer[T] = itemsList

    override val bindingPoints: BindingPointsList[T] = inner
}
