package com.aikitdigital.demoproject.model;

import com.aikitdigital.demoproject.parser.syntaxtree.Node;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchQuery {
	private Node node;
}
