# Jobcoin Mixer

## Developer Setup

### Prerequisites
1. Java 11
2. gradle
3. maven
4. ```git clone https://github.com/shaunaweiss/JobCoin.git```

### Intellij
1. Import the project to Intellij as an appropriate gradle or maven project
3. Set main class and Run: <br>
   ```com/gemini/jobcoin/JobcoinApplication.kt```

### Gradle
* Compile without running tests:
  ```./gradlew build -x test```
* Compile and run unit tests: ```./gradlew build```
* Style check: ```./gradlew ktlintformat```