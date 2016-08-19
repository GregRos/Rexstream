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

trait VectorTests {this : FlatSpec =>
    def transformInvariance(rex : => RexVector[Int]): Unit = {
        val r = rex
        val last = (Math.random() * 1000000).toInt

    }
}

trait ScalarTests  { this : FlatSpec=>

    def unique = (Math.random() * 100000).toInt

    def isWriteable(rex : => RexScalar[Int]) =
        it should "be writeable" in {
            assert(rex.canWrite)
        }

    def isReadable(rex : => RexScalar[Int]) =
        it should "be readable" in {
            assert(rex.canRead)
        }

    def isOpen(rex : => RexScalar[Int]) =
        it should "be open" in {
            assert(!rex.isClosed)
        }

    def changes(rex : => RexScalar[Int]) =
        it should "change" in {
            val r = rex
            var wasChanged = false
            val d = r.changed += (x => wasChanged = true)
            r.value = unique
            assert(wasChanged)
            d.close()
        }

    def closes(rex : => RexScalar[Int]) =
        it should "close" in {
            val r = rex
            var wasClosed = false
            var d = r.closing += (x => wasClosed = true)
            r.close()
            assert(wasClosed)
            assertThrows[ObjectClosedException](r.value, "")
            d.close()
        }

    def 


    def anyScalarWriteableRex(rex : => RexScalar[Int]) = {

        it should "be readable" in {
            assert(rex.canRead)
        }

        it should "be writeable" in {
            assert(rex.canWrite)
        }

        it should "be open" in {
            assert(!rex.isClosed)
        }

        it should "change" in {
            val r = rex
            var wasChanged = false
            val d = r.changed += (x => wasChanged = true)
            r.value = unique
            assert(wasChanged)
        }

        it should "close" in {

        }

        it should "be writeable and readable" in {
            val r = rex
            assert(r.canRead)
            assert(r.canWrite)
            assert(!r.isClosed)
            assert(r.depends != null)
            assert(r.info != null)
            r.consistencyCheck()
        }

        it should "change" in {
            val r = rex
            var wasChanged = false
            val d = r.changed += (x => wasChanged = false)
            r.value = 1
            assert(r.value == 1)
            assert(wasChanged, "Fire changed event")
            r.consistencyCheck()
            d.close()
        }

        it should "close" in {
            val r = rex
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