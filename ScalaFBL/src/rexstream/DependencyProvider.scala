package rexstream

import java.lang.annotation.Annotation

import scala.annotation.StaticAnnotation
import rexstream.util._

trait DependencyProvider extends Iterable[Dependency] {
    def apply(name : String) : Option[AnyRex]
}

object DependencyProvider {
    private class DefaultDependencyProvider(pairs : (String, AnyRex)*) extends DependencyProvider {
        private val _map = Map.apply(pairs : _*)
        override def apply(name: String): Option[AnyRex] = _map.get(name)

        override def iterator: Iterator[Dependency] = _map.map {case (name, dep) => new Dependency(name, dep)}.iterator
    }
    def sourceAndProvider(source : AnyRex, provider : AnyRex) : DependencyProvider = new DefaultDependencyProvider("source" -> source, "provider" -> provider)

    def source(source : AnyRex) : DependencyProvider = new DefaultDependencyProvider("source" -> source)

    def none : DependencyProvider = new DefaultDependencyProvider()
}

