package phonebook;

import java.io.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class PhoneBook {
    public static List<DirectoryEntry> directory = new ArrayList<>();
    public static List<DirectoryEntry> find = new ArrayList<>();
    public static List<DirectoryEntry> found = new ArrayList<>();
    public static void run() {
        loadDirectory();
        loadFind();

        System.out.println("Start searching...");
        long timeTaken = linearSearch();
        printTimeTaken(found.size(), find.size(), timeTaken);

        found = new ArrayList<>();

        System.out.println("\nStart searching (bubble sort + jump search)...");
        bubbleSortJumpSearch(timeTaken);

        found = new ArrayList<>();

        System.out.println("\nStart searching (quick sort + binary search)...");
        quickSortBinarySearch();

        found = new ArrayList<>();

        System.out.println("\nStart searching (hash table)...");
        hashTableSearch();
    }

    private static void hashTableSearch() {
        long start = System.currentTimeMillis();

        HashMap<String, DirectoryEntry> hashMap = new HashMap<>();
        for (DirectoryEntry entry: directory) {
            hashMap.put(entry.getName(), entry);
        }
        long creatingTime = System.currentTimeMillis() - start;

        for (DirectoryEntry entry: find) {
            found.add(hashMap.get(entry.getName()));
        }
        long searchingTime = System.currentTimeMillis() - creatingTime - start;

        printTimeTaken(found.size(), find.size(), creatingTime + searchingTime);
        System.out.printf("Creating time: %s%n", millisToDurationString(creatingTime));
        System.out.printf("Searching  time: %s%n", millisToDurationString(searchingTime));
    }

    private static void quickSortBinarySearch() {
        long start = System.currentTimeMillis();
        List<DirectoryEntry> sortedDirectory = new ArrayList<>(directory);

        quickSort(sortedDirectory, 0, sortedDirectory.size() - 1);
        long sortingTime = System.currentTimeMillis() - start;

        long searchingTime = binarySearch(sortedDirectory);
        printTimeTaken(found.size(), find.size(), sortingTime + searchingTime);
        System.out.printf("Sorting time: %s%n", millisToDurationString(sortingTime));
        System.out.printf("Searching time: %s%n", millisToDurationString(searchingTime));
    }

    private static long binarySearch(List<DirectoryEntry> sortedDirectory) {
        long start = System.currentTimeMillis();
        for (DirectoryEntry entry: find) {
            int left = 0;
            int right = sortedDirectory.size() - 1;
            while (left <= right) {
                int middle = (left + right) / 2;
                if (sortedDirectory.get(middle).equals(entry)) {
                    found.add(sortedDirectory.get(middle));
                    break;
                }
                else if (sortedDirectory.get(middle).compareTo(entry) > 0) {
                        right = middle - 1;
                    }
                else {
                    left = middle + 1;
                }
            }
        }

        return System.currentTimeMillis() - start;
    }

    private static void quickSort(List<DirectoryEntry> sortedDirectory, int from, int to) {
        if (from < to) {
            int pi = partition(sortedDirectory, from, to);

            quickSort(sortedDirectory, from, pi - 1);

            quickSort(sortedDirectory, pi + 1, to);
        }
    }

    private static int partition(List<DirectoryEntry> sortedDirectory, int from, int to) {
        DirectoryEntry pivot = sortedDirectory.get(to);

        int i = from - 1;

        for (int j = from; j <= to - 1; j++){
            if (sortedDirectory.get(j).compareTo(pivot) < 0){
                i++;
                DirectoryEntry temp = sortedDirectory.get(i);
                sortedDirectory.set(i, sortedDirectory.get(j));
                sortedDirectory.set(j, temp);
            }
        }
        DirectoryEntry temp = sortedDirectory.get(i + 1);
        sortedDirectory.set(i + 1, sortedDirectory.get(to));
        sortedDirectory.set(to, temp);
        return i + 1;
    }

    private static void bubbleSortJumpSearch(long linearSearchTime) {
        long start = System.currentTimeMillis();
        List<DirectoryEntry> sortedDirectory = new ArrayList<>(directory);
        int index = 0;
        int swaps = 0;
        boolean sortingSuccessful = false;
        while (System.currentTimeMillis() - start < 10 * linearSearchTime) {
            if (index == sortedDirectory.size() - 1) {
                if (swaps == 0) {
                    sortingSuccessful = true;
                    break;
                }
                swaps = 0;
                index = 0;
                continue;
            }
            if (sortedDirectory.get(index).compareTo(sortedDirectory.get(index + 1)) > 0) {
                DirectoryEntry temp = sortedDirectory.get(index);
                sortedDirectory.set(index, sortedDirectory.get(index + 1));
                sortedDirectory.set(index + 1, temp);
                swaps++;
            }

            index++;
        }

        long sortingTime = System.currentTimeMillis() - start;

        long searchingTime;
        if (!sortingSuccessful) {
            searchingTime = linearSearch();
        } else {
            searchingTime = jumpSearch(sortedDirectory);
        }

        printTimeTaken(found.size(), find.size(), sortingTime + searchingTime);

        System.out.printf("Sorting time: %s%s%n",
                millisToDurationString(sortingTime),
                sortingSuccessful ? "" : " - STOPPED, moved to linear search");
        System.out.printf("Searching time: %s%n", millisToDurationString(searchingTime));
    }

    private static long jumpSearch(List<DirectoryEntry> sortedDirectory) {
        long start = System.currentTimeMillis();

        int jumpSize = (int) Math.floor(Math.sqrt(directory.size()));

        for (DirectoryEntry entry: find) {
            int index = 0;
            while (true) {
                if (index + jumpSize < sortedDirectory.size()) {
                    index += jumpSize;
                } else {
                    index = sortedDirectory.size();
                }
                DirectoryEntry currentEntry = sortedDirectory.get(index);
                long comparisonResult = currentEntry.compareTo(entry);
                if (comparisonResult == 0) {
                    // Entry found
                    found.add(currentEntry);
                    break;
                } else if (comparisonResult < 0) {
                    // Entry is not in this block
                    continue;
                } else {
                    // Entry is in this block
                    boolean entryFound = false;
                    for (int i = index - 1; i >= index - jumpSize; i--) {
                        currentEntry = sortedDirectory.get(i);
                        if (currentEntry.getName().equals(entry.getName())) {
                            // Entry found
                            found.add(currentEntry);
                            entryFound = true;
                            break;
                        }
                    }
                    if (entryFound) {
                        break;
                    }
                }
            }
        }
        return System.currentTimeMillis() - start;
    }

    private static long linearSearch() {
        long start = System.currentTimeMillis();
        for (DirectoryEntry entry: directory) {
            if (find.stream().anyMatch(entry::equals)) {
                found.add(entry);
            }
        }

        return System.currentTimeMillis() - start;
    }

    private static void printTimeTaken(int size, int size1, long timeTaken) {
        System.out.printf("Found %d / %d entries. Time taken: %s%n",
                size,
                size1,
                millisToDurationString(timeTaken));
    }

    public static String millisToDurationString(long millis) {
        Duration duration = Duration.ofMillis(millis);
        return "%d min. %d sec. %d ms.".formatted(duration.toMinutesPart(), duration.toSecondsPart(), duration.toMillisPart());
    }

    private static void loadFind() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\files\\find.txt"))) {
            for (String str: reader.lines().toList()) {
                find.add(new DirectoryEntry("", str));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadDirectory() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src\\files\\directory.txt"))) {
            for (String str: reader.lines().toList()) {
                String[] split = str.split("\\s");
                String number = split[0];
                String name = Arrays.stream(split).skip(1).collect(Collectors.joining(" "));
                directory.add(new DirectoryEntry(number, name));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File does not exist");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
