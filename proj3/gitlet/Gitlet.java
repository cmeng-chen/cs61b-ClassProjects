package gitlet;

import java.io.Serializable;
import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.io.IOException;
import java.util.HashSet;

/** Represent the gitlet system.
 * @author Jim Bai, Meng Chen */
public class Gitlet implements Serializable {
    /** A new gitlet system in the current dir. */
    Gitlet() {
        CommitNode commit = new CommitNode(getTime(), "initial commit", null,
                null, null);
        untrackedFiles = new ArrayList<String>();
        commitTree = new CommitTree();
        commitTree.add(commit);
        commitTree.addBranch("master", commit);
        commitTree.switchBranch("master");
        stageArea = new File(".gitlet/staging_area/");
        File blob = new File(blobDir);
        stageArea.mkdir();
        blob.mkdir();
    }

    /** Adds a copy of the file as it currently exists to the staging area.
     * Serializa the file and save the file in the .gitlet directory with SHA1
     * id as its name.
     * @param filename name */
    public void add(String filename) {
        File infile = new File(filename);
        if (!infile.exists()) {
            System.err.println("File does not exist.");
            return;
        }
        if (untrackedFiles != null && untrackedFiles.contains(filename)) {
            untrackedFiles.remove(filename);
        }
        if (commitTree.headPointer().getFilepath() != null
                && equalsFile(filename,
                        commitTree.headPointer().getFilepath().get(filename))) {
            return;
        }
        File outfile = new File(".gitlet/staging_area/" + filename);
        copyFile(infile, outfile);
    }

    /** Saves a snapshot of certain files in the current commit and staging area
     * so they can be restored at a later time, creating a new commit.
     * @param message m */
    public void commit(String message) {
        String time = getTime();
        if (message.equals("")) {
            System.err.println("Please enter a commit message.");
            return;
        }
        HashMap<String, String> files = new HashMap<String, String>(
                commitTree.headPointer().getFiles());
        HashMap<String, String> filepath = new HashMap<String, String>(
                commitTree.headPointer().getFilepath());
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        if (staged.isEmpty() && untrackedFiles.isEmpty()) {
            System.err.println("No changes added to the commit.");
            return;
        }
        if (!staged.isEmpty()) {
            for (String file : staged) {
                String filePath = ".gitlet/staging_area/" + file;
                File f = new File(filePath);
                files.put(file, Utils.sha1(Utils.readContents(f)));
            }
        }
        if (!untrackedFiles.isEmpty()) {
            for (String file : untrackedFiles) {
                files.remove(file);
                filepath.remove(file);
            }
            untrackedFiles.clear();
        }
        CommitNode commit = new CommitNode(time, message, files,
                commitTree.headPointer(), filepath);
        if (!staged.isEmpty()) {
            for (String file : staged) {
                String filePath = ".gitlet/staging_area/" + file;
                String out = blobDir + commit.getSha() + "/" + file;
                filepath.put(file, out);
                File f = new File(filePath);
                saveBlob(commit, filePath, out);
                f.delete();
            }
        }
        commit.updateFilepath(filepath);
        commitTree.add(commit);
    }

    /** Untrack the file.
     * @param filename name */
    public void rm(String filename) {
        File untrack = new File(filename);
        File staged = new File(stagingArea + filename);
        int count = 0;
        if (staged.exists()) {
            staged.delete();
        } else {
            count++;
        }
        if (commitTree.headPointer().getFiles().containsKey(filename)) {
            untrack.delete();
            untrackedFiles.add(filename);
        } else {
            count++;
        }
        if (count == 2) {
            System.err.println("No reason to remove the file.");
            return;
        }
    }

    /** Starting at the current head commit, display information about each
     * commit backwards along the commit tree until the initial commit. */
    public void log() {
        CommitNode head = commitTree.headPointer();
        while (head != null) {
            head.printLog();
            head = head.getParent();
        }
    }

    /** Displays information about all commits ever made. */
    public void globallog() {
        for (CommitNode cn : commitTree.getCommitNode()) {
            cn.printLog();
        }
    }

