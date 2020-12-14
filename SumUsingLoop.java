import java.io.*;

class SumUsingLoop {
    
    public static int sum(int num) {
		int total = 0;
		while(num > 0){
		    total = total + num;
		    num = num - 1;
		}
		return total;
	}
	
	
	public static void main (String[] args) {
		System.out.println(sum(10));
	}
}
