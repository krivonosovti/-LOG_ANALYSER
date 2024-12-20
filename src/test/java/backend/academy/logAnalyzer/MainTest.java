package backend.academy.logAnalyzer;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {
    @Test
    public void testLocalDateConverter_correctInput() {
        Main.LocalDateConverter converter = new Main.LocalDateConverter();
        assertEquals(LocalDate.of(2023,03,15), converter.convert("2023-03-15"));
        assertEquals(LocalDate.of(2100,01,01), converter.convert("2100-01-01"));
        assertEquals(LocalDate.of(1900,01,01), converter.convert("1900-01-01"));
    }

    @Test
    public void testLocalDateConverter_incorrectInput() {
        Main.LocalDateConverter converter = new Main.LocalDateConverter();
        assertNull(converter.convert("2023-02-30"));
        assertNull(converter.convert("2023-13-10"));
        assertNull(converter.convert(" 2023-03-15 "));
        assertNull(converter.convert("15/03/2023"));
        assertNull(converter.convert(""));
        assertNull(converter.convert("Hello World"));
    }
}
