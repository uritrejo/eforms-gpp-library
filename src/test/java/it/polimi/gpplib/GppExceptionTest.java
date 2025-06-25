package it.polimi.gpplib;

import org.junit.Test;

import static org.junit.Assert.*;

public class GppExceptionTest {

    @Test
    public void testGppException_message() {
        GppException ex = new GppException("error message");
        assertEquals("error message", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void testGppException_messageAndCause() {
        Throwable cause = new RuntimeException("cause");
        GppException ex = new GppException("error message", cause);
        assertEquals("error message", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    public void testGppBadRequestException_message() {
        GppBadRequestException ex = new GppBadRequestException("bad request");
        assertEquals("bad request", ex.getMessage());
        assertNull(ex.getCause());
        assertTrue(ex instanceof GppException);
    }

    @Test
    public void testGppBadRequestException_messageAndCause() {
        Throwable cause = new IllegalArgumentException("bad arg");
        GppBadRequestException ex = new GppBadRequestException("bad request", cause);
        assertEquals("bad request", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertTrue(ex instanceof GppException);
    }

    @Test
    public void testGppInternalErrorException_message() {
        GppInternalErrorException ex = new GppInternalErrorException("internal error");
        assertEquals("internal error", ex.getMessage());
        assertNull(ex.getCause());
        assertTrue(ex instanceof GppException);
    }

    @Test
    public void testGppInternalErrorException_messageAndCause() {
        Throwable cause = new NullPointerException("null");
        GppInternalErrorException ex = new GppInternalErrorException("internal error", cause);
        assertEquals("internal error", ex.getMessage());
        assertEquals(cause, ex.getCause());
        assertTrue(ex instanceof GppException);
    }
}
