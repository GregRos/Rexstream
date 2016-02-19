package fbl.events

/**
	* Created by GregRos on 06/02/2016.
	*/
trait ContextualChangeInfo {

}

case class LastValueInfo[T](prevValue : T) extends ContextualChangeInfo {

}

case object EmptyChangeInfo extends ContextualChangeInfo {

}

/**
  * Created by GregRos on 06/02/2016.
  */
case class NoChangeInfo() extends ContextualChangeInfo {

}
