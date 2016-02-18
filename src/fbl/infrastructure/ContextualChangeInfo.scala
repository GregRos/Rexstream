package fbl.infrastructure

/**
	* Created by GregRos on 06/02/2016.
	*/
trait ContextualChangeInfo {

}

case class LastValueInfo[T](prevValue : T) extends ContextualChangeInfo {

}

object EmptyChangeInfo extends ContextualChangeInfo {

}
