package rexstream



private[rexstream] object Errors {
    def Cannot_read =
        new UnsupportedOperationException("This object does not support reading.")

    def Cannot_write =
        new UnsupportedOperationException("This object does not support writing.")

    def Convertion_bindable_must_have_conversion =
        new IllegalArgumentException("A conversion bindable must define a conversion in at least one direction.")

    def Object_closed(obj : Any, message : String = "") =
        new ObjectClosedException(message)

    def Inconsistency_found(message : String) =
        new IllegalStateException(s"An inconsistency has been found: $message")
}
