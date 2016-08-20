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

trait TestHelpers {
    def unique = (Math.random() * 100000).toInt
}

trait AnyRexTests extends TestHelpers {this : FlatSpec =>
    def isCorrectlyDefined(rex : => AnyRex[_]) =
        it should "be correctly defined" in {
            val r = rex
            assert(!r.isClosed, "is open")
            assert(r.info != null, "has info")
            assert(r.depends != null, "has dependencies")
            assert(r.metadata != null, "has metadata")
            r.metadata.description
            r.metadata.name
        }

    def canSetMetadata(rex : => AnyRex[_]) =
        it should "have mutable metadata" in {
            val r = rex
            val desc = s"Rex description #$unique"
            val name=  s"Rex name #$unique"
            val custom = s"Rex custom #$unique"
            r.metadata.description = desc
            assert(r.metadata.description == desc)
            assert(r.metadata.name == name)
            r.metadata("custom") = custom
            assert(r.metadata("description") == desc)
            assert(r.metadata("name") == name)
            assert(r.metadata("custom") == custom)
        }

    def isWriteable(rex : => AnyRex[_]) =
        it should "be writeable" in {
            assert(rex.canWrite, "returned false for `canWrite`")
        }

    def isReadable(rex : => AnyRex[_]) =
        it should "be readable" in {
            assert(rex.canRead, "returned false for `canRead`")
        }

    def isOpen(rex : => AnyRex[_]) =
        it should "be open" in {
            assert(!rex.isClosed, "returned true for `isClosed`")
        }

    def isConsistent(rex: => AnyRex[_]): Unit = {
        it should "be consistent" in {
            rex.consistencyCheck()
        }
    }

    def closes(rex: => AnyRex[_]) =
        it should "close" in {
            val r = rex
            var wasClosed = false
            var d = r.closing += (x => wasClosed = true)
            r.close()
            assert(wasClosed, "the *closing* event did not fire")
            assertThrows[ObjectClosedException](r.value, "ObjectClosedException wasn't thrown on access")
            d.close()
        }

}

trait VectorTests extends TestHelpers {this : FlatSpec =>
    def transformInvariance(rex : => RexVector[Int]): Unit = {
        it should "be transform invariant" in {
            val r = rex
            val v = unique
            val pos = Math.random() * r.length
            r.insert(pos.toInt, v)
            assert(r.contains(v), "element was not found in the vector")
        }
    }

    def orderInvariance(rex : => RexVector[Int]) =
        it should "be order invariant" in {
            val r = rex
            val v = unique
            val pos = Math.random() * r.length
            val prevLength = r.length
            r.insert(pos.toInt, v)
            if (r.length == prevLength + 1) {
                assert(r(pos.toInt) == v, "the insert succeeded, but the element at the position was different")
            }
        }

    def insertInvariance(rex : => RexVector[Int]) =
        it should "be insert invariant" in {
            val r = rex
            val v = unique
            val prevLength = r.lengthz
            val pos = Math.random() * r.length
            r.insert(pos.toInt, v)
            val dif = r.length - prevLength
            assert(dif == 1, s"length did not increase by 1. Instead, the dif was: $dif")
            assert(r.contains(v), s"vector does not contain the inserted value")
        }


}

trait ScalarTests extends TestHelpers {
    this: FlatSpec =>
    def changes(rex: => RexScalar[Int]) =
        it should "change" in {
            val r = rex
            var wasChanged = false
            val d = r.changed += (x => wasChanged = true)
            r.value = unique
            assert(wasChanged, "the *changed* event did not fire")
            d.close()
        }



    def canChangeEqual(rex: => RexScalar[Int], elem: Int): Unit = {
        it should "be equal to examplar" in {
            val r = rex
            r.value = elem
            assert(r.value == elem)
        }
    }
}
