package com.gudmarket.web.registration;

import java.util.Locale;

import org.springframework.context.ApplicationEvent;

import com.gudmarket.web.entity.Account;

@SuppressWarnings("serial")
public class PasswordResetCompleteEvent extends ApplicationEvent{
	private final String appUrl;
    private final Locale locale;
    private final Account user;

    public PasswordResetCompleteEvent(final Account user, final Locale locale, final String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
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
}
