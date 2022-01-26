package com.gemini.jobcoin.client

class JobcoinWebClientTest {
    /**
     * Three main options when mocking in our tests for Spring's Webclient
     *
     * 1. Use Mockito to mimic behavior of the WebClient
     * https://www.baeldung.com/mockito-series
     *
     * 2. Use Mockk to mockkClass(Webclient)
     *
     * 2. User Webclient for real, but mock the service it calls by using MockWebServer (okhttp)
     * https://github.com/square/okhttp/tree/master/mockwebserver
     */

    /**
     * Ideas for what I would test:
     * 1. test getTransactions on successful 2XX
     * 2. test getTransactions returns null on non 2XX responses (422)
     *
     * so on and so forth
     *
     */
}
