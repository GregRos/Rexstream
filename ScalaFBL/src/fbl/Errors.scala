package fbl

/**
	* Created by GregRos on 06/02/2016.
	*/

case class ObjectClosedException(message : String) extends Exception(message)

private[fbl] object Errors {
    def Cannot_read =
        new UnsupportedOperationException("This object does not support reading.")

    def Cannot_write =
        new UnsupportedOperationException("This object does not support writing.")

    def Convertion_bindable_must_have_conversion =
        new IllegalArgumentException("A conversion bindable must define a conversion in at least one direction.")

    def Object_closed(obj : Any, message : String = "") =
        new ObjectClosedException(message)
}
