import java.util.*;

public class RoundRobin {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        int bt[] = new int[n];
        int at[] = new int[n];
        int wt[] = new int[n];
        int tat[] = new int[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Enter Arrival Time and Burst Time for P" + (i + 1) + ": ");
            at[i] = sc.nextInt();
            bt[i] = sc.nextInt();
        }

        System.out.print("Enter Time Quantum: ");
        int tq = sc.nextInt();

        int remaining[] = Arrays.copyOf(bt, n);
        int time = 0, completed = 0;

        Queue<Integer> q = new LinkedList<>();
        boolean inQueue[] = new boolean[n];

        while (completed != n) {
            for (int i = 0; i < n; i++) {
                if (at[i] <= time && !inQueue[i] && remaining[i] > 0) {
                    q.add(i);
                    inQueue[i] = true;
                }
            }

            if (q.isEmpty()) {
                time++;
                continue;
            }

            int idx = q.poll();
            int exec = Math.min(tq, remaining[idx]);
            remaining[idx] -= exec;
            time += exec;

            for (int i = 0; i < n; i++) {
                if (at[i] <= time && !inQueue[i] && remaining[i] > 0) {
                    q.add(i);
                    inQueue[i] = true;
                }
            }

            if (remaining[idx] > 0) {
                q.add(idx);
            } else {
                completed++;
                tat[idx] = time - at[idx];
                wt[idx] = tat[idx] - bt[idx];
            }
        }

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
