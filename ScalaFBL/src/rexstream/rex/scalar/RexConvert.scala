/**
  * Created by GregRos on 06/02/2016.
  */
package rexstream.rex.scalar

import rexstream._
import rexstream.operations.Conversion
import rexstream.rex.DefaultRex



class RexConvert[TIn, TOut](
    inner: RexScalar[TIn],
    conversionProvider : RexScalar[Conversion[TIn, TOut]]) extends RexScalar[TOut] with DefaultRex {

    def convertOut = conversionProvider.value.out
    def convertIn = conversionProvider.value.in

    private var _lastValue: Option[TOut] = None
    private val innerToken = inner.changed.subscribe(args => {
        _lastValue = None
        this.changed.raise(null)
    })

    private val providerToken = conversionProvider.changed.subscribe(_ => {
        _lastValue = None
        this.changed.raise(null)
    })

    override def canRead = convertOut.isDefined && inner.canRead

    override def canWrite = convertIn.isDefined && inner.canWrite

    override val depends = DependencyProvider.sourceAndProvider(inner, conversionProvider)

    override val info = new RexInfo {
        val isLazy = true
        val isFunctional = true
        val rexType = RexTypeNames.scalarConvert
    }
    override def close() = {
        innerToken.close()
        providerToken.close()
        super.close()
    }

    override def value = {
        makeSureNotClosed()
        _lastValue match {
            case Some(v) => v
            case None =>
                val v = convertOut.get(inner.value)
                _lastValue = Some(v)
                v
        }
    }

    override def value_=(v: TOut) = {
        makeSureNotClosed()
        _lastValue = Some(v)
        inner.value = convertIn.get(v)
    }

    override def consistencyCheck(): Unit = {
        if (_lastValue.isDefined) {
            val lastValue = _lastValue.get
            if (convertIn.isDefined && inner.canRead) {
                if (convertIn.get(lastValue) != inner.value) {
                    throw Errors.Inconsistency_found("The cached value isn't equal to the inner value via the in-converter")
                }
            }
            if (convertOut.isDefined && inner.canRead) {
                if (convertOut.get(inner.value) != lastValue) {
                    throw Errors.Inconsistency_found("The cached value isn't equal to the inner value via the out-converter.")
                }
            }
        }

        inner.consistencyCheck()
    }
}




