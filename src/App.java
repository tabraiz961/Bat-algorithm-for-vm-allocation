
import java.util.Arrays;
import java.util.Random;
public class App {
    public static void main(String[] args) throws Exception {
        
        double cpu_utilized = 6000;
        double cpu_total = 7000;

        double mem_utilized = 3999;
        double mem_total = 4000;
        
        double bw_utilized = 208;
        double bw_total = 250;
        
        double p_min = 50;
        double p_max = 250;
        
        System.out.println(getSla(cpu_utilized,cpu_total));
        
        System.out.println(getResourceRemainingRate(cpu_utilized,cpu_total, mem_utilized, mem_total, bw_utilized, bw_total));
        
        System.out.println(getPowerConsumption(cpu_utilized, cpu_total, p_min, p_max) );
        // Random rand = new Random();
        // double[] FIT= new double[200];
        // for (int i = 0; i < 200; i++) {
        //     FIT[i] = rand.nextGaussian();
        // }
        // Arrays.sort(FIT);
        // for (int i = 0; i < 200; i++) {
        //     System.out.println(FIT[i]);
        // }
    }
    public static double getSla(double cpu_utilized, double cpu_total) {
        
        double cpu_utilize_r = cpu_utilized / cpu_total;
        // Sla for a single node, Sigmoid
    //    return 1/(1+Math.exp(cpu_utilize_r - 0.9));
       return cpu_utilized / cpu_total;
    }
    public static double getResourceRemainingRate(double cpu_utilized, double cpu_total, double mem_utilized, double mem_total, double bw_utilized, double bw_total) {
        // return (1-(cpu_utilized/cpu_total)) * (1-(mem_utilized/mem_total)) * (1-(bw_utilized/bw_total));
        return  ((((cpu_utilized/cpu_total)) + ((mem_utilized/mem_total)) + ((bw_utilized/bw_total)))/3);
    }
    
    public static double getPowerConsumption(double cpu_utilized, double cpu_total,double p_min, double p_max) {
        // Cpu utilized is the primary factor here
        return ( ((cpu_utilized/cpu_total) / (p_min +(p_max - p_min) * (cpu_utilized/cpu_total))) * p_max);
    }
}
