package fbl.infrastructure

/**
  * Created by GregRos on 13/02/2016.
  */
class ExpectEntry {
    private var _expect = false
    def canEnter = {
        !_expect
    }

    def cantEnter: Boolean ={
        _expect
    }

    def noEntry(act : Unit => Unit): Unit = {
        _expect = true
        act(())
        _expect = false
    }
}
