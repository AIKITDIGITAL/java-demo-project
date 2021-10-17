package com.aikitdigital.demoproject.formatter;

import com.aikitdigital.demoproject.model.SearchQuery;
import com.aikitdigital.demoproject.parser.SearchQueryParserComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.net.URLDecoder;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SearchQueryFormatter implements Formatter<SearchQuery> {

	private final SearchQueryParserComponent searchQueryParserComponent;

	@Override
	public SearchQuery parse(String text, Locale locale) {
		return searchQueryParserComponent.parse(URLDecoder.decode(text, SearchQueryParserComponent.ENCODING));
	}

	@Override
	public String print(SearchQuery object, Locale locale) {
		return "todo"; //todo print SearchQuery
	}
}
