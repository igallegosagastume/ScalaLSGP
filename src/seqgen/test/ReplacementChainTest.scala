package seqgen.test

import seqgen.model.SeqGenWithReplacementMap

object ReplacementChainTest {
  	/**
	 * Computes the time and generates the LS of requested order.
	 * 
	 * @param generator
	 * @param path
	 */
	def computeTimeFor(generator: SeqGenWithReplacementMap, progress:Int, showProgress:Boolean, showFinalMessage:Boolean, verbose:Boolean) : Double = {
		var startTime = System.nanoTime();
		var ls = generator.generateLS();
		var endTime = System.nanoTime();

		var duration :Long = endTime - startTime;
		var secs :Double = duration/1000000000d;
		
		if (verbose)
			System.out.println(ls);
		else
			if (showProgress) {
				System.out.println("Progress: "+progress+"%");
			}
		
		if (showFinalMessage)
			System.out.println("Random structure generated in "+secs+" seconds. Generation method: replacement chain");
		
		if (!ls.preservesLatinProperty()) {
			System.out.println();
			System.out.println("ERROR: the generated structure does not preserve the Latin property.");
			System.exit(0);
		} else {
			//System.out.println("The structure preserves the Latin property.");
		}
		return secs;
	}

	
	
	def repeatGeneration(generator: SeqGenWithReplacementMap, times:Int) : Unit = {
		var startTime = System.nanoTime();

		var verbose:Boolean = false;
		var showProgress = false;
		var showFinalMessage = true; 
		if (times == 1) {
			verbose = true;
			computeTimeFor(generator, 100, showProgress, showFinalMessage, verbose);
			return;
		}
		//List<Double> generationTimes = new ArrayList<Double>();
		var sum = 0.0;
		var progress = 0;
		var percetage = times/10;
		showFinalMessage = false;
		showProgress = false;
		for (i <- 1 to times) {
			var secsForAGen :Double = computeTimeFor(generator, progress, showProgress, showFinalMessage, verbose);
//			generationTimes.add(secs);

			if (i%percetage==0) {
				if (i!=0)
					progress += 10;
				showProgress = true; 
			} else {
				showProgress = false; 
			}
			sum +=secsForAGen;
		}
		var endTime = System.nanoTime();

		var duration = endTime - startTime;
		var secs = duration/1000000000d;
		var mins = secs / 60;
		var hours = mins/60;
		
		var averageTime : Double = sum / times;
		System.out.println("");
		if (hours>=1)
			System.out.println("Finished "+times+" repetitions of order "+generator.order+" after "+hours+" hours.");
		else 
			if (mins>=1)
				System.out.println("Finished "+times+" repetitions of order "+generator.order+" after "+mins+" minutes.");
			else
				System.out.println("Finished "+times+" repetitions of order "+generator.order+" after "+secs+" seconds.");
		System.out.println("Generation method: replacement chain");
		System.out.println("Average time of method is "+averageTime+" seconds.");
	}

	def main(args:Array[String]) : Unit = {
	  var generator = new SeqGenWithReplacementMap(256);
		repeatGeneration(generator, 1000);
	}
}