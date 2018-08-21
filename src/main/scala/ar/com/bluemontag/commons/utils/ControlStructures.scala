package ar.com.bluemontag.commons.utils

import util._

object ControlStructures {

  def tryUntilSucceed[T](fn: => T): T = {
    try {
      fn
    } catch {
      case e: Exception => tryUntilSucceed(fn)
    }
  }

  def retryUntilSucceed[T](fn: => T): T = {
    util.Try { fn } match {
      case Success(x) => x
      case Failure(e) => retryUntilSucceed(fn)
    }
  }

  // try n times, returning T, throwing the exception on failure
  @annotation.tailrec
  def retryTimes[T](n: Int)(fn: => T): T = {
    util.Try { fn } match {
      case util.Success(x) => x
      case _ if n > 1 => retryTimes(n - 1)(fn)
      case util.Failure(e) => throw e
    }
  }

  def main(args: Array[String]): Unit = {
    retryTimes(4)("fdkj".substring(7))

  }
}