package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Yu Xi Gui
 */
public class Main {
    /**The cur working directory.*/
    static final String CWD = System.getProperty("user.dir");
    /**The commitTree object serialized.*/
    private static CommitTree _tree = null;
    /**The stage object serialized.*/
    private static Stage _stage = null;

    /**
     * Stage variable accessor method.
     * @return A stage instance.
     */
    public static Stage getStage() {
        return _stage;
    }

    /**
     * Committree accessor method.
     * @return A commit tree instance.
     */
    public static CommitTree getTree() {
        return _tree;
    }
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        try {
            if (Files.exists(Paths.get(CWD, ".gitlet").resolve("tree.bin"))) {
                _tree = (CommitTree) readF("", "tree.bin");
                _stage = (Stage) readF("", "stage.bin");
            }

            if (args.length < 1) {
                throw new IllegalArgumentException("Please enter a command.");
            }
            List<String> list = Arrays.asList(args);
            if (list.contains("add-remote") || list.contains("rm-remote")
                    || list.contains("pull") || list.contains("push")
                    || list.contains("fetch")) {
                commandThree(args);
            } else if (list.contains("init") || list.contains("add")
                    || list.contains("commit") || list.contains("log")
                    || list.contains("global-log") || list.contains("find")
                    || list.contains("status") || list.contains("rm")) {
                commandsOne(args);
            } else {
                commandsTwo(args);
            }
            writeFile("", "tree.bin", _tree);
            writeFile("", "stage.bin", _stage);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
        } catch (NullPointerException f) {
            if (f.getMessage() != null) {
                System.out.println(f.getMessage());
            }
        }
    }

    /**
     * Takes in commands from the main.
     * @param args Specific commands matching user input.
     *             Errors if doesn't match.
     */
    public static void commandsOne(String[] args) {
        switch (args[0]) {
        case "init":
            initialize();
            break;
        case "add":
            if (args.length == 2) {
                add(args[1]);

            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
            break;
        case "commit":
            if (args.length < 2 || args[1].equals("")) {
                System.out.print("Please enter a commit message.");
            } else {
                commit(args[1]);
            }
            break;
        case "log":
            if (args.length == 1) {
                _tree.logT();
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "global-log":
            if (args.length == 1) {
                _tree.globLog();
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "find":
            if (args.length == 2) {
                _tree.findCommits(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "status":
            if (args.length == 1) {
                status();
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "rm":
            if (args.length == 2) {
                rm(args[1]);
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
            break;
        default:
            throw new IllegalArgumentException("No command "
                    + "with that name exists.");
        }
    }

    /**
     * Process the latter half of the commands.
     * @param args User input.
     */
    public static void commandsTwo(String[] args) {
        switch (args[0]) {
        case "checkout":
            if (args[1].equals("--") && args.length == 3) {
                caseOne(args[2]);
                break;
            } else if (args.length == 4 && args[2].equals("--")) {
                caseTwo(args[1], args[3]);
                break;
            } else if (args.length == 2) {
                caseThree(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "branch":
            if (args.length == 2) {
                branch(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "rm-branch":
            if (args.length == 2) {
                _tree.deleteBranch(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "reset":
            if (args.length == 2) {
                reset(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "merge":
            if (args.length == 2) {
                merge(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        default:
            throw new IllegalArgumentException("No command "
                    + "with that name exists.");

        }
    }

    /**
     * Do the remote methods.
     * @param args User input.
     */
    public static void commandThree(String[]args) {
        switch (args[0]) {
        case "add-remote":
            if (args.length == 3) {
                addRemote(args[1], args[2]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "rm-remote":
            if (args.length == 2) {
                rmRemote(args[1]);
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "push":
            if (args.length == 3) {
                try {
                    push(args[1], args[2]);
                } catch (IOException e) {
                    throw new GitletException();
                }
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "fetch":
            if (args.length == 3) {
                try {
                    fetch(args[1], args[2]);
                } catch (IOException e) {
                    throw new GitletException();
                }
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        case "pull":
            if (args.length == 3) {
                try {
                    pull(args[1], args[2]);
                } catch (IOException e) {
                    throw new GitletException();
                }
                break;
            } else {
                throw new IllegalArgumentException("Incorrect operands.");
            }
        default:
            throw new IllegalArgumentException("No command "
                    + "with that name exists.");

        }
    }

    /**
     * Adds remote.
     * @param dirName The directory name of the repo.
     * @param path The path to the repo.
     */
    public static void addRemote(String dirName, String path) {
        if (_tree.getRemote().containsKey(dirName)) {
            System.out.println("A remote with that name already exists.");
            return;
        }
        _tree.getRemote().put(dirName, path.replace('/', File.separatorChar));

    }

    /**
     * Removes the remove by given name.
     * @param remoteName The remote to remove.
     */
    public static void rmRemote(String remoteName) {
        if (!_tree.getRemote().containsKey(remoteName)) {
            System.out.println("A remote with that name does not exist.");
            return;
        }
        _tree.getRemote().remove(remoteName);
    }

    /**
     * Does the push command.
     * @param remName The remote repo name.
     * @param remBrNa The remote branch name.
     * @throws IOException
     */
    public static void push(String remName, String remBrNa) throws IOException {
        if (!_tree.getRemote().containsKey(remName)) {
            System.out.println("Remote directory not found.");
            return;
        }
        CommitTree remote;
        if (Utils.join(_tree.getRemote().get(remName)).exists()) {
            remote = readTree(_tree.getRemote().get(remName));
        } else {
            System.out.println("Remote directory not found.");
            return;
        }
        if (remote.getBr().containsKey(remBrNa)) {
            Commit remotehead = readRemoteobj(
                    Utils.join(_tree.getRemote().get(remName), ".commits",
                            remote.getBr().get(remBrNa)));
            Commit cur = _tree.getLastCommit();
            Commit com = cur;
            while (com != null
                    && !com.getId().equals(remotehead.getId())) {
                if (com.getParentOne().equals("")) {
                    com = null;
                } else {
                    com = (Commit) readF(".commits", com.getParentOne());
                }
            }
            if (com == null) {
                System.out.println(
                        "Please pull down remote changes before pushing.");
                return;
            }
            append(_tree.getRemote().get(remName), Utils.join(CWD,
                    ".gitlet").toString(), com, cur);
            remote.getBr().put(remBrNa, cur.getId());
        } else {
            remote.branch(remBrNa);
            Commit cur = _tree.getLastCommit();
            Commit com = cur;
            Commit remotehead = readRemoteobj(
                    Utils.join(_tree.getRemote().get(remName), ".commits",
                            remote.getBr().get(remBrNa)));
            while (com != null
                    && !com.getId().equals(remotehead.getId())) {
                com = (Commit) readF("/.commits", com.getParentOne());
            }
            if (com == null) {
                System.out.println(
                        "Please pull down remote changes before pushing.");
                remote.deleteBranch(remBrNa);
                return;
            }
            append(_tree.getRemote().get(remName), Utils.join(CWD,
                    ".gitlet").toString(), com, cur);

            remote.getBr().put(remBrNa, cur.getId());
            System.out.println(remote.getBr());
        }
        remote.save(_tree.getRemote().get(remName));
    }

    /**
     * Does the pull command.
     * @param remoteName The remote repo name.
     * @param remoteBrName The remote branch name.
     * @throws IOException
     */
    public static void pull(String remoteName,
                             String remoteBrName) throws IOException {
        fetch(remoteName, remoteBrName);
        merge(remoteName + "/" + remoteBrName);
    }
    /**
     * Append the info to remote repo.
     * @param to1 The place to copy it to.
     * @param from1 The place copies from.
     * @param from The from commit.
     * @param to The to commit.
     * @throws IOException
     */
    public static void append(String to1, String from1, Commit from, Commit to)
            throws IOException {
        while (!from.getId().equals(to.getId())) {
            Commit.dupCom(to1, from1, to.getId());
            for (String f : to.getBlobs().keySet()) {
                Blob.copyFile(to1, from1, to.getBlobs().get(f));
            }
            to = readRemoteobj(Utils.join(from1, ".commits",
                    to.getParentOne()));
        }
    }

    /**
     * Reads remote commit with the given file.
     * @param f The given rile to read.
     * @return A commit.
     */
    public static Commit readRemoteobj(File f) {
        if (f.exists()) {
            return Utils.readObject(f, Commit.class);
        }
        return null;
    }

    /**
     * Reads the tree in given directory.
     * @param dir The directory to read.
     * @return A commitTree object.
     */
    public static CommitTree readTree(String dir) {
        File t = new File(Utils.join(dir, "tree.bin").toString());
        if (t.exists()) {
            return Utils.readObject(t, CommitTree.class);
        }
        return null;
    }

    /**
     * Does the fetch command.
     * @param remoteName The remote repo name.
     * @param remoteBrName The remote branch name.
     * @throws IOException
     */
    public static void fetch(String remoteName,
                              String remoteBrName) throws IOException {
        if (!_tree.getRemote().containsKey(remoteName)) {
            System.out.println("Remote directory not found.");
            return;
        }
        CommitTree remote;
        if (Utils.join(_tree.getRemote().get(remoteName)).exists()) {
            remote = readTree(_tree.getRemote().get(remoteName));
        } else {
            System.out.println("Remote directory not found.");
            return;
        }

        if (!remote.getBr().containsKey(remoteBrName)) {
            System.out.println("That remote does not have that branch.");
            return;
        } else {
            String x = remoteName + "/" + remoteBrName;
            if (!_tree.getBr().containsKey(x)) {
                branch(x);
            }
            Commit remotehead = readRemoteobj(
                    Utils.join(_tree.getRemote().get(remoteName), ".commits",
                            remote.getBr().get(remoteBrName)));
            Commit cur = (Commit) readF(".commits",
                    _tree.getBr().get(x));
            Commit com = remotehead;
            while (!com.getParentOne().equals("")
                    && !com.getId().equals(
                    cur.getId())) {
                com = readRemoteobj(
                        Utils.join(_tree.getRemote().get(remoteName),
                        ".commits",
                        com.getParentOne()));
            }
            append(Utils.join(CWD,
                    ".gitlet").toString(),
                    _tree.getRemote().get(remoteName),
                    com, remotehead);
            _tree.getBr().put(x, remotehead.getId());
        }
    }

    /**This method quits with exit code of 0.*/
    private static void quit() {
        System.exit(0);
    }


    /**This method does the initialize command.*/
    public static void initialize() {
        Path mainPath = Paths.get(CWD, ".gitlet");
        if (Files.exists(mainPath)) {
            System.out.print("A Gitlet version-control system "
                    + "already exists in the current directory.");
            quit();
        } else {
            new File(mainPath.toString()).mkdirs();
            new File(Paths.get(CWD, ".gitlet", ".commits").toString()).mkdirs();
            new File(Paths.get(CWD, ".gitlet", ".blobs").toString()).mkdirs();
            new File(Paths.get(CWD, ".gitlet",
                    ".tempblobs").toString()).mkdirs();
            _tree = new CommitTree();
            _stage = new Stage();
            _stage.setBlobs(_tree.getLastCommit());
            writeFile("", "tree.bin", _tree);
            writeFile("", "stage.bin", _stage);
        }
    }

    /**
     * This method reads the file named as the given parent.
     * @param folder The directory in gitlet.
     * @param parent The file in the folder.
     * @return The object read out in that file parent.
     */
    public static Object readF(String folder, String parent) {
        Object obj = null;
        if (folder != null && parent != null) {
            Path path = Paths.get(CWD, ".gitlet", folder);
            path = path.resolve(parent);
            try {
                ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(path.toString()));
                while (true) {
                    obj = ois.readObject();
                    ois.close();
                    break;
                }
            } catch (FileNotFoundException e) {
                System.out.println("File does not exist.");
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException: " + e.getMessage());
            }
        }
        return obj;
    }

    /**
     * The method to save the object c in a new location
     * named by hash in the given folder.
     * @param folder The given folder to write the object in.
     * @param fileName The name of the file to write in,
     * @param c The object to write.
     */
    public static void writeFile(String folder, String fileName, Object c) {
        if (folder != null && fileName != null) {
            Path path = Paths.get(CWD, ".gitlet", folder);
            if (!Files.exists(path)) {
                new File(path.toString()).mkdirs();
            }
            path = path.resolve(fileName);
            try {
                ObjectOutputStream o = new ObjectOutputStream(
                        new FileOutputStream(path.toString()));
                o.writeObject(c);
                o.close();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        } else if (folder == null) {
            System.out.println("Null directory.");
        } else {
            System.out.println("Null filename.");
        }
    }


    /**
     * Adds a file with the given fileName to cur directory.
     * @param fileName String of the filename.
     */
    public static void add(String fileName) {
        if (!fileName.equals("")) {
            Path path = Paths.get(CWD, fileName);
            if (!Files.exists(path)) {
                System.out.print("File does not exist.");
                quit();
            } else {
                _stage.addToStage(fileName, _tree.getLastCommit());
            }

        }
    }

    /**
     * Create and write a new file in the cur directory.
     * @param dir The directory to write the file in.
     * @param fileName The file name of the file.
     * @param content The content of the file.
     */
    public static void writeToCWDFile(String dir,
                                      String fileName, String content) {
        if (dir != null && fileName != null) {
            Path path = Paths.get(CWD, dir);
            if (!Files.exists(path)) {
                new File(path.toString()).mkdirs();
            }
            path = path.resolve(fileName);
            File f = new File(path.toString());
            Utils.writeContents(f, content.getBytes());
        } else if (dir == null) {
            System.out.println("Null directory.");
        } else {
            System.out.println("Null filename.");
        }
    }

    /**
     * A method that reads a file in the cur directory and reads its content
     * and returns its content as a string.
     * @param dir The folder in cur directory.
     * @param fileName The filename in the dir.
     * @return A string of this file's content.
     */
    public static String readCWDFileToString(String dir, String fileName) {
        byte [] b = null;
        if (dir != null && fileName != null) {
            Path path = Paths.get(CWD, dir);
            path = path.resolve(fileName);
            try {
                b = Files.readAllBytes(path);
            } catch (IOException e) {
                System.out.println("File does not exist.");
            }
        }
        return new String(b);
    }

    /**
     * The commit method takes in a commit message.
     * @param commitMessage String of what user types after commit.
     */
    public static void commit(String commitMessage) {
        if (_stage.getAddToStage().isEmpty()
                && _stage.getRemovedStage().isEmpty()
                && _stage.getTrackmodified().isEmpty()) {
            System.out.print("No changes added to the commit.");
        } else {
            _tree.add(commitMessage,
                    _stage.getAddToStage(), _stage.getRemovedStage(), "");
            _stage.setBlobs(_tree.getLastCommit());
            _stage.empty();
        }
    }

    /**
     * A method that reads all files in the given directory.
     * @param dir A string representing the given directory.
     * @return An arraylist of objects in the files of this directory.
     */
    public static ArrayList<Object> readFs(String dir) {
        if (dir != null) {
            Path path = Paths.get(CWD, ".gitlet", dir);
            File f = new File(path.toString());
            File[] fi = f.listFiles();
            ArrayList<String> actualFiles = new ArrayList<>();
            for (File a : fi) {
                if (a.isFile()) {
                    actualFiles.add(a.getName());
                }
            }
            ArrayList<Object> ans = new ArrayList<>();
            for (String names : actualFiles) {
                ans.add(readF(dir, names));
            }
            return ans;
        } else {
            System.out.println("Null directory.");
            return null;
        }
    }

    /**
     * This method deletes the file with the given id in the given directory.
     * @param dir The given directory.
     * @param id The file to delete.
     */
    public static void deleteFile(String dir, String id) {
        if (dir != null && id != null) {
            Path path = Paths.get(CWD, ".gitlet", dir);
            path = path.resolve(id);
            try {
                Files.deleteIfExists(path);
            } catch (NoSuchFileException e) {
                System.out.println("NoSuchFileException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        } else if (dir == null) {
            System.out.println("Null directory.");
        } else {
            System.out.println("Null filename.");
        }
    }

    /**
     * Does the rm function of gitlet.
     * @param fileName The given file to remove from stage.
     */
    public static void rm(String fileName) {
        if (!fileName.equals("")) {
            _stage.removeFile(fileName, _tree.getLastCommit());
        } else {
            System.out.println("Null filename.");
        }
    }

    /**
     * Deletes the given file in the cur directory.
     * @param dir Directory.
     * @param fileName The file with the filename to be deleted.
     */
    public static void delCDFile(String dir, String fileName) {
        if (dir != null && fileName != null) {
            Path path = Paths.get(CWD, dir);
            path = path.resolve(fileName);
            try {
                Files.deleteIfExists(path);
            } catch (NoSuchFileException e) {
                System.out.println("NoSuchFileException: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        } else if (dir == null) {
            System.out.println("Null directory.");
        } else {
            System.out.println("Null filename.");
        }
    }

    /**
     * A method to get all the files' names in the cur directory.
     * @param dir The directory to look in.
     * @return An arraylist of file names in string forms.
     */
    public static ArrayList<String> getCWDFilesNames(String dir) {
        if (dir != null) {
            Path path = Paths.get(CWD, dir);
            File f = new File(path.toString());
            File[] ff = f.listFiles();
            ArrayList<String> names = new ArrayList<>();
            for (File a : ff) {
                if (a.isFile() && !a.isDirectory()) {
                    names.add(a.getName());
                }
            }
            return names;
        }
        return null;
    }

    /**
     * Displays the status of this gitlet by printing out.
     */
    public static void status() {
        File f = new File(CWD + "/.gitlet");
        if (f.exists() && f.isDirectory()) {
            _tree.status();
            _stage.status();
        } else {
            System.out.println("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * This method updates the file in CWD with the given name.
     * With the version of the file in the last commit.
     * @param fileName A string of file name.
     */
    public static void caseOne(String fileName) {
        Commit prev = _tree.getLastCommit();
        if (prev.getBlobs().containsKey(fileName)) {
            Blob add = (Blob) readF(".blobs", prev.getBlobs().get(fileName));
            Main.writeToCWDFile("", fileName, add.getContent());

        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    /**
     * Finds the file in the given commit with the commitID and update it.
     * @param commitid The commit id or hashvalue.
     * @param filename The file in the commit to update.
     */
    public static void caseTwo(String commitid, String filename) {
        commitid = getCommitIfExists(commitid);
        if (commitid.equals("")) {
            System.out.print("No commit with that id exists.");
            return;
        }
        Commit c  = (Commit) readF(".commits", commitid);
        if (c.getBlobs().containsKey(filename)) {
            Blob add = (Blob) Main.readF(".blobs",
                    c.getBlobs().get(filename));
            Main.writeToCWDFile("", filename, add.getContent());
        }  else {
            System.out.println("File does not exist in that commit.");
        }
    }

    /**
     * This method gets the commit with the given
     * commit message. Returns an empty string if
     * not found.
     * @param c The given commit message.
     * @return A string of the commit id.
     */
    public static String getCommitIfExists(String c) {

        Path path = Paths.get(CWD, ".commits");
        ArrayList f = readFs(".commits");

        for (Object file : f) {
            Commit c2 = (Commit) file;
            String c2Id = c2.getId();
            if (c2Id.substring(0, c.length()).equals(c)) {
                return ((Commit) file).getId();
            }
        }
        return "";
    }


    /**
     * Updates the files in the given branch.
     * @param gN The gN of the branch.
     */
    public static void caseThree(String gN) {
        if (!_tree.getBr().containsKey(gN)) {
            System.out.println("No such branch exists.");
            return;
        } else if (_tree.getHead().equals(gN)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        Commit c = (Commit) Main.readF(".commits",
                _tree.getBr().get(gN));
        HashMap<String, String> b = c.getBlobs();

        ArrayList<String> filesInCurrBlobs = new ArrayList<>(b.keySet());
        _stage.updateVars();

        for (String s :filesInCurrBlobs) {
            if (_stage.getUntracked() != null
                    && _stage.getUntracked().containsKey(s)) {
                System.out.println("There is an untracked file "
                        + "in the way; delete it, or add and commit it first.");
                return;
            }
        }

        for (String key : _tree.getLastCommit().getBlobs().keySet()) {
            Main.delCDFile("", key);
        }

        for (String key : filesInCurrBlobs) {
            String blobContent = ((Blob) Main.readF(".blobs",
                    b.get(key))).getContent();
            Main.writeToCWDFile("", key, blobContent);
        }
        _tree.setHead(gN);
        _stage.setBlobs(c);
        _stage.empty();
    }

    /**
     * The method that does the branch command.
     * @param gN The gN to create.
     */
    public static void branch(String gN) {
        if (_tree.getBr().containsKey(gN)) {
            System.out.println("A branch with that name already exists.");
        } else {
            _tree.branch(gN);
        }
    }

    /**
     * This method deletes the branch with the given.
     * @param gN The given branch name.
     */
    public static void deleteBranch(String gN) {
        if (!_tree.getBr().containsKey(gN)) {
            System.out.println("A branch with that name already exists.");
        } else if (_tree.getHead().equals(gN)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            _tree.deleteBranch(gN);
        }
    }

    /**
     * This method does the reset command.
     * @param commitId This checks out all the files
     *                 tracked by the given commit.
     */
    public static void reset(String commitId) {
        if (getCommitIfExists(commitId).equals("")) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit thisCom = (Commit) readF(".commits", commitId);

        _tree.reset(commitId, thisCom);
        _stage.empty();
    }

    /**
     * Checks the condition for merge.
     * @param gN The gN.
     * @return Error message.
     */
    public static String checkCond(String gN) {
        if (!_stage.getAddToStage().isEmpty()
                || !_stage.getRemovedStage().isEmpty()) {
            return "You have uncommitted changes.";

        }
        if (!_tree.getBr().containsKey(gN)) {
            return " A branch with that name does not exist.";

        }
        if (_tree.getHead().equals(gN)) {
            return "Cannot merge a branch with itself.";
        }
        return "";
    }
    /**
     * Does the merge command.
     * @param gN The given gN to merge.
     */
    public static void merge(String gN) {
        if (!checkCond(gN).equals("")) {
            System.out.println(checkCond(gN));
            return;
        }
        Commit hGiven = (Commit) Main.readF(".commits", _tree.getBr().get(gN));
        Commit splitPt = getSplitPt(gN, hGiven);
        if (splitPt == null) {
            return;
        }
        HashMap<String, String> gB = hGiven.getBlobs();
        HashMap<String, String> spBlobs = splitPt.getBlobs();
        HashMap<String, String> curB = _tree.getLastCommit().getBlobs();
        HashMap<String, String> r = new HashMap<>();
        HashMap<String, String> newBlobs = new HashMap<>();
        HashMap<String, String> confc = new HashMap<>();
        Main.writeFile("", ".temp_stage", _stage);
        _stage.updateVars();
        updateCwdAndStage(gB, spBlobs, curB, newBlobs, confc, hGiven);
        if (!_stage.getUntracked().isEmpty()) {
            for (String utFile : _stage.getUntracked().keySet()) {
                if (newBlobs.containsKey(utFile) || r.containsKey(utFile)) {
                    System.out.println(a());
                    _stage = (Stage) Main.readF("", ".temp_stage");
                    Main.deleteFile(CWD, ".temp_stage");
                    return;
                }
            }
        }
        for (String key : curB.keySet()) {
            if (spBlobs.containsKey(key) && !gB.containsKey(key)) {
                if (curB.get(key).equals(spBlobs.get(key))) {
                    Main.delCDFile("", key);
                }
            }
        }
        for (String key : gB.keySet()) {
            if (spBlobs.containsKey(key) && !curB.containsKey(key)) {
                if (gB.get(key).equals(spBlobs.get(key))) {
                    Main.delCDFile("", key);
                }
            }
        }
        _stage.afterMerge(newBlobs, r, confc);
        if (confc.isEmpty()) {
            _tree.add("Merged " + gN + " into " + _tree.getHead()
                    + ".", newBlobs, r, hGiven.getId());
        } else {
            ArrayList<String> c = Main.getCWDFilesNames("");
            for (String name : c) {
                if (confc.containsKey(name)) {
                    Main.writeToCWDFile("", name, confc.get(name));
                    _tree.add("Merged " + gN + " into " + _tree.getHead()
                            + ".", newBlobs, r, hGiven.getId());
                }
            }
            c();
            _stage.afterMerge(newBlobs, r, confc);
        }
    }

    /**
     * To shorten length of merge method.
     */
    public static void c() {
        System.out.println("Encountered a merge conflict.");
        _stage.empty();
    }
    /**
     * The method finds the split point of the given head and cur head.
     * @param gN The given branch name.
     * @param hGiven The given branch's head commit.
     * @return The commit of the least com ancestor.
     */
    public static Commit getSplitPt(String gN, Commit hGiven) {
        Commit splitPoint = findSplitPoint(gN);
        if (splitPoint != null && splitPoint.getId().equals(hGiven.getId())) {
            System.out.println(b());
            return null;
        }
        if (splitPoint != null && splitPoint.getId().equals
                (_tree.getLastCommit().getId())) {
            System.out.println("current branch fast-forwarded.");
            caseThree(gN);
            return null;
        }
        if (splitPoint == null) {
            Commit c = _tree.getLastCommit();
            while (!c.getParentOne().equals("")) {
                c = (Commit) Main.readF(".commits", c.getParentOne());
            }
            splitPoint = c;
        }
        return splitPoint;
    }

    /**
     * To shorten method length.
     * @return A string
     */
    public static String a() {
        return "There is an untracked file in the way; "
                + "delete it, or add and commit it first.";
    }
    /**
     * To shorten method length.
     * @return A string
     */
    public static String b() {
        return "Given branch is an "
                + "ancestor of the current branch.";
    }

    /**
     * Finds the split point.
     * @param givenBrName The given branch name.
     * @return The split point commit. Null if non.
     */
    public static Commit findSplitPoint(String givenBrName) {
        Commit splitPoint;
        Commit givenHead = (Commit) Main.readF(".commits",
                _tree.getBr().get(givenBrName));
        Commit currHead = _tree.getLastCommit();
        HashSet<String> givenAncestors = findAllAncestors(givenHead);
        while (currHead != null) {
            if (currHead.getCommitmessage().contains("Merged")) {
                HashMap<Integer, Commit> compares = new HashMap<>();
                Commit parent1 = (Commit) Main.readF(".commits",
                        currHead.getParentOne());
                Commit parent2 = (Commit) Main.readF(".commits",
                        currHead.getParent2());
                int count = 0;
                while (parent1 != null) {
                    if (givenAncestors.contains(parent1.getId())) {
                        compares.put(count, parent1);
                        break;
                    }
                    parent1 = (Commit) Main.readF(".commits",
                            parent1.getParentOne());
                    count++;
                }
                int counter2 = 0;
                while (parent2 != null) {
                    if (givenAncestors.contains(parent2.getId())) {
                        compares.put(counter2, parent2);
                        break;
                    }
                    parent2 = (Commit) Main.readF(".commits",
                            parent2.getParentOne());
                    counter2++;
                }
                int closest = Collections.min(compares.keySet());
                splitPoint = compares.get(closest);
                return splitPoint;
            } else if (givenAncestors.contains(currHead.getId())) {
                splitPoint = currHead;
                return splitPoint;
            }
            currHead = (Commit) Main.readF(".commits",
                    currHead.getParentOne());
        }
        return null;
    }
    /**
     * Finds all the ancestors of this commit.
     * @param head The commit head.
     * @return A hashset containg all ancestors of this head
     * including parent one and parent twos.
     */
    public static HashSet<String> findAllAncestors(Commit head) {
        HashSet<String> result = new HashSet<>();
        while (head != null) {
            if (head.getCommitmessage().contains("Merge")) {
                Commit p2 = (Commit) Main.readF(".commits", head.getParent2());
                result.addAll(findAllAncestors(p2));
            }
            result.add(head.getId());
            if (!head.getParentOne().equals("")) {
                head = (Commit) Main.readF(".commits", head.getParentOne());
            } else {
                break;
            }
        }
        return result;
    }


    /**
     * Updates the cur directory and stage.
     * @param gB The given blobs.
     * @param spBlobs The split point blobs.
     * @param curB The cur blobs.
     * @param newBlobs The new blobs.
     * @param confc The conflict files.
     * @param givenCommit The given commit.
     */
    public static void updateCwdAndStage(HashMap<String, String> gB,
                                         HashMap<String, String> spBlobs,
                                         HashMap<String, String> curB,
                                         HashMap<String, String> newBlobs,
                                         HashMap<String, String> confc,
                                         Commit givenCommit) {
        for (String key : gB.keySet()) {
            if (curB.containsKey(key) && spBlobs.containsKey(key)) {
                if ((curB.get(key).equals(spBlobs.get(key)))
                        && !(gB.get(key).equals(spBlobs.get(key)))) {
                    newBlobs.put(key, gB.get(key));
                } else if (!curB.get(key).equals(spBlobs.get(key))
                        && (gB.get(key).equals(spBlobs.get(key)))) {
                    newBlobs.put(key, spBlobs.get(key));
                }
            }
            if ((!curB.containsKey(key))
                    && !(spBlobs.containsKey(key))) {
                caseTwo(givenCommit.getId(), key);
                newBlobs.put(key, gB.get(key));
            }
            if (curB.containsKey(key) && spBlobs.containsKey(key)) {
                if (!gB.get(key).equals(spBlobs.get(key))
                        && !curB.get(key).equals(spBlobs.get(key))
                        && !curB.get(key).equals(gB.get(key))) {
                    writeConflictFile(curB, gB, confc, key);
                }
            }
            if (curB.containsKey(key) && !spBlobs.containsKey(key)) {
                if (!gB.get(key).equals(curB.get(key))) {
                    writeConflictFile(curB, gB, confc, key);
                }
            }
            if (!curB.containsKey(key)
                    && spBlobs.containsKey(key)
                    && gB.containsKey(key)) {
                if (!gB.get(key).equals(spBlobs.get(key))) {
                    writeConflictFile(curB, gB, confc, key);
                }
            }
        }
        for (String key : curB.keySet()) {
            if (spBlobs.containsKey(key) && !gB.containsKey(key)) {
                if (!spBlobs.get(key).equals(curB.get(key))) {
                    writeConflictFile(curB, gB, confc, key);
                }
            }
        }
    }

    /**
     * Stores the conflict files in the hashmap and reads the blobs.
     * @param curB The cur branch head commit blob.
     * @param gB The given branch head commit blob.
     * @param confc The files that are conflicting.
     * @param key The given file.
     */
    public static void writeConflictFile(HashMap<String, String> curB,
                                        HashMap<String, String> gB,
                                         HashMap<String, String> confc,
                                         String key) {
        String message = "<<<<<<< HEAD" + "\n";
        if (!curB.containsKey(key)) {
            message += "";
        } else {
            Blob curr = (Blob) Main.readF(".blobs", curB.get(key));

            message += curr.getContent();
        }
        message += "=======" + "\n";
        if (!gB.containsKey(key)) {
            message += "";
        } else {
            Blob given = (Blob) Main.readF(".blobs", gB.get(key));
            message += given.getContent();
        }
        message += ">>>>>>>" + "\n";
        confc.put(key, message);
    }

}
