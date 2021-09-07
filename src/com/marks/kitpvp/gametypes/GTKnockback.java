package com.marks.kitpvp.gametypes;

public class GTKnockback extends GTThreeLives {

	public GTKnockback() {
		super();
	}
	
	public static boolean isRightPlayers(int amt) {
		return amt >= 2;
	}
	
	@Override
	public String name() {
		return "Super Knockback";
	}
}
