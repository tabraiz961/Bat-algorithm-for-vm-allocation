import java.util.Random;
// import javafx.application.Application;
// import javafx.scene.Scene;
// import javafx.scene.chart.ScatterChart;
// import javafx.scene.chart.XYChart;
// import javafx.stage.Stage;

public class BatAlgorithmAI {

    private static final int POPULATION_SIZE = 10;
    private static final int MAX_ITERATIONS = 100;
    private static final double A = 0.5; // Loudness
    private static final int POSITIONS = 10; // Alpha value for updating velocity
    private static final double R = 0.5; // Pulse rate
    private static final double ALPHA = 0.9; // Alpha value for updating velocity

    private double[][] population;
    private double[] fitness;
    private double[] velocity;

    public BatAlgorithmAI() {
        initializePopulation();
    }

    private void initializePopulation() {
        Random rand = new Random();
        population = new double[POPULATION_SIZE][];
        fitness = new double[POPULATION_SIZE];
        velocity = new double[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = new double[POSITIONS]; // Assuming a 10-dimensional problem for simplicity
            
            for (int j = 0; j < fitness.length; j++) {
                population[i][j] = rand.nextDouble(); // Initialize position
            }
            velocity[i] = rand.nextDouble(); // Initialize velocity
            fitness[i] = evaluate(population[i]); // Evaluate fitness
        }
    }

    private double evaluate(double[] solution) {
        // Replace this with your objective function
        return Math.sin(solution[0]) * Math.cos(solution[1]);
    }

    public void run() {
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            for (int i = 0; i < POPULATION_SIZE; i++) {
                updateBat(i);
            }
        }

        // Find the best solution
        int bestIndex = findBest();
        double[] bestSolution = population[bestIndex];
        double bestFitness = fitness[bestIndex];

        System.out.println("Best Solution: " + arrayToString(bestSolution));
        System.out.println("Best Fitness: " + bestFitness);
    }

    private void updateBat(int index) {
        Random rand = new Random();

        // Generate a new solution
        double[] newSolution = new double[POSITIONS];
        for (int i = 0; i < POSITIONS; i++) {
            newSolution[i] = population[index][i] + velocity[index];
        }

        // Apply simple bounds check (adjust as needed for your problem)
        for (int i = 0; i < POSITIONS; i++) {
            if (newSolution[i] < 0) {
                newSolution[i] = 0;
            } else if (newSolution[i] > 1) {
                newSolution[i] = 1;
            }
        }

        // Evaluate the new solution
        double newFitness = evaluate(newSolution);

        // Update the solution if the new solution is better
        if (rand.nextDouble() < A && newFitness < fitness[index]) {
            for (int i = 0; i < POSITIONS; i++) {
                population[index][i] = newSolution[i];
            }
            fitness[index] = newFitness;
        }

        // Update velocity
        velocity[index] = ALPHA * (velocity[index] + (population[index][0] - newSolution[0]));
    }

    private int findBest() {
        int bestIndex = 0;
        double bestFitness = fitness[0];

        for (int i = 1; i < POPULATION_SIZE; i++) {
            if (fitness[i] < bestFitness) {
                bestFitness = fitness[i];
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    private String arrayToString(double[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        BatAlgorithmAI batAlgorithm = new BatAlgorithmAI();
        batAlgorithm.run();
    }
}