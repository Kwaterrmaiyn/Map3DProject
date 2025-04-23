package fortume.algorithm;

import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;

public class ResultForOpenGL {
    public ArrayList<Vector3f> vertices;
    public ArrayList<Vector3f> normals;
    public ArrayList<Vector3i> indexes;
    public ArrayList<Vector3f> colors;
    public String fileName;

    public ResultForOpenGL(ArrayList<Vector3f> vertexList, ArrayList<Vector3f> normalList,
                           ArrayList<Vector3i> indList, ArrayList<Vector3f> colorList){
        vertices = vertexList;
        normals = normalList;
        indexes = indList;
        colors = colorList;
    }

    public static Vector3f getNormal(Vector3f a, Vector3f b, Vector3f c){
        Vector3f ab = new Vector3f(b.x-a.x, b.y-a.y, b.z-a.z);
        Vector3f ac = new Vector3f(c.x-a.x, c.y-a.y, c.z-a.z);
        return new Vector3f(ab.y*ac.z - ab.z*ac.y,ab.z*ac.x-ab.x*ac.z, ab.x*ac.y-ab.y*ac.x).normalize();
    }
}
