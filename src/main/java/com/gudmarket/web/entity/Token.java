package com.gudmarket.web.entity;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.*;

@Entity
public class Token {
    private static final int EXPIRATION = 60 * 24;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String token;
  
    @OneToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "username")
    private Account user;
    
    private Date expiryDate;
   
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
    	final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    
    public Token() {
        super();
    }

    public Token(final String token) {
        super();

        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public Token(final String token, final Account user) {
        super();

        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
    public void updateToken (String token) {
    	this.token=token;
    	this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Account getUser() {
		return user;
	}

	public void setUser(Account user) {
		this.user = user;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
    
}
