package rexstream.rex.scalar

import rexstream.{DependencyProvider, Errors, RexInfo, RexScalar, RexTypeNames}
import rexstream.rex.DefaultRex
import rexstream.util._

import scala.reflect.ClassTag
/**
  * Created by lifeg on 13/06/2016.
  */
class RexMember[T : ClassTag](source : RexScalar[T], memberName : RexScalar[String]) extends RexScalar[T] with DefaultRex {
    private val _getterSetter = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]].getGetterSetter(memberName.value)

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

    override def depends: DependencyProvider = DependencyProvider.source(source)

    override def info: RexInfo = new RexInfo {
        val isFunctional = true
        val isLazy = true
        val rexType = RexTypeNames.scalarMember
    }

    override protected[rexstream] def consistencyCheck(): Unit = {
        //TODO: Improve this.
        source.consistencyCheck()
    }
}
