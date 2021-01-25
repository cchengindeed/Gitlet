package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The class that mirrors a stage.
 * @author Yu Xi Gui
 */
public class Stage implements Serializable {
    /**A variable that tracks file added to stage.*/
    private HashMap<String, String> addToStage;
    /**To track files removed from stage.*/
    private HashMap<String, String> removedStage;
    /**Files that are untracked.*/
    private HashMap<String, String> untracked;
    /**Files tracked that are modified.*/
    private HashMap<String, String> trackModified;
    /**Files that are tracked but deleted.*/
    private HashMap<String, String> trackDelete;
    /**The set of cur blobs the stage has.*/
    private HashMap<String, String> _currBlobs;

    /**
     * The stage constructor.
     */
    public Stage() {
        _currBlobs = new HashMap<>();
        addToStage = new HashMap<>();
        removedStage = new HashMap<>();
        untracked = new HashMap<>();
        trackModified = new HashMap<>();
        trackDelete = new HashMap<>();


    }

    /**
     *The method that sets the blobs of the stage to be
     * like that of the given commit.
     * @param c A commit.
     */
    public void setBlobs(Commit c) {
        _currBlobs = c.getBlobs();
    }

    /**
     * This method adds a copy of the file to the staging area.
     * If stage an already staged file, overwrite it with new contents.
     * If the cur version content is the same as the one in the commit,
     * don't stage it to be added and remove it from the stage if it is there.
     * @param fileName The file to be added to stage.
     * @param c The latest commit given by the commitTree.
     */
    public void addToStage(String fileName, Commit c) {
        String id;
        Boolean modified = false;
        if (addToStage.containsKey(fileName)) {
            String content = ((Blob) Main.readF(".blobs",
                    _currBlobs.get(fileName))).getContent();
            Main.writeToCWDFile("", fileName, content);
            id = _currBlobs.get(fileName);
        } else {
            String gBContent = Main.readCWDFileToString("",
                    fileName);
            if (_currBlobs.containsKey(fileName)) {
                modified = hasChanges(fileName, gBContent, true);
                if (!modified) {
                    removedStage.remove(fileName);
                    return;
                }
            }
            if (modified || !_currBlobs.containsKey(fileName)) {
                Blob temp = new Blob(fileName, gBContent);
                Main.writeFile(".temp_blobs", temp.getBlobname(), temp);
                id = temp.getBlobname();
            } else {
                id = _currBlobs.get(fileName);
            }
        }
        if (trackDelete.containsKey(fileName)) {
            trackDelete.remove(fileName);
            removedStage.put(fileName, id);
        } else if (trackModified.containsKey(fileName)) {
            trackModified.remove(fileName);
            addToStage.put(fileName, id);
        } else if (removedStage.containsKey(fileName)) {
            removedStage.remove(fileName);
        } else {
            untracked.remove(fileName);
            addToStage.put(fileName, id);
        }
    }

    /**
     * This method checks if the file has been modified.
     * @param filename The file name.
     * @param gBContent The given blob content.
     * @param track If it is tracked or not.
     * @return A boolean about whether this file has been
     * changed.
     */
    public boolean hasChanges(String filename,
                              String gBContent, Boolean track) {
        Blob currBlob;
        if (track) {
            currBlob = (Blob) Main.readF(".blobs", _currBlobs.get(filename));
        } else {
            currBlob = (Blob) Main.readF(".temp_blobs",
                    addToStage.get(filename));
        }
        return !(currBlob.getContent().equals(gBContent));
    }

    /**
     * A method that gets the files added to stage.
     * @return A hashmap.
     */
    public HashMap<String, String> getAddToStage() {
        return addToStage;
    }
    /**
     * A method that gets the files removed from stage.
     * @return A hashmap.
     */
    public HashMap<String, String> getRemovedStage() {
        return removedStage;
    }

    /**
     * When the stage gets emptied after a commit.
     */
    public void empty() {
        addToStage = new HashMap<>();
        removedStage = new HashMap<>();
        trackDelete = new HashMap<>();
        trackModified = new HashMap<>();

    }