    /** Prints out the ids of all commits that have the given commit message,
     * one per line.
     * @param message m */
    public void find(String message) {
        ArrayList<CommitNode> commits = commitTree.findBymessage(message);
        if (commits.isEmpty()) {
            System.err.println("Found no commit with that message.");
            return;
        }
        for (CommitNode c : commits) {
            System.out.println(c.getSha());
        }
    }

    /** Displays what branches currently exist, and marks the current branch
     * with a *. Also displays what files have been staged or marked for
     * untracking. */
    public void status() {
        System.out.println("=== Branches ===");
        HashMap<String, CommitNode> branches = commitTree.getBranch();
        String[] brInOrd = branches.keySet().toArray(new String[0]);
        Arrays.sort(brInOrd);
        for (String branch : brInOrd) {
            if (branch.equals(commitTree.getCurrentBranch())) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println("\n=== Staged Files ===");
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        String[] stInOrd = (String[]) staged.toArray();
        Arrays.sort(stInOrd);
        for (String stagedFile : stInOrd) {
            System.out.println(stagedFile);
        }
        System.out.println("\n=== Removed Files ===");
        String[] rmInOrd = untrackedFiles.toArray(new String[0]);
        Arrays.sort(rmInOrd);
        for (String removed : rmInOrd) {
            System.out.println(removed);
        }
        System.out.println("\n=== Modifications Not Staged For Commit ===");
        for (String files : modified()) {
            System.out.println(files);
        }
        System.out.println("\n=== Untracked Files ===");
        for (String files : notTracked()) {
            System.out.println(files);
        }
    }

    /** Takes the version of the file as it exists in the head commit, the front
     * of the current branch, and puts it in the working directory, overwriting
     * the version of the file that's already there if there is one. The new
     * version of the file is not staged.
     * @param filename name */
    public void checkoutF(String filename) {
        checkoutHelper(commitTree.headPointer(), filename);
    }

    /** Takes the version of the file as it exists in the commit with the given
     * id, and puts it in the working directory, overwriting the version of the
     * file that's already there if there is one. The new version of the file is
     * not staged.
     * @param commitid id
     * @param filename name */
    public void checkout(String commitid, String filename) {
        CommitNode cm;
        if (commitid.length() < commitTree.headPointer().getSha().length()) {
            cm = commitTree.findByShortid(commitid);
        } else {
            cm = commitTree.findByid(commitid);
        }
        if (cm == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        checkoutHelper(cm, filename);
    }

    /** The helper function of checkout.
     * @param cn cn
     * @param filename name */
    private void checkoutHelper(CommitNode cn, String filename) {
        CommitNode commit = cn;
        String id = commit.getSha();
        String filePath = commit.getFilepath().get(filename);
        if (filePath == null) {
            System.err.println("File does not exist in that commit.");
            return;
        }
        File infile = new File(filePath);
        if (!infile.exists()) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            File outfile = new File(filename);
            copyFile(infile, outfile);
        }
    }

    /** Takes all files in the commit at the head of the given branch, and puts
     * them in the working directory, overwriting the versions of the files that
     * are already there if they exist. Also, at the end of this command, the
     * given branch will now be considered the current branch (HEAD). Any files
     * that are tracked in the current branch but are not present in the
     * checked-out branch are deleted. The staging area is cleared, unless the
     * checked-out branch is the current branch.
     * @param branch branch */
    public void checkoutB(String branch) {
        CommitNode commit = commitTree.getBranch().get(branch);
        if (commit == null) {
            System.out.println("No such branch exists.");
            return;
        }
        if (deleteOrAddFirst(commit)) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return;
        }
        String currBranch = commitTree.getCurrentBranch();
        if (branch.equals(currBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        File currDir = new File(".");
        List<String> workingFiles = Utils.plainFilenamesIn(currDir);
        for (String file : workingFiles) {
            File f = new File(file);
            f.delete();
        }
        for (String f : commit.getFiles().keySet()) {
            checkoutHelper(commit, f);
        }
        commitTree.switchBranch(branch);
        clearstage();
    }

    /** Clear staging area. */
    public void clearstage() {
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        for (String file : staged) {
            File delete = new File(".gitlet/staging_area/" + file);
            delete.delete();
        }
    }

    /** Untracked files.
     * @return */
    public ArrayList<String> notTracked() {
        File currDir = new File(".");
        List<String> workingFiles = Utils.plainFilenamesIn(currDir);
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        ArrayList<String> nottracked = new ArrayList<>();
        String[] untrack;
        for (String file : workingFiles) {
            if (!staged.contains(file)) {
                CommitNode cm = commitTree.headPointer();
                if (!cm.getFiles().containsKey(file)) {
                    nottracked.add(file);
                }
            }
        }
        return nottracked;
    }

    /** Modified files.
     * @return */
    public String[] modified() {
        File currDir = new File(".");
        List<String> workingFiles = Utils.plainFilenamesIn(currDir);
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        Set<String> tracked = commitTree.headPointer().getFiles().keySet();
        HashMap<String, String> filepath = commitTree.headPointer()
                .getFilepath();
        ArrayList<String> modify = new ArrayList<>();
        String[] modi;
        for (String file : staged) {
            File workingDir = new File(file);
            if (workingDir.exists()) {
                if (!equalsFile(file, ".gitlet/staging_area/" + file)) {
                    modify.add(file + " (modified)");
                }
            } else {
                modify.add(file + " (deleted)");
            }
        }
        for (String file : tracked) {
            File workingDir = new File(file);
            if (workingDir.exists()) {
                if (!equalsFile(file, filepath.get(file))
                        && !staged.contains(file)) {
                    modify.add(file + " (modified)");
                }
            } else {
                if (!untrackedFiles.contains(file) && tracked.contains(file)) {
                    modify.add(file + " (deleted)");
                }
            }
        }
        modi = new String[modify.size()];
        modi = modify.toArray(modi);
        Arrays.sort(modi);
        return modi;
    }

    /** Delete or add.
     * @param c c
     * @return */
    public boolean deleteOrAddFirst(CommitNode c) {
        File currDir = new File(".");
        List<String> workingFiles = Utils.plainFilenamesIn(currDir);
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        for (String file : workingFiles) {
            if (!staged.contains(file)) {
                CommitNode cm = commitTree.headPointer();
                if (!cm.getFiles().containsKey(file)) {
                    if (c.getFiles().containsKey(file)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Return true if two files are equal.
     * @param file1Path path
     * @param file2Path path
     * @return */
    public boolean equalsFile(String file1Path, String file2Path) {
        if (file1Path == null || file2Path == null) {
            return false;
        }
        try {
            File f1 = new File(file1Path);
            File f2 = new File(file2Path);
            String sha1 = Utils.sha1(Files.readAllBytes(f1.toPath()));
            String sha2 = Utils.sha1(Files.readAllBytes(f2.toPath()));
            return sha1.equals(sha2);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Creates a new branch with the given name, and points it at the current
     * head node.
     * @param name name */
    public void branch(String name) {
        if (!commitTree.addBranch(name, commitTree.headPointer())) {
            System.err.println("A branch with that name already exists.");
        }
    }

    /** Deletes the branch with the given name. This only means to delete the
     * pointer associated with the branch.
     * @param name name */
    public void rmbranch(String name) {
        if (!commitTree.getBranch().containsKey(name)) {
            System.err.println("A branch with that name does not exist.");
            return;
        }
        if (commitTree.getCurrentBranch().equals(name)) {
            System.err.println("Cannot remove the current branch.");
            return;
        }
        commitTree.deleteBranch(name);
    }

    /** Checks out all the files tracked by the given commit. Removes tracked
     * files that are not present in the given file. Also moves the current
     * branch's head to that commit node. The staging area is cleared.
     * @param commitid id */
    public void reset(String commitid) {
        CommitNode cm;
        String id;
        Set<String> tracked = commitTree.headPointer().getFiles().keySet();
        if (commitid.length() < commitTree.headPointer().getSha().length()) {
            cm = commitTree.findByShortid(commitid);
        } else {
            cm = commitTree.findByid(commitid);
        }
        if (cm == null) {
            System.err.println("No commit with that id exists.");
            return;
        }
        id = cm.getSha();
        if (deleteOrAddFirst(cm)) {
            System.err.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return;
        }
        Set<String> files = commitTree.headPointer().getFiles().keySet();
        Set<String> nfiles = cm.getFiles().keySet();
        for (String f : tracked) {
            File file = new File(f);
            if (!nfiles.contains(f)) {
                rm(f);
                untrackedFiles.remove(f);
            }
        }
        for (String f : nfiles) {
            checkout(id, f);
        }
        commitTree.switchHead(cm);
        clearstage();
    }

    /** Decide whether to continue to merge.
     * @param name given
     * @return */
    public boolean mergeDe(String name) {
        if (deleteOrAddFirst(commitTree.getBranch().get(name))) {
            System.out.println("There is an untracked file in the way; "
                    + "delete it or add it first.");
            return false;
        }
        if (!commitTree.getBranch().containsKey(name)) {
            System.out.println("A branch with that name does not exist.");
            return false;
        }
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        if (!staged.isEmpty() || !untrackedFiles.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return false;
        }
        if (commitTree.getCurrentBranch().equals(name)) {
            System.out.println("Cannot merge a branch with itself.");
            return false;
        }
        return true;
    }

    /** Return false if merge is in failure cases.
     * @param curr curr
     * @param given given
     * @param splitPoint point
     * @param name name
     * @return */
    public boolean mergeSplit(CommitNode curr, CommitNode given,
            CommitNode splitPoint, String name) {
        if (splitPoint == null) {
            return false;
        }
        if (splitPoint.getSha().equals(curr.getSha())) {
            reset(given.getSha());
            System.err.println("Current branch fast-forwarded.");
            return false;
        }
        return true;
    }

    /** Merges files from the given branch into the current branch.
     * @param name name */
    public void merge(String name) {
        if (!mergeDe(name)) {
            return;
        }
        List<String> staged = Utils.plainFilenamesIn(stageArea);
        CommitNode curr = commitTree.headPointer();
        CommitNode given = commitTree.getBranch().get(name);
        CommitNode splitPoint = splitPoint(curr, given);
        if (!mergeSplit(curr, given, splitPoint, name)) {
            return;
        }
        HashMap<String, String> splitPointFiles = splitPoint.getFiles();
        HashMap<String, String> currFiles = curr.getFiles();
        HashMap<String, String> givenFiles = given.getFiles();
        boolean mergeConflict = false;
        for (String f1 : currFiles.keySet()) {
            if (splitPointFiles.containsKey(f1)) {
                if (givenFiles.containsKey(f1)) {
                    if (fileModified(splitPoint, curr, f1)) {
                        if (fileModified(given, splitPoint, f1)) {
                            mergeConflict = true;
                            mergeFiles(given.getFilepath().get(f1), f1);
                        }
                    } else {
                        if (fileModified(splitPoint, given, f1)) {
                            checkout(commitTree.getBranch().get(name).getSha(),
                                    f1);
                            add(f1);
                        }
                    }
                } else {
                    if (!fileModified(curr, splitPoint, f1)) {
                        rm(f1);
                    } else {
                        mergeConflict = true;
                        try {
                            File temp = File.createTempFile("empty", null);
                            mergeFiles(temp.getPath(), f1);
                        } catch (IOException excp) {
                            throw new IllegalArgumentException(
                                    excp.getMessage());
                        }
                    }
                }
            } else {
                if (givenFiles.containsKey(f1)) {
                    if (fileModified(given, curr, f1)) {
                        mergeConflict = true;
                        mergeFiles(given.getFilepath().get(f1), f1);
                    }
                }
            }
        }
        mergeGiven(givenFiles, currFiles, given, splitPoint, splitPointFiles,
                name);
        mergeconflits(mergeConflict, name);
    }

    /** Part 2 of merging.
     * @param givenFiles files
     * @param currFiles files
     * @param given node
     * @param splitPoint node
     * @param splitPointFiles files
     * @param name name */
    public void mergeGiven(HashMap<String, String> givenFiles,
            HashMap<String, String> currFiles, CommitNode given,
            CommitNode splitPoint, HashMap<String, String> splitPointFiles,
            String name) {
        for (String f2 : givenFiles.keySet()) {
            if (!currFiles.containsKey(f2)) {
                if (!splitPointFiles.containsKey(f2)) {
                    checkout(commitTree.getBranch().get(name).getSha(), f2);
                    add(f2);
                } else {
                    if (fileModified(given, splitPoint, f2)) {
                        try {
                            File toFile = new File(f2);
                            toFile.createNewFile();
                            mergeFiles(given.getFilepath().get(f2), f2);
                        } catch (IOException excp) {
                            throw new IllegalArgumentException(
                                    excp.getMessage());
                        }
                    }
                }
            }
        }
    }

    /** Decide if there is merge conflit.
     * @param mergeConflict conflict
     * @param name name */
    public void mergeconflits(boolean mergeConflict, String name) {
        if (!mergeConflict) {
            commit("Merged " + commitTree.getCurrentBranch() + " with " + name
                    + ".");
        } else {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** Merger two conflict files.
     * @param pathFrom from
     * @param pathTo to */
    public void mergeFiles(String pathFrom, String pathTo) {
        File from = new File(pathFrom);
        File to = new File(pathTo);
        String content1 = new String(Utils.readContents(from));
        String content2 = new String(Utils.readContents(to));
        String totalContent = "<<<<<<< HEAD\n";
        totalContent += content2 + "=======\n" + content1;
        totalContent += ">>>>>>>\n";
        Utils.writeContents(to, totalContent.getBytes());
    }

    /** Return true if file changes.
     * @param cm1 cm1
     * @param cm2 cm2
     * @param fileName name */
    public boolean fileModified(CommitNode cm1, CommitNode cm2,
            String fileName) {
        HashMap<String, String> cm1files = cm1.getFiles();
        HashMap<String, String> cm2files = cm2.getFiles();
        return !cm1files.get(fileName).equals(cm2files.get(fileName));
    }

    /** The split point.
     * @param a curr
     * @param b given
     * @return */
    public CommitNode splitPoint(CommitNode a, CommitNode b) {
        HashSet<String> history = new HashSet<String>();
        while (a != null) {
            history.add(a.getSha());
            a = a.getParent();
        }
        if (history.contains(b.getSha())) {
            System.err.println(
                    "Given branch is an ancestor of the current branch.");
            return null;
        }
        while (!history.contains(b.getSha())) {
            b = b.getParent();
        }
        return b;
    }

    /** Get the current time.
     * @return */
    public String getTime() {
        Date now = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return time.format(now);
    }

    /** Save the files.
     * @param commit commit
     * @param in in
     * @param out out */
    public void saveBlob(CommitNode commit, String in, String out) {
        File dir = new File(blobDir + commit.getSha());
        dir.mkdir();
        File infile = new File(in);
        File outfile = new File(out);
        copyFile(infile, outfile);
    }

    /** Copy files.
     * @param infile in
     * @param outfile out */
    public void copyFile(File infile, File outfile) {
        byte[] contents = Utils.readContents(infile);
        Utils.writeContents(outfile, contents);
    }

    /** The commit tree.
     * @return */
    public CommitTree getTree() {
        return commitTree;
    }

    /** The commit tree. */
    private CommitTree commitTree;
    /** Staging area for commit. */
    private File stageArea;
    /** The blob directory. */
    private String blobDir = ".gitlet/blob/";
    /** Untracked files. */
    private ArrayList<String> untrackedFiles;
    /** Staging area directory for commit. */
    private String stagingArea = ".gitlet/staging_area/";
}
