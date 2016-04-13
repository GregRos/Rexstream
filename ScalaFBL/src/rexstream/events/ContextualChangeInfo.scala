package rexstream.events

/**
	* Created by GregRos on 06/02/2016.
	*/
trait ContextualChangeInfo {

}

case class LastValueInfo[T](prevValue : T) extends SingleValueChangeInfo {

}

trait SingleValueChangeInfo extends ContextualChangeInfo {

}



