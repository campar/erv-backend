package com.example.erv.exceptions;

public class BlockedUserException extends ErvException{
	private static final long serialVersionUID = 1L;
	private final String username;
	private final Long remainingSeconds;
	
	public BlockedUserException(String username, Long remainingSeconds) {
        super("User is temporarily blocked: " + username);
        this.username = username;
        this.remainingSeconds = remainingSeconds;
    }

    public String getUsername() {
        return username;
    }
    
    public long getRemainingSeconds() {
        return remainingSeconds;
    }
}
