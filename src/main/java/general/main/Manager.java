package general.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import binarytopmatching.BinaryMatchingAlgorithm;
import bruteforce.BruteforceAlgorithm;
import bruteforce.ExplicitDistribution;
import general.Distribution;
import general.GeneralUtils;
import general.Mallows;
import general.main.GeneralArgs.ScenarioToNumOfItemsPair;
import pattern.Graph;
import pattern.GraphGenerator;
import sampled.SampledAlgorithm;
import sampled.SampledDistribution;
import stats.Stats;
import topmatching.SimpleDistribution;
import topmatching.TopMatchingAlgorithm;

/**
 * 
 * A class to manage the entire running flow. Reads the configuration file and
 * determine what to run and how.
 * 
 * @author uzicohen
 *
 */
public class Manager {

	private static final String BRUTE_FORCE = "Brute force";

	private static final String SAMPLED = "Sampled";

	private static final String TOP_MATCHNING = "Top Matching";

	private static final String BINARY_MATCHNING = "Binary Matching";

	private static final Logger logger = Logger.getLogger(Manager.class.getName());

	public static void run() {

		logger.info("Resolving running arguments");

		double phi = GeneralArgs.phi;

		ArrayList<ScenarioToNumOfItemsPair> scenarioToNumOfItemsPairs = GeneralArgs.scenarioToNumOfItemsPairs;
		for (ScenarioToNumOfItemsPair scenarioToNumOfItemsPair : scenarioToNumOfItemsPairs) {

			logger.info(String.format("Creating objects for scenario-number: %d, number of items: %d",
					scenarioToNumOfItemsPair.scenario, scenarioToNumOfItemsPair.numOfItems));

			Graph graph = GraphGenerator.GetGraph(Integer.parseInt(scenarioToNumOfItemsPair.scenario));

			Mallows model = new Mallows(GeneralUtils.getItems(scenarioToNumOfItemsPair.numOfItems), phi);

			Stats stats = new Stats(scenarioToNumOfItemsPair.scenario, scenarioToNumOfItemsPair.numOfItems, 0,
					graph.toString());

			// Run brute force
			if (GeneralArgs.runBruteforce) {
				GeneralArgs.currentAlgorithm = AlgorithmType.BRUTE_FORCE;

				stats.setAlgorithm(BRUTE_FORCE);

				Distribution explicitDistribution = new ExplicitDistribution(model);

				if (GeneralArgs.printDistribution) {
					System.out.println(explicitDistribution);
				}

				stats.setStartTimeDate(new Date());

				logger.info(String.format("Running brute-force algorithm for scenario: %d",
						scenarioToNumOfItemsPair.scenario));

				double exactProb = new BruteforceAlgorithm().calculateProbability(graph, explicitDistribution);

				stats.setEndTimeDate(new Date());

				stats.setProbability(exactProb);

				System.out.println(stats);

			}

			if (GeneralArgs.runSampled) {
				GeneralArgs.currentAlgorithm = AlgorithmType.SAMPLED;

				stats.setAlgorithm(SAMPLED);

				Distribution sampledDistribution = new SampledDistribution(model, GeneralArgs.numSamples);

				stats.setStartTimeDate(new Date());

				logger.info(
						String.format("Running sampled algorithm for scenario: %d", scenarioToNumOfItemsPair.scenario));

				double approxProb = new SampledAlgorithm().calculateProbability(graph, sampledDistribution);

				stats.setEndTimeDate(new Date());

				stats.setProbability(approxProb);

				System.out.println(stats);
			}

			if (GeneralArgs.runTopMatching) {
				GeneralArgs.currentAlgorithm = AlgorithmType.TOP_MATCHNING;

				stats.setAlgorithm(TOP_MATCHNING);

				Distribution simpleDistribution = new SimpleDistribution(model);

				stats.setStartTimeDate(new Date());

				logger.info(String.format("Running top-matchnig algorithm for scenario: %d",
						scenarioToNumOfItemsPair.scenario));

				double topMatchingProb = new TopMatchingAlgorithm().calculateProbability(graph, simpleDistribution);

				stats.setEndTimeDate(new Date());

				stats.setProbability(topMatchingProb);

				System.out.println(stats);

			}

			if (GeneralArgs.runBinaryMatching) {
				GeneralArgs.currentAlgorithm = AlgorithmType.BINARY_MATCHING;

				stats.setAlgorithm(BINARY_MATCHNING);

				Distribution simpleDistribution = new SimpleDistribution(model);

				stats.setStartTimeDate(new Date());

				logger.info(String.format("Running binary-matchnig algorithm for scenario: %d",
						scenarioToNumOfItemsPair.scenario));

				double topBinaryProb = new BinaryMatchingAlgorithm().calculateProbability(graph, simpleDistribution);

				stats.setEndTimeDate(new Date());

				stats.setProbability(topBinaryProb);

				System.out.println(stats);

			}
		}

	}

}
