package rexstream




/**
  * Created by GregRos on 04/06/2016.
  */
class CommonMetadataProvider extends MetadataProvider {
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

    override def iterator: Iterator[MetadataObject] = inner.map {case (k,v) => new CustomMetadata(k, v) }.iterator
}
