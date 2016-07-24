package rexstream.rex

import rexstream.events.ContextualChangeData
import rexstream.{AnyRex, BindPriority, BindState, BindingActionException}

/**
  * Created by GregRos on 23/07/2016.
  */
private[rexstream] abstract class StdBinding[TChange >: Null <: ContextualChangeData, TRex >: Null <: AnyRex[TChange]](
    val origin : TRex,
    val priority : BindPriority.Value
) {
    private val _originChangedToken = origin.changed += (data => onChange(data, origin))
    private val _originClosingToken = origin.closing += (x => this.close())
    private var _targetChangedToken : AutoCloseable = null;
    private var _targetClosingToken : AutoCloseable = null
    private var _target : TRex = null
    private var _state = BindState.Ready
    def target = _target
    def state = _state

    def setTarget(target : TRex): Unit = {
        if (_targetChangedToken != null) {
            _targetChangedToken.close()
            _targetClosingToken.close()
        }
        _target = target
        if (target != null) {
            _state = BindState.Active
            if (priority == BindPriority.Origin) {
                onChange(null, origin)
            } else {
                onChange(null, target)
            }
            _targetChangedToken = target.changed += (data => onChange(data, target))
            _targetClosingToken = target.closing += (data => setTarget(null))
        } else {
            _targetChangedToken = null
            _targetClosingToken = null
            _state = BindState.Ready
        }
    }

    private def isOrigin(what : TRex): Boolean = {
        what eq origin
    }

    protected def rectify(fromOrigin : Boolean, change : TChange): Unit

    private def onChange(data : TChange, notifier : TRex): Unit = {
        if (state != BindState.Active) {
            return
        }
        if (origin.isClosed) {
            close()
            return
        }
        if (target.isClosed) {
            setTarget(null)
            return
        }
        val fromOrigin = isOrigin(notifier)
        rectify(fromOrigin, data)
    }

    def close(): Unit = {
        if (_originChangedToken != null) {
            _originChangedToken.close()
            _originClosingToken.close()
        }
        setTarget(null)
        _state = BindState.Closed
    }

}
