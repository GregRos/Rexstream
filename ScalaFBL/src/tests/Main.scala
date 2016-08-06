package tests
import org.scalatest._
import org.scalatest.words._
import rexstream._
import rexstream.debug._
/**
  * Created by GregRos on 29/07/2016.
  */
object Main {
    def main(args : Array[String]): Unit = {

    }
}

trait CannedRexBehaviors  { this : FlatSpec=>
    def anyScalarWriteableRex(rex : Int => RexScalar[Int]) = {

        it should "be initialized correctly" in {
            var r = rex(0)
            assert(r.canRead, "Can read")
            assert(r.canWrite, "Can write")
            assert(!r.isClosed)
        }

        it should "be writeable and readable" in {
            val r = rex(0)
            assert(r.canRead)
            assert(r.canWrite)
            assert(!r.isClosed)
            assert(r.depends != null)
            assert(r.info != null)
            r.consistencyCheck()
        }

        it should "change" in {
            val r = rex(0)
            var wasChanged = false
            val d = r.changed += (x => wasChanged = false)
            r.value = 1
            assert(r.value == 1)
            assert(wasChanged, "Fire changed event")
            r.consistencyCheck()
            d.close()
        }

        it should "close" in {
            val r = rex(1)
            assert(!r.isClosed)
            var wasClosed = false
            val d = r.closing += (x => wasClosed = true)

            r.close()
            assert(r.isClosed)
            assert(wasClosed, "Fire closing event")

            //Disposal-unsafe methods:
            assertThrows[ObjectClosedException](r.value == 0)
            assertThrows[ObjectClosedException](r.value = 0)

            //These are disposal-safe methods:
            r.canWrite
            r.canRead
            r.toString
            r.depends
            r.info
            r.close() //closing it twice is okay too

            d.close()
        }
    }
}