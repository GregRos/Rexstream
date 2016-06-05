package rexstream
import scala.reflect.runtime.universe._
import rexstream.util._

/**
  * Created by GregRos on 04/06/2016.
  */
abstract class ReflectiveMetadataProvider extends MetadataProvider {
    private var metadata = Map[String, Any]()

    private def getSetter(name : String) = {
        val mirror = runtimeMirror(getClass.getClassLoader)
        val setterSymbol =
            mirror.classSymbol(getClass).toType.tryGetMethodsByName(s"${name}_=")
            .find(m => {
                m.paramLists match {
                    case List(List(x)) => true
                    case _ => false
                }
            })
        setterSymbol.map(s => (x : Any) => mirror.reflect(this).reflectMethod(s).apply(x))
    }

    private def getGetter(name : String) = {
        val mirror = runtimeMirror(getClass.getClassLoader)
        val getGetter =
            mirror.classSymbol(getClass).toType.tryGetMethodsByName(name)
                .find(m => m.paramLists.isEmpty && m.hasAnnotation[MetadataAnnotation])
        getGetter.map(s => () => mirror.reflect(this).reflectMethod(s).apply())
    }

    final def get(name : String) = {
        getGetter(name).map(f => f()).orElse(metadata.get(name))
    }

    final def update(name: String, value: Any): Unit = {
        getSetter(name) match {
            case Some(setter) => setter(value)
            case _ =>
                metadata = metadata.updated(name, value)
        }
    }

    final def drop(name: String) : Boolean = {
        if (metadata.contains(name)) {
            metadata = metadata - name
            true
        } else {
            false
        }
    }

    override def iterator: Iterator[MetadataObject] = {
        val mirror = runtimeMirror(getClass.getClassLoader)
        val reflectedMetadata =
            mirror.classSymbol(getClass).toType.decls
                .collect {case m : MethodSymbol => m}
                .filter(_.hasAnnotation[MetadataAnnotation])
            .map(m => new CustomMetadata(m.name.toString, mirror.reflect(this).reflectMethod(m).apply()))

        val customMetadata =
            metadata.map {case (name, v) => new CustomMetadata(name, v)}

        reflectedMetadata.++(customMetadata).iterator
    }
}
