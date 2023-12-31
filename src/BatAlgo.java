
import java.util.Random;
import java.util.Arrays;

public class BatAlgo {

	private double[][] POP_SOL_DIST; 		// Population/Solution (BATS x SOL_SIZE) 
	private double[][] V; 		// Velocities (BATS x SOL_SIZE)
	private double[][] FRE; 		// Frequency : 0 to F_MAX (BATS x 1)
	private double[] FIT;			// Fitness (BATS)
	private double PR; 			// Pulse Rate : 0 to 1
	private double L; 			// Louadness : L_MIN to L_MAX
	private double[][] lb;		// Lower bound (1 x SOL_SIZE)
	private double[][] ub;		// Upper bound (1 x SOL_SIZE)
	private double fmin; 		// Minimum fitness from FIT
 	private double[] BEST;			// Best solution array from POP_SOL_DIST (SOL_SIZE)	

	private final int BATS; 		// Number of bats
	private final int MAX; 		// Number of iterations
	private final double F_MIN = 0.0;
	private final double F_MAX = 2.0;
	private final double L_MIN;
	private final double L_MAX;
	private final double PR_MIN;
	private final double PR_MAX; 
	private final int SOL_SIZE = 10;
	
	private final Random rand = new Random();

	public BatAlgo(int BATS, int MAX, double L_MIN, double L_MAX, double PR_MIN, double PR_MAX){
		this.BATS = BATS;
		this.MAX = MAX;
		this.PR_MAX = PR_MAX;
		this.PR_MIN = PR_MIN;
		this.L_MAX = L_MAX;
		this.L_MIN = L_MIN;

		this.POP_SOL_DIST = new double[BATS][SOL_SIZE];
		this.V = new double[BATS][SOL_SIZE];
		this.FRE = new double[BATS][1];
		this.FIT = new double[BATS];
		this.PR = (PR_MAX + PR_MIN) / 2;
		this.L = (L_MIN + L_MAX) / 2;

		// Initialize bounds
		this.lb = new double[1][SOL_SIZE];
		for ( int i = 0; i < SOL_SIZE; i++ ){
			this.lb[0][i] = -2.0;
		}
		this.ub = new double[1][SOL_SIZE];
		for ( int i = 0; i < SOL_SIZE; i++ ){
			this.ub[0][i] = 2.0;
		}

		// Initialize FRE and V
		for ( int i = 0; i < BATS; i++ ){
			this.FRE[i][0] = 0.0;
		}
		for ( int i = 0; i < BATS; i++ ){
			for ( int j = 0; j < SOL_SIZE; j++ ) {
				this.V[i][j] = 0.0;
			}
		}

		// Initialize POP_SOL_DIST
		for ( int i = 0; i < BATS; i++ ){
			for ( int j = 0; j < SOL_SIZE; j++ ){
				this.POP_SOL_DIST[i][j] = lb[0][j] + (ub[0][j] - lb[0][j]) * rand.nextDouble(); 
			}
			this.FIT[i] = objective(POP_SOL_DIST[i]);
		}

		// Find initial best solution
		int fmin_i = 0;
		for ( int i = 0; i < BATS; i++ ){
			if ( FIT[i] < FIT[fmin_i] )
				fmin_i = i;
		}

		// Store minimum fitness and it's index.
		// BEST holds the best solution array[1xD]
		this.fmin = FIT[fmin_i];
		this.BEST = POP_SOL_DIST[fmin_i]; // (1xD)
	}

    // Can be replaced with ROSENBROCK 3d FUNCTION (https://www.sfu.ca/~ssurjano/rosen.html) easy valley but difficult global minima
	private double objective(double[] Xi){
		double sum = 0.0;
		for ( int i = 0; i < Xi.length; i++ ){
			sum = sum + Xi[i] * Xi[i];
		}
		return sum;
	}

	private double[] simpleBounds(double[] Xi){
		// Don't know if this should be implemented
		double[] Xi_temp = new double[SOL_SIZE];
		System.arraycopy(Xi, 0, Xi_temp, 0, SOL_SIZE);

		for ( int i = 0; i < SOL_SIZE; i++ ){
			if ( Xi_temp[i] < lb[0][i] )
				Xi_temp[i] = lb[0][i];
			else continue;
		}

		for ( int i = 0; i < SOL_SIZE; i++ ){
			if ( Xi_temp[i] > ub[0][i] )
				Xi_temp[i] = lb[0][i];
			else continue;
		}
		return Xi_temp;
	}

