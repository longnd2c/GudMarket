package com.gudmarket.web.registration;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.gudmarket.web.entity.Account;

@SuppressWarnings("serial")
public class OnChangeEmailCompleteEvent extends ApplicationEvent{
	private final String appUrl;
    private final Locale locale;
    private final Account user;
    private final String email;

    public OnChangeEmailCompleteEvent(final Account user, final Locale locale, final String appUrl, final String email) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.email = email;
    }

	public String getAppUrl() {
		return appUrl;
	}

	public Locale getLocale() {
		return locale;
	}

	public Account getUser() {
		return user;
	}

	public String getEmail() {
		return email;
	}
	
}
