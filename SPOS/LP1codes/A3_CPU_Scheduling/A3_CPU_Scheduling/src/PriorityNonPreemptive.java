import java.util.*;

class Process {
    int pid;              // Process ID
    int arrivalTime;      // Arrival Time
    int burstTime;        // Burst Time
    int priority;         // Priority (lower = higher priority)
    int completionTime;   // Completion Time
    int turnaroundTime;   // Turnaround Time
    int waitingTime;      // Waiting Time
}

public class PriorityNonPreemptive {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process p[] = new Process[n];

        for (int i = 0; i < n; i++) {
            p[i] = new Process();
            p[i].pid = i + 1;
            System.out.print("Enter Arrival Time, Burst Time, and Priority for P" + (i + 1) + ": ");
            p[i].arrivalTime = sc.nextInt();
            p[i].burstTime = sc.nextInt();
            p[i].priority = sc.nextInt();
        }

        Arrays.sort(p, Comparator.comparingInt(a -> a.arrivalTime));

        int completed = 0, time = 0;
        float totalWT = 0, totalTAT = 0;
        boolean[] done = new boolean[n];

        while (completed != n) {
            int idx = -1;
            int minPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!done[i] && p[i].arrivalTime <= time && p[i].priority < minPriority) {
                    minPriority = p[i].priority;
                    idx = i;
                }
            }

            if (idx == -1) {
                time++;
                continue;
            }

            time += p[idx].burstTime;
            p[idx].completionTime = time;
            p[idx].turnaroundTime = p[idx].completionTime - p[idx].arrivalTime;
            p[idx].waitingTime = p[idx].turnaroundTime - p[idx].burstTime;
            done[idx] = true;
            completed++;

            totalWT += p[idx].waitingTime;
            totalTAT += p[idx].turnaroundTime;
        }

        System.out.println("\nPID\tAT\tBT\tPR\tCT\tTAT\tWT");
        for (Process pr : p)
            System.out.println(pr.pid + "\t" + pr.arrivalTime + "\t" + pr.burstTime + "\t" +
                               pr.priority + "\t" + pr.completionTime + "\t" +
                               pr.turnaroundTime + "\t" + pr.waitingTime);

        System.out.println("\nAverage Waiting Time: " + (totalWT / n));
        System.out.println("Average Turnaround Time: " + (totalTAT / n));
    }
}

