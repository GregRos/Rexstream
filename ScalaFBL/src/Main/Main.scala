package Main

import rexstream._
import rexstream.debug._
/**
  * Created by GregRos on 06/02/2016.
  */
object Main {

    def main(arr: Array[String]): Unit = {
        val list = ListVar[Int]
        val mapped = list.map_>(_.convert_>(n => n.asInstanceOf[Long], (n : Long) => n.asInstanceOf[Int]))
        list += 0 += 1 += 2
        list(0) = -1
        list.remove(1)
        mapped += 3 += 4 += 5
        mapped.remove(1)
        val dfg = 54

        val filtered = mapped.filter_>(_.convert_>(x => x % 2 == 0))
        println(list)
        println(mapped)
        println(filtered)

        println(filtered.sum)
        println(mapped.sum)
        filtered += 5 += 10
        println(list)
        println(mapped)
        println(filtered)
        filtered.remove(0)
        filtered.insert(1, 5, 3)
        filtered(0) = 11
        filtered += 10 += 11 += 12
        filtered.remove(0)
        val sorted = mapped.sort_> map_>(_.link_>)
        sorted.consistencyCheck()
        println(list)
        println(mapped)
        println(filtered)
        sorted.consistencyCheck()
        list += 1 += 3
        println(mapped)
        println(sorted)
        sorted.consistencyCheck()
        println(filtered.sum_>)
        println(mapped.sum_>)
        println(mapped)
        println(sorted)
        sorted += 1 += 2 += 3
        list(0) = 1000
        sorted.consistencyCheck()



    }
}
