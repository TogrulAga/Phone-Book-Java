package phonebook;

import java.util.Objects;
import java.util.Scanner;

public class DirectoryEntry {
    private final String number;
    private final String name;

    DirectoryEntry(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public long compareTo(DirectoryEntry other) {
        return Objects.compare(this, other, DirectoryEntry::compare );
    }

    public static int compare(DirectoryEntry a, DirectoryEntry b) {
        return String.CASE_INSENSITIVE_ORDER.compare(a.getName(), b.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, name);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DirectoryEntry otherEntry)) {
            return false;
        }

        return Objects.equals(name, otherEntry.getName());
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
