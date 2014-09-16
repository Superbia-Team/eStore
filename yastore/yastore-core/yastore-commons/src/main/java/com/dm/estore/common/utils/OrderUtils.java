package com.dm.estore.common.utils;

import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

public class OrderUtils {
	
	/**
	 * Generate order code.
	 * TBD: it can be done with ZooKeeper in future. Or we can find the way to generate human readable code.
	 * 
	 * @return
	 */
	public static final String generateOrderCode() {
		return UUID.randomUUID().toString();
	}
	
	public static String resolveCountryByCurrency(final String currencyCode)
	{
		final Locale[] locales = Locale.getAvailableLocales();
		for (final Locale locale : locales)
		{
			final String currency = Currency.getInstance(locale).getCurrencyCode();
			if (currency.equalsIgnoreCase(currencyCode))
			{
				return locale.getCountry();
			}
		}

		return LocaleContextHolder.getLocale().getCountry();
	}
	
	public static Currency resolveCurrencyByLocale(final Locale locale)
	{
		return Currency.getInstance(locale);
	}
	
	public static Currency resolveCurrencyByCountryCode(final String countryCode)
	{
		if (!StringUtils.isEmpty(countryCode)) {
			final Locale[] locales = Locale.getAvailableLocales();
			for (final Locale locale : locales)
			{
				if (locale != null && locale.getCountry().equalsIgnoreCase(countryCode))
				{
					return Currency.getInstance(locale);
				}
			}
		}

		return Currency.getInstance(LocaleContextHolder.getLocale());
	}
}
