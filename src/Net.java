import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Net extends Bot{
	int score;
	int[] widths;
	ArrayList<Matrix> layers = new ArrayList<Matrix>();
	ArrayList<Matrix> weights = new ArrayList<Matrix>();
	ArrayList<Matrix> biases = new ArrayList<Matrix>();
	
	public Net (int[] widths){
		this.widths = widths;
		score = 0;
		for(int i = 0; i < widths.length; i++){
			layers.add(new Matrix(widths[i], 1));
		}
		for(int i = 0; i < widths.length-1; i++){
			weights.add(Matrix.random(widths[i+1], widths[i], 3));
			biases.add(Matrix.random(widths[i+1], 1, 3));
		}
	}
	
	public Net (int[] widths, ArrayList<Matrix> weights, ArrayList<Matrix> biases){
		this.widths = widths;
		this.weights = weights;
		this.biases = biases;
		for(int i = 0; i < widths.length; i++){
			layers.add(new Matrix(widths[i], 1));
		}
	}
	
	public  Net(Net n1, Net n2){
		widths = n1.widths;
		score = 0;
		for(int i = 0; i < widths.length; i++){
			layers.add(new Matrix(widths[i], 1));
		}
		for(int i = 0; i < n1.weights.size(); i++){
			weights.add(n1.weights.get(i).cross(n2.weights.get(i)));
			biases.add(n1.biases.get(i).cross(n2.biases.get(i)));
		}
	}
	
	public void mutate(){
		for(int i = 0; i < weights.size(); i++){
			Matrix weight = weights.get(i);
			Matrix bias = biases.get(i);
			
			weights.set(i, weight.mutate(0.5f));
			biases.set(i, bias.mutate(0.5f));
		}
	}
	
	@Override
	public Move move(byte[][] board) {
		int count = 0;
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				layers.get(0).set(count, 0, board[i][j]);
				count++;
			}
		}
		
		for(int i = 1; i < layers.size(); i++){
			Matrix result = weights.get(i-1).matrixMultiply(layers.get(i-1));
			result = result.matrixAdd(biases.get(i-1));
			result = result.matrixSigmoid();
			layers.set(i, result);
		}
		
		float[][] out = new float[board.length][board[0].length];
		Matrix lastLayer = layers.get(layers.size()-1);
		for(int i = 0; i < lastLayer.rows-1; i++){
			out[i/board.length][i%board[0].length] = lastLayer.get(i, 0);
		}
		float pass = lastLayer.get(lastLayer.rows-1, 0);
		
		return new Move(out, pass);
	}
	
	public void print(String filename){
		PrintWriter out = null;
		try {
			out = new PrintWriter("out");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		out.println(score);
		out.println(Arrays.toString(widths));

		for (Matrix w : weights) {
			for (int i = 0; i < w.rows; i++) {
				for (int j = 0; j < w.cols; j++) {
					out.print(w.get(i, j) + " ");
				}
				out.println();
			}
		}
		
		for(Matrix b : biases){
			for (int i = 0; i < b.rows; i++) {
				for (int j = 0; j < b.cols; j++) {
					out.print(b.get(i, j) + " ");
				}
				out.println();
			}
		}

		out.close();
	}

}

class Move{
	float[][] moves;
	float pass;
	public Move(float[][] moves, float pass){
		this.moves = moves;
		this.pass = pass;
	}
}
