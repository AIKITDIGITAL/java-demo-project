package com.aikitdigital.demoproject.parser;

import com.aikitdigital.demoproject.model.SearchQuery;
import com.aikitdigital.demoproject.parser.syntaxtree.NodesFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SearchQueryParserComponent {

	public static final Charset ENCODING = StandardCharsets.UTF_8;

	private final NodesFactory nodesFactory;

	public SearchQuery parse(String query) {
		if (query == null) {
			throw new IllegalArgumentException("Query must not be null");
		}
		final var inputStream = new ByteArrayInputStream(query.getBytes(ENCODING));
		final var parser = new SearchQueryParser(inputStream, ENCODING.name(), nodesFactory);

		try {
			return new SearchQuery(parser.Input());
		} catch (Throwable throwable) {
			throw new SearchQueryParserException(throwable);
		}
	}
}
