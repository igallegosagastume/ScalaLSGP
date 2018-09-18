package ar.com.bluemontag.commons.generators.test

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalactic.source.Position.apply
import ar.com.bluemontag.jacomatt.generators.JacobsonMatthewsLSGenerator
import ar.com.bluemontag.seqgen.generators.ReplacementChainLSGenerator

@RunWith(classOf[JUnitRunner])
class GeneratorsTest extends FlatSpec with Matchers {

  "A Jacobson and Matthews generator" should "generate a Latin Square of order 50" in {
    val generator = new JacobsonMatthewsLSGenerator(50);

    println(generator.generateLS())
  }

  "A Jacobson and Matthews generator" should "generate many LSs" in {

    val order = 50
    val times = 100

    println("\n\nParameters of the \"jacobson-matthews'\" method test: order of LSs=" + order + " repetitions=" + times + ".")

    val generator = new JacobsonMatthewsLSGenerator(order);

    Utils.repeatGeneration(generator, times);
  }

  "A Replacement Chain generator" should "generate a Latin Square of order 50" in {
    val generator = new ReplacementChainLSGenerator(50);

    println(generator.generateLS())
  }

  "A Replacement Chain generator" should "generate many LSs" in {

    val order = 50
    val times = 100

    println("\n\nParameters of the \"replacement chain\" method test: order of LSs=" + order + " repetitions=" + times + ".")

    val generator = new ReplacementChainLSGenerator(order);

    Utils.repeatGeneration(generator, times);
  }

}