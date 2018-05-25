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
		cols = input[0].length;
		data = new float[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				data[i][j] = input[i][j];
			}
		}
	}

	public static Matrix random(int rows, int cols, float scalar) {
		Random rand = new Random();
		float[][] c = new float[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				c[i][j] = (float) (scalar*rand.nextGaussian());
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
	
	public void set(int row, int col, int val){
		data[row][col] = val;
	}
	
	public Matrix cross(Matrix b){
		Random rand = new Random();
		
		float[][] c = new float[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				c[i][j] = rand.nextBoolean() ? data[i][j] : b.data[i][j];
			}
		}
		
		return new Matrix(c);
	}
	
	public Matrix mutate(float scalar){
		Random rand = new Random();
		float[][] c = new float[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				c[i][j] = (float) (data[i][j] + scalar * rand.nextGaussian());
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
	
	public Matrix matrixSigmoid(){
		float[][] c = new float[rows][cols];
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				c[i][j] = sigmoid(data[i][j]);
			}
		}
		return new Matrix(c);
	}
	
	public float get(int row, int col){
		if(row < 0 || row >= rows || col < 0 || col >= cols)
			throw new IllegalArgumentException("add error");
		return data[row][col];
	}

	public Matrix matrixAdd(Matrix b) {
		if (rows != b.rows || cols != b.cols)
			throw new IllegalArgumentException("add error");
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
	
	public float sigmoid(float f){
		return (float) (1/(1 + Math.exp(-f)));
	}

}
