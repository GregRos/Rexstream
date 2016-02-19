package fbl
import scala.collection._
/**
  * Created by GregRos on 19/02/2016.
  */
trait ListBindable[T] extends Bindable[mutable.Buffer[T]] {
    val bindingPoints : BindingPointsList[T]
}
