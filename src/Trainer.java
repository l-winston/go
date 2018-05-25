import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Trainer {
	final static int[] widths = { 81, 100, 150, 100, 82 };

	static ArrayList<Net> population = new ArrayList<Net>();

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			population.add(new Net(widths));
		}

		for (int gen = 0; gen < 100; gen++) {
			scorePopulation();
			System.out.println("Generation " + (gen + 1));
		}
		for (Net n : population) {
			System.out.println(n.score);
		}
	}

	public static void save(Net n) {
		n.print("out");
	}

	public static void scorePopulation() {
		Random rand = new Random();
		
		for(int i = 0; i < population.size(); i++){
			population.get(i).score = 0;
		}

		for (int i = 0; i < population.size(); i++) {
			for (int j = 0; j < population.size(); j++) {
				play(population.get(i), population.get(j));
			}
		}
		Collections.sort(population, new Comparator<Net>() {
			@Override
			public int compare(Net o1, Net o2) {
				return o1.score > o2.score ? -1 : o1.score < o2.score ? 1 : 0;
			}
		});

		save(population.get(0));

		ArrayList<Net> nextGen = new ArrayList<Net>();

		System.out.println(population.size());
		int popSize = population.size();

		// save top 25%
		for (int i = 0; i < popSize / 4; i++) {
			nextGen.add(population.remove(0));
		}

		// take random from middle 50%
		for (int i = 0; i < popSize / 4; i++) {
			Net remove = population.remove(rand.nextInt(popSize / 2));
			nextGen.add(remove);
		}

		// breed to fill bottom 50% again
		ArrayList<Net> breeded = new ArrayList<Net>();
		for (int i = 0; i < popSize / 2; i++) {
			Net rand1 = nextGen.get(rand.nextInt(population.size()));
			Net rand2 = nextGen.get(rand.nextInt(population.size()));
			breeded.add(new Net(rand1, rand2));
		}
		nextGen.addAll(breeded);

		population = nextGen;
	}

	public static void play(Net n1, Net n2) {
		Game g = new Game(9, 9);
		if (g.play(n1, n2)) {
			n1.score++;
		} else {
			n2.score++;
		}
	}
}
