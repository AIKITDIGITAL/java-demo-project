package com.aikitdigital.demoproject.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {

	private final long offset;
	private final int limit;
	private final Sort sort;

	public OffsetBasedPageRequest(long offset, int limit, Sort sort) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset must not be less than 0");
		}
		if (limit < 1) {
			throw new IllegalArgumentException("Limit must not be less than 1");
		}
		this.limit = limit;
		this.offset = offset;
		this.sort = sort;
	}

	@Override
	public int getPageNumber() {
		return (int) (offset / limit);
	}

	@Override
	public int getPageSize() {
		return limit;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public Pageable next() {
		return new OffsetBasedPageRequest(getOffset() + getPageSize(), getPageSize(), getSort());
	}

	@Override
	public Pageable previousOrFirst() {
		return hasPrevious() ? previous() : first();
	}

	public OffsetBasedPageRequest previous() {
		return hasPrevious() ? new OffsetBasedPageRequest(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
	}

	@Override
	public Pageable first() {
		return new OffsetBasedPageRequest(0, getPageSize(), getSort());
	}

	@Override
	public Pageable withPage(int pageNumber) {
		return null;
	}

	@Override
	public boolean hasPrevious() {
		return offset >= limit;
	}
}
