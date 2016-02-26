package fbl
import scala.collection._
import fbl.events._
/**
  * Created by GregRos on 26/02/2016.
  */
trait ListBindable[T] extends mutable.Buffer[T] with AnyBindable {
    type ChangeInfo = ItemChanged[T]
    override def toString = {
        s">${super.toString}<"
    }

    val bindingPoints : BindingPointsList[T]
}
