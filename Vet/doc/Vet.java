public class Vet {  
    int v[];
    float out;
  
    float average(int[] in) {
        v = in;
        out = 0.0f;
        int i = 0;

        try {
            while (i < v.length) {
                out += v[i];
                i++;
            }
            out = out / i;
        } catch (Exception e) {
            out = 0.0f;
            i = 0;
        } finally {
            v = null;
        }
        print((float) i);
        return out;
    }
  
    public void print(float n) {
        System.out.print(n + "\n");
    }
}