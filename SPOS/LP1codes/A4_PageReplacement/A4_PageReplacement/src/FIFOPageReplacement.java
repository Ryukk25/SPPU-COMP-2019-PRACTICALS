import java.util.*;

public class FIFOPageReplacement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of frames: ");
        int frames = sc.nextInt();
        System.out.print("Enter number of pages: ");
        int pages = sc.nextInt();

        int[] referenceString = new int[pages];
        System.out.println("Enter the page reference string:");
        for (int i = 0; i < pages; i++) {
            referenceString[i] = sc.nextInt();
        }

        Queue<Integer> queue = new LinkedList<>();
        int pageFaults = 0, pageHits = 0;

        System.out.println("\nPage\tFrames");
        for (int i = 0; i < pages; i++) {
            int page = referenceString[i];
            if (queue.contains(page)) {
                pageHits++;
            } else {
                pageFaults++;
                if (queue.size() == frames)
                    queue.poll();
                queue.add(page);
            }

            System.out.print(page + "\t" + queue + "\n");
        }

        System.out.println("\nTotal Page Faults: " + pageFaults);
        System.out.println("Total Page Hits: " + pageHits);
        System.out.printf("Hit Ratio: %.2f\n", (float) pageHits / pages);
        System.out.printf("Miss Ratio: %.2f\n", (float) pageFaults / pages);
    }
}
