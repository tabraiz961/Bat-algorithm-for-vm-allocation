import java.util.Arrays;

public class Sigmoid {

    public static void main(String[] args) {
        // Example vector of values
        double[] inputVector = {2.0, 1, 0.5, 16,99999,999999999};

        // Apply sigmoid function to the vector
        double[] resultVector = applySigmoid(inputVector);

        // Print the original and sigmoid-applied vectors
        System.out.println("Original vector: " + Arrays.toString(inputVector));
        System.out.println("Sigmoid-applied vector: " + Arrays.toString(resultVector));
    }

    // Function to apply sigmoid to each element of the vector
    private static double[] applySigmoid(double[] vector) {
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = sigmoid(vector[i]);
        }
        return result;
    }

    // Sigmoid function
    private static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}
