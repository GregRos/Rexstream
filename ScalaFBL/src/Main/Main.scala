package Main

import fbl._

/**
  * Created by GregRos on 06/02/2016.
  */
object Main {

    def main(arr: Array[String]): Unit = {
        val list = ListVar[Int]
        val mapped = list.map_>(_.convert(n => n.asInstanceOf[Long], (n : Long) => n.asInstanceOf[Int]))
        list += 0 += 1 += 2
        list(0) = -1
        list.remove(1)
        mapped += 3 += 4 += 5
        mapped.remove(1)
        val dfg = 54

        val filtered = mapped.filter_>(_.convert(x => x % 2 == 0))

        println(list.toList)
        println(mapped.toList)
        println(filtered.toList)

        println(filtered.sum)
        println(mapped.sum)
        filtered += 5 += 10
        println(list.toList)
        println(mapped.toList)
        println(filtered.toList)
        filtered.remove(0)
        filtered.insert(1, 0, 5, 3)
        filtered(0) = 11
        filtered += 10 += 11 += 12
        filtered.remove(0)
        println(list.toList)
        println(mapped.toList)
        println(filtered.toList)

        list += 1 += 3

        println(filtered.sum)
        println(mapped.sum)


    }
}
