import java.awt.Point;
import java.util.ArrayList;

public class Net extends Bot{
	
	int[] widths;
	ArrayList<Matrix> layers = new ArrayList<Matrix>();
	ArrayList<Matrix> weights = new ArrayList<Matrix>();
	
	public Net (int[] widths){
		this.widths = widths;
	}
	
	public Net(Net n1, Net n2){
		
	}
	
	public void mutate(){
		
	}
	
	@Override
	public Point move(byte[][] board) {
		// TODO Auto-generated method stub
		return null;
	}

}
