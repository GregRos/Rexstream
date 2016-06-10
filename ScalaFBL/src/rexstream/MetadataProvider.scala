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


object MetadataProvider {
    /**
      * Created by GregRos on 04/06/2016.
      */
    class DefaultMetadataProvider extends MetadataProvider {
        private var inner = Map.empty[String, Any]
        def name = this("name").asInstanceOf[String]
        def name_=(x : String) = this("name") = x

        def description = this("description").asInstanceOf[String]
        def description_=(x : String) =  this("description") = x

        override def drop(name: String): Boolean ={
            val result = inner.contains(name)
            inner = inner - name
            result
        }

        override def update(name: String, value: Any): Unit = inner = inner.updated(name, value)

        override def get(name: String): Option[Any] = inner.get(name)

        override def iterator: Iterator[MetadataObject] = inner.map {case (k,v) => new MetadataObject{
            val name: String = k

            val value = v
        } }.iterator
    }

    def apply() : MetadataProvider = new DefaultMetadataProvider()
}




