package ar.com.bluemontag.seqgen.test

import ar.com.bluemontag.seqgen.generators.ReplacementChainLSGenerator
import ar.com.bluemontag.commons.generators.test.Utils

/**
 * @author igallegosagas
 * 
 */
object ReplacementChainTest {

  def main(args: Array[String]): Unit = {
    var order: Int = args(0).toInt
    var times: Int = args(1).toInt

    println("Parameters of the test: order of LSs="+order+" repetitions="+times+".")
    var generator = new ReplacementChainLSGenerator(order);
    Utils.repeatGeneration(generator, times);
  }
}