	private void startBat(){

		double[][] S = new double[BATS][SOL_SIZE];
		int n_iter = 0;

        // int node = BATS; //Nodes/VMS  -> BATS
        // double[] nodes = new double[node];
        // Calculate and fill getResourceRemainingRate for all CPUS here
        // double[] rrR = new double[node];
        // Calculate and fill getPowerConsumption for all CPUS here
        // double[] pC = new double[node];
        // Calculate and fill getSla for all CPUS here
        // double[] sLA = new double[node];
        // for (int i = 0; i < node; i++) {
        //     rrR[i] = getResourceRemainingRate(nodes[i].cpu_utilized,nodes[i].cpu_total,nodes[i].mem_utilized, nodes[i].mem_total, nodes[i].bw_utilized, nodes[i].bw_total)
        //     pC[i] = getPowerConsumption(nodes[i].cpu_utilized,nodes[i].cpu_total, nodes[i].mem_utilized, nodes[i].mem_total, nodes[i].bw_utilized, nodes[i].bw_total)
        //     sLA[i] = getSla(nodes[i].cpu_utilized,nodes[i].cpu_total)
        // }
        // Arrays.sort(rrR);
        // F_MIN = rrR[0];
        // F_MAX = rrR[rrR.length-1];

        
        // for (int i = 0; i < node; i++) {
        // }
		
        // Loop for all iterations/generations(MAX)
		for ( int gen = 0; gen < MAX; gen++ ){
			// Loop for all bats(BATS) or NODES--
			for ( int i = 0; i < BATS; i++ ){
				
				// Update frequency (Nx1)
				FRE[i][0] = F_MIN + (F_MAX - F_MIN) * (rand.nextDouble() ); // Fmin and fmax will be replaced by node range of getResourceRemainingRate for all cpus
				// Update velocity (NxD)
				for ( int j = 0; j < SOL_SIZE; j++ ){
					V[i][j] = V[i][j] + (POP_SOL_DIST[i][j] - BEST[j]) * FRE[i][0];
				}
				// Update S = POP_SOL_DIST + V
				for ( int j = 0; j < SOL_SIZE; j++ ){
					S[i][j] = POP_SOL_DIST[i][j] + V[i][j];
				}
				// Apply bounds/limits
				// S[i] = simpleBounds(POP_SOL_DIST[i]);
				

                // PR will be replaced by getPowerConsumption(cpu_utilized, cpu_total, p_min, p_max) with a very gradual increase

                // Pulse rate
				if ( rand.nextDouble() > PR ) //pC
					for ( int j = 0; j < SOL_SIZE; j++ )
                    {
                        // Generating Local solution around best solution
						// 0.001 is the learning rate
						S[i][j] = BEST[j] + 0.01 * rand.nextGaussian(); // distribution of random data from -3 to 3 https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml 
                    }

				// Evaluate new solutions
				double fnew = objective(S[i]); //simple squared Sum

				// Update if the solution improves or not too loud
				if ( fnew <= FIT[i] && rand.nextDouble() < L ){ //Loudness with sLA, coolant to avoid overfitting 
                    // According to video increase pulse rate and reduce loudness here(https://www.youtube.com/watch?v=peqgggW-gcs)
					POP_SOL_DIST[i] = S[i];
					FIT[i] = fnew;
				}

				// Update the current best solution
				if ( fnew <= fmin ){
					BEST = POP_SOL_DIST[i];
					fmin = fnew;
				}
			} // end loop for BATS
			n_iter = n_iter + BATS;
		} // end loop for MAX
        for ( int i = 0; i < BATS; i++ ){
            System.out.println(objective(POP_SOL_DIST[i]));
        }

        System.out.println("------------");
		System.out.println("Number of evaluations : " + n_iter );
		System.out.println("Best = " + Arrays.toString(BEST) );
		System.out.println("fmin = " + fmin );
	}

	public static void main(String[] args) {
        // Bats will be replaced by nodes of server in your case
        // We will pass nodes as an array here, and then will simulatanously get resources of each node in the algorithm
		new BatAlgo(20, 1000, 0.0, 1.0, 0.0, 1.0).startBat();

	}
}