# Notes

- Fixed Rate vs Fixed Delay
  - We can run a scheduled task using Spring's @Scheduled annotation, but based on the properties fixedDelay and fixedRate, the nature of execution changes.
  - The **fixedDelay** property makes sure that there is a delay of n millisecond between the finish time of an execution of a task and the start time of the next execution of the task.
  - The **fixedRate** property runs the scheduled task at every n millisecond. It doesn't check for any previous executions of the task.
- Example of Scheduled Cron - ````@Scheduled(cron = "0 15 10 15 * ?")````
- 

## Resources 

### Provided Jobcoin Resources
- https://jobcoin.gemini.com/iron-unroll/api
- https://jobcoin.gemini.com/iron-unroll
- https://docs.google.com/document/d/1mlK67tEY7SvmtUDacVveJXufTWExwlQB3BPGTLyPkG0/edit

### Spring Webflux
- https://www.baeldung.com/spring-5-webclient
- https://www.baeldung.com/spring-scheduled-tasks
- https://www.baeldung.com/spring-mocking-webclient
- https://betterprogramming.pub/part-i-how-to-unit-test-your-kotlin-springboot-webflux-webclient-that-is-calling-external-api-714ccaa186c
- Dynamic Scheduling Setup - https://github.com/eugenp/tutorials/tree/master/spring-scheduling/src/main/java/com/baeldung/scheduling/dynamic



