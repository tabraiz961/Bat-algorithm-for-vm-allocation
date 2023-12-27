
import java.util.Random;
import java.util.Arrays;

public class OrigBatAlgorithm {

	private double[][] X; 		// Population/Solution (BATS x D)
	private double[][] V; 		// Velocities (BATS x D)
	private double[][] FREQ; 		// Frequency : 0 to FREQ_MAX (BATS x 1)
	private double[] FIT;			// Fitness (BATS)
	private double PR; 			// Pulse Rate : 0 to 1
	private double LOUD; 			// Louadness : LOUD_MIN to LOUD_MAX
	private double[][] lb;		// Lower bound (1 x D)
	private double[][] ub;		// Upper bound (1 x D)
	private double FIT_MIN; 		// Minimum fitness from FIT
 	private double[] B_ARR;			// Best solution array from X (D)	

	private final int BATS; 		// Number of bats
	private final int MAX; 		// Number of iterations
	private final double FREQ_MIN = 0.0;
	private final double FREQ_MAX = 2.0;
	private final double LOUD_MIN;
	private final double LOUD_MAX;
	private final double PR_MIN;
	private final double PR_MAX; 
	private final int D = 10;
	private final Random rand = new Random();

	public OrigBatAlgorithm(int BATS, int MAX, double LOUD_MIN, double LOUD_MAX, double PR_MIN, double PR_MAX){
		this.BATS = BATS;
		this.MAX = MAX;
		this.PR_MAX = PR_MAX;
		this.PR_MIN = PR_MIN;
		this.LOUD_MAX = LOUD_MAX;
		this.LOUD_MIN = LOUD_MIN;

		this.X = new double[BATS][D];
		this.V = new double[BATS][D];
		this.FREQ = new double[BATS][1];
		this.FIT = new double[BATS];
		this.PR = (PR_MAX + PR_MIN) / 2;
		this.LOUD = (LOUD_MIN + LOUD_MAX) / 2;

		// Initialize bounds
		this.lb = new double[1][D];
		for ( int i = 0; i < D; i++ ){
			this.lb[0][i] = -2.0;
		}
		this.ub = new double[1][D];
		for ( int i = 0; i < D; i++ ){
			this.ub[0][i] = 2.0;
		}

		// Initialize FREQ and V
		for ( int i = 0; i < BATS; i++ ){
			this.FREQ[i][0] = 0.0;
		}
		for ( int i = 0; i < BATS; i++ ){
			for ( int j = 0; j < D; j++ ) {
				this.V[i][j] = 0.0;
			}
		}

		// Initialize X
		for ( int i = 0; i < BATS; i++ ){
			for ( int j = 0; j < D; j++ ){
				this.X[i][j] = lb[0][j] + (ub[0][j] - lb[0][j]) * rand.nextDouble();
			}
			this.FIT[i] = objective(X[i]);
		}

		// Find initial best solution
		int fmin_i = 0;
		for ( int i = 0; i < BATS; i++ ){
			if ( FIT[i] < FIT[fmin_i] )
				fmin_i = i;
		}

		// Store minimum fitness and it's index.
		// B_ARR holds the best solution array[1xD]
		this.FIT_MIN = FIT[fmin_i];
		this.B_ARR = X[fmin_i]; // (1xD)
	}

	private double objective(double[] Xi){
		double sum = 0.0;
		for ( int i = 0; i < Xi.length; i++ ){
			sum = sum + Xi[i] * Xi[i];
		}
		return sum;
	}

	private double[] simpleBounds(double[] Xi){
		// Don't know if this should be implemented
		double[] Xi_temp = new double[D];
		System.arraycopy(Xi, 0, Xi_temp, 0, D);

		for ( int i = 0; i < D; i++ ){
			if ( Xi_temp[i] < lb[0][i] )
				Xi_temp[i] = lb[0][i];
			else continue;
		}

		for ( int i = 0; i < D; i++ ){
			if ( Xi_temp[i] > ub[0][i] )
				Xi_temp[i] = lb[0][i];
			else continue;
		}
		return Xi_temp;
	}

	private void startBat(){

		double[][] S = new double[BATS][D];
		int n_iter = 0;

		// Loop for all iterations/generations(MAX)
		for ( int t = 0; t < MAX; t++ ){
			// Loop for all bats(BATS)
			for ( int i = 0; i < BATS; i++ ){
				
				// Update frequency (Nx1)
				FREQ[i][0] = FREQ_MIN + (FREQ_MIN-FREQ_MAX) * rand.nextDouble();
				// Update velocity (NxD)
				for ( int j = 0; j < D; j++ ){
					V[i][j] = V[i][j] + (X[i][j] - B_ARR[j]) * FREQ[i][0];
				}
				// Update S = X + V
				for ( int j = 0; j < D; j++ ){
					S[i][j] = X[i][j] + V[i][j];
				}
				// Apply bounds/limits
				X[i] = simpleBounds(X[i]);
				// Pulse rate
				if ( rand.nextDouble() > PR )
					for ( int j = 0; j < D; j++ )
						X[i][j] = B_ARR[j] + 0.001 * rand.nextGaussian();


				// Evaluate new solutions
				double fnew = objective(X[i]);

				// Update if the solution improves or not too loud
				if ( fnew <= FIT[i] && rand.nextDouble() < LOUD ){
					X[i] = S[i];
					FIT[i] = fnew;
				}

				// Update the current best solution
				if ( fnew <= FIT_MIN ){
					B_ARR = X[i];
					FIT_MIN = fnew;
				}
			} // end loop for BATS
			n_iter = n_iter + BATS;
		} // end loop for MAX

		System.out.println("Number of evaluations : " + n_iter );
		System.out.println("Best = " + Arrays.toString(B_ARR) );
		System.out.println("FIT_MIN = " + FIT_MIN );
	}

	public static void main(String[] args) {
		new OrigBatAlgorithm(20, 1000, 0.0, 1.0, 0.0, 1.0).startBat();
	}
}