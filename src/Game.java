import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Game {
	byte[][] board;
	boolean p1Move = true;
	boolean[][] checked;
	byte[][] prev;
	int turn;
	boolean lastPass = false;

	public static void main(String[] args) throws IOException {
		Game g = new Game(13, 13);
		g.loadBoard("in");
		g.display();

		Scanner scan = new Scanner(System.in);
		while (true) {
			g.move(scan.nextInt(), scan.nextInt());
			g.display();
		}
	}

	public Game(int w, int h) {
		board = new byte[h][w];
		turn = 0;
		prev = new byte[h][w];
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
		if(arrayEquals(prev, board)){
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
			System.out.println("ERROR ILLEGAL MOVE");
			return false;
		}
	}

	public void pass() {
		p1Move ^= true;
		turn++;
		if(lastPass)
			System.out.println("game over");
		else
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
