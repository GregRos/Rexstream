package rexstream

import java.lang.reflect.{Executable, Method}

import scala.collection.generic._
import scala.collection.mutable
import scala.reflect.runtime.universe._
import scala.reflect.{ClassTag, _}
package object util {
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

    class GetterSetter(val getter : Option[Any => Any], val setter : Option[(Any, Any) => Unit]) {

    }

    implicit class MemberExt(m : Executable) {
        def annotationsOf[T  <: java.lang.annotation.Annotation : ClassTag] : List[T] = {
            val annotations = m.getAnnotations.toList
            annotations.collect { case (a: T) => a }.toList
        }
    }

    implicit class MethodSymbolExt(m : MethodSymbol) {
        def hasAnnotation[T  <: java.lang.annotation.Annotation : ClassTag] = {
            m.annotations.ofType[T].nonEmpty
        }
    }

    implicit class ClassExt(m : Class[_]) {
        def getAnnotatedMembers[TAnnotation <: java.lang.annotation.Annotation : ClassTag] : List[Method]= {
            m.getMethods.filter(x => {
                val annotations = x.annotationsOf[TAnnotation]
                annotations.nonEmpty
            }).toList

        }
    }

    implicit class MethodExt(m : MethodSymbol) {
        def invoke(target : Any, args : Any*): Any = {
            val mirror = runtimeMirror(getClass.getClassLoader)
            mirror.reflect(target).reflectMethod(m).apply(args : _*)
        }
    }

    implicit class TypeExt(t : Class[_]) {

        def toType = {
            val mirror = runtimeMirror(getClass.getClassLoader)
            val typ = mirror.classSymbol(t).toType
            typ
        }

        def tryGetMethodsByName(name : String): List[MethodSymbol]= {
            toType.decl(TermName(name)).alternatives.ofType[MethodSymbol].toList
        }

        def invokeGetter(target : Any, name : String): Option[Any] = {
            val getter = tryGetMethodsByName(name).find(m => m.paramLists.isEmpty)
            getter.map(m => m.invoke(target))
        }

        def hasGetter(target : Any, name : String): Boolean = {
            tryGetMethodsByName(name).exists(m => m.paramLists.isEmpty)
        }

        def hasSetter(target : Any, name : String) : Boolean = {
            tryGetMethodsByName(s"${name}_=").exists {case List(List(x)) => true}
        }

        def invokeSetter(target : Any, name : String, arg : Any) : Boolean = {
            val setter = tryGetMethodsByName(name + "_=").find {case List(List(x)) => true}
            setter.map(m => m.invoke(target, arg)).isDefined
        }

        def getGetterSetter(name : String) : GetterSetter = {
            val mirror = runtimeMirror(getClass.getClassLoader)
            val getter = t.tryGetMethodsByName(name).find(m => m.paramLists.isEmpty)
            val setter = t.tryGetMethodsByName(s"${name}_=").find {case List(List(x)) => true}
            val getterMethod = getter.map(method => (obj : Any) => mirror.reflect(obj).reflectMethod(method).apply())
            val setterMethod = setter.map(method => (obj : Any, arg : Any) => {
                mirror.reflect(obj).reflectMethod(method).apply(arg)
                ()
            })

            new GetterSetter(getterMethod, setterMethod)
        }
    }


}
