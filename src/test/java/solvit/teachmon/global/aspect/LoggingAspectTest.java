package solvit.teachmon.global.aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import solvit.teachmon.global.annotation.Trace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(OutputCaptureExtension.class)
class LoggingAspectTest {

    private TraceTarget proxy;

    @BeforeEach
    void setUp() {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new TraceTarget());
        proxyFactory.addAspect(new LoggingAspect());
        proxy = proxyFactory.getProxy();
    }

    @Test
    @DisplayName("성공 호출 시 TRACE 시작/종료 로그를 남긴다")
    void logsTraceStartAndEndWithArgumentsAndResult(CapturedOutput output) {
        String input = "hi";
        String result = "echo:" + input;

        String actual = proxy.echo(input);
        assertThat(actual).isEqualTo(result);

        assertThat(logs(output))
                .contains("[TRACE][echo][START]", "arguments=[" + input + "]")
                .contains("[TRACE][echo][END]", "result=" + result);
    }

    @Test
    @DisplayName("예외 발생 시 TRACE 에러 로그를 남긴다")
    void logsTraceErrorWithExceptionClassAndMessage(CapturedOutput output) {
        String exceptionMessage = "boom";

        assertThatThrownBy(proxy::fail)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(exceptionMessage);

        assertThat(logs(output))
                .contains("[TRACE][fail][ERROR]")
                .contains("fail")
                .contains("exClass=IllegalStateException")
                .contains("exMessage=" + exceptionMessage);
    }

    private String logs(CapturedOutput output) {
        return output.getOut();
    }

    @Trace
    static class TraceTarget {
        String echo(String input) {
            return "echo:" + input;
        }

        void fail() {
            throw new IllegalStateException("boom");
        }
    }
}
