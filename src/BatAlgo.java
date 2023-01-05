
import java.util.Random;
import java.util.Arrays;

public class BatAlgo {

	private double[][] POP_SOL; 		// Population/Solution (BATS x D) 
	private double[][] V; 		// Velocities (BATS x D)
	private double[][] FRE; 		// Frequency : 0 to F_MAX (BATS x 1)
	private double[] FIT;			// Fitness (BATS)
	private double PR; 			// Pulse Rate : 0 to 1
	private double L; 			// Louadness : L_MIN to L_MAX
	private double[][] lb;		// Lower bound (1 x D)
	private double[][] ub;		// Upper bound (1 x D)
	private double fmin; 		// Minimum fitness from FIT
 	private double[] B;			// Best solution array from POP_SOL (D)	

	private final int BATS; 		// Number of bats
	private final int MAX; 		// Number of iterations
	private final double F_MIN = 0.0;
	private final double F_MAX = 2.0;
	private final double L_MIN;
	private final double L_MAX;
	private final double PR_MIN;
	private final double PR_MAX; 
	private final int D = 10;
	private final Random rand = new Random();

	public BatAlgo(int BATS, int MAX, double L_MIN, double L_MAX, double PR_MIN, double PR_MAX){
		this.BATS = BATS;
		this.MAX = MAX;
		this.PR_MAX = PR_MAX;
		this.PR_MIN = PR_MIN;
		this.L_MAX = L_MAX;
		this.L_MIN = L_MIN;

		this.POP_SOL = new double[BATS][D];
		this.V = new double[BATS][D];
		this.FRE = new double[BATS][1];
		this.FIT = new double[BATS];
		this.PR = (PR_MAX + PR_MIN) / 2;
		this.L = (L_MIN + L_MAX) / 2;

		// Initialize bounds
		this.lb = new double[1][D];
		for ( int i = 0; i < D; i++ ){
			this.lb[0][i] = -2.0;
		}
		this.ub = new double[1][D];
		for ( int i = 0; i < D; i++ ){
			this.ub[0][i] = 2.0;
		}

		// Initialize FRE and V
		for ( int i = 0; i < BATS; i++ ){
			this.FRE[i][0] = 0.0;
		}
		for ( int i = 0; i < BATS; i++ ){
			for ( int j = 0; j < D; j++ ) {
				this.V[i][j] = 0.0;
			}
		}

		// Initialize POP_SOL
		for ( int i = 0; i < BATS; i++ ){
			for ( int j = 0; j < D; j++ ){
				this.POP_SOL[i][j] = lb[0][j] + (ub[0][j] - lb[0][j]) * rand.nextDouble(); 
			}
			this.FIT[i] = objective(POP_SOL[i]);
		}

		// Find initial best solution
		int fmin_i = 0;
		for ( int i = 0; i < BATS; i++ ){
			if ( FIT[i] < FIT[fmin_i] )
				fmin_i = i;
		}

		// Store minimum fitness and it's index.
		// B holds the best solution array[1xD]
		this.fmin = FIT[fmin_i];
		this.B = POP_SOL[fmin_i]; // (1xD)
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
				FRE[i][0] = F_MIN + (F_MIN-F_MAX) * rand.nextDouble(); //will replace the fMin and fmax with CPUmin and CPUmax 
				// Update velocity (NxD)
				for ( int j = 0; j < D; j++ ){
					V[i][j] = V[i][j] + (POP_SOL[i][j] - B[j]) * FRE[i][0];
				}
				// Update S = POP_SOL + V
				for ( int j = 0; j < D; j++ ){
					S[i][j] = POP_SOL[i][j] + V[i][j];
				}
				// Apply bounds/limits
				POP_SOL[i] = simpleBounds(POP_SOL[i]);
				
                // Pulse rate
				if ( rand.nextDouble() > PR )
					for ( int j = 0; j < D; j++ )
                    {
                        // Generating Local solution around best solution
						S[i][j] = B[j] + 0.001 * rand.nextGaussian(); // distribution of random data from -3 to 3 https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml 
                    }

				// Evaluate new solutions
				double fnew = objective(POP_SOL[i]); //simple squared Sum

				// Update if the solution improves or not too loud
				if ( fnew <= FIT[i] && rand.nextDouble() < L ){ //coolant to avoid overfitting 
                    // According to video increase pulse rate and reduce loudness here(https://www.youtube.com/watch?v=peqgggW-gcs)
					POP_SOL[i] = S[i];
					FIT[i] = fnew;
				}

				// Update the current best solution
				if ( fnew <= fmin ){
					B = POP_SOL[i];
					fmin = fnew;
				}
			} // end loop for BATS
			n_iter = n_iter + BATS;
		} // end loop for MAX
        for ( int i = 0; i < BATS; i++ ){
            System.out.println(objective(POP_SOL[i]));
        }

        System.out.println("------------");
		System.out.println("Number of evaluations : " + n_iter );
		System.out.println("Best = " + Arrays.toString(B) );
		System.out.println("fmin = " + fmin );
	}

	public static void main(String[] args) {
        // Bats will be replaced by nodes of server in your case
        // We will pass nodes as an array here, and then will simulatanously get resources of each node in the algorithm
		new BatAlgo(20, 1000, 0.0, 1.0, 0.0, 1.0).startBat();

	}
}