import java.util.*;

public class FCFS {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process p[] = new Process[n];

        for (int i = 0; i < n; i++) {
            p[i] = new Process();
            p[i].pid = i + 1;
            System.out.print("Enter Arrival Time and Burst Time for P" + (i + 1) + ": ");
            p[i].arrivalTime = sc.nextInt();
            p[i].burstTime = sc.nextInt();
        }

        Arrays.sort(p, Comparator.comparingInt(a -> a.arrivalTime));
        int currentTime = 0;
        float totalWT = 0, totalTAT = 0;

        for (int i = 0; i < n; i++) {
            currentTime = Math.max(currentTime, p[i].arrivalTime);
            p[i].completionTime = currentTime + p[i].burstTime;
            p[i].turnaroundTime = p[i].completionTime - p[i].arrivalTime;
            p[i].waitingTime = p[i].turnaroundTime - p[i].burstTime;
            currentTime = p[i].completionTime;
            totalWT += p[i].waitingTime;
            totalTAT += p[i].turnaroundTime;
        }

        System.out.println("\nPID\tAT\tBT\tCT\tTAT\tWT");
        for (Process pr : p)
            System.out.println(pr.pid + "\t" + pr.arrivalTime + "\t" + pr.burstTime + "\t" +
                               pr.completionTime + "\t" + pr.turnaroundTime + "\t" + pr.waitingTime);

        System.out.println("\nAverage Waiting Time: " + (totalWT / n));
        System.out.println("Average Turnaround Time: " + (totalTAT / n));
    }
}