    /**
     * A method that removed a particular file from a given commit.
     * @param fileName The filename to remove.
     * @param c The given commit.
     */
    public void removeFile(String fileName, Commit c) {
        if (_currBlobs.containsKey(fileName)) {
            addToStage.remove(fileName);
            trackModified.remove(fileName);
            untracked.remove(fileName);
            Main.delCDFile("", fileName);
            Main.deleteFile(".temp_blobs", fileName);
            removedStage.put(fileName, _currBlobs.get(fileName));
        } else if (addToStage.containsKey(fileName)
                || removedStage.containsKey(fileName)) {
            addToStage.remove(fileName);
            ArrayList<String> filesinCWD = Main.getCWDFilesNames("");
            if (!filesinCWD.contains(fileName)) {
                removedStage.put(fileName, addToStage.get(fileName));
            } else {
                untracked.put(fileName, addToStage.get(fileName));
            }
        } else {
            System.out.print("No reason to remove the file.");
        }
    }

    /**
     * Prints out the status of the staged files, removed files,
     * and modifications not staged for
     * commit, and untracked files.
     */
    public void status() {
        if (Main.getTree().getLastCommit().
                getCommitmessage().contains("Merged")) {
            this.empty();
        } else {
            updateVars();
        }
        System.out.println("=== Staged Files ===");
        Map<String, String> map = new TreeMap<>(addToStage);
        for (String b : map.keySet()) {
            System.out.println(b);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        Map<String, String> map2 = new TreeMap<>(removedStage);
        for (String b : map2.keySet()) {
            System.out.println(b);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        Map<String, String> map3 = new TreeMap<>(trackModified);
        for (String b : map3.keySet()) {
            System.out.println(b + " (modified)");
        }
        Map<String, String> map4 = new TreeMap<>(trackDelete);
        for (String b : map4.keySet()) {
            System.out.println(b + " (deleted)");
        }
        System.out.println();
        System.out.println("=== Untracked Files ===");
        Map<String, String> map5 = new TreeMap<>(untracked);
        for (String b : map5.keySet()) {
            System.out.println(b);
        }
    }

    /**
     * Check if the cur directory has been changed and updates variables.
     */
    public void updateVars() {
        Boolean ec = false;
        if (_currBlobs != null) {
            ArrayList<String> files = Main.getCWDFilesNames("");
            String content;
            for (String fileName : files) {
                content = Main.readCWDFileToString("", fileName);
                if (_currBlobs.containsKey(fileName)
                        && hasChanges(fileName, content, true)) {
                    trackModified.put(fileName, null);
                } else if ((!_currBlobs.containsKey(fileName)
                        && addToStage.containsKey(fileName)
                        && hasChanges(fileName, content, false))
                        || (!_currBlobs.containsKey(fileName)
                        && !addToStage.containsKey(fileName))) {
                    untracked.put(fileName, null);
                }
            }
            if (files.isEmpty() & !_currBlobs.isEmpty()
                    && !trackModified.isEmpty()) {
                ArrayList<Object> b = Main.readFs(".blobs");
                for (Object bb : b) {
                    Blob a = (Blob) bb;
                    if (a != null && trackModified.containsKey(a.getFilename())
                            && !files.contains(a.getBlobname())) {
                        trackDelete.put(a.getFilename(),
                                trackModified.remove(a.getFilename()));
                        ec = true;
                    }

                }

            }
            for (String key : _currBlobs.keySet()) {
                if (!files.contains(key)) {
                    if (!ec) {
                        removedStage.put(key, _currBlobs.get(key));
                    }
                }
            }

            for (String f : untracked.keySet()) {
                if (!files.contains(f)) {
                    untracked.remove(f);
                }
            }
            untracked.remove(".gitignore");
            untracked.remove("Makefile");
            untracked.remove("proj3.iml");
        }
    }

    /**
     * Get the untracked.
     * @return A hashmap.
     */
    public HashMap<String, String> getUntracked() {
        return untracked;
    }

    /**
     * Updates the stage after merge.
     * @param nB New blobs to update.
     * @param rem Removed blobs to update.
     * @param conf Connflict files found during merge.
     */
    public void afterMerge(HashMap<String, String> nB,
                                 HashMap<String, String> rem,
                                 HashMap<String, String> conf) {
        for (String key : conf.keySet()) {
            trackModified.put(key, null);
        }
        for (String key : rem.keySet()) {
            removedStage.put(key, rem.get(key));
        }

        for (String key : nB.keySet()) {
            addToStage(key, Main.getTree().getLastCommit());
        }


    }

    /**
     * Acess getcurrblobs.
     * @return A hashmap.
     */
    public HashMap<String, String> getCurrblobs() {
        return _currBlobs;
    }
    /**
     * Acess get track delete.
     * @return A hashmap.
     */
    public HashMap<String, String> getTrackdelete() {
        return trackDelete;
    }
    /**
     * Acess get track modified.
     * @return A hashmap.
     */
    public HashMap<String, String> getTrackmodified() {
        return trackModified;
    }
}
