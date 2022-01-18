# Jobcoin Mixer

## Developer Setup

### Prerequisites
1. Java 11
2. gradle
4. ```git clone https://github.com/shaunaweiss/JobCoin.git```

### Intellij
1. Import the project to Intellij as an appropriate gradle or maven project
3. Set main class and Run: <br>
   ```com/gemini/jobcoin/JobcoinApplication.kt```

### Gradle
* Compile and run unit tests: ```./gradlew build```
* Style check: ```./gradlew ktlintformat```

## Some assumptions I made throughout 
* The mixer's deposit address that we create is unique and we do not have to worry about the generator duplicating it. 
* The addresses provided to us by the user DO exist and are owned by that user. 
* Only one transaction is being sent to the address provided by the mixer. (If more than one transaction is made, that money is lost to the house address)

## Future Todos
If time was something I had more of, here are a couple of things I would've included/changed...
* UML Diagram
* Unit/Integration Test (100% Test Coverage) using technologies/frameworks such as 
    * Junit5
    * [MockK](https://mockk.io/)
    * [Spring-Boot-Starter-Test](https://docs.spring.io/spring-boot/docs/1.0.x-SNAPSHOT/reference/html/boot-features-testing.html)
* Utilized a Database of sorts. 
* More validation handling around the responses from the Jobcoin API (instead of throwing Runtime Exceptions)...
* Added Unit tests specifically around null property values throughout, to ensure null safety. 

### Future Feature Adds 
* Add a random delay to the tasks responsible for sending the "mixed" transactions to enhance the anonymity of the mixing process.
* Additional Retry Mechanisms- If calls to the external jobcoin API Fail, enqueue the tasks in question to retry at a later cron interval.
  * Further, if for some reason one of the "outgoingAddresses" does not exist or the transaction fails to go through, implement a solution that enables the coins can be sent to one of the other provided addresses, and if all else fails, back to the original sender address.
