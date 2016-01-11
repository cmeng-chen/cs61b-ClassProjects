package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

/** Represent the commit tree structure.
 * @author Jim Bai, Meng Chen*/
public class CommitTree implements Serializable {
    /** A new commit tree. */
    CommitTree() {
        headPointer = null;
        commitTree = new HashMap<String, CommitNode>();
        branches = new HashMap<String, CommitNode>();
    }

    /** Deleted the another constructor.
     * @param child child*/
    public void add(CommitNode child) {
        String id = child.getSha();
        headPointer = child;
        commitTree.put(id, child);
        if (currentBranch != null) {
            branches.put(currentBranch, child);
        }
    }

    /** Find node by commit id.
     * @param id id
     * @return
     */
    public CommitNode findByid(String id) {
        return commitTree.get(id);
    }

    /** Find node by short id.
     * @param id id
     * @return
     */
    public CommitNode findByShortid(String id) {
        for (String full : commitTree.keySet()) {
            if (id.equals(full.substring(0, id.length()))) {
                return commitTree.get(full);
            }
        }
        return null;
    }

    /** Find the node by message.
     * @param message m
     * @return
     */
    public ArrayList<CommitNode> findBymessage(String message) {
        ArrayList<CommitNode> result = new ArrayList<CommitNode>();
        for (CommitNode cn : getCommitNode()) {
            if (cn.getMessage().equals(message)) {
                result.add(cn);
            }
        }
        return result;
    }

    /** Add a new branch.
     * @param name name
     * @param commit c
     * @return
     */
    public boolean addBranch(String name, CommitNode commit) {
        if (branches.containsKey(name)) {
            return false;
        }
        branches.put(name, commit);
        return true;
    }

    /** Update the branch.
     * @param name name
     * @param commit c*/
    public void updateBranch(String name, CommitNode commit) {
        if (!branches.containsKey(name)) {
            return;
        } else {
            branches.put(name, commit);
        }
    }

    /** Switch branch.
     * @param branch branch
     */
    public void switchBranch(String branch) {
        CommitNode newPointer = branches.get(branch);
        headPointer = newPointer;
        currentBranch = branch;
    }

    /** Return current commit.
     * @return*/
    public CommitNode headPointer() {
        return headPointer;
    }

    /** Return branches.
     * @return*/
    public HashMap<String, CommitNode> getBranch() {
        return branches;
    }

    /** Return all commits.
     * @return*/
    public Collection<CommitNode> getCommitNode() {
        return commitTree.values();
    }

    /** Return current commit.
     * @return*/
    public String getCurrentBranch() {
        return currentBranch;
    }

    /** Delete branch.
     * @param branch branch*/
    public void deleteBranch(String branch) {
        branches.remove(branch);
    }

    /** Switch the head pointer.
     * @param cm cm
     */
    public void switchHead(CommitNode cm) {
        headPointer = cm;
        branches.put(currentBranch, cm);
    }
    /** The current commit. */
    private CommitNode headPointer;
    /** All commits. */
    private HashMap<String, CommitNode> commitTree;
    /** All branches. */
    private HashMap<String, CommitNode> branches;
    /** The current branch. */
    private String currentBranch;
}
