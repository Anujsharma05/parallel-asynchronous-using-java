package com.learnjava.apiclient;

import com.learnjava.domain.movie.Movie;
import com.learnjava.domain.movie.MovieInfo;
import com.learnjava.domain.movie.Review;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

public class MoviesClient {

  private final WebClient webClient;

  public MoviesClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public Movie retrieveMovie(Long movieInfoId) {

    var movieInfo = invokeMovieInfoService(movieInfoId);
    var review = invokeReviewService(movieInfoId);

    return new Movie(movieInfo, review);
  }

  public CompletableFuture<Movie> retrieveMovie_CF(Long movieInfoId) {

    CompletableFuture<MovieInfo> movieInfoCf = CompletableFuture.supplyAsync(() -> invokeMovieInfoService(movieInfoId));
    CompletableFuture<List<Review>> reviewCf = CompletableFuture.supplyAsync(() -> invokeReviewService(movieInfoId));

    return movieInfoCf.thenCombine(reviewCf, (movieInfo, reviews) -> new Movie(movieInfo, reviews));
  }

  public List<Movie> retrieveMovies(List<Long> movieInfoIds) {
    return movieInfoIds.stream().map(this::retrieveMovie).collect(Collectors.toList());
  }

  public List<Movie> retrieveMovies_CF(List<Long> movieInfoIds) {

    List<CompletableFuture<Movie>> movieCFs =  movieInfoIds.stream()
        .map(this::retrieveMovie_CF)
        .collect(Collectors.toList());

    return movieCFs.stream().map(CompletableFuture::join).collect(Collectors.toList());
  }

  private List<Review> invokeReviewService(Long movieInfoId) {

    String uriEndPoint = "/v1/reviews";

    String uri =
        UriComponentsBuilder
        .fromUriString(uriEndPoint)
        .queryParam("movieInfoId", movieInfoId)
        .buildAndExpand()
        .toString();

    List<Review> reviews =
        webClient
            .get()
            .uri(uri)
            .retrieve()
            .bodyToFlux(Review.class)
            .collectList()
            .block();
    return reviews;
  }

  private MovieInfo invokeMovieInfoService(Long movieInfoId) {

    String movieInfoUrlPath = "/v1/movie_infos/{movieInfoId}";

    MovieInfo movieInfo =
        webClient
        .get()
        .uri(movieInfoUrlPath, movieInfoId)
        .retrieve()
        .bodyToMono(MovieInfo.class)
        .block();
    return movieInfo;
  }


}
