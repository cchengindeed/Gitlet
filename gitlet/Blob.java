package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A blob object that contains content and a filename.
 * @author Yu Xi Gui
 */
public class Blob implements Serializable {
    /**The filename of the blob.*/
    private String _filename;
    /**The content in the blob.*/
    private String _content;
    /**The hash or id of the blob.*/
    private String _blobName;

    /**
     * Constructor for Blob object.
     * @param fileName A string representing the filename.
     * @param content A string that contains content.
     */
    public Blob(String fileName, String content) {
        _filename = fileName;
        _content = content;
        _blobName = Utils.sha1("Blob" + _filename + _content);

    }

    /**
     * A method that gets the blobname or the id.
     * @return A string of the blobname instance variable.
     */
    public String getBlobname() {
        return _blobName;
    }

    /**
     * A method that gets the blob content.
     * @return A string of the blob content.
     */
    public String getContent() {
        return _content;
    }

    /**
     * Returns the file name of the blob.
     * @return A string.
     */
    public String getFilename() {
        return _filename;
    }

    /**
     * Copies the Blob to the other repo if not there.
     * @param to The place to copy to
     * @param from Where the blob exists.
     * @param id The ID.
     * @throws IOException
     */
    public static void copyFile(String to, String from, String id)
            throws IOException {
        Path to1 = Paths.get(to, ".blobs",
                id);
        Path from1 = Paths.get(from, ".blobs",
                id);
        if (!Files.exists(to1)) {
            Files.copy(from1, to1);
        }
    }
}
