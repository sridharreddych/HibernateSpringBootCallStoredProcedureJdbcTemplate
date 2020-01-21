package com.bookstore.service;

import com.bookstore.entity.Author;
import java.util.List;
import java.util.Map;

//import java.util.Map;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

@Service
public class BookstoreService {

    private final JdbcTemplate jdbcTemplate;

    public BookstoreService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    void init() {
        jdbcTemplate.setResultsMapCaseInsensitive(true);
    }

    public void fetchAnthologyAuthors() {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("FETCH_AUTHOR_BY_GENRE");
        
        MapSqlParameterSource paramMap = new MapSqlParameterSource()
                .addValue("p_genre", "Anthology");
               
//        Map<String, Object> resultMap = jdbcCall.execute(paramMap);
//        resultMap.entrySet().forEach(System.out::println);
//        Map<String, String> map = new HashMap<>();
//        map.put("p_genre", "Anthology");
        
        List<Author> authors = simpleJdbcCall.execute(paramMap).entrySet()
                .stream()
                .filter(m -> "#result-set-1".equals(m.getKey()))
                .map(m -> (List<Map<String, Object>>) m.getValue())
                .flatMap(List::stream)
                .map(BookstoreService::fetchAuthor)
                .collect(toList());

        System.out.println("Result: " + authors);
    }

    public static Author fetchAuthor(Map<String, Object> data) {

        Author author = new Author();

        author.setId((Long) data.get("id"));
        author.setName((String) data.get("name"));
        author.setGenre((String) data.get("genre"));
        author.setAge((int) data.get("age"));

        return author;
    }
}
