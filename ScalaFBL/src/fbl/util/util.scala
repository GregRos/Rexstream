package fbl
import scala.collection.generic._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.reflect._

package object util {
    implicit class IterableExt[T](inner : Iterable[T]) {
        def choose[That, TOut](selector : T => Option[TOut])(implicit buider : CanBuildFrom[T, TOut, That]) = {
            val arr = Array.empty[Int]
            inner.map(selector).filter(x => x.isDefined).map(x => x.get)
        }

        def ofType[TOut : ClassTag] = {
            inner.filter(x => classTag[TOut].runtimeClass.isInstance(x)).map(x => x.asInstanceOf[TOut])
        }
    }
}
