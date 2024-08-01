package com.learnjava.apiclient;

import static com.learnjava.util.CommonUtil.startTimer;
import static com.learnjava.util.CommonUtil.timeTaken;
import static org.junit.jupiter.api.Assertions.*;

import com.learnjava.domain.movie.Movie;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class MoviesClientTest {

  WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();
  MoviesClient moviesClient = new MoviesClient(webClient);

  @RepeatedTest(10)
  void retrieveMovie() {

    startTimer();

    var movieInfoId = 1L;

    Movie movie = moviesClient.retrieveMovie(movieInfoId);

    System.out.println(movie);

    timeTaken();

    assertNotNull(movie);
    assertEquals("Batman Begins", movie.getMovieInfo().getName());
    assertEquals(1, movie.getReviewList().size());
  }

  @RepeatedTest(10)
  void retrieveMovie_CF() {
    var movieInfoId = 1L;

    startTimer();
    CompletableFuture<Movie> movieCf = moviesClient.retrieveMovie_CF(movieInfoId);

    Movie movie = movieCf.join();
    System.out.println(movie);

    timeTaken();
    assertNotNull(movie);
    assertEquals("Batman Begins", movie.getMovieInfo().getName());
    assertEquals(1, movie.getReviewList().size());
  }

  @RepeatedTest(10)
  void retrieveMovies() {

    startTimer();

    var movieInfoIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);

    List<Movie> movies = moviesClient.retrieveMovies(movieInfoIds);

    timeTaken();

    assertNotNull(movies);
    assertEquals(7, movies.size());
  }

  @RepeatedTest(10)
  void retrieveMovies_CF() {

    startTimer();

    var movieInfoIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);

    List<Movie> movies = moviesClient.retrieveMovies_CF(movieInfoIds);

    timeTaken();

    assertNotNull(movies);
    assertEquals(7, movies.size());
  }
}