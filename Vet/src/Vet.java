public class Vet
{
	int[] v;
	float out;

	public float average(int[] in)
	{
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

		return out;
	}
}
