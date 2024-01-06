
import java.util.Random;
import java.util.Arrays;

public class BatAlgo {

	private double[][] POP_SOL_DIST; 		// Population/Solution (BATS x SOL_SIZE) 
	private double[][] V; 		// Velocities (BATS x SOL_SIZE)
	private double[][] FRE; 	// Frequency : 0 to F_MAX (BATS x 1)
	private double[] FIT;		// Fitness (BATS)
	private double PR; 			// Pulse Rate : 0 to 1
	private double L; 			// Louadness : L_MIN to L_MAX
	private double[][] lb;		// Lower bound (1 x SOL_SIZE)
	private double[][] ub;		// Upper bound (1 x SOL_SIZE)
	private double fmin; 		// Minimum fitness from FIT
 	private double[] BEST;			// Best solution array from POP_SOL_DIST (SOL_SIZE)	

	private final int VMS; 		// Number of bats
	private final int MAX; 		// Number of iterations
	private final double F_MIN = 0.0;
	private final double F_MAX = 2.0;
	private final double L_MIN;
	private final double L_MAX;
	private final double PR_MIN;
	private final double PR_MAX; 
	private final int SOL_SIZE = 10;
	
	double cpu_utilized = 6500;
	double cpu_total = 7000;

	double mem_utilized = 3999;
	double mem_total = 4000;
	
	double bw_utilized = 208;
	double bw_total = 250;
	
	double p_min = 50;
	double p_max = 250;
	int BESTI = 0;
	
	private final Random rand = new Random();

	public BatAlgo(int VMS, int MAX, double L_MIN, double L_MAX, double PR_MIN, double PR_MAX){
		this.VMS = VMS;
		this.MAX = MAX;
		this.PR_MAX = PR_MAX;
		this.PR_MIN = PR_MIN;
		this.L_MAX = L_MAX;
		this.L_MIN = L_MIN;

		this.POP_SOL_DIST = new double[VMS][SOL_SIZE];
		this.V = new double[VMS][SOL_SIZE];
		this.FRE = new double[VMS][1];
		this.FIT = new double[VMS];
		this.PR = (PR_MAX + PR_MIN) / 3;
		this.L = (L_MIN + L_MAX) / 3;

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
		for ( int i = 0; i < VMS; i++ ){
			this.FRE[i][0] = 0.0;
		}
		for ( int i = 0; i < VMS; i++ ){
			for ( int j = 0; j < SOL_SIZE; j++ ) {
				this.V[i][j] = 0.0;
			}
		}

		boolean isSlaViolation = isSlaViolation(cpu_utilized,cpu_total);
		double _rrr = getResourceConsumed(cpu_utilized,cpu_total, mem_utilized, mem_total, bw_utilized, bw_total);
		double _pc = getPowerConsumption(cpu_utilized, cpu_total, p_min, p_max);
		// Initialize POP_SOL_DIST
		for ( int i = 0; i < VMS; i++ ){
			for ( int j = 0; j < SOL_SIZE; j++ ){
				this.POP_SOL_DIST[i][j] = lb[0][j] + (ub[0][j] - lb[0][j]) * rand.nextDouble(); 
			}
			this.FIT[i] = objective(isSlaViolation, _rrr, _pc, POP_SOL_DIST[i]);
		}

		// Find initial best solution
		int fmin_i = 0;
		for ( int i = 0; i < VMS; i++ ){
			if ( FIT[i] < FIT[fmin_i] )
				fmin_i = i;
		}

		// Store minimum fitness and it's index.
		// BEST holds the best solution array[1xD]
		this.fmin = FIT[fmin_i];
		this.BEST = POP_SOL_DIST[fmin_i]; // (1xD)
	}

