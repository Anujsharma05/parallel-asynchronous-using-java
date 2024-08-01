package com.learnjava.exception_handling;

import static com.learnjava.util.LoggerUtil.log;
import static org.junit.jupiter.api.Assertions.*;

import com.learnjava.service.HelloWorldService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletableFutureHelloWorldExceptionTest {

  @Mock
  HelloWorldService hws = Mockito.mock(HelloWorldService.class);

  @InjectMocks
  CompletableFutureHelloWorldException cfhwe;

  @Test
  void usingHandleMethod_ExceptionInHello() {
    Mockito.when(hws.hello()).thenThrow(new RuntimeException("Exception occurred"));
    Mockito.when(hws.world()).thenCallRealMethod();

    String result = cfhwe.helloWorld_combine_3_async_calls_withHandle();
    log("result: " + result);
    assertNotNull(result);
    assertEquals("DUMMY WORLD! HI COMPLETABLE FUTURE", result);
  }

  @Test
  void usingHandleMethod_ExceptionInBothHelloAndWorld() {
    Mockito.when(hws.hello()).thenThrow(new RuntimeException("Exception occurred"));
    Mockito.when(hws.world()).thenThrow(new RuntimeException("Exception occurred"));

    String result = cfhwe.helloWorld_combine_3_async_calls_withHandle();
    log("result: " + result);
    assertNotNull(result);
    assertEquals("TUMMY HI COMPLETABLE FUTURE", result);
  }

  @Test
  void usingExceptionallyMethod_ExceptionInBothHelloAndWorld() {
    Mockito.when(hws.hello()).thenThrow(new RuntimeException("Exception occurred"));
    Mockito.when(hws.world()).thenThrow(new RuntimeException("Exception occurred"));

    String result = cfhwe.helloWorld_combine_3_async_calls_withExceptionally();
    log("result: " + result);
    assertNotNull(result);
    assertEquals("TUMMY HI", result);
  }

  @Test
  void usingWhenCompleteMethod_ExceptionInHello() {
    Mockito.when(hws.hello()).thenThrow(new RuntimeException("Exception occurred"));
    Mockito.when(hws.world()).thenCallRealMethod();

    String result = cfhwe.helloWorld_combine_3_async_calls_withWhenComplete();
    log("result: " + result);
    assertNotNull(result);
    assertEquals("exception will come", result);
  }
}