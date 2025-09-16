package core.tasks.progress.integrity;

import static java.util.Objects.requireNonNull;

/**
 * An entry for integrity progress which contains
 * data about the verification of a specific file
 * @author Fedrix
 */
public record IntegrityEntry(String filename, String sha256, boolean verified, String note) {
    public IntegrityEntry(String filename, String sha256, boolean verified, String note){
        this.filename = requireNonNull(filename);
        this.sha256 = requireNonNull(sha256);
        this.note = requireNonNull(note);
        this.verified = verified;
    }
}
