package rexstream
import scala.collection.generic._
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._
import scala.reflect._

private[rexstream] object util {
    implicit class IterableExt[T](inner : Iterable[T]) {
        def choose[That, TOut](selector : T => Option[TOut])(implicit buider : CanBuildFrom[T, TOut, That]) = {
            inner.map(selector).filter(x => x.isDefined).map(x => x.get)
        }

        def ofType[TOut : ClassTag] = {
            inner.filter(x => classTag[TOut].runtimeClass.isInstance(x)).map(x => x.asInstanceOf[TOut])
        }

        def truncate(count : Int, ellipsis : T) : Iterable[T] = {
            val upToCount = inner.take(count + 1).toBuffer
            if (upToCount.length > count) {
                upToCount.dropRight(1) += ellipsis
            }
            upToCount
        }
    }

    implicit class BufferExt[T](inner : mutable.Buffer[T]) {
        def dropWhere(f : T => Boolean): Unit = {
            val indices = inner.indices.filter(i => f(inner(i)))
            for (ixIx <- indices.indices) {
                inner.remove(indices(ixIx) - ixIx)
            }
        }

        def dropItem(item : T): Unit = {
            dropWhere(x => x == item)
        }
    }
}
