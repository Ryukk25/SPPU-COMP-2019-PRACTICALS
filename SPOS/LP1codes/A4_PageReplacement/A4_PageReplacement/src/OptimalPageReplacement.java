import java.util.*;

public class OptimalPageReplacement {
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

        ArrayList<Integer> frameList = new ArrayList<>();
        int pageFaults = 0, pageHits = 0;

        System.out.println("\nPage\tFrames");
        for (int i = 0; i < pages; i++) {
            int page = referenceString[i];
            if (frameList.contains(page)) {
                pageHits++;
            } else {
                pageFaults++;
                if (frameList.size() < frames) {
                    frameList.add(page);
                } else {
                    int indexToReplace = findOptimalIndex(frameList, referenceString, i + 1);
                    frameList.set(indexToReplace, page);
                }
            }

            System.out.print(page + "\t" + frameList + "\n");
        }

        System.out.println("\nTotal Page Faults: " + pageFaults);
        System.out.println("Total Page Hits: " + pageHits);
        System.out.printf("Hit Ratio: %.2f\n", (float) pageHits / pages);
        System.out.printf("Miss Ratio: %.2f\n", (float) pageFaults / pages);
    }

    private static int findOptimalIndex(ArrayList<Integer> frameList, int[] ref, int start) {
        int farthest = -1, index = -1;
        for (int i = 0; i < frameList.size(); i++) {
            int page = frameList.get(i);
            int j;
            for (j = start; j < ref.length; j++) {
                if (ref[j] == page) break;
            }
            if (j == ref.length) return i;
            if (j > farthest) {
                farthest = j;
                index = i;
            }
        }
        return (index == -1) ? 0 : index;
    }
}
