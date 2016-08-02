package rexstream.rex.scalar

import rexstream.{DependencyProvider, RexInfo, RexScalar, RexTypeNames}
import rexstream.events.{AbsEvent, ScalarChangeData}
import rexstream.rex.DefaultRexWithScalarChange

/**
  * Created by GregRos on 15/07/2016.
  */
class RexChangeNotifier[T](inner: RexScalar[T], subscribe: T => AbsEvent[ScalarChangeData]) extends RexScalarLink[T](inner) {
    private var _notifierToken: AutoCloseable = _
    private val _rexNotifierToken = inner.changed.subscribe(x => registerNotifier())
    override val info = new RexInfo {
        val isLazy = false
        val isFunctional = true
        val rexType = RexTypeNames.scalarChangeNotifier
    }

    override val depends: DependencyProvider = DependencyProvider.source(inner)

    private def registerNotifier(): Unit = {
        if (_notifierToken != null) {
            _notifierToken.close()
        }
        _notifierToken = subscribe(inner.value) ++= this.changed
    }

    override def close(): Unit = {
        _rexNotifierToken.close()
        if (_notifierToken != null) {
            _notifierToken.close()
        }
    }

    override def consistencyCheck() = {
        inner.consistencyCheck()
    }
}
