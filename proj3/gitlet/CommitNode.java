package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Represent the commit node.
 * @author Jim Bai, Meng Chen*/
public class CommitNode implements Serializable {
    /** A new commit node.
     * @param time time
     * @param m m
     * @param files files
     * @param p parent
     * @param path path*/
    CommitNode(String time, String m,
            HashMap<String, String> files, CommitNode p,
            HashMap<String, String> path) {
        this.timeStamp = time;
        this.message = m;
        if (files == null) {
            this.trackedfiles = new HashMap<String, String>();
            this.filepath = new HashMap<String, String>();
        } else {
            this.trackedfiles = files;
            this.filepath = path;
        }
        this.parent = p;
        if (p != null && files != null) {
            this.sha = Utils.sha1(trackedfiles.keySet().toString(),
                    parent.getSha(), time, message);
        } else {
            this.sha = Utils.sha1(time, message);
        }
    }

    /** Return message.
     * @return
     */
    public String getMessage() {
        return message;
    }

    /** Return SHA-1.
     * @return
     */
    public String getSha() {
        return sha;
    }

    /** Return time.
     * @return
     */
    public String getDate() {
        return timeStamp;
    }

    /** Return parent.
     * @return
     */
    public CommitNode getParent() {
        return parent;
    }

    /** Return files.
     * @return
     */
    public HashMap<String, String> getFiles() {
        return trackedfiles;
    }

    /** Print the log. */
    public void printLog() {
        System.out.println("===\nCommit " + sha + "\n" + timeStamp + "\n"
                + message + "\n");
    }

    /** Return true if two nodes are equal.
     * @param cn node
     * @return
     */
    public boolean equal(CommitNode cn) {
        if (cn == null) {
            return false;
        }
        String id = cn.getSha();
        return id == sha;
    }

    /** Return filepath.
     * @return
     */
    public HashMap<String, String> getFilepath() {
        return filepath;
    }

    /** Update the filepath.
     * @param path path
     */
    public void updateFilepath(HashMap<String, String> path) {
        filepath = path;
    }

    /** Time. */
    private String timeStamp;
    /** Message. */
    private String message;
    /** SHA-1. */
    private String sha;
    /** Tracked files. */
    private HashMap<String, String> trackedfiles;
    /** The file path. */
    private HashMap<String, String> filepath;
    /** The parent. */
    private CommitNode parent;
}
