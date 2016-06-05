package rexstream

import java.lang.annotation.Annotation

import scala.annotation.StaticAnnotation
import rexstream.util._




trait     DependencyProvider extends Iterable[Dependency] {
    def byName(name : String) : Option[AnyRex]
}




