package rexstream.rex.scalar

import rexstream.{DependencyProvider, Errors, RexInfo, RexScalar}
import rexstream.rex.DefaultRex
import rexstream.util._

import scala.reflect.ClassTag
/**
  * Created by lifeg on 13/06/2016.
  */
class RexMember[T : ClassTag[T]](source : RexScalar[T], memberName : RexScalar[String]) extends RexScalar[T] with DefaultRex {
    private var _getterSetter = classOf[T].getGetterSetter(memberName)

    override def value: T = _getterSetter.getter match {
        case None => throw Errors.Cannot_read
        case Some(getter) => getter(source.value).asInstanceOf[T]
    }

    override def canWrite: Boolean = _getterSetter.setter.isDefined

    override def value_=(x: T): Unit = _getterSetter.setter match {
        case None => throw Errors.Cannot_write
        case Some(setter) => setter(source.value, x)
    }

    override def canRead: Boolean = _getterSetter.getter.isDefined

    override def depends: DependencyProvider = DependencyProvider.sourceAndProvider()

    override def info: RexInfo = ???

    override protected[rexstream] def consistencyCheck(): Unit = ???
}
