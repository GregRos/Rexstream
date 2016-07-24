package rexstream

/**
	* Created by GregRos on 06/02/2016.
	*/

case class ObjectClosedException(message : String, inner : Throwable = null) extends Exception(message, inner)

class BindingActionException(message : String, inner : Throwable) extends Exception(message, inner)