	public  double objective(boolean isSlaViolation, double rrr, double pc, double[] S) {
        double omega1 = 0.33;
        double omega2 = 0.33;
        double omega3 = 0.33;
        if(omega1+ omega2 +omega3 > 1){
//         You need to make sure that w1 +w2+ w3 =1
//          So you can decide the weights accordingly
            try {
                throw new Exception(new Throwable());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
		double obj = ((omega1* (isSlaViolation? 1: 0)) +(omega2 * rrr) +(omega3* pc)) / 3;
		double sum = Arrays.stream(S).sum();
        return obj * sum;
    }
    public static boolean isSlaViolation(double cpu_utilized, double cpu_total) {
        
		return (cpu_utilized / cpu_total) > 0.9;
	 }
    public static double getResourceConsumed(double cpu_utilized, double cpu_total, double mem_utilized, double mem_total, double bw_utilized, double bw_total) {
        // return (1-(cpu_utilized/cpu_total)) * (1-(mem_utilized/mem_total)) * (1-(bw_utilized/bw_total));
        return  ((((cpu_utilized/cpu_total)) + ((mem_utilized/mem_total)) + ((bw_utilized/bw_total)))/3);
    }
    
    public static double getPowerConsumption(double cpu_utilized, double cpu_total,double p_min, double p_max) {
        // Cpu utilized is the primary factor here
        return ( ((cpu_utilized/cpu_total) / (p_min +(p_max - p_min) * (cpu_utilized/cpu_total))) * p_max);
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

		double[][] S = new double[VMS][SOL_SIZE];
		int n_iter = 0;

     
		
        // Loop for all iterations/generations(MAX)
		for ( int gen = 0; gen < MAX; gen++ ){
			// Loop for all bats(BATS) or NODES--
			for ( int i = 0; i < VMS; i++ ){
				
				// Update frequency (Nx1)
				FRE[i][0] = F_MIN + (F_MAX - F_MIN) * (rand.nextDouble() ); 
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
				


                // Pulse rate
				// if ( rand.nextDouble() > PR ) //pC
					for ( int j = 0; j < SOL_SIZE; j++ )
                    {
                        // Generating Local solution around best solution
						// 0.001 is the learning rate
						S[i][j] = BEST[j] + 0.01 * rand.nextDouble(); // distribution of random data from -3 to 3 https://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml 
                    }
					
				// Evaluate new solutions
				boolean isSlaViolation = isSlaViolation(cpu_utilized,cpu_total);
				double _rrr = getResourceConsumed(cpu_utilized,cpu_total, mem_utilized, mem_total, bw_utilized, bw_total);
				double _pc = getPowerConsumption(cpu_utilized, cpu_total, p_min, p_max);
				double fnew = objective(isSlaViolation, _rrr, _pc, S[i]);

				// Update if the solution improves or not too loud
				if ( fnew <= FIT[i] 
				&& rand.nextDouble() < L 
				){ //Loudness with sLA, coolant to avoid overfitting 
                    // According to video increase pulse rate and reduce loudness here(https://www.youtube.com/watch?v=peqgggW-gcs)
					POP_SOL_DIST[i] = S[i];
					FIT[i] = fnew;
				}

				// Update the current best solution
				if ( fnew <= fmin ){
					BESTI = i; // Best VM
					BEST = POP_SOL_DIST[i];
					fmin = fnew;
				}
			} // end loop for BATS
			L  -= 0.000001;
			PR += 0.000001;
			n_iter = n_iter + VMS;

		} // end loop for MAX
		boolean isSlaViolation = isSlaViolation(cpu_utilized,cpu_total);
		double _rrr = getResourceConsumed(cpu_utilized,cpu_total, mem_utilized, mem_total, bw_utilized, bw_total);
		double _pc = getPowerConsumption(cpu_utilized, cpu_total, p_min, p_max);
        for ( int i = 0; i < VMS; i++ ){
			double fnew = objective(isSlaViolation, _rrr, _pc, S[i]);
            // System.out.println(objective(POP_SOL_DIST[i]));
        }

        System.out.println("------------");
		System.out.println("Number of evaluations : " + n_iter );
		System.out.println("Best = " + Arrays.toString(BEST) );
		System.out.println("BESTI = " + BESTI );
		System.out.println("fmin = " + fmin );
	}

	public static void main(String[] args) {
        // Bats will be replaced by nodes of server in your case
        // We will pass nodes as an array here, and then will simulatanously get resources of each node in the algorithm
		new BatAlgo(20, 100000, 0.0, 1.0, 0.0, 1.0).startBat();

	}
}