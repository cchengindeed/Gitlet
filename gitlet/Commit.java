package gitlet;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * The class representing a commit object.
 * @author Yu Xi Gui
 */

public class Commit implements Serializable {
    /**The SHA1-value after hashing the commit's type, files' hashes,
     * parent hashvalue, and commit message.*/
    private String _hashvalue;
    /**The id of this commit's parent.*/
    private String _parent;
    /**The time this commit is commited.*/
    private String _time;
    /**The commit message entered by the user.*/
    private String _commitMessage;
    /** The blob references made by this commit.*/
    private HashMap<String, String> _blobs;

    /**The id of this commit's parent2.*/
    private String _parent2;

    /**The commit constructor.*/
    public Commit() {
        _parent = "";
        _hashvalue = "";
        _blobs = null;
        _commitMessage = "";
        _time = "";
        _parent2 = "";


    }

    /**
     * The commit constructor.
     * @param parent Commit's parent.
     * @param addFiles Files to be added.
     * @param commitMessage Commit message entered.
     * @param removeFiles Files to remove.
     */
    @SuppressWarnings("unchecked")
    public Commit(String parent, HashMap<String, String> addFiles,
                  String commitMessage, HashMap<String, String> removeFiles) {
        _parent = parent;
        if (_parent.equals("")) {
            _time = "Thu Jan 01 00:00:00 1970 -0800";
        } else {
            _time = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy").
                    format(new Date()) + " -0800";
        }
        _commitMessage = commitMessage;
        if (parent.equals("")) {
            _blobs = new HashMap<>();
        } else {
            _blobs = new HashMap<>();
            _blobs = (HashMap<String, String>) ((Commit) Main.readF
                    (".commits", getParentOne())).getBlobs().clone();
        }
        for (String keys : addFiles.keySet()) {
            _blobs.put(keys, addFiles.get(keys));
        }
        for (String keys : removeFiles.keySet()) {
            _blobs.remove(keys);
        }
        String toBeHashed = "commit " + _parent + _commitMessage;
        for (String key : _blobs.keySet()) {
            toBeHashed += key;
        }
        _hashvalue = Utils.sha1(toBeHashed);
        _parent2 = "";
    }

    /**To get this commit's SHA1-value.
     * @return String of this commit's SHA1-value.
     */
    public String getId() {
        return this._hashvalue;
    }
    /**To get this commit's parent1.
     * @return String of this commit's parent1.
     */
    public String getParentOne() {
        return this._parent;
    }

    /**To get this commit's timestamp.
     * @return String of this commit's timestamp.
     */
    public String getTime() {
        return this._time;
    }
    /**To get this commit's blobs.
     * @return Hashmap of this commit's blobs.
     */
    public HashMap<String, String> getBlobs() {
        return _blobs;
    }
    /**To get this commit's commit message.
     * @return String of this commit's commit message.
     */
    public String getCommitmessage() {
        return _commitMessage;
    }

    /**To get this commit's parent2.
     * @return String of this commit's parent2.
     */
    public String getParent2() {
        return this._parent2;
    }

    /**
     * Changes the parentOne id.
     * @param id A string.
     */
    public void setParent1(String id) {
        _parent = id;
    }
    /**
     * Changes the parentTwo id.
     * @param id A string.
     */
    public void setParent2(String id) {
        _parent2 = id;
    }

    /**
     * The method to duplicate method to its to path.
     * @param to The repo of to.
     * @param from The repo of from.
     * @param id The id of the commit.
     * @throws IOException
     */
    public static void dupCom(String to, String from, String id)
            throws IOException {
        Path t = Paths.get(to, ".commits",
                id);
        Path g = Paths.get(from, ".commits",
                id);
        if (!Files.exists(t)) {
            Files.copy(g, t);
        }
    }


}
