import java.util.Arrays;
import java.util.Random;

public class Matrix {
	float[][] data;
	int rows, cols;

	public Matrix(int rows, int columns) {
		data = new float[rows][columns];
		this.rows = rows;
		this.cols = columns;
	}

	public Matrix(float[][] input) {
		rows = input.length;
		cols = input.length;
		data = new float[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				data[i][j] = input[i][j];
			}
		}
	}

	public static Matrix random(int rows, int cols) {
		Random rand = new Random();
		float[][] c = new float[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				c[i][j] = (float) (rand.nextGaussian());
			}
		}
		return new Matrix(c);
	}

	public Matrix matrixMultiply(Matrix b) {
		if (cols != b.rows)
			throw new IllegalArgumentException("A's Columns: " + cols + " did not match B's Rows " + b.rows + ".");
		float[][] c = new float[rows][b.cols];

		for (int i = 0; i < rows; i++) { // aRow
			for (int j = 0; j < b.cols; j++) { // bColumn
				for (int k = 0; k < cols; k++) { // aColumn
					c[i][j] += data[i][k] * b.data[k][j];
				}
			}
		}

		return new Matrix(c);
	}

	public Matrix scalarMultiply(int s) {
		float[][] c = new float[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				c[i][j] = data[i][j] * s;
			}
		}
		return new Matrix(c);
	}

	public Matrix matrixAdd(Matrix b) {
		if (rows != b.rows || cols != b.cols)
			throw new IllegalArgumentException("A's Columns: " + cols + " did not match B's Rows " + b.rows + ".");
		float[][] c = new float[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				c[i][j] = data[i][j] + b.data[i][j];
			}
		}
		return new Matrix(c);
	}

	public String toString() {
		StringBuilder str = new StringBuilder();
		for (float[] a : data)
			str.append(Arrays.toString(a) + "\n");
		return str.toString();
	}

}
