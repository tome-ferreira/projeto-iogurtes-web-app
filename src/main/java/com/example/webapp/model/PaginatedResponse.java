package com.example.webapp.model;

import java.util.List;

public class PaginatedResponse<T> {

    public List<T> content;
    public long totalElements;
    public int totalPages;
    public int number;
    public int size;
    public boolean first;
    public boolean last;
    public int numberOfElements;
    public boolean empty;

    public PaginatedResponse() {
    }
}
