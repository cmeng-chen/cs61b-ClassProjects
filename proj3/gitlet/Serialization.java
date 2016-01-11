package gitlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/** The serialization function.
 *  @author Jim Bai, Meng Chen
 */
public class Serialization implements Serializable {

    /** Serialize an object.
     * @param obj obj
     * @param name name*/
    public static void save(Object obj, String name) {
        String fileName = name;
        File outFile = new File(fileName);
        try {
            ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(obj);
            out.close();
        } catch (IOException excp) {
            /* No action */
        }
    }

    /** Deserialize an object.
     * @param fileName filename
     * @return
     */
    public static Object load(String fileName) {
        Object obj = null;
        File inFile = new File(fileName);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            obj = (Object) inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException excp) {
            obj = null;
        }
        return obj;
    }

}
