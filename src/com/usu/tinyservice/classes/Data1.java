package com.usu.tinyservice.classes;


import com.usu.tinyservice.messages.binary.IData;

public class Data1 extends IData {
	private static final long serialVersionUID = 1L;
	
	public int[] data11;
	public String[] data12;
	public byte[] data13;
	
	public Data1() { 
		this.data11 = new int[0];
		this.data12 = new String[0];
		this.data13 = new byte[0];
	}
	
	public Data1(int[] data11, String[] data12, byte[] data13) {
		this.data11 = data11;
		this.data12 = data12;
		this.data13 = data13;
	}
}
