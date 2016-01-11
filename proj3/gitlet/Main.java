package gitlet;

import java.io.File;
import java.io.Serializable;

/** Driver class for Gitlet, the tiny stupid version-control system.
 * @author Jim Bai, Meng Chen */
public class Main implements Serializable {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        if (args == null || args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String command = args[0].toLowerCase();
        int length = args.length;
        File gitdir = new File(".gitlet/");
        Gitlet gitlet;
        if (gitdir.exists()) {
            gitlet = (Gitlet) Serialization.load(".gitlet/gitlet");
            select1(command, args, length, gitlet);
        } else if (command.equals("init")) {
            gitdir.mkdir();
            gitlet = new Gitlet();
            Serialization.save(gitlet, ".gitlet/gitlet");
        } else {
            System.err.println("Not in an initialized gitlet directory.");
            return;
        }
    }

    /** Select the action part 1.
     * @param command c
     * @param args args
     * @param length length
     * @param gitlet gitlet*/
    private static void select1(String command, String[] args, int length,
            Gitlet gitlet) {
        if (!command.equals("init")) {
            switch (command) {
            case ("add"):
                if (length != 2) {
                    wrongOperands();
                }
                gitlet.add(args[1]);
                break;
            case ("commit"):
                if (length == 1) {
                    System.err.println("Please enter a commit message.");
                    return;
                }
                if (length != 2) {
                    wrongOperands();
                }
                gitlet.commit(args[1]);
                break;
            case ("rm"):
                if (length != 2) {
                    wrongOperands();
                }
                gitlet.rm(args[1]);
                break;
            case ("log"):
                if (length != 1) {
                    wrongOperands();
                }
                gitlet.log();
                break;
            case ("global-log"):
                if (length != 1) {
                    wrongOperands();
                }
                gitlet.globallog();
                break;
            case ("find"):
                if (length != 2) {
                    wrongOperands();
                }
                gitlet.find(args[1]);
                break;
            case ("status"):
                if (length != 1) {
                    wrongOperands();
                }
                gitlet.status();
                break;
            default:
                select2(command, args, length, gitlet);
            }
            Serialization.save(gitlet, ".gitlet/gitlet");
        } else {
            System.err.println("A gitlet version-control "
                    + "system already exists in the current directory.");
        }
    }

    /** Select the action part 2.
     * @param command c
     * @param args args
     * @param length length
     * @param gitlet gitlet*/
    private static void select2(String command, String[] args, int length,
            Gitlet gitlet) {
        switch (command) {
        case ("checkout"):
            if (length == 3 && args[1].equals("--")) {
                gitlet.checkoutF(args[2]);
            } else if (length == 4 && args[2].equals("--")) {
                gitlet.checkout(args[1], args[3]);
            } else if (length == 2) {
                gitlet.checkoutB(args[1]);
            } else {
                wrongOperands();
            }
            break;
        case ("branch"):
            if (length != 2) {
                wrongOperands();
            }
            gitlet.branch(args[1]);
            break;
        case ("rm-branch"):
            if (length != 2) {
                wrongOperands();
            }
            gitlet.rmbranch(args[1]);
            break;
        case ("reset"):
            if (length != 2) {
                wrongOperands();
            }
            gitlet.reset(args[1]);
            break;
        case ("merge"):
            if (length != 2) {
                wrongOperands();
            }
            gitlet.merge(args[1]);
            break;
        default:
            System.err.println("No command with that name exists.");
            return;
        }
        Serialization.save(gitlet, ".gitlet/gitlet");
    }

    /** Print the erro message. */
    private static void wrongOperands() {
        System.err.println("Incorrect operands.");
        return;
    }
}
