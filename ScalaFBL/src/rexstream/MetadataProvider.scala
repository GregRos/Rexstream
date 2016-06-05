package rexstream
import java.lang.annotation.Annotation

import scala.annotation.StaticAnnotation
import scala.collection.parallel._
import scala.reflect.runtime.universe._
import rexstream.util._

trait MetadataProvider extends Iterable[MetadataObject] {
    def get(name : String) : Option[Any]

    def apply(name : String) = {
        get(name).get
    }

    def drop(name : String) : Boolean

    def update(name : String, value : Any) : Unit

    var name : String

    var description : String
}



