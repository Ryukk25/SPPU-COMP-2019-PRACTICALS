import java.util.*;

public class SJFPreemptive {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        int bt[] = new int[n];
        int at[] = new int[n];
        int wt[] = new int[n];
        int tat[] = new int[n];
        int remaining[] = new int[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Arrival Time and Burst Time for P" + (i + 1) + ": ");
            at[i] = sc.nextInt();
            bt[i] = sc.nextInt();
            remaining[i] = bt[i];
        }

        int complete = 0, time = 0, minm = Integer.MAX_VALUE;
        int shortest = 0, finishTime;
        boolean check = false;

        while (complete != n) {
            for (int j = 0; j < n; j++) {
                if ((at[j] <= time) && (remaining[j] < minm) && remaining[j] > 0) {
                    minm = remaining[j];
                    shortest = j;
                    check = true;
                }
            }

            if (!check) {
                time++;
                continue;
            }

            remaining[shortest]--;
            minm = remaining[shortest];
            if (minm == 0)
                minm = Integer.MAX_VALUE;

            if (remaining[shortest] == 0) {
                complete++;
                check = false;
                finishTime = time + 1;
                wt[shortest] = finishTime - bt[shortest] - at[shortest];
                if (wt[shortest] < 0) wt[shortest] = 0;
            }
            time++;
        }

        for (int i = 0; i < n; i++)
            tat[i] = bt[i] + wt[i];

        float totalWT = 0, totalTAT = 0;
        System.out.println("\nPID\tAT\tBT\tTAT\tWT");
        for (int i = 0; i < n; i++) {
            System.out.println("P" + (i + 1) + "\t" + at[i] + "\t" + bt[i] + "\t" + tat[i] + "\t" + wt[i]);
            totalWT += wt[i];
            totalTAT += tat[i];
        }

        System.out.println("\nAverage Waiting Time: " + (totalWT / n));
        System.out.println("Average Turnaround Time: " + (totalTAT / n));
    }
}
