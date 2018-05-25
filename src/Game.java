import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {
	byte[][] board;
	boolean p1Move = true;
	boolean[][] checked;
	byte[][] prev;
	int turn;
	boolean lastPass = false;
	boolean inGame = true;
	boolean p1Win;

	public static void main(String[] args) throws IOException {
		Game g = new Game(9, 9);
		g.loadBoard("in");
		g.display();
		
		Net n = loadNet("out");

		Scanner scan = new Scanner(System.in);
		while (g.inGame) {
			g.move(scan.nextInt(), scan.nextInt());
			
			Move m1 = n.move(g.board);
			Point loc1 = null;
			ArrayList<Point> tries1 = new ArrayList<Point>();
			loop: do {
				float max = 0;
				if (tries1.size() > 10) {
					g.pass();
					break loop;
				}
				for (int i = 0; i < g.board.length; i++) {
					for (int j = 0; j < g.board[0].length; j++) {
						if (m1.moves[i][j] > max && !tries1.contains(new Point(i, j))) {
							max = m1.moves[i][j];
							loc1 = new Point(i, j);
						}
					}
				}
				if (m1.pass > max) {
					g.pass();
					break loop;
				} else {
					tries1.add(loc1);
				}
			} while (!g.move(loc1.x, loc1.y));
			g.display();
		}
	}
	
	public static Net loadNet(String filename){
		Scanner scan = null;
		try{
			scan = new Scanner(new File(filename));		
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		
		int[] widths = {81, 100, 150, 100, 82};
		ArrayList<Matrix> weights = new ArrayList<Matrix>();
		ArrayList<Matrix> biases = new ArrayList<Matrix>();
		
		String s = scan.nextLine();
		s =scan.nextLine();
		
		for(int i = 0; i < widths.length-1; i++){
			float[][] data = new float[widths[i+1]][widths[i]];
			for(int j = 0; j < widths[i+1]; j++){
				for(int k = 0; k < widths[i]; k++){
					data[j][k] = scan.nextFloat();
				}
			}
			weights.add(new Matrix(data));
		}
		
		for(int i = 0; i < widths.length-1; i++){
			float[][] data = new float[widths[i+1]][1];
			for(int j = 0; j < widths[i+1]; j++){
				data[j][0] = scan.nextFloat();
			}
			biases.add(new Matrix(data));
		}
		
		return new Net(widths, weights, biases);
	}

	public Game(int w, int h) {
		board = new byte[h][w];
		turn = 0;
		prev = new byte[h][w];
	}

	public boolean play(Bot p1, Bot p2) {
		while (inGame) {
			Move m1 = p1.move(board);
			Point loc1 = null;
			ArrayList<Point> tries1 = new ArrayList<Point>();
			loop: do {
				float max = 0;
				if (tries1.size() > 10) {
					pass();
					break loop;
				}
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[0].length; j++) {
						if (m1.moves[i][j] > max && !tries1.contains(new Point(i, j))) {
							max = m1.moves[i][j];
							loc1 = new Point(i, j);
						}
					}
				}
				if (m1.pass > max) {
					pass();
					break loop;
				} else {
					tries1.add(loc1);
				}
			} while (!move(loc1.x, loc1.y));
			
			
			
			Move m2 = p2.move(board);
			Point loc2 = null;
			ArrayList<Point> tries2 = new ArrayList<Point>();
			loop: do {
				float max = 0;
				if (tries2.size() > 10) {
					pass();
					break loop;
				}
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[0].length; j++) {
						if (m1.moves[i][j] > max && !tries2.contains(new Point(i, j))) {
							max = m1.moves[i][j];
							loc2 = new Point(i, j);
						}
					}
				}
				if (m1.pass > max) {
					pass();
					break loop;
				} else {
					tries2.add(loc2);
				}
			} while (!move(loc2.x, loc2.y));
		}
		
		return p1Win;
	}

	public boolean arrayEquals(byte[][] ar1, byte[][] ar2) {
		if (ar1.length != ar2.length || ar1[0].length != ar2[0].length)
			return false;
		for (int i = 0; i < ar1.length; i++) {
			for (int j = 0; j < ar1[0].length; j++) {
				if (ar1[i][j] != ar2[i][j])
					return false;
			}
		}
		return true;
	}

	public boolean ko(int i, int j) {
		if (turn < 2)
			return false;
		byte[][] save = arrayCopy(board);
		board[i][j] = (byte) (p1Move ? 1 : 2);
		check(i, j);
		if (arrayEquals(prev, board)) {
			board = save;
			return true;
		}
		board = save;
		return false;
	}

	public boolean isLegal(int i, int j) {
		if (board[i][j] != 0)
			return false;

		checked = new boolean[board.length][board[0].length];
		boolean temp = isDead(i, j);

		if (temp) {
			return false;
		}

		if (ko(i, j))
			return false;

		return true;
	}

	public void score() {
		int p1 = 0;
		int p2 = 0;
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[0].length; j++){
				if(board[i][j] == 1)
					p1++;
				if(board[i][j] == 2)
					p2++;
			}
		}
		
		if(p1 >= p2)
			p1Win = true;
		else
			p1Win = false;
	}

	public void kill(int i, int j, byte type) {
		if (board[i][j] == 0)
			return;
		else
			board[i][j] = 0;

		if (withinBounds(i - 1, j))
			if (board[i - 1][j] == type)
				kill(i - 1, j, type);

		if (withinBounds(i + 1, j))
			if (board[i + 1][j] == type)
				kill(i + 1, j, type);

		if (withinBounds(i, j + 1))
			if (board[i][j + 1] == type)
				kill(i, j + 1, type);

		if (withinBounds(i, j - 1))
			if (board[i][j - 1] == type)
				kill(i, j - 1, type);

	}

	public void check(int i, int j) {
		if (withinBounds(i - 1, j))
			if (board[i - 1][j] != board[i][j] && board[i - 1][j] != 0)
				if (!alive(i - 1, j, new boolean[this.checked.length][this.checked[0].length]))
					kill(i - 1, j, board[i - 1][j]);

		if (withinBounds(i + 1, j))
			if (board[i + 1][j] != board[i][j] && board[i + 1][j] != 0)
				if (!alive(i + 1, j, new boolean[this.checked.length][this.checked[0].length]))
					kill(i + 1, j, board[i + 1][j]);

		if (withinBounds(i, j + 1))
			if (board[i][j + 1] != board[i][j] && board[i][j + 1] != 0)
				if (!alive(i, j + 1, new boolean[this.checked.length][this.checked[0].length]))
					kill(i, j + 1, board[i][j + 1]);

		if (withinBounds(i, j - 1))
			if (board[i][j - 1] != board[i][j] && board[i][j - 1] != 0)
				if (!alive(i, j - 1, new boolean[this.checked.length][this.checked[0].length]))
					kill(i, j - 1, board[i][j - 1]);
	}

	public boolean alive(int i, int j, boolean[][] checked) {
		if (checked[i][j]) {
			return false;
		} else
			checked[i][j] = true;

		if (anyLiberties(i, j))
			return true;

		if (withinBounds(i - 1, j))
			if (board[i - 1][j] == board[i][j])
				if (alive(i - 1, j, checked))
					return true;
		if (withinBounds(i + 1, j))
			if (board[i + 1][j] == board[i][j])
				if (alive(i + 1, j, checked))
					return true;
		if (withinBounds(i, j - 1))
			if (board[i][j - 1] == board[i][j])
				if (alive(i, j - 1, checked))
					return true;
		if (withinBounds(i, j + 1))
			if (board[i][j + 1] == board[i][j])
				if (alive(i, j + 1, checked))
					return true;
		return false;
	}

	public boolean move(int i, int j) {
		if (isLegal(i, j)) {
			p1Move ^= true;
			prev = arrayCopy(board);
			board[i][j] = (byte) (!p1Move ? 1 : 2);
			turn++;
			check(i, j);
			lastPass = false;
			return true;
		} else {
			//System.out.println("ERROR ILLEGAL MOVE");
			return false;
		}
	}

	public void pass() {
		p1Move ^= true;
		turn++;
		if (lastPass) {
			//System.out.println("game over");
			inGame = false;
			score();
		} else
			lastPass = true;
	}

	public boolean isEmpty(int i, int j) {
		if (!withinBounds(i, j))
			return false;
		if (board[i][j] == 0)
			return true;
		return false;
	}

	public boolean anyLiberties(int i, int j) {
		if (isEmpty(i - 1, j) || isEmpty(i + 1, j) || isEmpty(i, j + 1) || isEmpty(i, j - 1)) {
			return true;
		}
		return false;
	}

	public boolean isDead(int i, int j) {
		if (!withinBounds(i, j))
			return true;

		if (anyLiberties(i, j))
			return false;

		if (board[i][j] != 0)
			return true;

		board[i][j] = (byte) (p1Move ? 1 : 2);
		boolean temp = alliesAlive(i, j);
		board[i][j] = 0;
		if (temp)
			return false;

		return true;
	}

	public boolean alliesAlive(int i, int j) {
		if (!withinBounds(i, j))
			return false;
		if (checked[i][j]) {
			return false;
		} else
			checked[i][j] = true;
		if (anyLiberties(i, j))
			return true;

		if (withinBounds(i - 1, j))
			if (board[i - 1][j] == (byte) (!p1Move ? 1 : 2))
				if (!alive(i - 1, j, new boolean[board.length][board[0].length]))
					return true;
		if (withinBounds(i + 1, j))
			if (board[i + 1][j] == (byte) (!p1Move ? 1 : 2))
				if (!alive(i + 1, j, new boolean[board.length][board[0].length]))
					return true;
		if (withinBounds(i, j + 1))
			if (board[i][j + 1] == (byte) (!p1Move ? 1 : 2))
				if (!alive(i, j + 1, new boolean[board.length][board[0].length]))
					return true;
		if (withinBounds(i, j - 1))
			if (board[i][j - 1] == (byte) (!p1Move ? 1 : 2))
				if (!alive(i, j - 1, new boolean[board.length][board[0].length]))
					return true;

		if (withinBounds(i - 1, j))
			if (board[i - 1][j] == board[i][j])
				if (alliesAlive(i - 1, j))
					return true;

		if (withinBounds(i + 1, j))
			if (board[i + 1][j] == board[i][j])
				if (alliesAlive(i + 1, j))
					return true;

		if (withinBounds(i, j - 1))
			if (board[i][j - 1] == board[i][j])
				if (alliesAlive(i, j - 1))
					return true;

		if (withinBounds(i, j + 1))
			if (board[i][j + 1] == board[i][j])
				if (alliesAlive(i, j + 1))
					return true;

		return false;
	}

	public boolean withinBounds(int i, int j) {
		if (i < 0 || i > board.length - 1 || j < 0 || j > board[0].length - 1)
			return false;
		return true;
	}

	public void rawDisplay() {
		System.out.println("----------------------------------------");
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("----------------------------------------");

	}

	public void display() {
		System.out.println("----------------------------------------");
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				char c = 'E';
				switch (board[i][j]) {
				case 0:
					c = '.';
					break;
				case 1:
					c = 'X';
					break;
				case 2:
					c = 'O';
					break;
				}
				System.out.print(c + " ");
			}
			System.out.println();
		}
		System.out.println("----------------------------------------");
	}

	public void loadBoard(String filename) throws IOException {
		Scanner scan = new Scanner(new File(filename));
		int cnt = 0;
		while (scan.hasNextByte()) {
			board[cnt / board[0].length][cnt % board.length] = scan.nextByte();
			cnt++;
		}

		scan.close();
		p1Move = true;
	}

	public byte[][] arrayCopy(byte[][] ar) {
		byte[][] ret = new byte[ar.length][ar[0].length];
		for (int i = 0; i < ar.length; i++) {
			for (int j = 0; j < ar[0].length; j++) {
				ret[i][j] = ar[i][j];
			}
		}

		return ret;
	}
}
