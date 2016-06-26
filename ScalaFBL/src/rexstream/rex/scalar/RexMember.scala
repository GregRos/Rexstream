package rexstream.rex.scalar

import rexstream.{DependencyProvider, Errors, RexInfo, RexScalar, RexTypeNames}
import rexstream.rex.DefaultRex
import rexstream.util._

import scala.reflect.ClassTag
/**
  * Created by lifeg on 13/06/2016.
  */
class RexMember[T, TOut](source : RexScalar[T], memberName : RexScalar[String]) extends RexScalar[TOut] with DefaultRex {

    def currentClass = source.value.getClass

    val innerToken = source.changed.subscribe(_ => {
        this.changed.raise(null)
    })

    val memberToken = source.changed.subscribe(_ => {
        this.changed.raise(null)
    })

    override def value: TOut = {
        currentClass.invokeGetter(source.value, memberName.value).asInstanceOf[TOut]
    }

    override def canWrite: Boolean = currentClass.hasSetter(source.value, memberName.value)

    override def value_=(x: TOut): Unit = currentClass.invokeSetter(source.value, memberName.value, x)

    override def canRead: Boolean = currentClass.hasGetter(source.value, memberName.value)

    override def depends: DependencyProvider = DependencyProvider.source(source)

    override def info: RexInfo = new RexInfo {
        val isFunctional = true
        val isLazy = true
        val rexType = RexTypeNames.scalarMember
    }

    override protected[rexstream] def consistencyCheck(): Unit = {
        //TODO: Improve this.
        memberName.consistencyCheck()
        source.consistencyCheck()
    }
}
