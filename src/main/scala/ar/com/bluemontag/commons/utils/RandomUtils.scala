package ar.com.bluemontag.commons.utils

import scala.collection.mutable.HashSet
import scala.None

object RandomUtils {

  val rand = new java.security.SecureRandom

  def randomChoice(set: Set[Int]): Int = {
    val index = rand.nextInt(set.size)
    var i = 0
    for (obj <- set) {
      if (i == index)
        return obj
      i = i + 1
    }
    Int.MinValue //TODO REVISAR ESTO!!!
  }

  def randomChoice(list: List[Int]): Int = {
    val idx = rand.nextInt(list.size)
    list(idx)
  }

  def randomChoice(vec: Vector[Int]): Int = {
    val idx = rand.nextInt(vec.size)

    vec(idx)
  }

  def randomTriple(list: List[(Int, Int, Int)]): (Int, Int, Int) = {
    val idx = rand.nextInt(list.size)
    list(idx)
  }

  def oneToN(n: Int): HashSet[Int] = {
    var numbers = new HashSet[Int]()

    for (i <- 0 to (n - 1)) {
      numbers.add(i)
    }
    numbers
  }

  def pickAnInt(n: Int): Int = {
    rand.nextInt(n);
  }

  def getNullElem(): /*Option[*/Int = {
    9999
  }

}