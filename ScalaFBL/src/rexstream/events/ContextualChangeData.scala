package rexstream.events

/**
	* Created by GregRos on 06/02/2016.
	*/
trait ContextualChangeData {

}


case class LastValueData[T](prevValue : T) extends ScalarChangeData {

}

trait ScalarChangeData extends ContextualChangeData {

}



