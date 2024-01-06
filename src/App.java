import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    public static void main(String[] args) throws Exception {
        
        double cpu_utilized = 6500;
        double cpu_total = 7000;

        double mem_utilized = 3999;
        double mem_total = 4000;
        
        double bw_utilized = 208;
        double bw_total = 250;
        
        double p_min = 50;
        double p_max = 250;
        
        System.out.println(isSlaViolation(cpu_utilized,cpu_total));
        
        System.out.println(getResourceConsumed(cpu_utilized,cpu_total, mem_utilized, mem_total, bw_utilized, bw_total));
        
        System.out.println(getPowerConsumption(cpu_utilized, cpu_total, p_min, p_max) );

        
        // String ramInfo = "Ram: used 792 of 2048";
        // String bandwidthInfo = "Bandwidth: used 1548 of 10000";

        // int[] ramValues = extractUsedAndTotal(ramInfo);
        // int[] bandwidthValues = extractUsedAndTotal(bandwidthInfo);

        // System.out.println("RAM - Used: " + ramValues[0] + ", Total: " + ramValues[1]);
        // System.out.println("Bandwidth - Used: " + bandwidthValues[0] + ", Total: " + bandwidthValues[1]);
        
    }
    public static double objective(double sla, double rrr, double pc) {
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
        return ((omega1* sla) +(omega2 * rrr) +(omega3* pc)) / 3  ;
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
}
