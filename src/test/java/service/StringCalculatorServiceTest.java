package service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StringCalculatorServiceTest {

    static StringCalculatorService stringCalculatorService;

    @BeforeEach
    void makeStringCalculatorService() {
        stringCalculatorService = new StringCalculatorService();
    }

    private static Stream<Arguments> methodSourceOfGetCustomSeparator() {
        return Stream.of(
            Arguments.arguments("//.\n", "."),
            Arguments.arguments("//~\n", "~"),
            Arguments.arguments("//123\n", "123")
        );
    }


    @ParameterizedTest(name = "{0}을 입력하면 커스텀 구분자 {1}이 추출된다.")
    @MethodSource("methodSourceOfGetCustomSeparator")
    @DisplayName("//과 \\n 사이의 string은 커스텀 구분자이다")
    void testCustomSeparator(String expression, String expectedCustomSeparator) {
        assertThat(stringCalculatorService.getCustomSeparator(expression))
            .isEqualTo(expectedCustomSeparator);
    }

    private static Stream<Arguments> validMethodSourceOfPlusAllTokens() {
        return Stream.of(
            Arguments.arguments(List.of("1", "2", "3"), 6),
            Arguments.arguments(List.of("10", "20", "30", "40"), 100)
        );
    }

    @ParameterizedTest(name = "{0}을 모두 더한 값은 {1}이다.")
    @MethodSource("validMethodSourceOfPlusAllTokens")
    @DisplayName("유효한 String[] 숫자 배열이 주어지면 그 값을 모두 더한 값이 return 된다.")
    void validTestOfPlusAllTokens(List<String> tokens, int expectedResult) {
        assertThat(stringCalculatorService.plusAllTokens(tokens))
            .isEqualTo(expectedResult);
    }

    private static Stream<Arguments> invalidMethodSourceOfPlusAllTokens() {
        return Stream.of(
            Arguments.arguments(List.of("a", "1", "2")),
            Arguments.arguments(List.of(".", "3")),
            Arguments.arguments(List.of("가", "3"))
        );
    }

    @ParameterizedTest(name = "숫자가 아닌 값 {0}을 입력하면 에러가 반환된다.")
    @MethodSource("invalidMethodSourceOfPlusAllTokens")
    @DisplayName("숫자가 아닌 값이 들어오면 에러가 반환된다.")
    void invalidTestOfPlusAllTokens(List<String> tokens) {
        assertThatThrownBy(() -> stringCalculatorService.plusAllTokens(tokens))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("[ERROR] %s은(는) 유효한 숫자가 아닙니다.", tokens.get(0)));
    }

    private static Stream<Arguments> minusValueMethodSourceOfPlusAllTokens() {
        return Stream.of(
            Arguments.arguments(List.of("-1", "1", "2")),
            Arguments.arguments(List.of("-2", "3")),
            Arguments.arguments(List.of("-3", "3"))
        );
    }

    @ParameterizedTest(name = "음수 {0}을 입력하면 에러가 반환된다.")
    @MethodSource("minusValueMethodSourceOfPlusAllTokens")
    @DisplayName("음수인 값이 들어오면 에러가 반환된다.")
    void minusValueTestOfPlusAllTokens(List<String> tokens) {
        assertThatThrownBy(() -> stringCalculatorService.plusAllTokens(tokens))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(String.format("[ERROR] 음수 값 %s은(는) 입력하실 수 없습니다.", tokens.get(0)));
    }

    private static Stream<Arguments> methodSourceOfGetTokens() {
        return Stream.of(
            Arguments.arguments("1,2,3", List.of("1", "2", "3")),
            Arguments.arguments("//.\n1.2:3.4", List.of("1", "2", "3", "4")),
            Arguments.arguments("//[\n1[2[3", List.of("1", "2", "3")),
            Arguments.arguments("//!\n1!2!3", List.of("1", "2", "3")),
            Arguments.arguments("//\t\n1\t2\t3", List.of("1", "2", "3")),
            Arguments.arguments("//'\n1'2:3", List.of("1", "2", "3")),
            Arguments.arguments("//\b\n1\b2:3", List.of("1", "2", "3")),
            Arguments.arguments("//\s\n1\s2:3", List.of("1", "2", "3")),
            Arguments.arguments("//|\n1|2:3", List.of("1", "2", "3"))
        );
    }

    @ParameterizedTest(name = "{0}의 입력에서 더할 숫자들의 집합은 {1}이다.")
    @MethodSource("methodSourceOfGetTokens")
    @DisplayName("기본 구분자 ','와 ':' 그리고 커스텀 구분자를 활용하여 더할 숫자들의 집합을 구할 수 있다.")
    void getTokensTest(String expression, List<String> expectedTokens) {
        assertThat(stringCalculatorService.getTokens(expression))
            .containsExactlyElementsOf(expectedTokens);
    }

    private static Stream<Arguments> methodSourceOfCalculateTest() {
        return Stream.of(
            Arguments.arguments("1,2|3", 6),
            Arguments.arguments("//|\n10|20:30", 60),
            Arguments.arguments("//.\n10.20:30,40", 100)
        );
    }

    @ParameterizedTest(name = "{0}의 입력의 문자열 계산기 결과값은 {2}이다.")
    @MethodSource("methodSourceOfCalculateTest")
    @DisplayName("기본 구분자와 커스텀 구분자를 활용하여 숫자를 구분해내고 그 합을 정상적으로 반환할 수 있다.")
    void calculateTest(String expression, int expectedResult) {
        assertThat(stringCalculatorService.calculate(expression))
            .isEqualTo(expectedResult);
    }

}
