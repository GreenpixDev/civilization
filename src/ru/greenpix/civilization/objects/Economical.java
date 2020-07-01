package ru.greenpix.civilization.objects;

public interface Economical {

	String TAXRATE = "taxrate";
	
	public double getBalance();
	
	public void setBalance(double balance);
	
	public void withdraw(double balance);
	
	public void deposit(double balance);
	
}
