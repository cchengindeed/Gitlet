package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * A tree of commits to track commits.
 * @author Yu Xi Gui
 */

public class CommitTree implements Serializable {
    /*** The curBranchKey.*/
    private String _head;
    /*** Maps branches' name with the corresponding commit hashvalue.*/
    private HashMap<String, String> _branches;
    /** Map for track remote directories. */
    private HashMap<String, String> _remote;

    /**
     * The constructor for CommitTree that comes with the initial commit.
     */
    public CommitTree() {
        _branches = new HashMap<>();
        Commit c = new Commit("", new HashMap<>(),
                "initial commit", new HashMap<>());
        Main.writeFile(".commits", c.getId(), c);
        _branches.put("master", c.getId());
        _head = "master";
        _remote = new HashMap<>();

    }

    /**
     * To get the remote hashmap.
     * @return The hashmap of _remote.
     */
    public HashMap<String, String> getRemote() {
        return _remote;
    }

    /**
     * This method returns the latest commit or the one the head points to.
     *
     * @return A commit object.
     */
    public Commit getLastCommit() {
        String currCommHash = _branches.get(_head);
        return (Commit) Main.readF(".commits", currCommHash);
    }

    /**
     * Head accessor method.
     * @return The head instance variable.
     */
    public String getHead() {
        return _head;
    }

    /**
     * When I add a commit to the commitTree.
     * I need to delete the temp blobs and add those in the blobs folder.
     *
     * @param commitMess The commit message.
     * @param add Hashmap for files to add.
     * @param remove Hashmap for files to remove.
     * @param p2 Parent two id.
     */
    public void add(String commitMess, HashMap<String, String> add,
                    HashMap<String, String> remove, String p2) {
        Commit last = getLastCommit();
        Commit newComm = new Commit(last.getId(), add,
                commitMess, remove);
        if (!p2.equals("")) {
            newComm.setParent2(p2);
        }
        Main.writeFile(".commits", newComm.getId(), newComm);
        ArrayList<Object> files = Main.readFs(".temp_blobs");
        for (Object file : files) {
            Blob tempBlob = (Blob) file;
            Main.writeFile(".blobs", tempBlob.getBlobname(), tempBlob);
            Main.deleteFile(".temp_blobs", tempBlob.getBlobname());
        }
        Main.getStage().getAddToStage().clear();
        _branches.put(_head, newComm.getId());
    }


    /**
     * This method prints out the commits from head to initial commit.
     */
    public void logT() {
        Commit headCommit = (Commit) Main.readF(".commits",
                _branches.get(_head));
        while (headCommit != null) {
            System.out.println("===");
            System.out.println("commit " + headCommit.getId());
            if (!headCommit.getParent2().equals("")) {
                System.out.println("Merge: "
                        + headCommit.getParentOne().substring(0, 7)
                        + " " + headCommit.getParent2().substring(0, 7));
            }
            System.out.println("Date: " + headCommit.getTime());
            System.out.println(headCommit.getCommitmessage());
            String p = headCommit.getParentOne();
            if (p.equals("")) {
                return;
            } else {
                System.out.println();
            }
            headCommit = (Commit) Main.readF(".commits", p);

        }
    }

    /**
     * This method prints out all the commits ever created.
     */
    public void globLog() {
        ArrayList allCommits = Main.readFs(".commits");
        for (Object o : allCommits) {
            Commit c = (Commit) o;
            System.out.println("===");
            System.out.println("commit " + c.getId());
            if (c.getCommitmessage().contains("Merged")) {
                System.out.println("Merge: "
                        + c.getParentOne().substring(7)
                        + " " + c.getParent2().substring(7));
            }
            System.out.println("Date: " + c.getTime());
            System.out.println(c.getCommitmessage());
            System.out.println();
        }
    }

    /**
     * Prints out the corresponding commit's id with the given message.
     * @param message The commit message.
     */
    public void findCommits(String message) {
        ArrayList allCommits = Main.readFs(".commits");
        Boolean found = false;
        for (Object o : allCommits) {
            Commit c = (Commit) o;
            if (c.getCommitmessage().equals(message)) {
                System.out.println(c.getId());
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    /**
     * The method that gives status about the branches.
     */
    public void status() {
        System.out.println("=== Branches ===");
        Map<String, String> map = new TreeMap<>(_branches);
        for (String b : map.keySet()) {
            if (b.equals(_head)) {
                System.out.print("*");
            }
            System.out.println(b);
        }
        System.out.println("    ");

    }

    /**
     * Gets branches.
     * @return A hashmap of the branches
     */
    public HashMap<String, String> getBr() {
        return _branches;
    }

    /**
     * Set the head variable to the the name to.
     * @param to The name of the head to set to.
     */
    public void setHead(String to) {
        _head = to;
    }

    /**
     * This creates a new branch with the given name.
     * And it makes the new one point to the one the head
     * branch points to.
     * @param gN The string of the branch name.
     */
    public void branch(String gN) {
        if (gN != null) {
            _branches.put(gN, _branches.get(_head));
        }
    }

    /**
     * The method that does the rm-branch command.
     * @param gN The branch to remove.
     */
    public void deleteBranch(String gN) {
        if (gN != null) {
            if (!_branches.containsKey(gN)) {
                System.out.println("A branch with that name does not exist.");
            } else if (gN.equals(_head)) {
                System.out.println("Cannot remove the current branch.");
            } else {
                _branches.remove(gN);
            }
        }
    }

    /**
     * This saves the given tree.
     * @param dir The given repo.
     * @throws IOException
     */
    public void save(String dir) throws IOException {
        File t = new File(Utils.join(dir, "tree.bin").toString());
        if (!t.exists()) {
            t.createNewFile();
        }
        Utils.writeObject(t, this);
    }

    /**
     * Does the reset command.
     * @param commitId A String of commit id.
     * @param c The commit to reset.
     */
    public void reset(String commitId, Commit c) {
        if (!c.equals("")) {
            HashMap<String, String> cBlobs = c.getBlobs();
            Main.getStage().updateVars();
            if (!Main.getStage().getUntracked().isEmpty()) {
                for (String fname : Main.getStage().getUntracked().keySet()) {
                    if (cBlobs.containsKey(fname)) {
                        System.out.println("There is an untracked file "
                                + "in the way;"
                                + " delete it, or add and commit it first.");
                        return;
                    }
                }
            }
            Commit temp = getLastCommit();
            Boolean initial = false;
            while (temp != null && !temp.getId().equals(commitId)) {
                HashMap<String, String> b = temp.getBlobs();
                for (String fname : temp.getBlobs().keySet()) {
                    if (!cBlobs.containsValue(b.get(fname))) {
                        Main.delCDFile("", fname);
                    }
                }
                if (initial) {
                    break;
                }
                if (temp.getParentOne().equals("")) {
                    break;
                }
                temp = (Commit) Main.readF(".commits", temp.getParentOne());
                if (temp != null && temp.getParentOne().equals("")) {
                    initial = true;
                }

            }
            for (String file : cBlobs.keySet()) {
                Blob t = (Blob) Main.readF(".blobs", cBlobs.get(file));
                Main.writeToCWDFile("", file, t.getContent());
            }
            _branches.put(_head, c.getId());
            Main.getStage().empty();
            Main.getStage().setBlobs(c);
        }

    }

}
